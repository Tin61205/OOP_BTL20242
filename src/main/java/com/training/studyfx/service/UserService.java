package com.training.studyfx.service;

import com.training.studyfx.model.Message;
import com.training.studyfx.model.User;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
        private static UserService instance;
        private User currentUser;
        private Connection connection;
        private final String DB_URL = "jdbc:sqlite:studyfx.db";

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

                // Create tables if they don't exist
                Statement stmt = connection.createStatement();

                // Users table
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "password TEXT NOT NULL, " +
                        "email TEXT NOT NULL, " +
                        "status TEXT DEFAULT 'Available', " +
                        "profile_image_path TEXT DEFAULT '/images/default-profile.png'" +
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

                stmt.close();
            } catch (SQLException e) {
                System.err.println("Database initialization error: " + e.getMessage());
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

        public void saveMessage(Message message) {
            if (currentUser == null) return;

            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO messages (content, sender, timestamp, is_from_bot, username) " +
                                "VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, message.getContent());
                stmt.setString(2, message.getSender());
                stmt.setString(3, message.getTimestamp().toString());
                stmt.setInt(4, message.isFromBot() ? 1 : 0);
                stmt.setString(5, currentUser.getUsername());

                stmt.executeUpdate();
                stmt.close();

                // Add to current user's messages list
                currentUser.addMessage(message);
            } catch (SQLException e) {
                System.err.println("Error saving message: " + e.getMessage());
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