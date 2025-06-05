package movies;

import java.sql.*;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class VerifyPassword {
    public static void main(String[] args) throws Exception {

        System.out.println(verifyCredentials("a@email.com", "a2"));
        System.out.println(verifyCredentials("a@email.com", "a3"));

    }

    private static boolean verifyCredentials(String email, String password) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String query = String.format("SELECT * from customers where email='%s'", email);

        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet rs = statement.executeQuery(query);

        boolean success = false;
        if (rs.next()) {
            String encryptedPassword = rs.getString("password");
            success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        }

        rs.close();
        statement.close();
        connection.close();

        System.out.println("verify " + email + " - " + password);

        return success;
    }

}