package org.fsb.FlixFlow.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.fsb.FlixFlow.Models.Film;
import org.fsb.FlixFlow.Models.Serie;
import org.fsb.FlixFlow.Utilities.DatabaseUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class HomePageController {
	private UserDashboardController userDashboardController;

	public HomePageController(UserDashboardController userDashboardController) {
		this.userDashboardController = userDashboardController;
	}

	@FXML
	Label lab1;
	@FXML
	Label lab2;
	Font bebasNeueFont = Font.loadFont(getClass().getResourceAsStream("/FXML/fonts/BebasNeue-Regular.ttf"), 50);

	Font Montessart = Font.loadFont(getClass().getResourceAsStream("/FXML/fonts/BebasNeue-Regular.ttf"), 20);

	@FXML
	private HBox trendingPane;

	@FXML
	private FlowPane sortedByViewsPane;
	@FXML
	private Image gifImage;

	@FXML
	public void initialize() {
		loadTrendingMovies();
		loadSortedByViewsMoviesAndSeries();
		lab1.setFont(bebasNeueFont);
		lab2.setFont(bebasNeueFont);

	}

	private void loadFilm(List<Film> films, HBox trendingPane2) {
		for (Film film : films) {
			ImageView poster = new ImageView(new Image(film.getUrl_image()));
			poster.setFitHeight(250);
			poster.setFitWidth(200);
			Label title = new Label(film.getNom());
			title.setWrapText(true);
			title.setMaxWidth(150);
			title.setAlignment(Pos.CENTER);
			title.setFont(Montessart);
			VBox mediaContainer = new VBox(5);
			mediaContainer.getChildren().addAll(poster, title);
			mediaContainer.setAlignment(Pos.CENTER);
			mediaContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));
			mediaContainer.setOnMouseClicked(event -> {
				openMovieSeriesDetails(film.getId_film(), true, userDashboardController);
			});

			trendingPane2.getChildren().add(mediaContainer);
		}
	}

	private void loadSerie(List<Serie> series, HBox sortedByViewsPane2) {
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
				openMovieSeriesDetails(serie.getId_serie(), false, userDashboardController);
			});

			sortedByViewsPane2.getChildren().add(mediaContainer);
		}
	}

	private void loadTrendingMovies() {
		try {
			List<Film> movies = DatabaseUtil.getTrendingMovies();
			loadFilm(movies, trendingPane);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadSortedByViewsMoviesAndSeries() {
		try {
			List<Film> movies = DatabaseUtil.getMoviesSortedByViews();
			List<Serie> series = DatabaseUtil.getSeriesSortedByViews();

			loadFilm(movies, sortedByViewsPane);
			loadSerie(series, sortedByViewsPane);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadSerie(List<Serie> series, FlowPane sortedByViewsPane2) {
		// TODO Auto-generated method stub
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
				openMovieSeriesDetails(serie.getId_serie(), false, userDashboardController);
			});

			sortedByViewsPane2.getChildren().add(mediaContainer);
		}
	}

	private void loadFilm(List<Film> movies, FlowPane sortedByViewsPane2) {
		// TODO Auto-generated method stub
		for (Film film : movies) {
			ImageView poster = new ImageView(new Image(film.getUrl_image()));
			poster.setFitHeight(250);
			poster.setFitWidth(200);

			Label title = new Label(film.getNom());
			title.setWrapText(true);
			title.setMaxWidth(150);
			title.setAlignment(Pos.CENTER);
			title.setFont(Montessart);
			VBox mediaContainer = new VBox(5);
			mediaContainer.getChildren().addAll(poster, title);
			mediaContainer.setAlignment(Pos.CENTER);
			mediaContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));
			mediaContainer.setOnMouseClicked(event -> {
				openMovieSeriesDetails(film.getId_film(), true, userDashboardController);
			});

			sortedByViewsPane2.getChildren().add(mediaContainer);
		}
	}

	private void openMovieSeriesDetails(int id, boolean isMovie, UserDashboardController userDashboardController) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MovieSeriesDetails.fxml"));
			loader.setControllerFactory(
					param -> new MovieSeriesDetailsController(id, isMovie, userDashboardController));
			Parent detailsPage = loader.load();

			Platform.runLater(() -> {
				userDashboardController.getContentPane().getChildren().setAll(detailsPage);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
