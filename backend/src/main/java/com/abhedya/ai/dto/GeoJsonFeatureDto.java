package com.abhedya.ai.dto;

import java.util.Map;

public class GeoJsonFeatureDto {

    private String type = "Feature";
    private String id;
    private Object geometry;
    private Map<String, Object> properties;

    public GeoJsonFeatureDto() {}

    public GeoJsonFeatureDto(String id, Object geometry, Map<String, Object> properties) {
        this.type = "Feature";
        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Object getGeometry() { return geometry; }
    public void setGeometry(Object geometry) { this.geometry = geometry; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}

