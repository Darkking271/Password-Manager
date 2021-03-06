package Main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**Created by Bohdan Yevdokymov
 *
 * Controller class for login widow, manages logging in as
 * well as registering a new user.
 */
public class LoginController {

    @FXML private PasswordField passField;
    @FXML private Label invalidLabel1;
    @FXML private Label invalidLabel2;
    @FXML private Button signInBtn;
    @FXML private TextField userNameField;

    //New account window
    @FXML private TextField createName;
    @FXML private PasswordField createPass;
    @FXML private PasswordField repeatPass;
    @FXML private Button createBtn;
    @FXML private Label passShortLabel;
    @FXML private Label passDontMatchLabel;
    @FXML private Label userNameTooShort;
    //New account window


    private Stage newFileWindow;
    private Scene createPanel;
    private Stage MainWindow;

    /**
     * If this window is initialized from main window -
     * will autofill last used username
     */
    @FXML
    public void initialize(){
        if(Main.fileManager != null) {
            String name = Main.fileManager.getUsername();
            userNameField.setText(name);
            userNameField.setFocusTraversable(false);
            passField.requestFocus();


        }

    }

    /**
     * When "Log In" button is pressed - checks the password,
     * if it was correct initializes main window, if not highlights that
     * either username or password is incorrect
     */
    @FXML
    private void LogInPressed(){

        if( Main.fileManager.tryOpen(userNameField.getText(), toByte(passField.getText()))){
            //Close Login Window
            Stage stage = (Stage) signInBtn.getScene().getWindow();
            stage.close();
            //Main.fileManager.populateTable();

            //Open Main Window
            this.MainWindow = new Stage();
            try {
                AnchorPane pane = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
                Scene mainScene = new Scene(pane);
                MainWindow.setTitle("Password Manager");
                MainWindow.setScene(mainScene);
                //Save on-Close
                MainWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        // Main.fileManager.save();
                        System.exit(0);
                    }
                });
                MainWindow.show();
            } catch(IOException e){
                e.printStackTrace();
            }

        }else{ // file doesn't exist or password is incorrect
            userNameField.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            passField.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            invalidLabel1.setVisible(true);
            invalidLabel2.setVisible(true);
            passField.requestFocus();
            passField.selectAll();
        }

    }


    /**
     * Initializes window with a form to register new user
     */
    @FXML
    private void registerPressed(){
        this.newFileWindow = new Stage();
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("CreateDB.fxml"));
            createPanel = new Scene(pane);

            newFileWindow.setTitle("Register an account");
            newFileWindow.initModality(Modality.APPLICATION_MODAL);
            newFileWindow.setScene(createPanel);
            newFileWindow.showAndWait();

        } catch (Exception e) {System.err.println(e);
            e.printStackTrace();}


    }

    /**
     * Handles button from "Register new user" window
     * Checks if username is at least 5 char long, password is
     * at least 6 chars long, then checks if username is available
     * if everything passes - registers new user on a server.
     */
    @FXML
    private void createBtnPressed(){
        //Resetting all warnings
        userNameTooShort.setText("username at least 5 characters long");
        passShortLabel.setVisible(false);
        passShortLabel.setBorder(null);
        passDontMatchLabel.setVisible(false);
        passDontMatchLabel.setBorder(null);
        userNameTooShort.setVisible(false);
        userNameTooShort.setBorder(null);

        createName.setBorder(null);
        createPass.setBorder(null);
        repeatPass.setBorder(null);
        createPass.setBorder(null);


        if(createName.getText().length() < 5){
            userNameTooShort.setVisible(true);
            //userNameTooShort.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            createName.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        if(createPass.getText().length() < 5){
            createPass.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            passShortLabel.setVisible(true);
            //passShortLabel.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        if(!createPass.getText().equals(repeatPass.getText())){
            passDontMatchLabel.setVisible(true);
            //passDontMatchLabel.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            createPass.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            repeatPass.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));


        }
        if(createName.getText().length() >= 5 && createPass.getText().length() >= 5 && createPass.getText().equals(repeatPass.getText())) {
            Connection conn = Connection.getInstance();
            if(conn.isUserAvailable(createName.getText())) {
                Main.fileManager.registerUser(createName.getText(), toByte(createPass.getText()));
                Stage stage = (Stage) createBtn.getScene().getWindow();
                stage.close();
            } else {
                userNameTooShort.setText("username is already taken...");
                createName.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                userNameTooShort.setVisible(true);

            }
        }

    }


    /**
     * Enables "Sign In" button only when username and at least 6 char password are entered
     */
    @FXML
    private void setSignInBtn(){
        if(passField.getText().length() > 5 && userNameField.getText().length() > 4){
            signInBtn.setDisable(false);
        } else {
            signInBtn.setDisable(true);
        }
    }

    /**
     * Converts a String to byte array.
     * @param s String to convert
     * @return byte array
     */
    private byte[] toByte(String s){
        byte[] ar;
        ar = s.getBytes();
        return ar;
    }

}