package org.example.proto;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField PasswordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    // Database connection details
    private final String DB_URL = "jdbc:mysql://localhost:3306/women_shop";
    private final String DB_USERNAME = "root";  // Use your MySQL root username
    private final String DB_PASSWORD = "Ordinateur1#";  // Use your MySQL password

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> {
            String username = usernameTextField.getText();
            String password = PasswordTextField.getText();

            if (validateLogin(username, password)) {
                switchToDashboard(event);
            } else {
                errorLabel.setVisible(true);
            }
        });

        cancelButton.setOnAction(event -> {
            usernameTextField.clear();
            PasswordTextField.clear();
            errorLabel.setVisible(false);
        });
    }

    // Method to validate login credentials from MySQL database
    private boolean validateLogin(String username, String password) {
        boolean isValid = false;

        try {
            // 1. Connect to MySQL database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // 2. Prepare SQL query to check the username and password
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // 3. Execute query and check if any result is returned
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                isValid = true; // Valid credentials
            }

            // 4. Close connection
            preparedStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    // Method to switch scene after successful login
    private void switchToDashboard(ActionEvent event) {
        try {
            // Load the dashboard FXML
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/org/example/proto/hello-view.fxml"));

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene with the dashboard view
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}