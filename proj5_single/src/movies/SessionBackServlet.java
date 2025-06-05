package movies;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "SessionBackServlet", urlPatterns = "/api/save-session-params")
public class SessionBackServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        session.setAttribute("last_genre", request.getParameter("genre"));
        session.setAttribute("last_title", request.getParameter("title"));
        session.setAttribute("last_sort", request.getParameter("sort"));
        session.setAttribute("last_limit", Integer.parseInt(request.getParameter("limit")));
        session.setAttribute("last_page", Integer.parseInt(request.getParameter("page")));

        response.setStatus(200);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // to GET saved session values
    }
}