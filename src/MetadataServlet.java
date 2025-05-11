import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        StringBuilder metadata = new StringBuilder();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet tables = dbMeta.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                metadata.append("Table: ").append(tableName).append("\n");

                ResultSet columns = dbMeta.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    metadata.append("    ").append(columnName).append(" - ").append(columnType).append("\n");
                }
                metadata.append("\n");
                columns.close();
            }
            tables.close();

            json.addProperty("metadata", metadata.toString());
        } catch (Exception e) {
            json.addProperty("metadata", "Error retrieving metadata.");
        }

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}
