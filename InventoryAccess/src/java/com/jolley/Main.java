package com.jolley;

import com.jolley.Tools.Hologram.Hologram;
import com.jolley.Tools.POJO.Item;
import com.jolley.Tools.Phant.PhantProcedures;
import com.jolley.Tools.databaseclass.SQLiteJDBCConnection;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    // sets up the initial database connection
    static SQLiteJDBCConnection sqLConnection = new SQLiteJDBCConnection();

    public static void main(String[] args) {
        // setting up the initial sql connection
        sqLConnection.connect();

        while (true) {
            //get the stream of information
            PhantProcedures phantProcedures = new PhantProcedures();

            if (phantProcedures.getPiInventoryJsonData() != null) {


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

                    // Now we need to add the new items to the phant
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

                        //If the inventory update is null, you shouldn't be printing stuff out
                        Hologram hologram = new Hologram();
                        boolean messageSuccess = hologram.runHologramCode(a.getDescription()
                                , a.getQuantity().toString(), a.getTriggerLvl().toString());

                        if (messageSuccess) {
                            //run the sql code to update the already triggered
                            sqLConnection.updateTriggeredValue(a.getItemID(), true);
                        }

//                    Runtime rt = Runtime.getRuntime();
//

//                    Process pr = rt.exec(commands);
//                    //get wait result here
//                    BufferedReader stdInput = new BufferedReader(new InputStreamReader((pr.getInputStream())));
//                    BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
//                    String s = null;
//
//
//                    // read the output from the command
//                    System.out.println("Here is the standard output of the command:\n");
//                    while ((s = stdInput.readLine()) != null) {
//                        System.out.println(s);
//                    }
//
//                    // read any errors from the attempted command
//                    System.out.println("Here is the standard error of the command (if any):\n");
//                    while ((s = stdError.readLine()) != null) {
//                        System.out.println(s);
//                    }
//
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

}


