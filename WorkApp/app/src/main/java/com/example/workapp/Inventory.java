package com.example.workapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Inventory extends AppCompatActivity {

    private static boolean isInventoryCreated = false;
    private InventoryData theData;

    Button btnUpdate, btnAdd, btnRemove, btnSearch;
    EditText etSearch, etRow, etPartNum, etDescription, etQuantity, etPrice;
    TextView tvInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        etRow = findViewById(R.id.etRow);
        etPartNum = findViewById(R.id.etPartNum);
        etDescription = findViewById(R.id.etDescription);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        tvInventory = findViewById(R.id.tvInventory);

        //initialize inventory on first creation. Otherwise open file on phone memory
        if (!isInventoryCreated) {
            theData = new InventoryData();
            isInventoryCreated = true;
        }
        else
            theData = new InventoryData();
            theData.loadInventoryData(getBaseContext());
            updateDisplayInventory();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sNewPartnum = etPartNum.getText().toString().trim();
                String sNewDescription = etDescription.getText().toString().trim();
                String sNewPrice = etPrice.getText().toString().trim();
                String sNewQuantity = etQuantity.getText().toString().trim();
                String sRow = etRow.getText().toString().trim();
                int row = Integer.valueOf(sRow) - 1;

                // if the current row's description is not "", then it is filled, do not add
                if (!theData.isEmpty(theData.getDescription(row))) {
                    theData.showErrorBar(v, "Use Update button.");
                    etRow.setText("");
                    return;
                }

                if ((!theData.isEmpty(sNewDescription)) && (!theData.isEmpty(sNewPartnum)) &&
                        (!theData.isEmpty(sNewPrice)) && (!theData.isEmpty(sNewQuantity))) {
                    theData.addRow(row, sNewDescription, sNewPartnum, sNewPrice, sNewQuantity);
                    updateDisplayInventory();
                    etPartNum.setText("");
                    etDescription.setText("");
                    etPrice.setText("");
                    etQuantity.setText("");
                    etRow.setText("");
                }
                else {
                    theData.showErrorBar(v, "Missing information.");
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sNewPartnum = etPartNum.getText().toString().trim();
                String sNewDescription = etDescription.getText().toString().trim();
                String sNewPrice = etPrice.getText().toString().trim();
                String sNewQuantity = etQuantity.getText().toString().trim();
                String sRow = etRow.getText().toString().trim();

                boolean isUpdateGood = false;

                if (theData.isEmpty(sRow)) {
                    theData.showErrorBar(v, "Which row to update?");
                    return;
                }
                else {
                    int row = Integer.valueOf(sRow) - 1;
                    if ((!sNewDescription.equalsIgnoreCase(theData.getDescription(row))) &&
                            (!theData.isEmpty(sNewDescription)))
                        isUpdateGood = theData.updateDescription(row, sNewDescription);

                    if ((!sNewQuantity.equalsIgnoreCase(theData.getQuantity(row))) &&
                            (!theData.isEmpty(sNewQuantity)))
                        isUpdateGood = theData.updateQuantity(row, sNewQuantity);

                    if ((!sNewPrice.equalsIgnoreCase(theData.getPrice(row))) &&
                            (!theData.isEmpty(sNewPrice)))
                        isUpdateGood = theData.updatePrice(row, sNewPrice);

                    if ((!sNewPartnum.equalsIgnoreCase(theData.getPartNum(row))) &&
                            (!theData.isEmpty(sNewPartnum)))
                        isUpdateGood = theData.updatePartNum(row, sNewPartnum);
                }

                if (isUpdateGood) {
                    updateDisplayInventory();
                    etPartNum.setText("");
                    etDescription.setText("");
                    etPrice.setText("");
                    etQuantity.setText("");
                    etRow.setText("");
                }
                else
                    theData.showErrorBar(v, "Wrong or no new information.");
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sRow = etRow.getText().toString().trim();

                if (sRow.matches("")) {
                    Snackbar.make(v, "No row to delete.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    int row = Integer.valueOf(sRow) - 1;
                    boolean isDeleteGood = theData.removeRow(row);
                    if (!isDeleteGood)
                        theData.showErrorBar(v, "Invalid row.");
                    else {
                        updateDisplayInventory();
                        etPartNum.setText("");
                        etDescription.setText("");
                        etPrice.setText("");
                        etQuantity.setText("");
                        etRow.setText("");
                    }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = etSearch.getText().toString().trim();
                searchByDescription(searchString);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        theData.saveInventoryData(getBaseContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        theData.loadInventoryData(getBaseContext());
        updateDisplayInventory();
    }

    /**Saves a boolean to SharedPreferences. Controls whether or not to create a new
     * InventoryData object or to load the one already created. After initial creation, always
     * true
     *
     */
    public void saveData() {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isInventoryCreated", isInventoryCreated);
        editor.commit();
    }

    /**Loads the boolean from SharedPreferences.
     *
     */
    public void loadData() {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isInventoryCreated = sp.getBoolean("isInventoryCreated", isInventoryCreated);
    }

    /**Redraws the Inventory view
     *
     */
    public void updateDisplayInventory() {
        int row;
        String sDescription, sPartNum, sPrice, sQuant,
                hFormat = getString(R.string.inventoryTableFormat),
                lFormat = getString(R.string.inventoryTableLineFormat),
                hNum = getString(R.string.row), hDesc = getString(R.string.description),
                hPrice = getString(R.string.price), hQuant = getString(R.string.quantity),
                hPart = getString(R.string.partNum);

        tvInventory.setText("");
        tvInventory.setText(String.format(hFormat, hNum, hDesc, hPart, hPrice, hQuant));
        for (int i = 0; i < theData.getMaxRows(); i++) {
            if (theData.isEmpty(theData.getDescription(i)))  // empty rows skips
                continue;

            row = i + 1;  // convert index to row number
            sDescription = theData.getDescription(i);
            sPartNum = theData.getPartNum(i);
            sPrice = theData.getPrice(i);
            sQuant = theData.getQuantity(i);
            tvInventory.append(String.format(lFormat, row, sDescription, sPartNum, sPrice, sQuant));
        }
    }

    /**Called when the search button is pressed. Changes the text on the button to 'clear search'
     * to reset the search field
     *
     * @param subString String, the description
     */
    public void searchByDescription(String subString) {
        String itemDescription, sDescription, sPartNum, sPrice, sQuant;
        String hFormat = getString(R.string.inventoryTableFormat),
                lFormat = getString(R.string.inventoryTableLineFormat),
                hNum = getString(R.string.row), hDesc = getString(R.string.description),
                hPrice = getString(R.string.price), hQuant = getString(R.string.quantity),
                hPart = getString(R.string.partNum);
        CharSequence sub = subString.toLowerCase();
        int row;

        if (theData.isEmpty(subString)) {  // if the string passed is empty, redraw the table
            updateDisplayInventory();
            btnSearch.setText(getString(R.string.search));
        }
        else {
            tvInventory.setText(String.format(hFormat, hNum, hDesc, hPart, hPrice, hQuant));
            for (int i = 0; i < theData.getMaxRows(); i++) {
                if (theData.isEmpty(theData.getDescription(i)))
                    continue;
                else {
                    itemDescription = theData.getDescription(i).toLowerCase();
                    if (itemDescription.contains(sub)) {  // print line
                        sDescription = theData.getDescription(i);
                        sPartNum = theData.getPartNum(i);
                        sPrice = theData.getPrice(i);
                        sQuant = theData.getQuantity(i);
                        row = i + 1;
                        tvInventory.append(String.format(lFormat, row, sDescription,
                                sPartNum, sPrice, sQuant));
                    }
                }
            }
            etSearch.setText("");  // clear search field
            btnSearch.setText(getString(R.string.clearSearch));
        }
    }
}
