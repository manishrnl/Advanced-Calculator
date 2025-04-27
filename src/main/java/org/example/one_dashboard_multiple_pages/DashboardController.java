package org.example.one_dashboard_multiple_pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private FlowPane flowPane;
    @FXML
    private ImageView myImageView;

    private Stage stage;

    // Method to set stage from Main class
    public void setStage(Stage stage) {
        this.stage = stage;
        try {
            showStandardPage(); // Load initial page after stage is set
        } catch (IOException e) {
            System.err.println("Error loading initial page: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applyRoundedCorners(myImageView, 20);
        // Removed sceneProperty listener since we're setting stage explicitly
    }

    private void loadPage(String page, String title) throws IOException {
        try {
            // Ensure the path matches your project structure
            URL fxmlLocation = getClass().getResource("/org/example/one_dashboard_multiple_pages/" + page + ".fxml");
            if (fxmlLocation == null) {
                throw new IOException("Cannot find FXML file: " + page + ".fxml");
            }
            Parent pageContent = FXMLLoader.load(fxmlLocation);

            if (stage != null) {
                stage.setTitle(title);
                flowPane.getChildren().setAll(pageContent);
            } else {
                throw new IllegalStateException("Stage is not initialized");
            }
        } catch (Exception e) {
            throw new IOException("Failed to load FXML file: " + page + ".fxml", e);
        }
    }

    public void showStandardPage() throws IOException {
        loadPage("Standard", "Calculator - Standard Mode");
    }

    public void showScientificPage() throws IOException {
        loadPage("Scientific", "Calculator - Scientific Mode");
    }

    public void showUnitConversionPage() throws IOException {
        loadPage("UnitConversion", "Unit Converter");
    }

    public void showCurrencyConversionPage() throws IOException {
        loadPage("Currency", "Currency Converter");
    }

    private void applyRoundedCorners(ImageView imageView, double radius) {
        if (imageView != null) {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(radius * 2);
            clip.setArcHeight(radius * 2);
            clip.widthProperty().bind(imageView.fitWidthProperty());
            clip.heightProperty().bind(imageView.fitHeightProperty());
            imageView.setClip(clip);
        }
    }
}