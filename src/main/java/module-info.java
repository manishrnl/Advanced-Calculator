module org.example.one_dashboard_multiple_pages {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;  // Required for HTTP requests
    requires com.google.gson; // Add this line to use GSON

    opens org.example.one_dashboard_multiple_pages to javafx.fxml;
    exports org.example.one_dashboard_multiple_pages;
}
