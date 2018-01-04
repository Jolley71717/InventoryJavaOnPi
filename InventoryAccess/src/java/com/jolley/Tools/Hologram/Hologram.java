package com.jolley.Tools.Hologram;

import com.jolley.Tools.Passwords.SensativeInfo;
import com.jolley.Tools.Telnet.AutomatedTelnetClient;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Hologram {

    //bring in the password and login information
    private SensativeInfo sensativeInfo = new SensativeInfo();
    public Hologram(){}
        //created to all for the instantiation of Hologram

    public boolean runLocalProcess(String inventoryItem, String inventoryItemQuantity, String inventoryItemTrigger){
        //When running on the pi
        try{
            String inventoryMessage = "Item " + inventoryItem + " has fallen to: " + inventoryItemQuantity
                    + " below its trigger: " + inventoryItemTrigger;

            //File tempScript = createHologramScript(inventoryMessage);
            File tempScript = createTempScript();


            try {
                ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString()).inheritIO();

                Process process = pb.start();
                //String firstOutput = readinfo(process);

                System.out.println("first Output:");
                //System.out.println(firstOutput);
                // Get input and output stream references
                OutputStream outputStream = process.getOutputStream();
                System.out.println("Second Output:");
                System.out.println(outputStream);
                process.waitFor();

                BufferedReader stdInput = new BufferedReader(new InputStreamReader((process.getInputStream())));
                // BufferedReader stdOutput = new BufferedReader(new OutputStream((process.getOutputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s = null;


                // read the output from the command
                System.out.println("Here is the standard output (using tempscript) of the command:\n");
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }
                // read any errors from the attempted command
                System.out.println("Here is the standard error (using tempscript) of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }

            } finally {
                Files.deleteIfExists(tempScript.toPath());
            }

            // hologram network disconnect if needed
        }catch (Exception ex){
            System.out.println(ex);
        }

        return true;
    }

    // Used for running from my mac
    public boolean runHologramCode(String inventoryItem, String inventoryItemQuantity, String inventoryItemTrigger){

        boolean returnStatus = false;
        String inventoryMessage = "Item " + inventoryItem + " has fallen to: " + inventoryItemQuantity
                + " below its trigger: " + inventoryItemTrigger;

        //has to be run from telnet when I'm using on the mac
        //Otherwise run natively on the pi
        try {
            AutomatedTelnetClient telnet = new AutomatedTelnetClient(sensativeInfo.getRaspberryPiServer(),
                    sensativeInfo.getRaspberryPiLogin(), sensativeInfo.getRaspberryPiPassword());
            String resultString = telnet.sendCommandReadResult("sudo hologram send --cloud --authtype 'totp' '"
                    + inventoryMessage + "' ", "~$");
            System.out.println(resultString);
            returnStatus = !resultString.isEmpty() && resultString.contains("sent successfully");
            telnet.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return returnStatus;
    }


    private String executeCommand(String[] commands) {

        StringBuilder output = new StringBuilder();

        Process p;
        try {
            p = Runtime.getRuntime().exec(commands);
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


    public File createHologramScript(String message) throws IOException{
        File tempScript = File.createTempFile("script", null);
        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
                tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);
        printWriter.println("#!/bin/bash");
        printWriter.println("sudo hologram send --cloud --authtype 'totp' '" + message + "' ");
        printWriter.close();
        return tempScript;
    }

    // used to test the local test script functionality
    public File createTempScript() throws IOException{
        File tempScript = File.createTempFile("script", null);
        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);
        printWriter.println("#!/bin/bash");
        printWriter.println("ping -c 4 localhost");
        printWriter.close();

        return tempScript;
    }

}
