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

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // FOR FUZZY SEARCH
//    private int getThreshold(String input) {
//        if (input.length() <= 4) return 1;
//        if (input.length() <= 7) return 2;
//        return 3;
//    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String query = request.getParameter("query");
        JsonArray suggestions = new JsonArray();

        if (query == null || query.trim().length() < 3) {
            response.getWriter().write(suggestions.toString());
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String[] tokens = query.trim().split("\\s+");
            StringBuilder matchExpr = new StringBuilder();
            for (String token : tokens) {
                matchExpr.append("+").append(token).append("* ");
            }

            // ADDED BLOCK TO TEST FUZZY SEARCH ----------------------------------------------------
//            String sql = "SELECT id, title FROM movies " +
//                    "WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) " +
//                    "OR title LIKE ? " +
//                    "OR edth(title, ?, ?) = 1 " +
//                    "LIMIT 10";
//
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.setString(1, matchExpr.toString().trim());       // for MATCH
//            ps.setString(2, "%" + query + "%");                 // for LIKE
//            ps.setString(3, query);                             // for edth
//            ps.setInt(4, getThreshold(query));                  // edit threshold

            String sql = "SELECT id, title FROM movies WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, matchExpr.toString().trim());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject suggestion = new JsonObject();
                suggestion.addProperty("value", rs.getString("title"));
                JsonObject data = new JsonObject();
                data.addProperty("movieId", rs.getString("id"));
                suggestion.add("data", data);
                suggestions.add(suggestion);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            suggestions.add(error);
        }

        response.getWriter().write(suggestions.toString());
    }
}