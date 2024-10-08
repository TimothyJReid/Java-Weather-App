# Weather Information App

## Overview

The Weather Information App is a JavaFX-based desktop application that provides users with real-time weather information and forecasts for a specified location. The app utilizes the OpenWeatherMap API to fetch weather data, displaying current conditions, hourly forecasts, and maintaining a history of recent searches.

## Features

- **Current Weather Information:** Displays the current temperature, humidity, wind speed, and weather conditions for the entered location.
- **Hourly Forecast:** Shows the weather forecast for the next three hours, including temperature, time, and an appropriate weather icon.
- **Dynamic Backgrounds:** The app background changes dynamically based on the time of day (morning, day, evening, night).
- **History Tracking:** Tracks and displays the last 10 weather searches with timestamps, temperature, and an icon representing the weather condition.
- **Unit Selection:** Allows users to choose between Celsius and Fahrenheit for temperature display.
- **Error Handling:** Displays appropriate error messages for invalid locations, network issues, or API request failures.

## Prerequisites

- **Java Development Kit (JDK) 21.0.2**
- **JavaFX SDK 22.0.2**
- **Gson Library for JSON parsing**

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/TimothyJReid/Java-weather-app
   ```
   
2. **Set Up JavaFX:**
   Ensure that you have the JavaFX SDK properly set up in your environment. Download it from [JavaFX](https://openjfx.io/) if you haven't already.

3. **Configure the API Key:**
   - Obtain an API key from [OpenWeatherMap](https://openweathermap.org/).
   - Replace the placeholder API key in the `WeatherAPIClient.java` file with your actual API key.

4. **Compile the Application:**
   Navigate to the `src/main/java` directory and compile the application:
   ```bash
   javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp ".;path/to/gson-2.11.0.jar" WeatherInformationApp.java
   ```

5. **Run the Application:**
   Run the compiled application:
   ```bash
   java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp ".;path/to/gson-2.11.0.jar;path/to/resources" WeatherInformationApp
   ```

## Usage

1. **Enter a Location:**
   Type the name of the location you want to check the weather for in the text field provided.

2. **Select Unit:**
   Choose between Celsius or Fahrenheit for the temperature display.

3. **View Weather Information:**
   Click the "Get Weather" button to fetch and display the current weather and hourly forecast.

4. **Check History:**
   View the history of your recent searches, which includes the location, time of search, temperature, and weather condition.

## Screenshots
<img width="461" alt="Output Day" src="https://github.com/user-attachments/assets/7da1f313-24a5-4f7b-98dc-eff3700acf7f">
<img width="458" alt="Output evening" src="https://github.com/user-attachments/assets/c3c76418-c6e1-47ee-b0f3-ec3b3fc65fc6">



## Troubleshooting

- **Invalid Location Error:** Ensure that the location name is spelled correctly and exists in the OpenWeatherMap database.
- **API Request Failed (401):** Double-check your API key in the `WeatherAPIClient.java` file to ensure it's correct and active.
- **Background Image Not Found:** Verify that the image files are in the correct directory (`resources/images/`).


## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for providing the weather data API.
- [JavaFX](https://openjfx.io/) for the UI framework.
