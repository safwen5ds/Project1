package org.fsb.FlixFlow.Controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fsb.FlixFlow.Models.Acteur;
import org.fsb.FlixFlow.Models.Serie;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;
import org.fsb.FlixFlow.Views.PageNavigationUtil;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SearchSeriesController {
	@FXML
	private TextField actorSearchTextField;
	@FXML
	private TextField countryOfOriginSearchTextField;

	@FXML
	private TextField genreSearchTextField;

	@FXML
	private TextField languageSearchTextField;

	private ObservableList<Serie> masterData;

	Font Montessart = Font.loadFont(getClass().getResourceAsStream("/FXML/fonts/BebasNeue-Regular.ttf"), 20);

	@FXML
	private TextField producerSearchTextField;

	@FXML
	private TextField releaseYearSearchTextField;

	private Map<Integer, List<Acteur>> seriesActorsMap;

	@FXML
	private FlowPane seriesFlowPane;
	private HashMap<Integer, Serie> seriesMap = new HashMap<>();

	@FXML
	private TextField serieTitleSearchTextField;

	private final UserDashboardController userDashboardController;

	public SearchSeriesController(UserDashboardController userDashboardController) {
		this.userDashboardController = userDashboardController;
	}

	@FXML
	public void initialize() {
		loadAllSeries();
		setupSearchFieldListeners();
	}

	private void loadAllSeries() {
		try {
			List<Serie> series = DatabaseUtil.getSeriesSortedByViews();
			masterData = FXCollections.observableArrayList();
			seriesActorsMap = new HashMap<>();
			seriesMap = new HashMap<>();
			for (Serie serie1 : series) {
				Serie serie = DatabaseUtil.getSerieById(serie1.getId_serie());
				if (serie != null) {
					masterData.add(serie);
					seriesMap.put(serie.getId_serie(), serie);
					List<Acteur> actors = DatabaseUtil.getActorsBySerieId(serie.getId_serie());
					seriesActorsMap.put(serie.getId_serie(), actors);
				}
			}
			updateSeriesFlowPane(masterData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadSerie(List<Serie> series, FlowPane seriesFlowPane) {
		for (Serie serie : series) {
			ImageView poster = new ImageView(new Image(serie.getUrl_image()));
			poster.setFitHeight(250);
			poster.setFitWidth(200);

			Label title = new Label(serie.getNom());
			title.setWrapText(true);
			title.setMaxWidth(150);
			title.setAlignment(Pos.CENTER);
			title.setFont(Montessart);
			VBox mediaContainer = new VBox(5);
			mediaContainer.getChildren().addAll(poster, title);
			mediaContainer.setAlignment(Pos.CENTER);
			mediaContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));
			mediaContainer.setOnMouseClicked(event -> {
				PageNavigationUtil.openMovieSeriesDetails(serie.getId_serie(), true, userDashboardController);
			});

			seriesFlowPane.getChildren().add(mediaContainer);
		}
	}

	private void searchSeries() {
		String serieTitleFilter = serieTitleSearchTextField.getText().toLowerCase();
		String releaseYearFilter = releaseYearSearchTextField.getText().toLowerCase();
		String genreFilter = genreSearchTextField.getText().toLowerCase();
		String languageFilter = languageSearchTextField.getText().toLowerCase();
		String countryOfOriginFilter = countryOfOriginSearchTextField.getText().toLowerCase();
		String producerFilter = producerSearchTextField.getText().toLowerCase();
		String actorFilter = actorSearchTextField.getText().toLowerCase();

		List<Serie> filteredData = masterData.stream()
				.filter(serie -> serieTitleFilter.isEmpty() || serie.getNom().toLowerCase().contains(serieTitleFilter))
				.filter(serie -> releaseYearFilter.isEmpty()
						|| String.valueOf(serie.getAnnee_sortie()).contains(releaseYearFilter))
				.filter(serie -> genreFilter.isEmpty()
						|| (serie.getNom_genre() != null && serie.getNom_genre().toLowerCase().contains(genreFilter)))
				.filter(serie -> languageFilter.isEmpty()
						|| serie.getNom_langue().toLowerCase().contains(languageFilter))
				.filter(serie -> countryOfOriginFilter.isEmpty()
						|| serie.getNom_pays().toLowerCase().contains(countryOfOriginFilter))
				.filter(serie -> producerFilter.isEmpty()
						|| serie.getNom_producteur().toLowerCase().contains(producerFilter))
				.filter(serie -> {
					if (actorFilter.isEmpty()) {
						return true;
					}
					List<Acteur> actors = seriesActorsMap.get(serie.getId_serie());
					return actors.stream().anyMatch(actor -> actor.getNom().toLowerCase().contains(actorFilter));
				}).collect(Collectors.toList());
		for (Serie f : filteredData) {
			System.out.println(f.toString());
		}

		updateSeriesFlowPane(FXCollections.observableArrayList(filteredData));
	}

	private void setDelayedSearchListener(TextField textField) {
		PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
		textField.setOnKeyReleased(event -> {
			pause.setOnFinished(e -> searchSeries());
			pause.playFromStart();
		});
	}

	private void setupSearchFieldListeners() {
		setDelayedSearchListener(serieTitleSearchTextField);
		setDelayedSearchListener(releaseYearSearchTextField);
		setDelayedSearchListener(genreSearchTextField);
		setDelayedSearchListener(languageSearchTextField);
		setDelayedSearchListener(countryOfOriginSearchTextField);
		setDelayedSearchListener(producerSearchTextField);
		setDelayedSearchListener(actorSearchTextField);
	}

	private void updateSeriesFlowPane(ObservableList<Serie> series) {
		seriesFlowPane.getChildren().clear();
		loadSerie(series, seriesFlowPane);
	}
}