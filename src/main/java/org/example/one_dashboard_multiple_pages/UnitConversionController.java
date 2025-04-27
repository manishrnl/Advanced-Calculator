package org.example.one_dashboard_multiple_pages;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

public class UnitConversionController {

    @FXML
    private Button convertButton;

    @FXML
    private ComboBox<String> fromComboBox, toComboBox, selectUnits;

    @FXML
    private TextField fromTextField, toTextField;

    private final Map<String, Map<String, Double>> unitCategories = new HashMap<>();

    @FXML
    private void initialize() {
        initializeUnitCategories();

        // Populate selectUnits ComboBox
        selectUnits.getItems().addAll(unitCategories.keySet());

        // Add event listener to update unit selection boxes when category changes
        selectUnits.setOnAction(event -> updateUnitComboBoxes());

        // Set default selection
        selectUnits.getSelectionModel().select("Length");
        updateUnitComboBoxes();
    }

    private void initializeUnitCategories() {
        Map<String, Double> lengthUnits = Map.of(
                "Meter (m)", 1.0, "Centimeter (cm)", 100.0, "Millimeter (mm)", 1000.0,
                "Kilometer (km)", 0.001, "Inch (in)", 39.3701, "Foot (ft)", 3.28084,
                "Yard (yd)", 1.09361, "Mile (mi)", 0.000621371
        );

        Map<String, Double> weightUnits = Map.of(
                "Kilogram (kg)", 1.0, "Gram (g)", 1000.0, "Milligram (mg)", 1_000_000.0,
                "Pound (lb)", 2.20462, "Ounce (oz)", 35.274, "Stone (st)", 0.157473
        );

        Map<String, Double> volumeUnits = Map.of(
                "Liter (L)", 1.0, "Milliliter (mL)", 1000.0, "Cubic Meter (m³)", 0.001,
                "Gallon (US)", 0.264172, "Gallon (UK)", 0.219969, "Pint (US)", 2.11338
        );

        Map<String, Double> timeUnits = Map.of(
                "Second (s)", 1.0, "Minute (min)", 1/60.0, "Hour (h)", 1/3600.0,
                "Day (d)", 1/86400.0, "Week (wk)", 1/604800.0, "Year (yr)", 1/3.154e+7
        );

        Map<String, Double> speedUnits = Map.of(
                "Meter per second (m/s)", 1.0, "Kilometer per hour (km/h)", 3.6,
                "Miles per hour (mph)", 2.23694, "Foot per second (ft/s)", 3.28084,
                "Knot (kn)", 1.94384
        );

        unitCategories.put("Length", lengthUnits);
        unitCategories.put("Weight/Mass", weightUnits);
        unitCategories.put("Volume", volumeUnits);
        unitCategories.put("Time", timeUnits);
        unitCategories.put("Speed", speedUnits);
        unitCategories.put("Temperature", null); // Special case, handled separately
    }

    private void updateUnitComboBoxes() {
        String selectedCategory = selectUnits.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            if (selectedCategory.equals("Temperature")) {
                fromComboBox.setItems(FXCollections.observableArrayList("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"));
                toComboBox.setItems(FXCollections.observableArrayList("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"));
            } else {
                Map<String, Double> selectedUnits = unitCategories.get(selectedCategory);
                if (selectedUnits != null) {
                    ObservableList<String> unitList = FXCollections.observableArrayList(selectedUnits.keySet());
                    fromComboBox.setItems(unitList);
                    toComboBox.setItems(unitList);
                }
            }
        }
    }

    @FXML
    private void performConversion(MouseEvent mouseEvent) {
        try {
            String fromUnit = fromComboBox.getValue();
            String toUnit = toComboBox.getValue();
            String category = selectUnits.getValue();

            if (fromUnit == null || toUnit == null) {
                toTextField.setText("Select valid units");
                return;
            }

            double amount = Double.parseDouble(fromTextField.getText());

            if (category.equals("Temperature")) {
                toTextField.setText(String.format("%.2f", convertTemperature(amount, fromUnit, toUnit)));
            } else {
                Map<String, Double> unitMap = unitCategories.get(category);
                double fromFactor = unitMap.get(fromUnit);
                double toFactor = unitMap.get(toUnit);

                double result = (amount * toFactor) / fromFactor;
                toTextField.setText(String.format("%.4f", result));
            }
        } catch (NumberFormatException e) {
            toTextField.setText("Invalid number format");
        } catch (Exception e) {
            toTextField.setText("Conversion error");
        }
    }

    private double convertTemperature(double value, String from, String to) {
        if (from.equals(to)) return value;

        // Convert `from` to Celsius
        double celsius;
        switch (from) {
            case "Fahrenheit (°F)":
                celsius = (value - 32) * 5 / 9;
                break;
            case "Kelvin (K)":
                celsius = value - 273.15;
                break;
            default:
                celsius = value;
                break;
        }

        // Convert Celsius to `to`
        switch (to) {
            case "Fahrenheit (°F)":
                return (celsius * 9 / 5) + 32;
            case "Kelvin (K)":
                return celsius + 273.15;
            default:
                return celsius;
        }
    }
}
