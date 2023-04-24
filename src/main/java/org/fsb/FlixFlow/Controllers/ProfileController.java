package org.fsb.FlixFlow.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fsb.FlixFlow.Models.Utilisateur;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;

import java.io.IOException;
import java.time.ZoneId;

public class ProfileController {

	@FXML
	private TextField SurNameField;
	
	@FXML
	private Button Disconnect;
	

	@FXML
	private DatePicker date;

	@FXML
	private Button deleteButton;

	@FXML
	private TextField emailField;

	@FXML
	private TextField nameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button updateButton;
	private Utilisateur user;

	private UserDashboardController userDashboardController;

    public ProfileController(UserDashboardController userDashboardController) {
        this.userDashboardController = userDashboardController;
    }

	@FXML
	private void initialize() {
		loadUserInfo();

		updateButton.setOnAction(e -> updateUserInfo());
		deleteButton.setOnAction(e -> deleteUserProfile());
	}

	private void loadUserInfo() {
		user = DatabaseUtil.readUserFromFile();
		if (user != null) {
			nameField.setText(user.getPrenom());
			SurNameField.setText(user.getNom());
			emailField.setText(user.getEmail());
			java.util.Date utilDate = user.getDate_de_naissance();
			java.time.LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			date.setValue(localDate);
			passwordField.setText(user.getMot_de_passe());
		}
	}

	private void updateUserInfo() {
		if (user != null) {
			user.setPrenom(nameField.getText());
			user.setEmail(emailField.getText());
			user.setNom(SurNameField.getText());
			user.setMot_de_passe(passwordField.getText());
			java.time.LocalDate newLocalDate = date.getValue();
			java.util.Date newUtilDate = java.util.Date
					.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			user.setDate_de_naissance(newUtilDate);
			boolean isUpdated = DatabaseUtil.updateUserInDatabase(user);
			if (isUpdated) {
				DatabaseUtil.storeUserToFile(user);
			}
		}
	}

	private void deleteUserProfile() {
		if (user != null) {
			boolean isDeleted = DatabaseUtil.deleteUserFromDatabase(user.getId_utilisateur());
			if (isDeleted) {
				DatabaseUtil.deleteUserFromFile();
				Stage stage = (Stage) deleteButton.getScene().getWindow();
				stage.close();
			}
		}
	}
	
	public void Disconnect() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
	        Parent loginRoot = loader.load();

	        Scene loginScene = new Scene(loginRoot);

	        Stage profileStage = (Stage) Disconnect.getScene().getWindow();

	        if (userDashboardController != null && UserDashboardController.dashboardStage != null) {
	            UserDashboardController.dashboardStage.close();
	        }

	        Stage loginStage = new Stage();
	        loginStage.setScene(loginScene);
	        loginStage.setTitle("Login");
	        loginStage.show();

	        profileStage.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}






}
