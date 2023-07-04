package org.football.stats.props;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class JsonReader {
    public static Object read(String filePath, String key) {

        try (final InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(Arrays.toString(inputStream.readAllBytes()));

                JSONObject jsonObject = (JSONObject) obj;

                // Retrieve the value based on the key
                return jsonObject.get(key);
            } else {
                throw new RuntimeException("File '" + filePath + "' not found.");
            }

        } catch (IOException e) {
            throw new ResourseReadFailedException("Problem occurred during reading properties file", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}

