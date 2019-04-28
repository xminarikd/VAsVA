package controller;

import entity.Customer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminView {

    private static final Logger LOG = Logger.getLogger(AdminView.class.getName());

    public Label lastNameLabel;
    public Label firstNameLabel;
    public Label login;
    public Label windowTitleLabel;
    public Label you;
    public Button manageFilmsButton;
    public Button manageBookingsButton;
    public Button logOutButton;
    public ComboBox language;

    Customer c;
    private Stage stage;
    private String lan;

    public void setStage(Stage stage) {

        login.setText(c.getUsername());
        lastNameLabel.setText(c.getLastName());
        firstNameLabel.setText(c.getFirstName());
        language.setItems(FXCollections.observableArrayList("en","sk","sw"));
        language.getSelectionModel().select(lan);
        this.stage = stage;
    }

    public void changeLanguage() {
        lan = language.getSelectionModel().getSelectedItem().toString();
        setLanguage(lan);
    }

        public void setLanguage(String lan){
        this.lan = lan;
        ResourceBundle rb =	ResourceBundle.getBundle("Label", Locale.forLanguageTag(lan));
        windowTitleLabel.setText(rb.getString("adminViewLabel"));
        you.setText(rb.getString("loginInfo"));
        manageFilmsButton.setText(rb.getString("addFilm"));
        manageBookingsButton.setText(rb.getString("addScreening"));
        logOutButton.setText(rb.getString("logOutButton"));
    }

    public void setUser(Customer c) {
        this.c = c;
    }

    public void sendEmailClick(ActionEvent actionEvent) {
    }

    public void editInfoClick(ActionEvent actionEvent) {
    }

    public void manageMoviesClick(ActionEvent actionEvent) {
        SceneCreator sc = new SceneCreator();
        try {
            sc.launchManageFilmScene(c,lan);
            ((javafx.scene.Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"cannot open back scene",e);
        }
    }

    public void manageBookingsClick(ActionEvent actionEvent) {
        SceneCreator sc = new SceneCreator();
        try {
            sc.launchSceneMovies(c,lan);
            ((javafx.scene.Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"cannot open booking scene",e);
        }
    }

    public void logOutClick(ActionEvent actionEvent) {
        SceneCreator sc = new SceneCreator();
        try {
            sc.launchLogInScene(lan);
            ((javafx.scene.Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"cannot open back scene",e);
        }
    }
}
