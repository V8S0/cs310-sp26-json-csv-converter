package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {

    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {

        String result = "{}";

        try {

            /* Parse CSV */
            CSVReader reader = new CSVReader(new java.io.StringReader(csvString));
            java.util.List<String[]> rows = reader.readAll();

            /* Root JSON object */
            JsonObject root = new JsonObject();

            /* Column Headings */
            JsonArray colHeadings = new JsonArray();
            String[] header = rows.get(0);
            for (String h : header) {
                colHeadings.add(h);
            }

            /* ProdNums and Data */
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                /* ProdNum (first column) */
                prodNums.add(row[0]);

                /* Episode data (remaining columns) */
                JsonArray episode = new JsonArray();
                episode.add(row[1]);                       // Title
                episode.add(Integer.parseInt(row[2]));    // Season
                episode.add(Integer.parseInt(row[3]));    // Episode
                episode.add(row[4]);                       // Stardate
                episode.add(row[5]);                       // OriginalAirdate
                episode.add(row[6]);                       // RemasteredAirdate

                data.add(episode);
            }

            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);

            result = root.toJson();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
    }

    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {

        String result = "";

        try {

            /* Parse JSON */
            JsonObject root = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray data = (JsonArray) root.get("Data");

            java.io.StringWriter writer = new java.io.StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            /* Write header row */
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                header[i] = colHeadings.get(i).toString();
            }
            csvWriter.writeNext(header);

            /* Write data rows */
            for (int i = 0; i < data.size(); i++) {

                JsonArray episode = (JsonArray) data.get(i);
                String[] row = new String[header.length];

                row[0] = prodNums.get(i).toString();             // ProdNum
                row[1] = episode.get(0).toString();              // Title
                row[2] = episode.get(1).toString();              // Season

                /* Episode number with leading zero */
                int epNum = Integer.parseInt(episode.get(2).toString());
                row[3] = String.format("%02d", epNum);

                row[4] = episode.get(3).toString();              // Stardate
                row[5] = episode.get(4).toString();              // OriginalAirdate
                row[6] = episode.get(5).toString();              // RemasteredAirdate

                csvWriter.writeNext(row);
            }

            csvWriter.close();
            result = writer.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
    }
}    

