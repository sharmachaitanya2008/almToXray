package code;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class XrayClient {
    private static final String XRAY_BASE = "https://xray.example.com/rest/api";
    private static final String JIRA_PROJECT_KEY = "QA";
    private static final String XRAY_TOKEN = "YOUR_XRAY_BEARER_TOKEN";

    private final CloseableHttpClient client = HttpClients.createDefault();
    private final ObjectMapper mapper = new ObjectMapper();

    public void createTest(Map<String, Object> xrayFields) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fields", xrayFields);

        HttpPost post = new HttpPost(XRAY_BASE + "/v2/import/test");
        post.setHeader("Authorization", "Bearer " + XRAY_TOKEN);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(mapper.writeValueAsString(payload)));

        try (CloseableHttpResponse response = client.execute(post)) {
            String body = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to create Xray test: " + body);
            } else {
                System.out.println("Test created successfully.");
            }
        }
    }

    public String ensureFolder(String path) throws Exception {
        // Normalize path and remove trailing slash
        path = path.replaceAll("^/+", "").replaceAll("/+$", "");
        if (path.isEmpty()) return null;

        String projectKey = "QA";  // Or pass it as a parameter

        String url = XRAY_BASE + "/v2/testrepository/paths";
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + XRAY_TOKEN);
        post.setHeader("Content-Type", "application/json");

        Map<String, Object> payload = new HashMap<>();
        payload.put("projectKey", projectKey);
        payload.put("path", path);

        String jsonBody = mapper.writeValueAsString(payload);
        post.setEntity(new StringEntity(jsonBody));

        try (CloseableHttpResponse response = client.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());

            if (statusCode == 200 || statusCode == 201) {
                System.out.println("Folder ensured: " + path);
                return path;
            } else {
                throw new RuntimeException("Failed to ensure folder: " + path + " Response: " + body);
            }
        }
    }

}

