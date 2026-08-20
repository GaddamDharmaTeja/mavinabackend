package com.maviinamane.content;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("site_content")
public class SiteContent {

    @Id
    private String id;
    private String key;
    private Map<String, Object> value;

    public String getId() {
        return id;
    }

    public void setId(String v) {
        id = v;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String v) {
        key = v;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> v) {
        value = v;
    }
}