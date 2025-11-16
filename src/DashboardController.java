import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList; //auto-updates the TableView
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Number> idCol;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> courseCol;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField courseField;
    @FXML private TextField searchField;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredList;

    @FXML
    public void initialize() {

        idCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()));
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        courseCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourse()));

        studentList.addAll(DataStore.loadStudents());
               // for searching
        filteredList = new FilteredList<>(studentList, p -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredList.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String keyword = newValue.toLowerCase();

                return student.getName().toLowerCase().contains(keyword)
                        || student.getEmail().toLowerCase().contains(keyword)
                        || student.getCourse().toLowerCase().contains(keyword);
            });
        });

        SortedList<Student> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedList);

        studentTable.setOnMouseClicked(this::fillInputsFromSelection);
    }

    @FXML
    public void handleAddStudent() {
        String name = nameField.getText();
        String email = emailField.getText();
        String course = courseField.getText();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            showAlert("Error", "All fields required");
            return;
        }

        Student s = new Student(name, email, course);
        DataStore.insertStudent(s);
        studentList.add(s);

        clearInputs();
    }

    @FXML
    public void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Select a row to delete.");
            return;
        }

        DataStore.deleteStudent(selected.getId());
        studentList.remove(selected);
    }

    @FXML
    public void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Select a row to update.");
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String course = courseField.getText();

        DataStore.updateStudent(selected.getId(), name, email, course);

        selected.setName(name);
        selected.setEmail(email);
        selected.setCourse(course);

        studentTable.refresh();
    }
    @FXML 
    public void handleLogout() {
        try {
            // Load login page again
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, 400, 300));
            loginStage.show();
            // Close the dashboard window
            studentTable.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fillInputsFromSelection(MouseEvent e) {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s == null) return;

        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        courseField.setText(s.getCourse());
    }

    private void clearInputs() {
        nameField.clear();
        emailField.clear();
        courseField.clear();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.show();
    }
}
