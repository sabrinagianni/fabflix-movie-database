import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.util.HashMap;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        HttpSession session = request.getSession();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNumber = request.getParameter("cardNumber");
        String expiration = request.getParameter("expiration");

        if (!expiration.matches("\\d{4}/\\d{2}/\\d{2}")) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "fail");
            errorJson.addProperty("message", "Expiration date format must be YYYY/MM/DD.");
            response.getWriter().write(errorJson.toString());
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int customerId = (int) session.getAttribute("user_id");

                HashMap<String, CartServlet.CartDisplay> cart = (HashMap<String, CartServlet.CartDisplay>) session.getAttribute("cart");
                if (cart != null) {
                    for (CartServlet.CartDisplay cd : cart.values()) {
                        for (int i = 0; i < cd.getQuantity(); i++) {
                            PreparedStatement insertSale = conn.prepareStatement(
                                    "INSERT INTO sales(customerId, movieId, saleDate) VALUES (?, ?, ?)"
                            );
                            insertSale.setInt(1, customerId);
                            insertSale.setString(2, cd.getMovieId());
                            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            insertSale.setString(3, today);
                            insertSale.executeUpdate();
                        }
                    }
                }

                session.removeAttribute("cart");

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Payment Successful!");
            } else {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid credit card information!");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Server error");
        }
        response.setStatus(200);
        response.setContentType("application/json");
        response.getWriter().write(responseJsonObject.toString());
    }
}
