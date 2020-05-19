package com.example.workapp;

import android.content.Context;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorkOrderData {
    private AddressLog todaysCalls = new AddressLog();
    private String partsUsed = "",
            address = "",
            accessCodes = "",
            workDone = "",
            arrival = "",
            departure = "",
            date = "";
    private double[] prices = new double[4]; // four dollar amount fields
    private boolean isTimeFinished = false;

    public WorkOrderData(String address, String accessCodes) {
        this.address = address;
        this.accessCodes = accessCodes;

    }

    /**commitWorkOrder creates a basic txt file containing critical information of the work order
     *
     * @param c the context of this function (activity)
     * @param filename name of the file to save to
     */
    public void commitWorkOrder(Context c, String filename) {
        try {
            FileOutputStream fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(address.getBytes());
            fos.write(workDone.getBytes());
            fos.write(partsUsed.getBytes());
            for (double price: prices)
                fos.write(String.valueOf(price).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // getters and setters

    public String getAddress() {
        return address;
    }

    public String getAccessCodes() {
        return accessCodes;
    }

    public String getWorkDone() {
        return workDone;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDate() {
        return date;
    }

    public String getPartsUsed() {
        return partsUsed;
    }

    public String getProblem(int currentWorkOrder) {
        return todaysCalls.getProblem(currentWorkOrder);
    }

    public double[] getPrices() {
        return prices;
    }

    public boolean getIsTimeFinished() {
        return isTimeFinished;
    }

    public boolean setWorkDone(String workCompleted) {
        this.workDone = workCompleted;
        return true;
    }

    public boolean setPartsUsed(String partsUsed) {
        this.partsUsed = partsUsed;
        return true;
    }

    public boolean setArrival(String arrival) {
        this.arrival = arrival;
        return true;
    }

    public boolean setDeparture(String departure) {
        this.departure = departure;
        return true;
    }

    public boolean setDate(String date) {
        this.date = date;
        return true;
    }

    public boolean setPrices(double subTotal, double labor, double tax, double total) {
        prices[0] = subTotal;
        prices[1] = labor;
        prices[2] = tax;
        prices[3] = total;
        return true;
    }

    public boolean setIsTimeFinished(boolean isTimeCompleted) {
        isTimeFinished = isTimeCompleted;
        return true;
    }
}
