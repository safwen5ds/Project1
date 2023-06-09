package org.fsb.FlixFlow.Controllers;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import org.fsb.FlixFlow.Models.Episode;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class EpisodeAddController {

	@FXML
	private TableColumn<Episode, Date> dateDiffusionColumn;
	@FXML
	private DatePicker dateDiffusionField;
	private ObservableList<Episode> episodes;
	@FXML
	private TableView<Episode> episodeTable;
	@FXML
	private TableColumn<Episode, Integer> idEpisodeColumn;
	@FXML
	private TextField idEpisodeField;
	@FXML
	private TableColumn<Episode, Integer> idSaisonColumn;

	@FXML
	private TextField idSaisonField;
	@FXML
	private TableColumn<Episode, Integer> idSerieColumn;
	@FXML
	private TextField idSerieField;
	@FXML
	private TableColumn<Episode, Integer> numEpisodeColumn;
	@FXML
	private TextField numEpisodeField;
	@FXML
	private TableColumn<Episode, String> synopsisColumn;
	@FXML
	private TextField synopsisField;
	@FXML
	private TableColumn<Episode, String> urlEpisodeColumn;

	@FXML
	private TextField urlEpisodeField;

	@FXML
	private void addEpisode() {
		try {
			Episode newEpisode = new Episode();
			newEpisode.setId_episode(Integer.parseInt(idEpisodeField.getText()));
			newEpisode.setId_saison(Integer.parseInt(idSaisonField.getText()));
			newEpisode.setId_serie(Integer.parseInt(idSerieField.getText()));
			newEpisode.setNum_episode(Integer.parseInt(numEpisodeField.getText()));
			LocalDate localDate = dateDiffusionField.getValue();
			newEpisode.setDate_diffusion(Date.valueOf(localDate));
			newEpisode.setSynopsis(synopsisField.getText());
			newEpisode.setUrl_episode(urlEpisodeField.getText());

			DatabaseUtil.createEpisode(newEpisode);
			episodes.add(newEpisode);

		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Adding Epiosde ! ");
		} catch (NumberFormatException e) {
			showErrorDialog("Invalid input. Please check your input fields and try again !");
		}
		episodeTable.refresh();
	}

	@FXML
	private void deleteEpisode() {
		Episode selectedEpisode = episodeTable.getSelectionModel().getSelectedItem();
		if (selectedEpisode != null) {
			try {
				DatabaseUtil.deleteEpisode(selectedEpisode.getId_episode());
				episodes.remove(selectedEpisode);
			} catch (SQLException e) {
				e.printStackTrace();
				showErrorDialog("Error Deleting Epiosde ! ");
			}
		}
		episodeTable.refresh();
	}

	public void initialize() {
		idEpisodeColumn.setCellValueFactory(new PropertyValueFactory<>("id_episode"));
		idSaisonColumn.setCellValueFactory(new PropertyValueFactory<>("id_saison"));
		idSerieColumn.setCellValueFactory(new PropertyValueFactory<>("id_serie"));
		numEpisodeColumn.setCellValueFactory(new PropertyValueFactory<>("num_episode"));
		dateDiffusionColumn.setCellValueFactory(new PropertyValueFactory<>("date_diffusion"));
		synopsisColumn.setCellValueFactory(new PropertyValueFactory<>("synopsis"));
		urlEpisodeColumn.setCellValueFactory(new PropertyValueFactory<>("url_episode"));

		try {
			episodes = FXCollections.observableArrayList(DatabaseUtil.getAllEpisodes());
		} catch (SQLException e) {
			e.printStackTrace();
			episodes = FXCollections.observableArrayList();
		}

		episodeTable.setItems(episodes);
		episodeTable.refresh();
		episodeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				updateTextFields(newSelection);
			}
		});
		episodeTable.refresh();

	}

	private void showErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	private void updateEpisode() {
		Episode selectedEpisode = episodeTable.getSelectionModel().getSelectedItem();
		if (selectedEpisode != null) {
			try {
				selectedEpisode.setId_episode(Integer.parseInt(idEpisodeField.getText()));
				selectedEpisode.setId_saison(Integer.parseInt(idSaisonField.getText()));
				selectedEpisode.setId_serie(Integer.parseInt(idSerieField.getText()));
				selectedEpisode.setNum_episode(Integer.parseInt(numEpisodeField.getText()));
				LocalDate localDate = dateDiffusionField.getValue();
				selectedEpisode.setDate_diffusion(Date.valueOf(localDate));
				selectedEpisode.setSynopsis(synopsisField.getText());
				selectedEpisode.setUrl_episode(urlEpisodeField.getText());

				DatabaseUtil.updateEpisode(selectedEpisode);
			} catch (SQLException e) {
				e.printStackTrace();
				showErrorDialog("Error Updating Epiosde ! ");
			} catch (NumberFormatException e) {
				showErrorDialog("Invalid input. Please check your input fields and try again !");
			}
		}
		episodeTable.refresh();
	}

	private void updateTextFields(Episode episode) {
		idEpisodeField.setText(String.valueOf(episode.getId_episode()));
		idSaisonField.setText(String.valueOf(episode.getId_saison()));
		idSerieField.setText(String.valueOf(episode.getId_serie()));
		numEpisodeField.setText(String.valueOf(episode.getNum_episode()));
		dateDiffusionField.setValue(episode.getDate_diffusion().toLocalDate());
		synopsisField.setText(episode.getSynopsis());
		urlEpisodeField.setText(episode.getUrl_episode());
	}

}
