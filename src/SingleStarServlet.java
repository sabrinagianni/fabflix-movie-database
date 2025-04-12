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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT s.id as starId, s.name as starName, s.birthYear, " +
                    "m.id as movieId, m.title as movieTitle, m.year as movieYear, m.director as movieDirector " +
                    "FROM stars as s " +
                    "JOIN stars_in_movies as sim ON s.id = sim.starId " +
                    "JOIN movies as m ON sim.movieId = m.id " +
                    "WHERE s.id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

//            JsonArray jsonArray = new JsonArray();
            JsonObject starJson = new JsonObject();

            // Iterate through each row of rs
            // Store star info
            if (rs.next()) {
                starJson.addProperty("star_id", rs.getString("starId"));
                starJson.addProperty("star_name", rs.getString("starName"));
                starJson.addProperty("star_dob", rs.getString("birthYear") != null ? rs.getString("birthYear") : "N/A");

                // Create a JsonArray to hold all movies for the star
                JsonArray moviesArray = new JsonArray();
                do {
                    JsonObject movieJson = new JsonObject();
                    movieJson.addProperty("movie_id", rs.getString("movieId"));
                    movieJson.addProperty("movie_title", rs.getString("movieTitle"));
                    movieJson.addProperty("movie_year", rs.getString("movieYear"));
                    movieJson.addProperty("movie_director", rs.getString("movieDirector"));
                    moviesArray.add(movieJson);
                } while (rs.next());

                // Add movies to the star's JSON object
                starJson.add("movies", moviesArray);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(starJson);
            out.write(jsonArray.toString());
            System.out.println("JSON Response: " + jsonArray.toString());
            request.getServletContext().log("Star ID: " + id);
            request.getServletContext().log("Query: " + query);
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}