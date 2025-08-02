package code;

import java.util.*;

public class ALMToXrayMigrator {

    public static void main(String[] args) throws Exception {
        String fieldMappingPath = "resources/field_mapping.csv";
        Map<String, String> fieldMap = FieldMappingLoader.loadFieldMapping(fieldMappingPath);

        ALMClient alm = new ALMClient();
        alm.authenticate("yourALMUser", "yourALMPassword");

        XrayClient xray = new XrayClient();
        List<Map<String, Object>> tests = alm.fetchTests();

        for (Map<String, Object> test : tests) {
            Map<String, Object> xrayFields = new HashMap<>();

            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                String almKey = entry.getKey();
                String xrayKey = entry.getValue();
                if (test.containsKey(almKey)) {
                    xrayFields.put(xrayKey, test.get(almKey));
                }
            }

            xrayFields.putIfAbsent("project", Map.of("key", "QA"));
            xrayFields.putIfAbsent("issuetype", Map.of("name", "Test"));

            xray.createTest(xrayFields);
        }
    }
}
