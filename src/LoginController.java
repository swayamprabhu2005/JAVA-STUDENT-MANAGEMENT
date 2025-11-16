import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validation check
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Both fields are required!");
            return;
        }

        // Check credentials from DB
        boolean isValid = DataStore.validateLogin(username, password);

        if (isValid) {
            openDashboard();
        } else {
            showAlert("Login Failed", "Invalid username or password!");
        }
    }

    private void openDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            // Creates a new window
            Stage stage = new Stage();
            stage.setTitle("Student Dashboard");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Close login window
            usernameField.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
