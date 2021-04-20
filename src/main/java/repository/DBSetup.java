package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBSetup {

    public void updateQuery(String query, Properties connectionProperties) {

        String url = connectionProperties.getProperty("url");
        String user = connectionProperties.getProperty("user");
        String pwd = connectionProperties.getProperty("password");

        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {

            Statement statement = connection.createStatement();

            statement.executeUpdate(query);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
