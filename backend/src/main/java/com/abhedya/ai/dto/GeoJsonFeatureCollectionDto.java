package com.abhedya.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonFeatureCollectionDto {

    private String type = "FeatureCollection";
    private List<GeoJsonFeatureDto> features = new ArrayList<>();

    public GeoJsonFeatureCollectionDto() {}

    public GeoJsonFeatureCollectionDto(List<GeoJsonFeatureDto> features) {
        this.type = "FeatureCollection";
        this.features = features != null ? features : new ArrayList<>();
    }

    public void addFeature(GeoJsonFeatureDto feature) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        this.features.add(feature);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<GeoJsonFeatureDto> getFeatures() { return features; }
    public void setFeatures(List<GeoJsonFeatureDto> features) { this.features = features; }
}

