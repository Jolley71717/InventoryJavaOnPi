package com.jolley;

import com.jolley.Tools.Hologram.Hologram;
import com.jolley.Tools.POJO.Item;
import com.jolley.Tools.Phant.PhantProcedures;
import com.jolley.Tools.databaseclass.SQLiteJDBCConnection;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InventoryApplication {
    // sets up the initial database connection
    static SQLiteJDBCConnection sqLConnection = new SQLiteJDBCConnection();

    public static void main(String[] args) {
        // setting up the initial sql connection
        sqLConnection.connect();

        //Hologram localTelnetTestHologram = new Hologram();
        while (true) {
            //get the stream of information
            PhantProcedures phantProcedures = new PhantProcedures();
            phantProcedures.getRaspberryPiInventoryTrackerStream();


            // TODO
            // Remove once sending to the actual system
            Hologram localTelnetTestHologram = new Hologram();
            boolean testmessageSuccess = localTelnetTestHologram.runLocalProcess("Test", "Test","Test");

            if (phantProcedures.getPiInventoryJsonData() != null) {
                phantCode();
                try {
                    //gets inventory items sent from the photon to the raspberry pi
                    List<Item> items = phantProcedures.getRPiItems();
                    for (Item f : items) {
                        System.out.println(f.getDescription() + " at time " + f.getTimestamp()
                                + " with quantity:" + f.getQuantity());
                        sqLConnection.updateQuantity(f.getItemID(), f.getQuantity());
                    }

                    // after successful insert, remove the information from the pi phant
                    phantProcedures.clearRPiTrackerStream();

                    // clear the photon phant
                    phantProcedures.clearPhotonTrackerStream();

                    // Now we need to add the new items to the photon phant
                    List<Item> newQuantities = sqLConnection.getItemQuantities();
                    for (Item q : newQuantities) {
                        phantProcedures.inputPhantStreamPhoton(q.getItemID(), q.getDescription(), q.getQuantity());
                    }
                    //close the connection
                    //sqLConnection.closeConnection();

                } catch (Exception ex) {
                    //close the connection if an exception occured
                    if (sqLConnection != null) {
                        //sqLConnection.closeConnection();
                    }
                }
            }

            // Monitor the level of the trigger
            try {
                //SQLiteJDBCConnection sqLiteJDBCConnection = new SQLiteJDBCConnection();

                List<Item> inventoryItems = sqLConnection.getAllItems();
                for (Item a : inventoryItems) {
                    //check to see if the quantity is at or below the trigger. If it is, run the nova commands
                    if (a.shouldITrigger() && !a.amIAlreadyTriggered()) {
                        //Runs locally
                        Hologram localHologram = new Hologram();
                        boolean messageSuccess = localHologram.runLocalProcess(a.getDescription()
                                , a.getQuantity().toString(), a.getTriggerLvl().toString());

                        if (messageSuccess) {
                            //run the sql code to update the already triggered
                            sqLConnection.updateTriggeredValue(a.getItemID(), true);
                        }else
                            sqLConnection.updateTriggeredValue(a.getItemID(), true);

                        System.out.println(a.getDescription() + " fell bellow the trigger level: "
                                + a.getTriggerLvl().toString() + " and has a quantity of " + a.getQuantity().toString());
                    } else if (!a.shouldITrigger() && a.amIAlreadyTriggered()) {
                        // if the item has returned to above it's trigger levels and it has already been triggered
                        // we need to reset the triggered back to zero
                        sqLConnection.updateTriggeredValue(a.getItemID(), false);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
            
            //set a wait/sleep so that it doesn't burn it out
            // delays the process so it doesn't run every second
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }

    private static void phantCode() {
    }

}


