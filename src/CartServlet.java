import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public static class cartDisplay{
        private String movieId;
        private String title;
        private int quantity;
        private double price;

        public cartDisplay(String movieId, String title, int quantity, double price) {
            this.movieId = movieId;
            this.title = title;
            this.quantity = 1;
            this.price = price;
        }

        public void increaseQuantity(){
            this.quantity++;
        }

        public void decreaseQuantity(){
            if (this.quantity > 0){
                this.quantity--;
            }
        }

        public int getQuantity(){
            return quantity;
        }

        public JsonObject makeJsonObject(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movieId", movieId);
            jsonObject.addProperty("title", title);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("price", price);
            jsonObject.addProperty("total", quantity * price);
            return jsonObject;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, cartDisplay> cart = (HashMap<String, cartDisplay>) session.getAttribute("cart");

        if (cart == null){
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        JsonArray jsonArray = new JsonArray();
        for(cartDisplay cd : cart.values()){
            jsonArray.add(cd.makeJsonObject());
        }
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("movieId");
        String title = request.getParameter("title");
        String priceInString = request.getParameter("price");
        double priceInDouble = Double.parseDouble(priceInString);

        HttpSession session = request.getSession();
        HashMap<String, cartDisplay> cart = (HashMap<String, cartDisplay>) session.getAttribute("cart");

        if (cart == null){
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        if (cart.containsKey(movieId)){
            cart.get(movieId).increaseQuantity();
        } else {
            cart.put(movieId, new cartDisplay(movieId, title, priceInDouble));
        }

        JsonObject newJsonObject = new JsonObject();
        newJsonObject.addProperty("status", "success");
        response.getWriter().write(newJsonObject.toString());
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("movieId");
        String quantity = request.getParameter("quantity");
        int quantityInt = Integer.parseInt(quantity);

        HttpSession session = request.getSession();
        HashMap<String, cartDisplay> cart = (HashMap<String, cartDisplay>) session.getAttribute("cart");

        if (cart == null){
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        if (cart.containsKey(movieId)){
            cartDisplay cd = cart.get(movieId);
            if(quantityInt > 0){
                cd.quantity = quantityInt;
            } else {
                cart.remove(movieId);
            }
        }

        JsonObject newJsonObject = new JsonObject();
        newJsonObject.addProperty("status", "success");
        response.getWriter().write(newJsonObject.toString());
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("movieId");

        HttpSession session = request.getSession();
        HashMap<String, cartDisplay> cart = (HashMap<String, cartDisplay>) session.getAttribute("cart");

        if (cart != null){
            cart.remove(movieId);
        }

        JsonObject newJsonObject = new JsonObject();
        newJsonObject.addProperty("status", "success");
        response.getWriter().write(newJsonObject.toString());
    }
}
