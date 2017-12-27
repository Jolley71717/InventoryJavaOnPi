package com.jolley.Tools.Hologram;

import com.jolley.Tools.Passwords.SensativeInfo;
import com.jolley.Tools.Telnet.AutomatedTelnetClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Hologram {

    //bring in the password and login information
    private SensativeInfo sensativeInfo = new SensativeInfo();

    public Hologram(){}

    public boolean runHologramCode(String inventoryItem, String inventoryItemQuantity, String inventoryItemTrigger){

        boolean returnStatus = false;
        String inventoryMessage = "Item " + inventoryItem + " has fallen to: " + inventoryItemQuantity  +" below its trigger: " + inventoryItemTrigger;


        //has to be run from telnet when I'm using on the mac
        //Otherwise run natively on the pi
        try {
            AutomatedTelnetClient telnet = new AutomatedTelnetClient(sensativeInfo.getRaspberryPiServer(),
                    sensativeInfo.getRaspberryPiLogin(), sensativeInfo.getRaspberryPiPassword());
            String resultString = telnet.sendCommandReadResult("sudo hologram send --cloud --authtype 'totp' '"
                    + inventoryMessage+"' ", "~$");
            System.out.println(resultString);
            returnStatus = !resultString.isEmpty() && resultString.contains("sent successfully");
            telnet.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return returnStatus;
        /*

        try{
            Runtime rt = Runtime.getRuntime();
            //won't need to telnet once it's set up to run on the raspberry pi
            String[] commands = {"bash","-c","telnet " + sensativeInfo.getTelnetServerAddress(),sensativeInfo.getRaspberryPiPassword()
                    ,sensativeInfo.getRaspberryPiPassword(), "sudo hologram modem location"};

            String command = ""; // "telnet ipAddress && username && password"
            String output = createBad(commands);
            System.out.println(output);

            if(false){
                Process pr = rt.exec(commands);
                //get wait result here
                BufferedReader stdInput = new BufferedReader(new InputStreamReader((pr.getInputStream())));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                String s = null;


                // read the output from the command
                System.out.println("Here is the standard output of the command:\n");
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }

                // read any errors from the attempted command
                System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }


            // hologram network disconnect if needed
        }catch (Exception ex){
            System.out.println(ex);
        }
        */

    }


    // I will use these in the java app version
    private String createBad(String[] commands){
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            //p = new ProcessBuilder(commands).command("bash","-c",command).start();
            ProcessBuilder pb = new ProcessBuilder(commands);

            p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private String executeCommand(String[] command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            //p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


}
