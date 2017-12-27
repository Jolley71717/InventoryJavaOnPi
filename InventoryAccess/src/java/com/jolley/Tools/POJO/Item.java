package com.jolley.Tools.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Item{

    //only used when reading from the json
    @JsonProperty("timestamp") public Timestamp timestamp;

    //used when reading from the database
    private Integer triggerLvl = null;
    private Integer triggered = null;

    //used both when reading from the database and when reading from the json
    @JsonProperty("Description")private   String description;
    @JsonProperty("ItemID") private Integer itemID;
    @JsonProperty("Quantity") private Integer quantity;

    public Item(){}

    public Item(Integer itemID, String description, Integer quantity){
        this.description = description;
        this.itemID = itemID;
        this.quantity = quantity;
    }

    public Item(Integer itemID, String description, Integer quantity, Integer triggerLvl, Integer triggered){
        this.description = description;
        this.itemID = itemID;
        this.quantity = quantity;
        this.triggered = triggered;
        this.triggerLvl = triggerLvl;
    }

    public Item(Integer itemID, String description,  Integer quantity, Timestamp timestamp){
    this.description = description;
    this.itemID = itemID;
    this.quantity = quantity;
    this.timestamp = timestamp;
    }

    public Boolean shouldITrigger(){
        return  quantity <= triggerLvl? true:false;
    }

    public Boolean amIAlreadyTriggered(){

        return triggered > 0 ? true:false;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID){
        this.itemID = itemID;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTriggerLvl() {
        return triggerLvl;
    }

    public void setTriggerLvl(Integer triggerLvl) {
        this.triggerLvl = triggerLvl;
    }

    public Integer getTriggered() {
        return triggered;
    }

    public void setTriggered(Integer triggered) {
        this.triggered = triggered;
    }

}
