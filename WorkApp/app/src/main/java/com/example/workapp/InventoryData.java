package com.example.workapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.View;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryData implements Serializable {
    private static String[][] inventoryArray;
    private static final int ITEMS_PER_ROW = 4;
    private static final int MAX_ROWS = 50;
    private static int currentRow = 0;
    private String filename = "inventoryData.txt";

    /**One general constructor is needed. This creates our *static* 2D array which contains all of
     * our inventory data. This is done so for all instances of this object, it shares the same
     * information.
     * 
     */
    public InventoryData() {
        inventoryArray = new String[MAX_ROWS][ITEMS_PER_ROW];
        for (int i = 0; i < MAX_ROWS; i++) {
            inventoryArray[i][0] = "";
            inventoryArray[i][1] = "";
            inventoryArray[i][2] = "";
            inventoryArray[i][3] = "";
        }
    }

    /**Adds a row of data into our inventoryArray
     * 
     * @param row int, add to an empty row (its index)
     * @param itemName String, the description
     * @param partNumber String, the partnumber
     * @param price, String, the unit price
     * @param quantity String, amount in the inventory
     * @return true for successful entry
     */
    public boolean addRow(int row, String itemName, String partNumber, String price, String quantity) {
        if (currentRow < MAX_ROWS) {
            inventoryArray[row][0] = itemName;
            inventoryArray[row][1] = partNumber;
            inventoryArray[row][2] = price;
            inventoryArray[row][3] = quantity;
            currentRow++;
            return true;
        }
        else
            return false;
    }

    /**Removes a row of data by making all fields null.
     * 
     * @param row int, the row to be deleted (its index)
     * @return true if successful
     */
    public boolean removeRow(int row) {
        if ((row < 0) || (row > MAX_ROWS)) {
            return false;
        }
        else {
            for (int i = 0; i < ITEMS_PER_ROW; i++) {
                inventoryArray[row][i] = "";
                currentRow--;
            }
            return true;
        }
    }

    /**Creates a snack bar containing the error message on the current activity
     * 
     * @param v View, the view the error bar is to be displayed
     * @param error String, the error message
     */
    public void showErrorBar(View v, String error) {
        Snackbar.make(v, error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**Used as a check function in Inventory.java for row additions 
     * 
     * @param text String, any string to be checked if empty
     * @return true if null
     */
    public boolean isEmpty(String text) {
        if (text == null)
            return true;
        else
            return text.length() == 0;
    }

    /**Saves all the data in this object to memory. Creates a .txt file containing the object
     * data
     * 
     * @param c Context, the activity. Can use 'this'
     */

    public void saveInventoryData(Context c) {
        try {
            FileOutputStream fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(InventoryData.getArray());
            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Loads data from memory by reading the .txt file
     * 
     * @param c Context, the activity. Can use 'this'
     */
    public void loadInventoryData(Context c) {
        try {
            FileInputStream fin = c.openFileInput(filename);
            ObjectInputStream in = new ObjectInputStream(fin);
            updateArray((String[][]) in.readObject());
            in.close();
            fin.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**Used in WorkOrders.java - searches the inventory array for the description. If it is found,
     * returns a formatted String which is to be appended to a TextView. If not found, returns a
     * null string.
     * 
     * @param description String, the description to be found and formatted
     * @param quantityUsed String, amount used.
     * @return String, the row with formatting
     */
    public String getRowFormatted(String description, String quantityUsed) {
        if (description.equalsIgnoreCase(""))
            return "";
        
        int lineItem = 0;
        String currentDescription, lFormat = "%-8s%-4s%-25s%-10s%-10s%n", sRowTotal, sUnit;
        double rowTotal;
        DecimalFormat priceFormat = new DecimalFormat("#00.00");

        // find the line containing the description
        for (int i = 0; i < MAX_ROWS; i++) {
            currentDescription = inventoryArray[i][0];
            if (currentDescription.contains(description)) {
                lineItem = i;
                break;
            }
            else if (inventoryArray[i][0] == null)
                continue;
        }

        sUnit = "$" + priceFormat.format(Double.valueOf(inventoryArray[lineItem][2]));
        rowTotal = Integer.valueOf(quantityUsed) * Double.valueOf(inventoryArray[lineItem][2]);
        sRowTotal = "$" + priceFormat.format(rowTotal);
        // construct the string array to return
        return String.format(lFormat, inventoryArray[lineItem][1],
                quantityUsed, inventoryArray[lineItem][0], sUnit, sRowTotal);
    }

    // getters and setters

    public boolean updatePrice(int row, String price) {
        if (Double.valueOf(price) < 0) {
            return false;
        }
        else {
            inventoryArray[row][2] = price;
            return true;
        }
    }

    public boolean updateQuantity(int row, String newAmount) {
        if ((newAmount.matches("")) || (!newAmount.equalsIgnoreCase("0"))) {
            inventoryArray[row][3] = newAmount;
            return true;
        }
        else
            return false;
    }

    public boolean updatePartNum(int row, String newName) {
        if (newName == null) {
            return false;
        }
        else {
            inventoryArray[row][1] = newName;
            return true;
        }
    }

    public boolean updateDescription(int row, String newDescription) {
        if (newDescription != null) {
            inventoryArray[row][0] = newDescription;
            return true;
        }
        else
            return false;
    }

    public void updateArray(String[][] array) {
        inventoryArray = array;
    }

    public String getDescription(int row) {
        return inventoryArray[row][0];
    }

    public String getPartNum(int row) {
        return inventoryArray[row][1];
    }

    public String getPrice(int row) {
        return inventoryArray[row][2];
    }

    public String getQuantity(int row) {
        return inventoryArray[row][3];
    }

    public static String[][] getArray() {
        return inventoryArray;
    }

    public int getMaxRows() {
        return MAX_ROWS;
    }

    public int getRowID(String description) {
        int lineItem = 0;
        String currentDescription;

        // find the line containing the description
        for (int i = 0; i < MAX_ROWS; i++) {
            currentDescription = inventoryArray[i][0];
            if (currentDescription.contains(description)) {
                lineItem = i;
                break;
            }
            else if (inventoryArray[i][0] == null)
                continue;
        }
        return lineItem;
    }
}