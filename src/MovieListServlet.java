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
import java.util.ArrayList;
import java.util.List;

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
            String genre = request.getParameter("genre");
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String sort = request.getParameter("sort");
            int limit = Integer.parseInt(request.getParameter("limit"));
            int page = Integer.parseInt(request.getParameter("page"));
            int offset = (page - 1) * limit;


            StringBuilder query = new StringBuilder(

                    "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                            "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres, " +
                            "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name)" +
                            "ORDER BY IFNULL(sc.movie_count, 0) DESC, s.name ASC SEPARATOR ', ') AS stars " +
                            "FROM movies m " +
                            "JOIN ratings r ON m.id = r.movieId " +
                            "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                            "LEFT JOIN genres g ON gm.genreId = g.id " +
                            "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                            "LEFT JOIN stars s ON sm.starId = s.id " +
                            "LEFT JOIN (SELECT starId, COUNT(*) AS movie_count FROM stars_in_movies GROUP BY starId) sc ON sm.starId = sc.starId ");

            List<String> filters = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (genre != null && !genre.isEmpty()) {
                filters.add("g.name = ?");
                params.add(genre);
            }

            if (title != null && !title.isEmpty()) {
                if (title.equals("*")) {
                    filters.add("m.title REGEXP '^[^a-zA-Z0-9]'");
                } else {
                    filters.add("m.title LIKE ?");
                    params.add(title + "%");
                }
            }

            if (year != null && !year.isEmpty()) {
                filters.add("m.year = ?");
                params.add(year);
            }

            if (director != null && !director.isEmpty()) {
                filters.add("m.director LIKE ?");
                params.add("%" + director + "%");
            }

            if (star != null && !star.isEmpty()) {
                filters.add("s.name LIKE ?");
                params.add("%" + star + "%");
            }

            if (!filters.isEmpty()) {
                query.append("WHERE ").append(String.join(" AND ", filters)).append(" ");
            }

            query.append("GROUP BY m.id ");

            if (sort != null) {
                query.append("ORDER BY ");
                switch (sort) {
                    case "titleasc_ratingdesc":
                        query.append("m.title ASC, r.rating DESC ");
                        break;
                    case "titleasc_ratingasc":
                        query.append("m.title ASC, r.rating ASC ");
                        break;
                    case "titledesc_ratingasc":
                        query.append("m.title DESC, r.rating ASC ");
                        break;
                    case "titledesc_ratingdesc":
                        query.append("m.title DESC, r.rating DESC ");
                        break;
                    case "ratingasc_titleasc":
                        query.append("r.rating ASC, m.title ASC ");
                        break;
                    case "ratingasc_titledesc":
                        query.append("r.rating ASC, m.title DESC ");
                        break;
                    case "ratingdesc_titleasc":
                        query.append("r.rating DESC, m.title ASC ");
                        break;
                    case "ratingdesc_titledesc":
                        query.append("r.rating DESC, m.title DESC ");
                        break;
                    default:
                        query.append("r.rating DESC, m.title ASC "); // fallback
                }
            }

            query.append("LIMIT ? OFFSET ?");

            PreparedStatement statement = conn.prepareStatement(query.toString());

            int i = 1;
            for (String p : params) {
                statement.setString(i++, p);
            }
            statement.setInt(i++, limit);
            statement.setInt(i, offset);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject movie = new JsonObject();
                movie.addProperty("id", rs.getString("id"));
                movie.addProperty("title", rs.getString("title"));
                movie.addProperty("year", rs.getString("year"));
                movie.addProperty("director", rs.getString("director"));
                movie.addProperty("rating", rs.getString("rating"));

                JsonArray genresArray = new JsonArray();
                if (rs.getString("genres") != null) {
                    for (String g : rs.getString("genres").split(",\\s*")) {
                        genresArray.add(g);
                    }
                }
                movie.add("genres", genresArray);

                JsonArray starsArray = new JsonArray();
                if (rs.getString("stars") != null) {
                    for (String s : rs.getString("stars").split(",\\s*")) {
                        String[] parts = s.split(":");
                        if (parts.length == 2) {
                            JsonObject starObj = new JsonObject();
                            starObj.addProperty("id", parts[0]);
                            starObj.addProperty("name", parts[1]);
                            starsArray.add(starObj);
                        }
                    }
                }
                movie.add("stars", starsArray);

                jsonArray.add(movie);
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
