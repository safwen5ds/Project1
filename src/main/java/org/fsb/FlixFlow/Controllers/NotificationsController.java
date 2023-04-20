package org.fsb.FlixFlow.Controllers;

import org.fsb.FlixFlow.Models.Notification;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML
    private ListView<String> notificationsList;

    private ObservableList<String> notifications;

    public NotificationsController() {
        notifications = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        // Add notifications to the ListView
        notificationsList.setItems(notifications);
    }

    public void addNotification(Notification notification) {
        String notificationText = String.format("New episode released: %s, S%dE%d", notification.getSerieNom(), notification.getIdSerie(), notification.getNumEpisode());
        notifications.add(notificationText);
    }
}
