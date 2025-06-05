package movies;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "InsertStarServlet", urlPatterns = "/api/insert-star")
public class InsertStarServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadWrite");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String birthYearStr = request.getParameter("birthYear");
        JsonObject json = new JsonObject();

        if (name == null || name.trim().isEmpty()) {
            json.addProperty("status", "fail");
            json.addProperty("message", "Star name is required.");
            response.setContentType("application/json");
            response.getWriter().write(json.toString());
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            // Generate new star ID
            String maxIdQuery = "SELECT MAX(id) AS maxId FROM stars";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(maxIdQuery);

            String newId = "nm0000001"; // default starting ID
            if (rs.next()) {
                String maxId = rs.getString("maxId");
                if (maxId != null && maxId.startsWith("nm")) {
                    int maxNum = Integer.parseInt(maxId.substring(2));
                    newId = "nm" + String.format("%07d", maxNum + 1);
                }
            }
            rs.close();
            stmt.close();

            // Prepare insert query
            String insertQuery;
            PreparedStatement ps;
            if (birthYearStr == null || birthYearStr.isEmpty()) {
                insertQuery = "INSERT INTO stars (id, name) VALUES (?, ?)";
                ps = conn.prepareStatement(insertQuery);
                ps.setString(1, newId);
                ps.setString(2, name);
            } else {
                insertQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(insertQuery);
                ps.setString(1, newId);
                ps.setString(2, name);
                ps.setInt(3, Integer.parseInt(birthYearStr));
            }

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                json.addProperty("status", "success");
                json.addProperty("message", "Star inserted successfully with ID: " + newId);
            } else {
                json.addProperty("status", "fail");
                json.addProperty("message", "Star insertion failed.");
            }

        } catch (Exception e) {
            json.addProperty("status", "fail");
            json.addProperty("message", "Error inserting star.");
            e.printStackTrace(); //debugging
        }

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}
