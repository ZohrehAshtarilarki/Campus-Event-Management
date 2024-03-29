package dal;

import model.User;
import util.DbConnectionInt;
import util.singletonDbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DbConnectionInt dbConnection;

    public UserDAO() {
        dbConnection = singletonDbConnection.getInstance();
    }

    public String registerUser(User user) {
        Connection connection = dbConnection.getConnection();
        String insertUserQuery = "INSERT INTO user (sjsu_id, sjsu_email, username, password, role) VALUES (?, ?, ?, ?, ?)";

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
            if(e instanceof SQLIntegrityConstraintViolationException) {
                // This exception is thrown when a duplicate primary key is inserted
                return "User with SJSUID " + user.getSjsuId() + " already exists.";
            }
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }
        return "Oops.. Something went wrong there..!";
    }

    public boolean checkUserExists(int sjsuId) {
        Connection connection = dbConnection.getConnection();
        String checkUserQuery = "SELECT * FROM user WHERE sjsu_id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(checkUserQuery);
            stmt.setInt(1, sjsuId);

            ResultSet rs = stmt.executeQuery();
            // If the result set is not empty, it means a user with this SJSUID exists
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("DB operation failure. reason:\n");
            e.printStackTrace();
            // Handle exceptions appropriately later
        } finally {
            dbConnection.closeConnection();
        }

        return false;
    }

    public boolean updateUser(User user) {
        Connection connection = dbConnection.getConnection();
        boolean updateResult = false;
        String updateQuery = "UPDATE user SET sjsu_email=?, username=?, password=?, role=? WHERE sjsu_id=?";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, user.getSjsuEmail());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setInt(5, user.getSjsuId());


            // Execute the update
            int affectedRows = preparedStatement.executeUpdate();
            updateResult = affectedRows > 0; // true if the update affected at least one row
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging this exception or handle it appropriately
        } finally {
            // Close resources
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            dbConnection.closeConnection();
        }

        return updateResult;
    }

    public void deleteUser(int sjsuId) {
        Connection connection = dbConnection.getConnection();
        String deleteQuery = "DELETE FROM user WHERE sjsu_id = ?";

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
        String selectQuery = "SELECT * FROM user WHERE sjsu_id = ?";
        User user = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, sjsuId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setSjsuId(Integer.parseInt(resultSet.getString("sjsu_id")));
                user.setSjsuEmail(resultSet.getString("sjsu_email"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
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
        String selectQuery = "SELECT * FROM user WHERE username = ?";
        User user = null;

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setSjsuId(Integer.parseInt(resultSet.getString("sjsu_id")));
                    user.setSjsuEmail(resultSet.getString("sjsu_email"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception later
        }
        return user;
    }


    public List<User> getAllUsers() {
        Connection connection = dbConnection.getConnection();
        String selectQuery = "SELECT * FROM user";
        List<User> userList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setSjsuId(Integer.parseInt(resultSet.getString("sjsu_id")));
                user.setSjsuEmail(resultSet.getString("sjsu_email"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));

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
