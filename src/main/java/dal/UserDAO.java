package dal;

import model.Attendee;
import model.User;
import util.DbConnectionInt;
import util.singletonDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DbConnectionInt dbConnection;

    public UserDAO() {
        dbConnection = singletonDbConnection.getInstance();
    }

    public String registerUser(User user) {
        Connection connection = dbConnection.getConnection();
        String insertUserQuery = "INSERT INTO User (SJSUID, SJSUEmail, Username, Password, Role) VALUES (?, ?, ?, ?, ?)";

        try {
            // Insert user
            PreparedStatement UserStm = connection.prepareStatement(insertUserQuery);
            UserStm.setInt(1, user.getSjsuId());
            UserStm.setString(2, user.getSjsuEmail());
            UserStm.setString(3, user.getUsername());
            UserStm.setString(4, user.getPassword());
            UserStm.setString(5, user.getRole());

            int i = UserStm.executeUpdate();
            //Just to ensure data has been inserted into the database
            if(i != 0)	return "SUCCESS";
        } catch (SQLException e) {
            System.out.println("DB operation failure. reason:\n");
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }
        // On failure, send a message from here.
        return "Oops.. Something went wrong there..!";
    }

    public void updateUser(User user) {
        Connection connection = dbConnection.getConnection();
        String updateQuery = "UPDATE User SET SJSUEmail=?, Username=?, Password=?, Role=? WHERE SJSUID=?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, user.getSjsuEmail());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setInt(4, user.getSjsuId());
            preparedStatement.setString(5, user.getRole());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }
    }

    public void deleteUser(int sjsuId) {
        Connection connection = dbConnection.getConnection();
        String deleteQuery = "DELETE FROM User WHERE SJSUID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, sjsuId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }
    }

    public User getUserById(int sjsuId) {
        Connection connection = dbConnection.getConnection();
        String selectQuery = "SELECT * FROM User WHERE SJSUID = ?";
        User user = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, sjsuId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setSjsuId(Integer.parseInt(resultSet.getString("SJSUID")));
                user.setSjsuEmail(resultSet.getString("SJSUEmail"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }

        return user;
    }

    public User getUserByUsername(String username) {
        Connection connection = dbConnection.getConnection();
        String selectQuery = "SELECT * FROM User WHERE Username = ?";
        User user = null;

        try {
//            if (!(connection != null && connection.isValid(2))){
//                System.out.println("Connection has died");
//            }
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setSjsuId(Integer.parseInt(resultSet.getString("SJSUID")));
                user.setSjsuEmail(resultSet.getString("SJSUEmail"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }

        return user;
    }

    public List<User> getAllUsers() {
        Connection connection = dbConnection.getConnection();
        String selectQuery = "SELECT * FROM User";
        List<User> userList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setSjsuId(Integer.parseInt(resultSet.getString("SJSUID")));
                user.setSjsuEmail(resultSet.getString("SJSUEmail"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
                user.setRole(resultSet.getString("Role"));

                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }

        return userList;
    }
}