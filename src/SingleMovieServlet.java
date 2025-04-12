import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/singlemovie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // check web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String movieId = request.getParameter("id");

        request.getServletContext().log("getting id: " + movieId);

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT m.id, m.title, m.year, m.director, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genres, " +
                    "GROUP_CONCAT(DISTINCT s.name, ':', s.id) AS stars " +
                    "FROM movies m " +
                    "LEFT JOIN genres_in_movies gim ON m.id = gim.movieId " +
                    "LEFT JOIN genres g ON gim.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sim ON m.id = sim.movieId " +
                    "LEFT JOIN stars s ON sim.starId = s.id " +
                    "WHERE m.id = ? " +
                    "GROUP BY m.id";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, movieId);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while(rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");
                String g = rs.getString("genres");
                String s = rs.getString("stars");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);

                JsonArray genresArr = new JsonArray();
                if(g != null) {
                    String[] gArr = g.split(",");
                    for(String gArr1 : gArr) {
                        genresArr.add(gArr1.trim());
                    }
                }
                jsonObject.add("genres", genresArr);

                JsonArray starsArr = new JsonArray();
                if(s != null) {
                    String[] sArr = s.split(",");
                    for(String sArr1 : sArr) {
                        String[] sArr2 = sArr1.split(":");
                        if(sArr2.length == 2) {
                            JsonObject str = new JsonObject();
                            str.addProperty("name", sArr2[0].trim());
                            str.addProperty("id", sArr2[1].trim());
                            starsArr.add(str);
                        }
                    }
                }
                jsonObject.add("stars", starsArr);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");

            out.write(jsonArray.toString());

            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}