package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.*;
import java.util.*;
import com.google.cloud.translate.*;

public class Main {
    private static final String BASE_URL = "https://huggingface.co/datasets/lavita/ChatDoctor-HealthCareMagic-100k/viewer/default/train?p=";

    public static void main(String[] args) {
        String targetLanguage = "pl"; // Переклад на польську
        int rowLimit = 10; // Ліміт кількості рядків для перекладу
        int page = 0;  // Початкова сторінка

        try {
            Translator translator = new Translator();
            JsonlWriter writer = new JsonlWriter();

            List<String[]> translatedData = new ArrayList<>();


            while (true) {
                String url = BASE_URL + page;
                HtmlParser parser = new HtmlParser();
                List<String[]> data = parser.extractData(url, rowLimit);  // Витягуємо дані з HTML

                if (data.isEmpty()) {

                    break;
                }


                for (String[] row : data) {
                    String condition = translator.translateText(row[0], targetLanguage);
                    String description = translator.translateText(row[1], targetLanguage);
                    String answer = translator.translateText(row[2], targetLanguage);
                    translatedData.add(new String[]{condition, description, answer});
                }


                if (isNextPageAvailable(url)) {
                    page++;
                } else {
                    break;
                }
            }

            // Записуємо в JSONL
            writer.writeDataToJsonl(translatedData, "translated_data.jsonl");

            System.out.println("Data processing and translation complete!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для перевірки наявності наступної сторінки
    public static boolean isNextPageAvailable(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element nextPageLink = doc.select("a:contains(Next)").first(); // Знаходимо посилання на наступну сторінку
            return nextPageLink != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
