package com.DigiTwin.demo.Model;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("assets")
public class Asset {
    @Id private String id;
    private String projectId;
    private String siteId;
    private String name;
    private String type;
    private String description;
    private String modelUrl;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double width;
    private Double depth;
    private Double height;
    private Double rotation;
    private Map<String,Object> metadata;
    private Instant createdAt = Instant.now();

    public Asset() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getProjectId(){return projectId;} public void setProjectId(String v){projectId=v;}
    public String getSiteId(){return siteId;} public void setSiteId(String v){siteId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getModelUrl(){return modelUrl;} public void setModelUrl(String v){modelUrl=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public Double getAltitude(){return altitude;} public void setAltitude(Double v){altitude=v;}
    public Double getWidth(){return width;} public void setWidth(Double v){width=v;}
    public Double getDepth(){return depth;} public void setDepth(Double v){depth=v;}
    public Double getHeight(){return height;} public void setHeight(Double v){height=v;}
    public Double getRotation(){return rotation;} public void setRotation(Double v){rotation=v;}
    public Map<String,Object> getMetadata(){return metadata;} public void setMetadata(Map<String,Object> v){metadata=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
