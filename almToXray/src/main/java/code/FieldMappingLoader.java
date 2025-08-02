package code;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

public class FieldMappingLoader {
    public static Map<String, String> loadFieldMapping(String csvPath) throws IOException, CsvValidationException {
        Map<String, String> mapping = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if (line.length >= 2) {
                    mapping.put(line[0].trim(), line[1].trim());
                }
            }
        }
        return mapping;
    }
}
