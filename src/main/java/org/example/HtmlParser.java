package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.*;
import java.util.*;

public class HtmlParser {
    public List<String[]> extractData(String url, int rowLimit) throws IOException {
        List<String[]> data = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        int rowsProcessed = 0;


        for (Element row : doc.select("tr.group")) {
            if (rowsProcessed >= rowLimit) break;

            String condition = row.select("td:nth-child(1) span").text();
            String description = row.select("td:nth-child(2) span").text();
            String answer = row.select("td:nth-child(3) span").text();

            data.add(new String[]{condition, description, answer});
            rowsProcessed++;
        }

        return data;
    }

}

