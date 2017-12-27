package com.jolley.Tools.Phant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolley.Tools.POJO.Item;
import com.jolley.Tools.POJO.ItemComparator;
import com.jolley.Tools.Passwords.SensativeInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class PhantProcedures {

    //bring in the password and login information
    private SensativeInfo sensativeInfo = new SensativeInfo();

    private String photonInventoryJsonData;
    private String piInventoryJsonData;


    public void clearRPiTrackerStream()  {
// http://data.sparkfun.com/input/PUBLIC_KEY/clear?private_key=PRIVATE_KEY
            String streamURL = "http://" + sensativeInfo.getPhantIP() +":" + sensativeInfo.getPhantPort() + "/input/"+
                    sensativeInfo.getPhantStreamRPITrackerPublicKey() +"/clear?private_key="
                    + sensativeInfo.getPhantStreamPRITrackerPrivateKey();
        clearTrackerStream(streamURL);
    }
    public void clearPhotonTrackerStream()  {
// http://data.sparkfun.com/input/PUBLIC_KEY/clear?private_key=PRIVATE_KEY
            String streamURL = "http://" + sensativeInfo.getPhantIP() +":" + sensativeInfo.getPhantPort() +
                    "/input/"+ sensativeInfo.getPhantStreamPhotonTrackerPublicKey()
                    + "/clear?private_key=" + sensativeInfo.getPhantStreamPhotonTrackerPrivateKey();
        clearTrackerStream(streamURL);
    }

    private void clearTrackerStream(String streamUrl){
        try{
            URL url = new URL(streamUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            System.out.println(content);
            in.close();
            con.disconnect();

        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    public void getRaspberryPiInventoryTrackerStream(){
        //set up a while loop here so that this thing constantly goes
        String stringURL = "http://" + sensativeInfo.getPhantIP() + ":" + sensativeInfo.getPhantPort() + "/output/"
                +sensativeInfo.getPhantStreamRPITrackerPublicKey()
                +".json";

        try{
            piInventoryJsonData = readUrl(stringURL);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void getPhotonInventoryTrackerStream(){
        //set up a while loop here so that this thing constantly goes
        String stringURL = "http://" + sensativeInfo.getPhantIP() +":" + sensativeInfo.getPhantPort()
                + "/output/"+ sensativeInfo.getPhantStreamPhotonTrackerPublicKey()+".json";

        try{
            photonInventoryJsonData = readUrl(stringURL);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public List<Item> getPhotonItems(){
        return getItems(photonInventoryJsonData);
    }

    public List<Item> getRPiItems(){

        return getItems(piInventoryJsonData);
    }

    public List<Item> getItems(String jsonData){
        ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,true);

            List<Item> items = mapper.readValue(jsonData ,new TypeReference<List<Item>>(){});
            Collections.sort(items, new ItemComparator());

            return items;

        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    //push updates to the listening phant


    public void inputPhantStreamPhoton(Integer itemID, String description, Integer quantity){
        String phantInputURL = "http://" + sensativeInfo.getPhantIP() +":" + sensativeInfo.getPhantPort() + "/input/"
                + sensativeInfo.getPhantStreamPhotonTrackerPublicKey()+"?" +
                "private_key=" + sensativeInfo.getPhantStreamPhotonTrackerPrivateKey();
        phantInputURL = phantInputURL + "&ItemID=" + itemID.toString() + "&Description="
                + description + "&Quantity=" + quantity.toString();
        String result = "";
        try{
            result = readUrl(phantInputURL);
        }catch (Exception e){
            System.out.println(e);
        }

        System.out.println(result);

    }

    public String getPhotonInventoryJsonData() {
        return photonInventoryJsonData;
    }

    public void setPhotonInventoryJsonData(String photonInventoryJsonData) {
        this.photonInventoryJsonData = photonInventoryJsonData;
    }

    public String getPiInventoryJsonData() {
        return piInventoryJsonData;
    }

    public void setPiInventoryJsonData(String piInventoryJsonData) {
        this.piInventoryJsonData = piInventoryJsonData;
    }


}
