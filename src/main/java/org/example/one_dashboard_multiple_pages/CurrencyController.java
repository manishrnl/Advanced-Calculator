package org.example.one_dashboard_multiple_pages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CurrencyController implements Initializable {

    @FXML
    private Button convertButton;

    @FXML
    private ComboBox<String> fromComboBox, toComboBox;

    @FXML
    private TextField fromTextField, toTextField;

    private final Map<String, String> currencyMap = new HashMap<>();
    private static final String API_KEY = "6391953a1b38589c9e5a6abc";
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private final Map<String, JsonObject> cachedRates = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCurrencyMap();
        // Sort the currency list alphabetically by country name
        ObservableList<String> currencyList = FXCollections.observableArrayList(
                currencyMap.keySet().stream().sorted().collect(Collectors.toList())
        );
        fromComboBox.setItems(currencyList);
        toComboBox.setItems(currencyList);
        setupComboBox(fromComboBox);
        setupComboBox(toComboBox);

        Platform.runLater(() -> {
            fromComboBox.getSelectionModel().select("United States Dollar (USD)");
            fromComboBox.setValue("United States Dollar (USD)");
            fromTextField.setText("1.0");

        });

        // Add conversion triggers with safety checks
        fromComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && isInputValid()) triggerConversion();
        });
        toComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && isInputValid()) triggerConversion();
        });
        fromTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isInputValid()) triggerConversion();
        });
        convertButton.setOnMouseClicked(this::performConversion);
    }

    @FXML
    private void performConversion(MouseEvent event) {
        try {
            String from = currencyMap.get(fromComboBox.getValue());
            String to = currencyMap.get(toComboBox.getValue());
            if (from == null || to == null) {
                toTextField.setText("Invalid currency selection");
                return;
            }
            double amount = Double.parseDouble(fromTextField.getText());
            double result = fetchLiveRate(amount, from, to);
            toTextField.setText(String.format("%.2f", result));
        } catch (NumberFormatException e) {
            toTextField.setText("Invalid number format");
        } catch (IOException | JsonSyntaxException e) {
            toTextField.setText("Error: " + e.getMessage());
        }
    }

    private void initializeCurrencyMap() {
        currencyMap.put("Afghanistan Afghani (AFN)", "AFN");
        currencyMap.put("Albania Lek (ALL)", "ALL");
        currencyMap.put("Algeria Dinar (DZD)", "DZD");
        currencyMap.put("Angola Kwanza (AOA)", "AOA");
        currencyMap.put("Argentina Peso (ARS)", "ARS");
        currencyMap.put("Armenia Dram (AMD)", "AMD");
        currencyMap.put("Australia Dollar (AUD)", "AUD");
        currencyMap.put("Azerbaijan Manat (AZN)", "AZN");
        currencyMap.put("Bahamas Dollar (BSD)", "BSD");
        currencyMap.put("Bahrain Dinar (BHD)", "BHD");
        currencyMap.put("Bangladesh Taka (BDT)", "BDT");
        currencyMap.put("Barbados Dollar (BBD)", "BBD");
        currencyMap.put("Bolivia Boliviano (BOB)", "BOB");
        currencyMap.put("Bosnia-Herzegovina Convertible Mark (BAM)", "BAM");
        currencyMap.put("Brazil Real (BRL)", "BRL");
        currencyMap.put("Brunei Dollar (BND)", "BND");
        currencyMap.put("Bulgaria Lev (BGN)", "BGN");
        currencyMap.put("Burundi Franc (BIF)", "BIF");
        currencyMap.put("Canada Dollar (CAD)", "CAD");
        currencyMap.put("Chile Peso (CLP)", "CLP");
        currencyMap.put("China Yuan (CNY)", "CNY");
        currencyMap.put("Colombia Peso (COP)", "COP");
        currencyMap.put("Costa Rica Colón (CRC)", "CRC");
        currencyMap.put("Croatia Kuna (HRK)", "HRK");
        currencyMap.put("Cuba Peso (CUP)", "CUP");
        currencyMap.put("Czech Republic Koruna (CZK)", "CZK");
        currencyMap.put("Denmark Krone (DKK)", "DKK");
        currencyMap.put("Djibouti Franc (DJF)", "DJF");
        currencyMap.put("Dominican Republic Peso (DOP)", "DOP");
        currencyMap.put("Egypt Pound (EGP)", "EGP");
        currencyMap.put("Eritrea Nakfa (ERN)", "ERN");
        currencyMap.put("Ethiopia Birr (ETB)", "ETB");
        currencyMap.put("Eurozone Euro (EUR)", "EUR");
        currencyMap.put("Fiji Dollar (FJD)", "FJD");
        currencyMap.put("Gambia Dalasi (GMD)", "GMD");
        currencyMap.put("Georgia Lari (GEL)", "GEL");
        currencyMap.put("Ghana Cedi (GHS)", "GHS");
        currencyMap.put("Guatemala Quetzal (GTQ)", "GTQ");
        currencyMap.put("Honduras Lempira (HNL)", "HNL");
        currencyMap.put("Hong Kong Dollar (HKD)", "HKD");
        currencyMap.put("Hungary Forint (HUF)", "HUF");
        currencyMap.put("Iceland Króna (ISK)", "ISK");
        currencyMap.put("India Rupee (INR)", "INR");
        currencyMap.put("Indonesia Rupiah (IDR)", "IDR");
        currencyMap.put("Iran Rial (IRR)", "IRR");
        currencyMap.put("Iraq Dinar (IQD)", "IQD");
        currencyMap.put("Israel New Shekel (ILS)", "ILS");
        currencyMap.put("Jamaica Dollar (JMD)", "JMD");
        currencyMap.put("Japan Yen (JPY)", "JPY");
        currencyMap.put("Jordan Dinar (JOD)", "JOD");
        currencyMap.put("Kazakhstan Tenge (KZT)", "KZT");
        currencyMap.put("Kenya Shilling (KES)", "KES");
        currencyMap.put("Kuwait Dinar (KWD)", "KWD");
        currencyMap.put("Laos Kip (LAK)", "LAK");
        currencyMap.put("Lebanon Pound (LBP)", "LBP");
        currencyMap.put("Lesotho Loti (LSL)", "LSL");
        currencyMap.put("Liberia Dollar (LRD)", "LRD");
        currencyMap.put("Libya Dinar (LYD)", "LYD");
        currencyMap.put("Macedonia Denar (MKD)", "MKD");
        currencyMap.put("Madagascar Ariary (MGA)", "MGA");
        currencyMap.put("Malaysia Ringgit (MYR)", "MYR");
        currencyMap.put("Mexico Peso (MXN)", "MXN");
        currencyMap.put("Moldova Leu (MDL)", "MDL");
        currencyMap.put("Mongolia Tögrög (MNT)", "MNT");
        currencyMap.put("Morocco Dirham (MAD)", "MAD");
        currencyMap.put("Mozambique Metical (MZN)", "MZN");
        currencyMap.put("Myanmar Kyat (MMK)", "MMK");
        currencyMap.put("Nepal Rupee (NPR)", "NPR");
        currencyMap.put("Netherlands Antillean Guilder (ANG)", "ANG");
        currencyMap.put("New Zealand Dollar (NZD)", "NZD");
        currencyMap.put("Nigeria Naira (NGN)", "NGN");
        currencyMap.put("Norway Krone (NOK)", "NOK");
        currencyMap.put("Oman Rial (OMR)", "OMR");
        currencyMap.put("Pakistan Rupee (PKR)", "PKR");
        currencyMap.put("Philippines Peso (PHP)", "PHP");
        currencyMap.put("Poland Złoty (PLN)", "PLN");
        currencyMap.put("Qatar Riyal (QAR)", "QAR");
        currencyMap.put("Romania Leu (RON)", "RON");
        currencyMap.put("Russia Ruble (RUB)", "RUB");
        currencyMap.put("Saudi Arabia Riyal (SAR)", "SAR");
        currencyMap.put("Singapore Dollar (SGD)", "SGD");
        currencyMap.put("South Africa Rand (ZAR)", "ZAR");
        currencyMap.put("South Korea Won (KRW)", "KRW");
        currencyMap.put("Sri Lanka Rupee (LKR)", "LKR");
        currencyMap.put("Sudan Pound (SDG)", "SDG");
        currencyMap.put("Sweden Krona (SEK)", "SEK");
        currencyMap.put("Switzerland Franc (CHF)", "CHF");
        currencyMap.put("Syria Pound (SYP)", "SYP");
        currencyMap.put("Taiwan New Dollar (TWD)", "TWD");
        currencyMap.put("Tanzania Shilling (TZS)", "TZS");
        currencyMap.put("Thailand Baht (THB)", "THB");
        currencyMap.put("Tunisia Dinar (TND)", "TND");
        currencyMap.put("Turkey Lira (TRY)", "TRY");
        currencyMap.put("Uganda Shilling (UGX)", "UGX");
        currencyMap.put("Ukraine Hryvnia (UAH)", "UAH");
        currencyMap.put("United Arab Emirates Dirham (AED)", "AED");
        currencyMap.put("United Kingdom Pound (GBP)", "GBP");
        currencyMap.put("United States Dollar (USD)", "USD");
        currencyMap.put("Venezuela Bolívar (VES)", "VES");
        currencyMap.put("Vietnam Đồng (VND)", "VND");
        currencyMap.put("West Africa CFA Franc (XOF)", "XOF");
        currencyMap.put("Zambia Kwacha (ZMW)", "ZMW");
    }

    private void triggerConversion() {
        performConversion(null);
    }

    private double fetchLiveRate(double amount, String from, String to) throws IOException {
        if (from.equals(to)) return amount;
        JsonObject rates = cachedRates.computeIfAbsent(from, k -> {
            try {
                return fetchRatesFromAPI(API_BASE_URL + from);
            } catch (IOException e) {
                throw new RuntimeException("Failed to fetch rates: " + e.getMessage());
            }
        });
        if (rates == null || !rates.has(to)) {
            throw new IOException("Currency rate for " + to + " not available.");
        }
        return amount * rates.get(to).getAsDouble();
    }

    private JsonObject fetchRatesFromAPI(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            String json = scanner.useDelimiter("\\A").next();
            JsonObject response = JsonParser.parseString(json).getAsJsonObject();
            if (response.has("result") && response.get("result").getAsString().equals("success")) {
                return response.getAsJsonObject("conversion_rates");
            } else {
                throw new IOException("API error: " + response.get("error-type").getAsString());
            }
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON response", e);
        } finally {
            conn.disconnect();
        }
    }

    private boolean isInputValid() {
        return fromComboBox.getValue() != null &&
                toComboBox.getValue() != null &&
                !fromTextField.getText().isEmpty() &&
                fromTextField.getText().matches("\\d*\\.?\\d+");
    }

    private void setupComboBox(ComboBox<String> comboBox) {
        comboBox.setEditable(false);
        // Set visible row count to ensure all items are accessible
        comboBox.setVisibleRowCount(20); // Adjust this value based on your needs (e.g., 20-30)
        comboBox.setOnAction(event -> {
            String selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                comboBox.setValue(selected);
            }
        });
    }
}