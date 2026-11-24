package com.DigiTwin.demo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sites")
public class Site {
    @Id private String id;
    private String projectId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;

    public Site() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getProjectId(){return projectId;} public void setProjectId(String v){projectId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
}
