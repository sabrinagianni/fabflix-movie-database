import java.sql.*;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateSecurePassword {
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        PreparedStatement alterStatement = connection.prepareStatement(alterQuery);
        int alterResult = alterStatement.executeUpdate();
        alterStatement.close();
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");

        String query = "SELECT id, password from customers";

        PreparedStatement queryStatement = connection.prepareStatement(query);

        ResultSet rs = queryStatement.executeQuery(query);

        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList = new ArrayList<>();

        System.out.println("encrypting password (this might take a while)");
        while (rs.next()) {
            String id = rs.getString("id");
            String password = rs.getString("password");

            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            String updateQuery = String.format("UPDATE customers SET password='%s' WHERE id=%s;", encryptedPassword,
                    id);
            updateQueryList.add(updateQuery);
        }
        rs.close();
        queryStatement.close();

        System.out.println("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            int updateResult = updateStatement.executeUpdate();
            updateStatement.close();
            count += updateResult;
        }
        System.out.println("updating password completed, " + count + " rows affected");

        connection.close();

        System.out.println("finished");

    }

}