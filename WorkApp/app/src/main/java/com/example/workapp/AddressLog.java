package com.example.workapp;

public class AddressLog {
    private static final int MAX_CITIES = 50;
    private static final int MAX_CALLS = 10;
    private static String[][] cityTaxes = new String[MAX_CITIES][2];
    private static String[][] addressLog = new String[MAX_CALLS][5];
    private int currentAddressPosition = 0;

    /**Creates the AddressLog class. It is the same for all instances. Its contents are predefined
     * for the purpose of this project.
     *
     */
    public AddressLog() {
        loadAddresses();
        loadTaxes();
    }

    private boolean addAddress(String street, String city, String zip,
                               String accessCodes, String problem) {
        if ((street == null) || (city == null) || (zip == null) ||
                (currentAddressPosition >= MAX_CALLS))
            return false;
        else {
            addressLog[currentAddressPosition][0] = street;
            addressLog[currentAddressPosition][1] = city;
            addressLog[currentAddressPosition][2] = zip;
            addressLog[currentAddressPosition][3] = accessCodes;
            addressLog[currentAddressPosition][4] = problem;
            currentAddressPosition++;
            return true;
        }
    }

    private void loadTaxes() {
        cityTaxes[0][0] = "Arcadia";
        cityTaxes[0][1] = "9.5";
        cityTaxes[1][0] = "La Canada";
        cityTaxes[1][1] = "10.5";
    }

    private void loadAddresses() {
        addAddress("123 main st", "Los Angeles", "91001", "",
                "S/O");
        addAddress("44 fano st", "Arcadia", "91006",
                "MC: PATR", "S/C");
        addAddress("1440 Arrow Highway", "Irwindale", "91764",
                "A: TH", "gate hit");

    }

    // this class only needs getters, no setters (for the purpose of this assignment)
    public String getAddress(int addressPosition) {
        String address = "";
        if (addressLog[addressPosition][0] == null)
            return address;

        address += addressLog[addressPosition][0] + "\n";
        address += addressLog[addressPosition][1];
        address += ", CA " + addressLog[addressPosition][2];
        return address;
    }

    public int getCallAmount() {
        return currentAddressPosition + 1;
    }

    public String getAccessCodes(int addressPosition) {
        return addressLog[addressPosition][3];
    }

    public String getProblem(int addressPosition) {
        return addressLog[addressPosition][4];
    }

    public String getTaxRate(int addressPosition) {
        String cityToFind = addressLog[addressPosition][1], taxRate = "";

        for (int i = 0; i < cityTaxes.length; i++) {
            String city = cityTaxes[i][0];
            if (city == null)
                continue;
            if (city.equalsIgnoreCase(cityToFind)) {
                taxRate = cityTaxes[i][1];
                break;
            }
        }

        if (taxRate.equalsIgnoreCase(""))
            return "8.5";
        else
            return taxRate;
    }

    public String getFileName(int addressPosition) {
        return addressLog[addressPosition][0] + " " + addressLog[addressPosition][1];
    }
}

