import java.sql.*;

public class Database {

    public static Connection getConnection(String dbURL, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, user, password);
        connection.setAutoCommit(true);
        return connection;
    }

    public static void closeConnection(Connection connection) {

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            // exception.printStackTrace();
        }

    }

    public static void closeStatement(Statement statement) {

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException exception) {
            // exception.printStackTrace();
        }

    }

    public static void closeResultSet(ResultSet resultSet) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException exception) {
            // exception.printStackTrace();
        }

    }

}
