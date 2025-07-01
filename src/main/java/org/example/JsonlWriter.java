package org.example;

import java.io.*;
import java.util.*;

public class JsonlWriter {
    // Метод для запису даних у JSONL файл
    public void writeDataToJsonl(List<String[]> data, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String[] row : data) {
                String jsonLine = String.format("{\"condition\": \"%s\", \"description\": \"%s\", \"answer\": \"%s\"}",
                        row[0], row[1], row[2]);
                writer.write(jsonLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

