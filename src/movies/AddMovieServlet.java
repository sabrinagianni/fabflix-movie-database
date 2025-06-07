package movies;

import com.google.gson.JsonObject;
import common.JwtUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadWrite");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String starBirthYearStr = request.getParameter("starBirthYear");
        String genre = request.getParameter("genre");

        JsonObject json = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement cs = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?, ?)}");

            cs.setString(1, title);
            cs.setInt(2, Integer.parseInt(year));
            cs.setString(3, director);
            cs.setString(4, starName);

            if (starBirthYearStr == null || starBirthYearStr.isEmpty()) {
                cs.setNull(5, Types.INTEGER);
            } else {
                cs.setInt(5, Integer.parseInt(starBirthYearStr));
            }

            cs.setString(6, genre);

            // OUT parameter for result message
            cs.registerOutParameter(7, Types.VARCHAR);

            cs.execute();

            String resultMessage = cs.getString(7);
            json.addProperty("status", "success");
            json.addProperty("message", resultMessage);

            cs.close();

//            json.addProperty("status", "success");
//            json.addProperty("message", "Movie added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("status", "fail");
            json.addProperty("message", "Error adding movie.");
        }

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}