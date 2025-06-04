import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;
import java.io.PrintWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


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

//    private boolean verifyRecaptcha(String gRecaptchaResponse) throws Exception {
//        String secret = "6LeeekgrAAAAAMngw7YCP4U_BZGP8cHxlk2yNiAT";
//        URL verifyUrl = new URL("https://www.google.com/recaptcha/api/siteverify");
//
//        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
//        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//        String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;
//
//        conn.setDoOutput(true);
//        OutputStream outStream = conn.getOutputStream();
//        outStream.write(postParams.getBytes());
//        outStream.flush();
//        outStream.close();
//
//        InputStream inputStream = conn.getInputStream();
//        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//
//        JsonObject jsonObject = new com.google.gson.Gson().fromJson(inputStreamReader, JsonObject.class);
//        inputStreamReader.close();
//
//        return jsonObject.get("success").getAsBoolean();
//    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
System.out.println("Login attempt received");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("username");
        String password = request.getParameter("password");
//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        System.out.println("g-recaptcha-response: " + gRecaptchaResponse);
        JsonObject json = new JsonObject();
    System.out.println("[LoginServlet] Received login request");
    System.out.println("[LoginServlet] Username: " + email);
    System.out.println("[LoginServlet] Password: " + password);
//        try{
//            boolean recaptcha = verifyRecaptcha(gRecaptchaResponse);
//            if(!recaptcha){
//                json.addProperty("status", "fail");
//                json.addProperty("message", "reCAPTCHA verification failed.");
//                out.write(json.toString());
//                return;
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace(); // <--- ADD THIS
//            request.getServletContext().log("reCAPTCHA verification error", e);
//            json.addProperty("status", "fail");
//            json.addProperty("message", "Error verifying reCAPTCHA.");
//            out.write(json.toString());
//            return;
//        }

        try (Connection conn = dataSource.getConnection()) {
System.out.println("[LoginServlet] Successfully connected to database");
            request.getServletContext().log("Connected to database");

            String query = "SELECT * FROM customers WHERE email = ?" /*AND password = ?*/;
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
        System.out.println("[LoginServlet] Executing query: " + query);

//            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
System.out.println("Executed query for email: " + email);

            if (rs.next()) {
//                String encryptedPassword = rs.getString("password");
//                boolean passwordMatch = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
//                if (passwordMatch) {
            System.out.println("[LoginServlet] User found: " + email);
                  String storedPassword = rs.getString("password");
            System.out.println("[LoginServlet] Stored password: " + storedPassword);
                  if (password.equals(storedPassword)) {
                    int customerId = rs.getInt("id");
                    request.getSession().setAttribute("user", new User(email));
                    request.getSession().setAttribute("user_id", customerId);
                System.out.println("[LoginServlet] Login success, session attributes set");
                    json.addProperty("status", "success");
                    json.addProperty("message", "success");
                } else {
                System.out.println("[LoginServlet] Password mismatch");
                    json.addProperty("status", "fail");
                    json.addProperty("message", "Invalid email or password");
                }
            } else {
           System.out.println("[LoginServlet] No account found for email: " + email);
                json.addProperty("status", "fail");
                json.addProperty("message", "No account with that email");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
        System.out.println("[LoginServlet] Exception occurred:");
            e.printStackTrace();
            request.getServletContext().log("Login error", e); // log to tomcat
            json.addProperty("status", "fail");
            json.addProperty("message", "Server error");
        }

//        response.setContentType("application/json");
//        response.getWriter().write(json.toString());

        System.out.println("Returning JSON: " + json.toString());
        out.write(json.toString());
        out.close();

//        response.getWriter().write(json.toString());
//        out.close();
    }
}
