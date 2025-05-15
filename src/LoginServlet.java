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
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyRecaptcha(String gRecaptchaResponse) throws Exception {
        String secret = "6LcgnDorAAAAAAKKjcz8i_oMON2qb_GXzdkVo0cW";
        URL verifyUrl = new URL("https://www.google.com/recaptcha/api/siteverify");

        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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

        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("g-recaptcha-response: " + gRecaptchaResponse);
        JsonObject json = new JsonObject();

        try{
            boolean recaptcha = verifyRecaptcha(gRecaptchaResponse);
            if(!recaptcha){
                json.addProperty("status", "fail");
                json.addProperty("message", "reCAPTCHA verification failed.");
                out.write(json.toString());
                return;
            }
        }
        catch (Exception e) {
            json.addProperty("status", "fail");
            json.addProperty("message", "Error verifying reCAPTCHA.");
            out.write(json.toString());
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            request.getServletContext().log("Connected to database");

            String query = "SELECT * FROM customers WHERE email = ?" /*AND password = ?*/;
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
//            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                boolean passwordMatch = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if (passwordMatch) {
                    int customerId = rs.getInt("id");
                    request.getSession().setAttribute("user", new User(email));
                    request.getSession().setAttribute("user_id", customerId);
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
