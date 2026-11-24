package com.DigiTwin.demo.Model;

/** Building-specific input model; persisted through the common Asset collection. */
public class Building extends Asset {
    public Building() { setType("BUILDING"); }
    private Integer floors;
    private String function;
    public Integer getFloors(){return floors;} public void setFloors(Integer v){floors=v;}
    public String getFunction(){return function;} public void setFunction(String v){function=v;}
}
