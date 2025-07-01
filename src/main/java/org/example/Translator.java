package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Translator {
    private static final String API_URL = "https://libretranslate.de/translate"; // нова адреса
    private static final int DELAY_BETWEEN_REQUESTS_MS = 1000; // 1 секунда затримки

    public String translateText(String text, String targetLanguage) {
        try {
            // Формуємо JSON для запиту
            String jsonInputString = String.format(
                    "{\"q\":\"%s\", \"source\":\"en\", \"target\":\"%s\", \"format\":\"text\"}",
                    text.replace("\"", "\\\""),
                    targetLanguage
            );

            // Налаштування HTTP POST запиту
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Відправка запиту
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            if (status == 429) { // обробка перевищення ліміту
                System.out.println("Received 429 Too Many Requests. Waiting 5 seconds before retry...");
                Thread.sleep(5000);
                return translateText(text, targetLanguage);
            } else if (status != 200) {
                throw new IOException("HTTP error code: " + status);
            }

            // Читання відповіді
            InputStream responseStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8));
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            // Парсинг JSON вручну
            String response = responseBuilder.toString();
            int start = response.indexOf("\"translatedText\":\"") + "\"translatedText\":\"".length();
            int end = response.indexOf("\"", start);
            String translatedText = response.substring(start, end);

            // Затримка після перекладу
            Thread.sleep(DELAY_BETWEEN_REQUESTS_MS);

            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            return text; // повертаємо оригінал при помилці
        }
    }
}
