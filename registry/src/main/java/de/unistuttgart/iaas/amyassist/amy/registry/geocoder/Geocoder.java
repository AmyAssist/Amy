package de.unistuttgart.iaas.amyassist.amy.registry.geocoder;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.joining;

/**
 * Simple google geocoder interface
 *
 * @author Benno Krauß
 */
@Service
public class Geocoder {

    private static final String GOOGLE_API_PATH = "https://" + "maps.google.com" + "/maps/api/geocode/json";
    private static final String INVALID_RESPONSE_MESSAGE = "Invalid response";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String CONFIG_NAME = "geocoder.config";
    private static final String API_CREDENTIALS_KEY = "GOOGLE_API_KEY";

    @Reference
    public ConfigurationLoader configurationLoader;
    private Properties config;

    /**
     * Geocode an address
     * @param address the full address string
     * @return a pair of latitude and longitude values
     * @throws GeocoderException if any error occurs
     */
    public Pair<Double, Double> geocodeAddress(String address) throws GeocoderException {
        Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("key", getAPIKey());

        String urlString = params.keySet().stream()
                .map(key -> key + "=" + urlEncode(params.get(key)))
                .collect(joining("&", Geocoder.GOOGLE_API_PATH + "?", ""));

        String response = httpGET(urlString);

        JsonObject j = Json.parse(response).asObject();
        String status = j.getString("status", null);
        if (!(status.equals("OK") || status.equals("ZERO_RESULTS"))) {
            throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
        }

        JsonArray results = getJson(j, "results", JsonArray.class);
        if (results.isEmpty()) {
            throw new GeocoderException("Address not found");
        }

        JsonValue result = results.get(0);
        if (!(result instanceof JsonObject)) {
            throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
        }
        JsonObject resultObject = (JsonObject)result;

        JsonObject geometry = getJson(resultObject, "geometry", JsonObject.class);
        JsonObject location = getJson(geometry, "location", JsonObject.class);

        double lat = getJson(location, "lat", JsonValue.class).asDouble();
        double lng = getJson(location, "lng", JsonValue.class).asDouble();

        return new Pair<>(lat, lng);
    }

    private String getAPIKey() throws GeocoderException {
        Properties p = configurationLoader.load(CONFIG_NAME);
        String value;
        if ((value = p.getProperty(API_CREDENTIALS_KEY)) == null) {
            throw new GeocoderException("Geocoder needs a config file with a value for " + API_CREDENTIALS_KEY);
        }
        return value;
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            // This will never happen because the encoding name is a constant and UTF-8 is ubiquitous
            return null;
        }
    }

    private static <T extends JsonValue> T getJson(JsonObject o, String member, Class<T> cls) throws GeocoderException {
        JsonValue r = o.get(member);
        if (!cls.isInstance(r)) {
            throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
        }
        return cls.cast(r);
    }

    /**
     * Perform an HTTP GET request
     * @param requestURL the full url string
     * @return the response content
     * @throws GeocoderException if any error occurs during the request
     */
    private static String httpGET(String requestURL) throws GeocoderException {
        BufferedReader reader = null;
        try {
            URL request = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection)request.openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode > 299 || responseCode < 200) {
                throw new GeocoderException("Invalid response code " + responseCode);
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING_UTF8));
            StringBuilder b = new StringBuilder();
            String s = "";
            while ((s = reader.readLine()) != null) {
                b.append(s);
            }

            return b.toString();
        } catch (IOException readingError) {
            throw new GeocoderException(readingError.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e){
                    // Ignore
                }
            }
        }
    }
}
