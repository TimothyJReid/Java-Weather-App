import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPIClient {

    private static final String API_KEY = "ADD_API_KEY_HERE";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "http://api.openweathermap.org/data/2.5/forecast";

    public static String getWeatherData(String location, String unit) {
        StringBuilder result = new StringBuilder();
        try {
            String urlString = WEATHER_API_URL + "?q=" + location + "&appid=" + API_KEY + "&units=" + unit;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Success
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } else if (responseCode == 404) { // Not Found
                return "Error: Location not found";
            } else { // Other errors
                return "Error: API request failed with response code " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Network issue or invalid request";
        }
        return result.toString();
    }

    public static String getForecastData(String location, String unit) {
        StringBuilder result = new StringBuilder();
        try {
            String urlString = FORECAST_API_URL + "?q=" + location + "&appid=" + API_KEY + "&units=" + unit;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Success
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } else if (responseCode == 404) { // Not Found
                return "Error: Location not found";
            } else { // Other errors
                return "Error: API request failed with response code " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Network issue or invalid request";
        }
        return result.toString();
    }
}
