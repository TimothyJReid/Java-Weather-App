import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class WeatherInformationApp extends Application {

    private Label temperatureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label conditionLabel;
    private ImageView weatherIcon;
    private VBox timeColumn;
    private VBox tempColumn;
    private VBox iconColumn;
    private BorderPane mainLayout;
    private ObservableList<HistoryEntry> historyList; // For tracking search history
    private ListView<HistoryEntry> historyListView; // ListView to display search history

    @Override
    public void start(Stage primaryStage) {
        // Main Layout
        mainLayout = new BorderPane();

        // User Input Section
        Label locationLabel = new Label("Enter Location:");
        TextField locationInput = new TextField();
        Button getWeatherButton = new Button("Get Weather");

        // Unit Selection ComboBox
        ComboBox<String> unitSelector = new ComboBox<>();
        unitSelector.getItems().addAll("Celsius", "Fahrenheit");
        unitSelector.setValue("Celsius"); // Default selection

        // Initialize the history list and ListView
        historyList = FXCollections.observableArrayList();
        historyListView = new ListView<>(historyList);
        historyListView.setPrefHeight(150); // Adjust the height of the history section

        // Custom cell factory to display temp and icon next to history
        historyListView.setCellFactory(new Callback<ListView<HistoryEntry>, ListCell<HistoryEntry>>() {
            @Override
            public ListCell<HistoryEntry> call(ListView<HistoryEntry> param) {
                return new ListCell<HistoryEntry>() {
                    @Override
                    protected void updateItem(HistoryEntry item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox cellLayout = new HBox(10);
                            cellLayout.setAlignment(Pos.CENTER);

                            Label historyLabel = new Label(item.getDisplayText());
                            historyLabel.setTextFill(Color.DARKGRAY);

                            Label tempLabel = new Label(String.format("%.2f%s", item.getTemperature(),
                                    item.getUnit().equals("Celsius") ? "°C" : "°F"));
                            tempLabel.setTextFill(Color.DARKGRAY);

                            ImageView iconView = new ImageView(
                                    getClass().getResource("/images/" + item.getIcon()).toExternalForm());
                            iconView.setFitHeight(20);
                            iconView.setFitWidth(20);

                            cellLayout.getChildren().addAll(historyLabel, tempLabel, iconView);
                            setGraphic(cellLayout);
                        }
                    }
                };
            }
        });

        getWeatherButton.setOnAction(e -> {
            String location = locationInput.getText();
            String selectedUnit = unitSelector.getValue().equals("Celsius") ? "metric" : "imperial";
            String weatherData = WeatherAPIClient.getWeatherData(location, selectedUnit);
            if (weatherData.startsWith("Error")) {
                showError(weatherData);
            } else {
                updateWeatherInfo(weatherData, unitSelector.getValue());
                updateBackgroundBasedOnTime(); // Update background based on the time of day
                addToHistory(location, weatherData, unitSelector.getValue()); // Add the search to the history
            }
        });

        HBox inputSection = new HBox(10, locationLabel, locationInput, unitSelector, getWeatherButton);
        inputSection.setPadding(new Insets(10, 10, 10, 10));
        inputSection.setAlignment(Pos.CENTER); // Center align the input section
        mainLayout.setTop(inputSection);

        // Weather Information Display
        temperatureLabel = new Label("Temperature: N/A");
        humidityLabel = new Label("Humidity: N/A");
        windSpeedLabel = new Label("Wind Speed: N/A");
        conditionLabel = new Label("Condition: N/A");

        weatherIcon = new ImageView(); // Initialize weatherIcon

        // Add text color and adjust visibility
        temperatureLabel.setTextFill(Color.WHITE);
        humidityLabel.setTextFill(Color.WHITE);
        windSpeedLabel.setTextFill(Color.WHITE);
        conditionLabel.setTextFill(Color.WHITE);

        temperatureLabel.setStyle("-fx-font-size: 18px;");
        humidityLabel.setStyle("-fx-font-size: 18px;");
        windSpeedLabel.setStyle("-fx-font-size: 18px;");
        conditionLabel.setStyle("-fx-font-size: 18px;");

        VBox weatherInfoSection = new VBox(10, weatherIcon, temperatureLabel, humidityLabel, windSpeedLabel,
                conditionLabel);
        weatherInfoSection.setPadding(new Insets(20, 20, 20, 20));
        weatherInfoSection.setAlignment(Pos.CENTER); // Center align the weather info
        weatherInfoSection.setMaxWidth(300); // Limit the width to avoid stretching

        StackPane weatherInfoContainer = new StackPane(weatherInfoSection);
        weatherInfoContainer.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.6), new CornerRadii(10), Insets.EMPTY)));

        // Hourly Forecast Display Section with columns
        timeColumn = new VBox(10);
        tempColumn = new VBox(10);
        iconColumn = new VBox(10);

        timeColumn.setAlignment(Pos.CENTER_LEFT);
        tempColumn.setAlignment(Pos.CENTER);
        iconColumn.setAlignment(Pos.CENTER_RIGHT);

        timeColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        tempColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");

        HBox forecastSection = new HBox(20, timeColumn, tempColumn, iconColumn);
        forecastSection.setPadding(new Insets(10, 10, 10, 10));
        forecastSection.setAlignment(Pos.CENTER);

        StackPane forecastContainer = new StackPane(forecastSection);
        forecastContainer.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.6), new CornerRadii(10), Insets.EMPTY)));
        forecastContainer.setMaxHeight(150); // Adjust height to fit 3 items
        forecastContainer.setMaxWidth(300); // Adjust width to match weather info section

        // Style the history section similarly and center its contents
        StackPane historyContainer = new StackPane(historyListView);
        historyContainer.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.6), new CornerRadii(10), Insets.EMPTY)));
        historyContainer.setPadding(new Insets(10, 10, 10, 10));
        historyContainer.setMaxWidth(450); // Limit the width
        historyContainer.setPrefWidth(450); // Set preferred width
        historyListView.setStyle("-fx-background-color: transparent; -fx-text-fill: lightgray;");
        historyListView.setPrefHeight(200); // Adjust height to only fit content

        // Stack weather info, forecast, and history vertically
        VBox stackedSections = new VBox(20, weatherInfoContainer, forecastContainer, historyContainer);
        stackedSections.setAlignment(Pos.CENTER);

        // Automatically size the box to the content
        stackedSections.setMaxWidth(VBox.USE_PREF_SIZE);
        stackedSections.setMaxHeight(VBox.USE_PREF_SIZE);

        // Set padding to avoid it stretching to fill the window
        stackedSections.setPadding(new Insets(20));

        // Wrap in another StackPane to center it properly
        StackPane centeredStackPane = new StackPane(stackedSections);
        centeredStackPane.setAlignment(Pos.CENTER);

        // Use centeredStackPane instead of stackedSections directly
        mainLayout.setCenter(centeredStackPane);

        mainLayout.setCenter(stackedSections); // Align stacked sections to the center

        // Set Scene and Show Stage with adjusted dimensions
        Scene scene = new Scene(mainLayout, 600, 600); // Reduced height
        primaryStage.setScene(scene);
        primaryStage.setTitle("Weather Information App");
        primaryStage.show();
    }

    private void updateWeatherInfo(String jsonData, String unit) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

        // Extract weather information
        JsonObject main = jsonObject.getAsJsonObject("main");
        double temperature = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        JsonObject wind = jsonObject.getAsJsonObject("wind");
        double windSpeed = wind.get("speed").getAsDouble();

        // Convert wind speed based on selected unit
        String windSpeedText;
        if (unit.equals("Celsius")) {
            windSpeed *= 3.6; // Convert from m/s to km/h
            windSpeedText = String.format("Wind Speed: %.2f km/h", windSpeed);
        } else {
            windSpeed *= 2.237; // Convert from m/s to mph
            windSpeedText = String.format("Wind Speed: %.2f mph", windSpeed);
        }

        String weatherDescription = jsonObject.getAsJsonArray("weather")
                .get(0).getAsJsonObject().get("description").getAsString();

        // Determine the correct unit symbol
        String unitSymbol = unit.equals("Celsius") ? "°C" : "°F";

        // Update labels with the extracted information
        temperatureLabel.setText("Temperature: " + String.format("%.2f", temperature) + " " + unitSymbol);
        humidityLabel.setText("Humidity: " + humidity + " %");
        windSpeedLabel.setText(windSpeedText);
        conditionLabel.setText("Condition: " + weatherDescription);

        // Load and display appropriate weather icon
        String iconFileName = getIconName(weatherDescription);
        String imagePath = "/images/" + iconFileName;
        try {
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            weatherIcon.setImage(image);
            weatherIcon.setFitHeight(100);
            weatherIcon.setFitWidth(100);
        } catch (NullPointerException e) {
            System.out.println("Error: Image file not found at path " + imagePath);
            e.printStackTrace();
        }

        // Fetch and display hourly forecast data
        String forecastData = WeatherAPIClient.getForecastData(jsonObject.get("name").getAsString(), unit);
        if (forecastData.startsWith("Error")) {
            showError(forecastData);
        } else {
            updateForecastInfo(forecastData, unit);
        }
    }

    private void updateForecastInfo(String forecastData, String unit) {
        JsonObject jsonObject = JsonParser.parseString(forecastData).getAsJsonObject();
        timeColumn.getChildren().clear();
        tempColumn.getChildren().clear();
        iconColumn.getChildren().clear();

        int count = 0;
        for (JsonElement element : jsonObject.getAsJsonArray("list")) {
            if (count >= 3) // Limit to 3 entries
                break;
            JsonObject entry = element.getAsJsonObject();
            String time = entry.get("dt_txt").getAsString().substring(11, 16); // Display only the time part
            JsonObject main = entry.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();

            // Convert temperature to Celsius or Fahrenheit based on the selected unit
            if (unit.equals("Celsius")) {
                temp -= 273.15; // Convert from Kelvin to Celsius
            } else if (unit.equals("Fahrenheit")) {
                temp = (temp - 273.15) * 9 / 5 + 32; // Convert from Kelvin to Fahrenheit
            }

            String weather = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Convert time and temp strings to Labels
            Label timeLabel = new Label(time);
            Label tempLabel = new Label(String.format("%.2f%s", temp, unit.equals("Celsius") ? "°C" : "°F"));

            // Set style for labels
            timeLabel.setStyle("-fx-text-fill: lightgray;");
            tempLabel.setStyle("-fx-text-fill: lightgray;");

            // Load the appropriate weather icon
            ImageView weatherIcon = new ImageView(
                    getClass().getResource("/images/" + getIconName(weather)).toExternalForm());
            weatherIcon.setFitHeight(30);
            weatherIcon.setFitWidth(30);

            // Add labels and icon to their respective columns
            timeColumn.getChildren().add(timeLabel);
            tempColumn.getChildren().add(tempLabel);
            iconColumn.getChildren().add(weatherIcon);

            count++;
        }
    }

    private void updateBackgroundBasedOnTime() {
        String backgroundFileName = "morning.jpg";
        LocalTime time = LocalTime.now();
        if (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(18, 0))) {
            backgroundFileName = "day.jpg";
        } else if (time.isAfter(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(21, 0))) {
            backgroundFileName = "evening.jpg";
        } else {
            backgroundFileName = "night.jpg";
        }

        String imagePath = "images/" + backgroundFileName;
        setBackground(imagePath);
    }

    private void setBackground(String imagePath) {
        try {
            // Load the background image
            Image backgroundImage = new Image(getClass().getClassLoader().getResource(imagePath).toExternalForm());

            // Adjust the background size to fit the window
            BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false,
                    true, false);

            // Create a BackgroundImage using the scaled image
            BackgroundImage bgImage = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    backgroundSize);

            // Set the background to the main layout
            mainLayout.setBackground(new Background(bgImage));
        } catch (Exception e) {
            System.out.println("Error loading background image from path: " + imagePath);
            e.printStackTrace();
        }
    }

    private String getIconName(String weatherDescription) {
        weatherDescription = weatherDescription.toLowerCase(); // Make it case-insensitive
        if (weatherDescription.contains("clear") && !weatherDescription.contains("night")) {
            return "sun.png";
        } else if (weatherDescription.contains("clear") && weatherDescription.contains("night")) {
            return "night_clear.png";
        } else if (weatherDescription.contains("cloud") && weatherDescription.contains("few")) {
            return "partly_cloudy.png";
        } else if (weatherDescription.contains("cloud")) {
            return "cloud.png";
        } else if (weatherDescription.contains("overcast")) {
            return "overcast.png";
        } else if (weatherDescription.contains("rain") && weatherDescription.contains("heavy")) {
            return "heavy_rain.png";
        } else if (weatherDescription.contains("rain")) {
            return "rain.png";
        } else if (weatherDescription.contains("thunderstorm")) {
            return "thunderstorm.png";
        } else if (weatherDescription.contains("snow")) {
            return "snow.png";
        } else if (weatherDescription.contains("fog") || weatherDescription.contains("mist")) {
            return "fog.png";
        } else if (weatherDescription.contains("wind")) {
            return "wind.png";
        }
        return "default.png"; // Fallback icon for unknown conditions
    }

    private void addToHistory(String location, String weatherData, String unit) {
        JsonObject jsonObject = JsonParser.parseString(weatherData).getAsJsonObject();
        double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description")
                .getAsString();
        String icon = getIconName(weatherDescription);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        historyList.add(new HistoryEntry("[" + timestamp + "] " + location, temp, unit, icon));
        if (historyList.size() > 10) {
            historyList.remove(0); // Remove the oldest entry if more than 10 items in history
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // HistoryEntry class to store history details
    public class HistoryEntry {
        private String displayText;
        private double temperature;
        private String unit;
        private String icon;

        public HistoryEntry(String displayText, double temperature, String unit, String icon) {
            this.displayText = displayText;
            this.temperature = temperature;
            this.unit = unit;
            this.icon = icon;
        }

        public String getDisplayText() {
            return displayText;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getUnit() {
            return unit;
        }

        public String getIcon() {
            return icon;
        }
    }
}
