package login;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyRecaptcha(String gRecaptchaResponse) throws Exception {
        String secret = "6LeeekgrAAAAAMngw7YCP4U_BZGP8cHxlk2yNiAT";
        URL verifyUrl = new URL("https://www.google.com/recaptcha/api/siteverify");

        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("login.User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;

        conn.setDoOutput(true);
        OutputStream outStream = conn.getOutputStream();
        outStream.write(postParams.getBytes());
        outStream.flush();
        outStream.close();

        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        JsonObject jsonObject = new com.google.gson.Gson().fromJson(inputStreamReader, JsonObject.class);
        inputStreamReader.close();

        return jsonObject.get("success").getAsBoolean();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("g-recaptcha-response: " + gRecaptchaResponse);

        try{
            boolean recaptcha = verifyRecaptcha(gRecaptchaResponse);
            if(!recaptcha){
                jsonResponse.addProperty("status", "fail");
                jsonResponse.addProperty("message", "reCAPTCHA verification failed.");
                out.write(jsonResponse.toString());
                return;
            }
        }
        catch (Exception e) {
            jsonResponse.addProperty("status", "fail");
            jsonResponse.addProperty("message", "Error verifying reCAPTCHA.");
            out.write(jsonResponse.toString());
            return;
        }

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

        //response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
