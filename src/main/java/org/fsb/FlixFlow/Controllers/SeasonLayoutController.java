package org.fsb.FlixFlow.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.fsb.FlixFlow.Models.Commentaire_saison;
import org.fsb.FlixFlow.Models.Saison;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SeasonLayoutController {

	private static void showAlert(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@FXML
	private Button addcommentbtn;
	@FXML
	private Button addfav;
	@FXML
	private Label average;
	Font bebasNeueFont = Font.loadFont(getClass().getResourceAsStream("/FXML/fonts/BebasNeue-Regular.ttf"), 20);

	@FXML
	private TextField commentinput;
	@FXML
	private Button delbtn;

	@FXML
	private Button hd;

	@FXML
	private Rectangle image;

	@FXML
	private ListView<Commentaire_saison> listcomment;

	@FXML
	private Button modifbtn;

	@FXML
	private Label nbreps;

	private int saisonId;

	@FXML
	private Slider saisonRatingSlider;

	@FXML
	private Label seasonDate_debutLabel;

	@FXML
	private Label seasonNumLabel;

	@FXML
	private Text seasonSynopsistext;

	@FXML
	private Label seasonViewsLabel;

	private int serieId;

	@FXML
	private Label serieNameLabel;

	@FXML
	private Button submitSaisonRatingButton;
	@FXML
	private WebView videoweb;

	@FXML
	private Label vote;

	@FXML
	private Button votebtn;
	@FXML
	private Button watchnow;

	@FXML
	private Button watchtrailer;

	private void addComment() {
		int userId = DatabaseUtil.readUserFromFile().getId_utilisateur();
		String content = commentinput.getText();

		try {
			DatabaseUtil.addCommentForSeason(userId, saisonId, content);
			updateCommentList();
			commentinput.clear();
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Adding Comment ! ");
		}
	}

	private void deleteComment() {
		Commentaire_saison selectedComment = listcomment.getSelectionModel().getSelectedItem();
		if (selectedComment != null) {
			try {
				DatabaseUtil.deleteCommentForSeason(selectedComment.getComment_id());
				updateCommentList();
			} catch (SQLException e) {
				e.printStackTrace();
				showErrorDialog("Error Deleting Comment ! ");
			}
		}
	}

	public void initData(Saison season) throws SQLException {
		if (season != null) {
			watchnow.setFont(bebasNeueFont);
			watchtrailer.setFont(bebasNeueFont);
			submitSaisonRatingButton.setFont(bebasNeueFont);
			hd.setFont(bebasNeueFont);
			setIds(season.getId_saison(), season.getId_serie());
			serieNameLabel.setText(season.getNom_serie());
			String numberColor = "black";
			String style = String.format(".slider .axis .axis-label { -fx-text-fill: %s; }", numberColor);
			saisonRatingSlider.setStyle(style);
			serieNameLabel.setFont(bebasNeueFont);
			seasonNumLabel.setText(" - Season: " + season.getNum_saison());
			seasonNumLabel.setFont(bebasNeueFont);
			seasonSynopsistext.setText(season.getSynopsis());
			seasonSynopsistext.setFont(bebasNeueFont);
			seasonDate_debutLabel.setText("Date: " + season.getDate_debut());
			seasonDate_debutLabel.setFont(bebasNeueFont);
			watchtrailer.setOnAction(event -> openUrlInNewWindow(season.getUrl_video()));
			if ("admin".equals(DatabaseUtil.readUserFromFile().getType())) {
				seasonViewsLabel.setFont(bebasNeueFont);
				seasonViewsLabel.setText("Views: " + season.getVues());
			}

			double averageScore = DatabaseUtil.calculateAverageSeasonScore(season.getId_saison());
			average.setText(String.format("%.2f", averageScore));
			average.setFont(bebasNeueFont);

			String imageUrl = season.getUrl_image();
			Image imageContent = new Image(imageUrl);
			ImagePattern pattern = new ImagePattern(imageContent);
			image.setFill(pattern);

		}
		updateCommentList();

		updateVoteCount();
		updateTotalEpisodes();
	}

	@FXML
	public void initialize() {
		listcomment.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Commentaire_saison item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getNom_User() + " : " + item.getContenu());
				}
			}
		});

		watchnow.setOnAction(event -> openEpisodeLayout());
		submitSaisonRatingButton.setOnAction(event -> submitSaisonRating());
		addcommentbtn.setOnAction(event -> addComment());
		delbtn.setOnAction(event -> deleteComment());
		modifbtn.setOnAction(event -> modifyComment());
		watchtrailer.setOnAction(event -> openUrlInNewWindow(null));
		updateCommentList();
	}

	private void modifyComment() {
		Commentaire_saison selectedComment = listcomment.getSelectionModel().getSelectedItem();
		if (selectedComment != null) {
			String newContent = commentinput.getText();
			try {
				DatabaseUtil.updateCommentForSeason(selectedComment.getComment_id(), newContent);
				updateCommentList();
				commentinput.clear();
			} catch (SQLException e) {
				e.printStackTrace();
				showErrorDialog("Error Modifying Comment ! ");
			}
		}
	}

	private void openEpisodeLayout() {
		if ("admin".equals(DatabaseUtil.readUserFromFile().getType())) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/episode.fxml"));
				Parent root = loader.load();
				EpisodeLayoutController controller = loader.getController();
				controller.initData(saisonId, serieId);

				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setWidth(1270);
				stage.setHeight(720);
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
				showErrorDialog("Error Openning Episodes ! ");
			}
		} else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/episode_Others.fxml"));
				Parent root = loader.load();
				EpisodeLayoutController controller = loader.getController();
				controller.initData(saisonId, serieId);

				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setWidth(1270);
				stage.setHeight(720);
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
				showErrorDialog("Error Openning Episodes ! ");
			}
		}

	}

	private void openUrlInNewWindow(String url) {
		Stage newWindow = new Stage();
		newWindow.initModality(Modality.APPLICATION_MODAL);
		final WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();
		webEngine.load(url);
		newWindow.setOnHidden(e -> webView.getEngine().load(null));
		newWindow.setScene(new Scene(new StackPane(webView), 800, 600));
		newWindow.show();
	}

	public void setIds(int saisonId, int serieId) {
		this.saisonId = saisonId;
		this.serieId = serieId;
	}

	private void showErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void submitSaisonRating() {
		int userId = DatabaseUtil.readUserFromFile().getId_utilisateur();
		int rating = (int) Math.round(saisonRatingSlider.getValue());
		boolean updated = DatabaseUtil.submitSaisonRating(userId, saisonId, rating);
		if (updated) {
			showAlert("Rating Updated", "Your rating for this season has been updated.");
		}
		updateAverageScore();
	}

	private void updateAverageScore() {
		try {
			double averageScore = DatabaseUtil.calculateAverageSeasonScore(saisonId);
			average.setText(String.format("%.2f", averageScore));
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Updating Average ! ");
		}
	}

	private void updateCommentList() {
		try {
			List<Commentaire_saison> comments = DatabaseUtil.getCommentaireSaisonsByMediaId(saisonId);
			listcomment.getItems().setAll(comments);
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Updating Comment ! ");
		}
	}

	private void updateTotalEpisodes() {

		try {
			int totalEpisodes = DatabaseUtil.getTotalEpisodesForSeason(saisonId);
			if ("admin".equals(DatabaseUtil.readUserFromFile().getType())) {
				nbreps.setFont(bebasNeueFont);
				nbreps.setText("Nbr.Eps : " + String.valueOf(totalEpisodes));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Updating Total Episode ! ");

		}

	}

	private void updateVoteCount() {

		try {
			int voteCount = DatabaseUtil.getVoteCountForSeason(saisonId);
			if ("admin".equals(DatabaseUtil.readUserFromFile().getType())) {
				vote.setFont(bebasNeueFont);
				vote.setText(" / " + String.valueOf(voteCount) + " Voted");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorDialog("Error Updating Votes ! ");
		}

	}

}
