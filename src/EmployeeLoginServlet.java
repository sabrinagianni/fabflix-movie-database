import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM employees WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                boolean passwordMatch = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (passwordMatch) {
                    request.getSession().setAttribute("employee", email);
                    jsonResponse.addProperty("status", "success");
                } else {
                    jsonResponse.addProperty("status", "fail");
                    jsonResponse.addProperty("message", "Incorrect password.");
                }
            } else {
                jsonResponse.addProperty("status", "fail");
                jsonResponse.addProperty("message", "No such employee.");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            jsonResponse.addProperty("status", "fail");
            jsonResponse.addProperty("message", "Server error.");
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
