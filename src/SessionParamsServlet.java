import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "SessionParamsServlet", urlPatterns = "/api/get-session-params")
public class SessionParamsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        String genre = (String) session.getAttribute("last_genre");
        String title = (String) session.getAttribute("last_title");
        String sort = (String) session.getAttribute("last_sort");
        Integer limit = (Integer) session.getAttribute("last_limit");
        Integer page = (Integer) session.getAttribute("last_page");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("genre", genre != null ? genre : "");
        responseJson.addProperty("title", title != null ? title : "");
        responseJson.addProperty("sort", sort != null ? sort : "ratingdesc_titleasc");
        responseJson.addProperty("limit", limit != null ? limit : 10);
        responseJson.addProperty("page", page != null ? page : 1);

        response.setContentType("application/json");
        response.getWriter().write(responseJson.toString());
    }
}