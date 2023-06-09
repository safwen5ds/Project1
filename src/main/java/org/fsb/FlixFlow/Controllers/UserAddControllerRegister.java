package org.fsb.FlixFlow.Controllers;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.fsb.FlixFlow.Models.Utilisateur;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserAddControllerRegister {
	@FXML
	private DatePicker birthDatePicker;

	@FXML
	private TextField emailTextField;

	@FXML
	private TextField firstNameTextField;

	@FXML
	private Button inputjoin;

	@FXML
	private TextField lastNameTextField;

	@FXML
	private PasswordField passwordTextField;

	@FXML
	private void addUtilisateur() {
		if (validateInput()) {
			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setNom(firstNameTextField.getText());
			utilisateur.setPrenom(lastNameTextField.getText());
			utilisateur.setEmail(emailTextField.getText());
			utilisateur.setMot_de_passe(passwordTextField.getText());
			utilisateur.setDate_de_naissance(Date.valueOf(birthDatePicker.getValue()));
			utilisateur.setType("Normal User");

			try {
				DatabaseUtil.addUtilisateur(utilisateur);
				clearInputFields();
				DatabaseUtil.storeUserToFile(utilisateur);
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/dash_User.fxml"));
					Parent secondaryRoot = loader.load();
					Scene secondaryScene = new Scene(secondaryRoot);
					secondaryScene.getStylesheets()
							.add(getClass().getResource("/FXML/Styles/discord_theme.css").toExternalForm());
					Stage primaryStage = (Stage) inputjoin.getScene().getWindow();
					primaryStage.setScene(secondaryScene);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void clearInputFields() {
		firstNameTextField.clear();
		lastNameTextField.clear();
		emailTextField.clear();
		passwordTextField.clear();
		birthDatePicker.setValue(null);
	}

	@FXML
	public void initialize() {

		inputjoin.setOnAction(event -> addUtilisateur());

	}

	private void showErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private boolean validateInput() {
		String errorMessage = "";

		if (firstNameTextField.getText() == null || firstNameTextField.getText().isEmpty()) {
			errorMessage += "No valid first name!\n";
		}
		if (lastNameTextField.getText() == null || lastNameTextField.getText().isEmpty()) {
			errorMessage += "No valid last name!\n";
		}
		if (emailTextField.getText() == null || emailTextField.getText().isEmpty()) {
			errorMessage += "No valid email!\n";
		} else {
			if (!Pattern.matches("^(.+)@(.+)$", emailTextField.getText())) {
				errorMessage += "Invalid email format!\n";
			}
		}
		if (passwordTextField.getText() == null || passwordTextField.getText().isEmpty()) {
			errorMessage += "No valid password!\n";
		}
		if (birthDatePicker.getValue() == null) {
			errorMessage += "No valid birth date!\n";
		}

		if (errorMessage.isEmpty()) {
			return true;
		} else {
			showErrorDialog(errorMessage);
			return false;
		}
	}

}
