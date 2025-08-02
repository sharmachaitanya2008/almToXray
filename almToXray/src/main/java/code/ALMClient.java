package code;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.*;
import java.util.*;

public class ALMClient {

    private static final String ALM_BASE = "https://alm.example.com/qcbin";
    private final CloseableHttpClient client = HttpClients.createDefault();
    private final ObjectMapper mapper = new ObjectMapper();

    public void authenticate(String user, String pass) throws Exception {
        HttpPost loginPost = new HttpPost(ALM_BASE + "/authentication-point/authenticate");
        loginPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        loginPost.setEntity(new StringEntity("username=" + user + "&password=" + pass));
        try (CloseableHttpResponse response = client.execute(loginPost)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("ALM authentication failed.");
            }
        }
    }

    public List<Map<String, Object>> fetchTests() throws Exception {
        HttpGet get = new HttpGet(ALM_BASE + "/rest/domains/DEFAULT/projects/YOUR_PROJECT/tests?fields=id,name,description,parent-id");
        get.setHeader("Accept", "application/json");
        try (CloseableHttpResponse response = client.execute(get)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonNode root = mapper.readTree(json);
            List<Map<String, Object>> results = new ArrayList<>();
            for (JsonNode entity : root.path("entities")) {
                Map<String, Object> test = new HashMap<>();
                for (JsonNode field : entity.get("Fields")) {
                    String name = field.get("Name").asText();
                    String value = field.path("values").get(0).path("value").asText();
                    test.put(name, value);
                }
                results.add(test);
            }
            return results;
        }
    }
}
