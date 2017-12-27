package com.jolley.Tools.databaseclass;

import com.jolley.Tools.POJO.Item;
import com.jolley.Tools.Passwords.SensativeInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
     *
     * @author Luke Dutton
     */
    public class SQLiteJDBCConnection {

        //bring in the password and login information
        private SensativeInfo sensativeInfo = new SensativeInfo();

        public  void SQLiteJDBCConnection(){}

        /**
         * Connect to database on computer
         */
        private static Connection conn = null;

        public  void connect() {


            try {
                // db parameters, db should go somewhere accessable on the rpi
                String url = "jdbc:sqlite:" + sensativeInfo.getSqliteFileLocation();

                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        public  void closeConnection(){
            try{
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException ex){
                System.out.println(ex.getMessage());
            }
        }

        public List<Item> getItemQuantities(){
            List<Item> photonItems = selectMin();


            return  photonItems;
        }

        public List<Item> getAllItems(){
            return selectAll();
        }

        private List<Item> selectMin(){
            List<Item> listOfItems = new ArrayList<>();
            String sql = "SELECT ItemID, Description, Quantity FROM INVENTORY";

            try (Statement stmt  = conn.createStatement();
                 ResultSet rs    = stmt.executeQuery(sql)){

                // loop through the result set
                while (rs.next()) {
                    Item currentItem = new Item(rs.getInt("ItemID"),rs.getString("Description")
                            ,rs.getInt("Quantity"));
                    listOfItems.add(currentItem);

                }

                return listOfItems;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        private List<Item> selectAll(){
            List<Item> listOfItems = new ArrayList<>();
            String sql = "SELECT ItemID, Description, Quantity,TriggerLvl, Triggered FROM INVENTORY";

            try {
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                // loop through the result set
                while (rs.next()) {
                    Item currentItem = new Item(rs.getInt("ItemID"),rs.getString("Description")
                            ,rs.getInt("Quantity"),rs.getInt("TriggerLvl"), rs.getInt("Triggered"));
                    listOfItems.add(currentItem);

                }

                return listOfItems;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        private void update(int ItemID, int quantity) {
            updateSQL(ItemID, quantity, "UPDATE inventory SET Quantity = ?  " + "WHERE ItemID = ?");
        }

        private void updateTriggered(Integer itemID, Integer triggered){
            updateSQL(itemID, triggered, "UPDATE inventory SET Triggered = ?  " + "WHERE ItemID = ?");
        }

    private void updateSQL(Integer itemID, Integer fieldValue, String sql) {
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setInt(1, fieldValue);
            pstmt.setInt(2, itemID);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateTriggeredValue(Integer inventoryID, boolean triggered){
            Integer triggeredInteger = triggered ? 1:0;
            updateTriggered(inventoryID,triggeredInteger);

        }

        public void updateQuantity(Integer inventoryID, Integer quantity){
            //update the sqlite database item quantity
            update(inventoryID, quantity);
        }

    }

