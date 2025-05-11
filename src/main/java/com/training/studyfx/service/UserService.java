package com.training.studyfx.service;
import com.training.studyfx.model.Message;
import com.training.studyfx.model.User;
import java.io.*;
import java.sql.*;

public class UserService {
    private static UserService instance;
    private User currentUser;
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:studyfx.db";
    private final String SQL_SCRIPT_PATH = "/database/db.sql";

    private UserService() {
        initializeDatabase();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);

            // Execute SQL script from file
            executeSqlScript();

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void executeSqlScript() {
        try {
            // Read SQL file from resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database/db.sql");
            if (inputStream == null) {
                //System.err.println("Could not find SQL script: database/db.sql");
                // create tables directly
                createTablesDirectly();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments
                if (!line.trim().startsWith("--")) {
                    sqlScript.append(line).append(" ");
                }
            }

            // Split by semicolon to execute each statement separately
            String[] statements = sqlScript.toString().split(";");

            Statement stmt = connection.createStatement();
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
            stmt.close();

        } catch (IOException | SQLException e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
            e.printStackTrace();
            // Fall back to creating tables directly
            createTablesDirectly();
        }
    }

    private void createTablesDirectly() {
        try {
            Statement stmt = connection.createStatement();

            // Users table with full_name field
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "email TEXT UNIQUE, " +
                    "full_name TEXT, " +
                    "status TEXT DEFAULT 'Available', " +
                    "profile_image_path TEXT DEFAULT '/images/default_profile.png', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Messages table
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT NOT NULL, " +
                    "sender TEXT NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "is_from_bot INTEGER NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username)" +
                    ")");

            // Insert default user if it doesn't exist
            stmt.execute("INSERT OR IGNORE INTO users (username, password, email, full_name, status) " +
                    "VALUES ('bach', 'bach123', 'bach@gmail.com', 'NT_Bach', 'Online')");

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creating tables directly: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean register(String username, String password, String email) {
        try {
            // Check if username already exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Username already exists
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();

            // Insert new user
            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.executeUpdate();
            insertStmt.close();

            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create user object from database data
                currentUser = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                currentUser.setFullName(rs.getString("full_name"));
                currentUser.setStatus(rs.getString("status"));
                currentUser.setProfileImagePath(rs.getString("profile_image_path"));

                // Load user messages
                loadUserMessages();

                rs.close();
                stmt.close();
                return true;
            }

            rs.close();
            stmt.close();
            return false;
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void loadUserMessages() {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT * FROM messages WHERE username = ? ORDER BY timestamp");
                stmt.setString(1, currentUser.getUsername());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Message message = new Message(
                            rs.getString("content"),
                            rs.getString("sender"),
                            rs.getInt("is_from_bot") == 1
                    );
                    currentUser.addMessage(message);
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error loading messages: " + e.getMessage());
                e.printStackTrace();
            }
        }

    public void updateUserProfile(String status, String profileImagePath) {
        if (currentUser == null) return;

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET status = ?, profile_image_path = ? WHERE username = ?");
            stmt.setString(1, status);
            stmt.setString(2, profileImagePath);
            stmt.setString(3, currentUser.getUsername());

            stmt.executeUpdate();
            stmt.close();

            // Update current user object
            currentUser.setStatus(status);
            currentUser.setProfileImagePath(profileImagePath);
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateProfileImage(String imagePath) {
        if (currentUser == null) return false;

        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET profile_image_path = ? WHERE username = ?");
            stmt.setString(1, imagePath);
            stmt.setString(2, currentUser.getUsername());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                currentUser.setProfileImagePath(imagePath);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error updating profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}