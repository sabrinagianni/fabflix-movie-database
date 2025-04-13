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

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query =
                    "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                            "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres, " +
                            "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name SEPARATOR ', ') AS stars " +
                            "FROM movies m " +
                            "JOIN ratings r ON m.id = r.movieId " +
                            "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                            "LEFT JOIN genres g ON g.id = gm.genreId " +
                            "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                            "LEFT JOIN stars s ON s.id = sm.starId " +
                            "GROUP BY m.id " +
                            "ORDER BY r.rating DESC " +
                            "LIMIT 20";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", rs.getString("id"));
                jsonObject.addProperty("title", rs.getString("title"));
                jsonObject.addProperty("year", rs.getString("year"));
                jsonObject.addProperty("director", rs.getString("director"));
                jsonObject.addProperty("rating", rs.getString("rating"));

                String genresStr = rs.getString("genres");
                JsonArray genresArray = new JsonArray();
                if (genresStr != null) {
                    for (String genre : genresStr.split(",\\s*")) {
                        genresArray.add(genre);
                    }
                }
                jsonObject.add("genres", genresArray);

                String starsStr = rs.getString("stars");
                JsonArray starsArray = new JsonArray();
                if (starsStr != null) {
                    for (String star : starsStr.split(",\\s*")) {
                        String[] parts = star.split(":");
                        if (parts.length == 2) {
                            JsonObject starObj = new JsonObject();
                            starObj.addProperty("id", parts[0]);
                            starObj.addProperty("name", parts[1]);
                            starsArray.add(starObj);
                        }
                    }
                }
                jsonObject.add("stars", starsArray);

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            out.write(error.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
