package login;

import java.sql.*;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class EncryptEmployeePassword {
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        // change the employees table password column from VARCHAR(20) to VARCHAR(128)
        String alterQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        PreparedStatement alterStatement = connection.prepareStatement(alterQuery);
        int alterResult = alterStatement.executeUpdate();
        alterStatement.close();
        System.out.println("altering employees table schema completed, " + alterResult + " rows affected");

        // get the email and password for each employee
        String query = "SELECT email, password FROM employees";

        PreparedStatement queryStatement = connection.prepareStatement(query);
        ResultSet rs = queryStatement.executeQuery();

        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        ArrayList<String> updateQueryList = new ArrayList<>();

        System.out.println("encrypting passwords...");
        while (rs.next()) {
            String email = rs.getString("email");
            String password = rs.getString("password");

            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s';",
                    encryptedPassword, email);
            updateQueryList.add(updateQuery);
        }

        rs.close();
        queryStatement.close();

        System.out.println("updating passwords in DB...");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            int updateResult = updateStatement.executeUpdate();
            updateStatement.close();
            count += updateResult;
        }

        connection.close();
        System.out.println("password encryption completed for employees, " + count + " rows affected");
    }
}
