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

import java.io.PrintWriter;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();

        String email = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject json = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            request.getServletContext().log("Connected to database");

            String query = "SELECT * FROM customers WHERE email = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("id");
                request.getSession().setAttribute("user_id", customerId);
                request.getSession().setAttribute("user", new User(email));
                json.addProperty("status", "success");
                json.addProperty("message", "success");
            } else {
                json.addProperty("status", "fail");
                json.addProperty("message", "Invalid email or password");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            request.getServletContext().log("Login error", e); // log to tomcat
            json.addProperty("status", "fail");
            json.addProperty("message", "Server error");
        }

//        response.setContentType("application/json");
//        response.getWriter().write(json.toString());

//        out.write(json.toString());
//        out.close();

        response.getWriter().write(json.toString());
    }
}
