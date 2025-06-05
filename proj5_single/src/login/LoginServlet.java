/* package login;

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
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
System.out.println("Login attempt received");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject json = new JsonObject();
    System.out.println("[login.LoginServlet] Received login request");
    System.out.println("[login.LoginServlet] Username: " + email);
    System.out.println("[login.LoginServlet] Password: " + password);
        try (Connection conn = dataSource.getConnection()) {
System.out.println("[login.LoginServlet] Successfully connected to database");
            request.getServletContext().log("Connected to database");

            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
        System.out.println("[login.LoginServlet] Executing query: " + query);
           ResultSet rs = statement.executeQuery();
System.out.println("Executed query for email: " + email);

            if (rs.next()) {
            System.out.println("[login.LoginServlet] login.User found: " + email);
                  String storedPassword = rs.getString("password");
            System.out.println("[login.LoginServlet] Stored password: " + storedPassword);
                  if (password.equals(storedPassword)) {
                    int customerId = rs.getInt("id");
                    request.getSession().setAttribute("user", new User(email));
                    request.getSession().setAttribute("user_id", customerId);
                System.out.println("[login.LoginServlet] Login success, session attributes set");
                    json.addProperty("status", "success");
                    json.addProperty("message", "success");
                } else {
                System.out.println("[login.LoginServlet] Password mismatch");
                    json.addProperty("status", "fail");
                    json.addProperty("message", "Invalid email or password");
                }
            } else {
           System.out.println("[login.LoginServlet] No account found for email: " + email);
                json.addProperty("status", "fail");
                json.addProperty("message", "No account with that email");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
        System.out.println("[login.LoginServlet] Exception occurred:");
            e.printStackTrace();
            request.getServletContext().log("Login error", e);
            json.addProperty("status", "fail");
            json.addProperty("message", "Server error");
        }

        System.out.println("Returning JSON: " + json.toString());
        out.write(json.toString());
        out.close();
   }
} */

package login;

import com.google.gson.JsonObject;
import common.JwtUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject json = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (password.equals(storedPassword)) {

                    Map<String, Object> claims = new HashMap<>();
                    String loginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    claims.put("loginTime", loginTime);
                    claims.put("email", email);


                    String token = JwtUtil.generateToken(email, claims);
                    JwtUtil.updateJwtCookie(request, response, token);

                    json.addProperty("status", "success");
                    json.addProperty("message", "success");
                } else {
                    json.addProperty("status", "fail");
                    json.addProperty("message", "Invalid email or password");
                }
            } else {
                json.addProperty("status", "fail");
                json.addProperty("message", "No account with that email");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("status", "fail");
            json.addProperty("message", "Server error");
        }

        out.write(json.toString());
        out.close();
    }
}

