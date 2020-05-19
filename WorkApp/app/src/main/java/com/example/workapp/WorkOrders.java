package com.example.workapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class WorkOrders extends AppCompatActivity {
    private int currentWorkOrder = 0;
    private static final double laborRate = 95.00;
    private double subTotal = 0.0, taxAmount = 0.0, laborTotal = 0.0, total = 0.0;
    TextView tvPartsUsed, tvAddress, tvJobInfo, tvArrived, tvDeparted, tvDate, tvLaborDisplay,
            tvSubTotal, tvTax, tvTotal, tvTaxText;
    EditText etWorkDone, etQuantityUsed;
    Button btnPrevious, btnNext, btnClock, btnAdd, btnFinish;
    AutoCompleteTextView etPartsUsed;

    private InventoryData theData;
    private AddressLog addresses;
    private WorkOrderData[] todaysWorkOrders;
    private LocalDateTime dateTimeStart, dateTimeEnd;
    DecimalFormat priceFormat = new DecimalFormat("###0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_orders);

        initializeActivity();

        theData = new InventoryData();
        theData.loadInventoryData(getBaseContext());
        addresses = new AddressLog();
        createWorkOrderList();
        createCurrentWorkOrder(currentWorkOrder);

        etPartsUsed.setAdapter(getDescriptions(this));
        etPartsUsed.setThreshold(1);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWorkOrder == 0)
                    return;
                else if ((currentWorkOrder > 0) && (currentWorkOrder <= addresses.getCallAmount())) {
                    saveCurrentWorkOrder(currentWorkOrder);
                    currentWorkOrder--;
                    createCurrentWorkOrder(currentWorkOrder);
                    btnNext.setAlpha(1.0f);
                    if (currentWorkOrder == 0)
                        btnPrevious.setAlpha(0);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWorkOrder < addresses.getCallAmount()) {
                    btnPrevious.setAlpha(1.0f);
                    saveCurrentWorkOrder(currentWorkOrder);
                    currentWorkOrder++;
                    createCurrentWorkOrder(currentWorkOrder);
                    if (currentWorkOrder == (addresses.getCallAmount()- 1))
                        btnNext.setAlpha(0);
                    return;
                }
                else {
                    btnNext.setAlpha(0);
                    return;
                    }
                }
        });

        btnClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClock.getText().toString().equalsIgnoreCase(getString(R.string.startTime))) {
                    jobClock(getString(R.string.start));
                    btnClock.setText(getString(R.string.StopTime));
                }
                else {
                    jobClock(getString(R.string.stop));
                    btnClock.setAlpha(0);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etPartsUsed.getText().toString().trim();
                String quantity = etQuantityUsed.getText().toString().trim();
                int rowOfItem = theData.getRowID(description);
                tvPartsUsed.append(theData.getRowFormatted(description, quantity));
                etPartsUsed.setText("");
                etQuantityUsed.setText("");
                String price = theData.getPrice(rowOfItem);
                tvSubTotal.setText(updateSubTotal(price, quantity));
                tvTax.setText(updateTax(currentWorkOrder));
                tvTotal.setText(updateTotal());
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentWorkOrder(currentWorkOrder);
                commitWorkOrder(currentWorkOrder);
                Toast completed = Toast.makeText(getApplicationContext(), getString(R.string.savedToast),
                        Toast.LENGTH_LONG);
                completed.show();
            }
        });
    }

    /* helper function to initialize the activity's interactables.
     *
     */
    private void initializeActivity() {
        etPartsUsed = findViewById(R.id.etPartsUsed);
        etQuantityUsed = findViewById(R.id.etQuantityUsed);
        etWorkDone = findViewById(R.id.etWorkDone);
        tvPartsUsed = findViewById(R.id.tvPartsUsed);
        tvAddress = findViewById(R.id.tvAddress);
        tvJobInfo = findViewById(R.id.tvJobInfo);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnClock = findViewById(R.id.btnClock);
        btnAdd = findViewById(R.id.btnAdd);
        btnFinish = findViewById(R.id.btnFinish);
        tvArrived = findViewById(R.id.tvArrived);
        tvDeparted = findViewById(R.id.tvDeparted);
        tvDate = findViewById(R.id.tvDate);
        tvLaborDisplay = findViewById(R.id.tvLaborDisplay);
        tvSubTotal = findViewById(R.id.tvSubTotalDisplay);
        tvTax = findViewById(R.id.tvTaxDisplay);
        tvTotal = findViewById(R.id.tvTotalDisplay);
        tvTaxText = findViewById(R.id.tvTaxText);

        btnPrevious.setAlpha(0);

        tvPartsUsed.setText(String.format(getString(R.string.workOrderPartFormat),
                getString(R.string.partNum), getString(R.string.qty),
                getString(R.string.description), getString(R.string.unit),
                getString(R.string.total)));
        tvPartsUsed.setMovementMethod(new ScrollingMovementMethod());
        etWorkDone.setMovementMethod(new ScrollingMovementMethod());
    }

    /**jobClock takes in either "start" or "stop" and depending on the string passed,
     * augments the arrival, departed, and date fields.
     */
    private void jobClock(String action) {
        if (action.equalsIgnoreCase(getString(R.string.start))) {
            dateTimeStart = LocalDateTime.now();
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(getString(R.string.formatDate));
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(getString(R.string.formatTime));
            String date = dateTimeStart.format(formatterDate);
            String arrived = dateTimeStart.format(formatterTime);
            tvArrived.append("\n");
            tvArrived.append(arrived);
            tvDate.append("\n");
            tvDate.append(date);
        }
        else {
            dateTimeEnd = LocalDateTime.now();
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(getString(R.string.formatTime));
            String departed = dateTimeEnd.format(formatterTime);
            tvDeparted.append("\n");
            tvDeparted.append(departed);
            btnClock.setAlpha(0);

            calculateLabor(dateTimeStart, dateTimeEnd);
            tvTotal.setText(updateTotal());
            todaysWorkOrders[currentWorkOrder].setIsTimeFinished(true);
        }
    }

    /**getDescriptions is used for the autocomplete field on the activity.
     *
     * @param c: the activity it is being called from.
     * @return: an ArrayAdapter
     */
    private ArrayAdapter<String> getDescriptions(Context c) {
        String[] descriptions = new String[theData.getMaxRows()];

        for (int i = 0; i < theData.getMaxRows(); i++) {
            if (theData.getDescription(i).equalsIgnoreCase(""))
                continue;
            else
                descriptions[i] = theData.getDescription(i);
        }

        return new ArrayAdapter<String>(c, android.R.layout.simple_dropdown_item_1line,
                descriptions);
    }

    /**createWorkOrderList initializes todaysWorkOrders array
     *
     */
    private void createWorkOrderList() {
        int amountOfCalls = addresses.getCallAmount();

        todaysWorkOrders = new WorkOrderData[amountOfCalls];

        for (int i = 0; i < amountOfCalls; i++) {
            todaysWorkOrders[i] = new WorkOrderData(addresses.getAddress(i),
                    addresses.getAccessCodes(i));
        }
    }

    /**createCurrentWorkOrder updates all related fields on the activity to display the
     * current work order.
     *
     * @param currentWorkOrder an integer index of the current work order
     */
    private void createCurrentWorkOrder(int currentWorkOrder) {
        updateAddress(currentWorkOrder);
        updateJobInfo(currentWorkOrder);
        loadCurrentWorkOrder(currentWorkOrder);
    }

    /**Saves any input data of the current work order to its class variables.
     *
     * @param currentWorkOrder int, the index of the current work order
     */
    private void saveCurrentWorkOrder(int currentWorkOrder) {
        todaysWorkOrders[currentWorkOrder].setArrival(tvArrived.getText().toString());
        todaysWorkOrders[currentWorkOrder].setDeparture(tvDeparted.getText().toString());
        todaysWorkOrders[currentWorkOrder].setDate(tvDate.getText().toString());
        todaysWorkOrders[currentWorkOrder].setWorkDone(etWorkDone.getText().toString());
        todaysWorkOrders[currentWorkOrder].setPartsUsed(tvPartsUsed.getText().toString());
        todaysWorkOrders[currentWorkOrder].setPrices(subTotal, laborTotal, taxAmount, total);
        subTotal = 0.0;
        laborTotal = 0.0;
        taxAmount = 0.0;
        total = 0.0;
    }

    /**Retrives the class variables and inserts them with formatting into their fields
     *
     * @param currentWorkOrder int, index of the work order
     */
    private void loadCurrentWorkOrder(int currentWorkOrder) {
        if (todaysWorkOrders[currentWorkOrder].getWorkDone().equalsIgnoreCase("")) {
            etWorkDone.setText("");
            etWorkDone.setHint(getString(R.string.workDoneHint));
        }
        else
            etWorkDone.setText(todaysWorkOrders[currentWorkOrder].getWorkDone());

        if (todaysWorkOrders[currentWorkOrder].getPartsUsed().equalsIgnoreCase(""))
            tvPartsUsed.setText(String.format(getString(R.string.workOrderPartFormat),
                    getString(R.string.partNum), getString(R.string.qty),
                    getString(R.string.description), getString(R.string.unit),
                    getString(R.string.total)));
        else
            tvPartsUsed.setText(todaysWorkOrders[currentWorkOrder].getPartsUsed());

        if (todaysWorkOrders[currentWorkOrder].getArrival().equalsIgnoreCase(""))
            tvArrived.setText(getString(R.string.arrivedHeader));
        else
            tvArrived.setText(todaysWorkOrders[currentWorkOrder].getArrival());

        if (todaysWorkOrders[currentWorkOrder].getDeparture().equalsIgnoreCase(""))
            tvDeparted.setText(getString(R.string.departedHeader));
        else
            tvDeparted.setText(todaysWorkOrders[currentWorkOrder].getDeparture());

        if (todaysWorkOrders[currentWorkOrder].getDate().equalsIgnoreCase(""))
            tvDate.setText(getString(R.string.dateHeader));
        else
            tvDate.setText(todaysWorkOrders[currentWorkOrder].getDate());


        double[] prices = todaysWorkOrders[currentWorkOrder].getPrices();
        if (prices[0] == 0) {
            tvSubTotal.setText("");
            tvTax.setText("");
            tvTaxText.setText("Tax:");
            tvLaborDisplay.setText("");
            tvTotal.setText("");
        }
        else {
            subTotal = prices[0];
            laborTotal = prices[1];
            taxAmount = prices[2];
            total = prices[3];
            tvSubTotal.setText("$" + priceFormat.format(subTotal));
            tvLaborDisplay.setText("$" + priceFormat.format(laborTotal));
            tvTax.setText(updateTax(currentWorkOrder));
            tvTotal.setText("$" + priceFormat.format(total));
        }

        if (!todaysWorkOrders[currentWorkOrder].getIsTimeFinished()) {
            btnClock.setText("start time");
            btnClock.setAlpha(1.0f);
        }
        else
            btnClock.setAlpha(0);
    }

    /**Creates the file name for the current work order and calls the commitWorkOrder method
     * to create a txt file of the class variables
     *
     * @param currentWorkOrder int, index of current work order
     */
    private void commitWorkOrder(int currentWorkOrder) {
        String filename = addresses.getFileName(currentWorkOrder) + currentWorkOrder +
                getString(R.string.fileNameSuffix);
        todaysWorkOrders[currentWorkOrder].commitWorkOrder(this, filename);
    }

    // the following are 'setters' in a vague respect. each function updates the currently
    // displayed fields

    private void updateAddress(int currentWorkOrder) {
        if (todaysWorkOrders[currentWorkOrder].getAddress().equalsIgnoreCase(""))
            tvAddress.setText(getString(R.string.emptyAddress));
        else
            tvAddress.setText(todaysWorkOrders[currentWorkOrder].getAddress());
    }

    private void updateJobInfo(int currentWorkOrder) {
        tvJobInfo.setText(getString(R.string.accessCodesHeader));

        if (todaysWorkOrders[currentWorkOrder].getAccessCodes() == null)
            tvJobInfo.append(getString(R.string.emptyAccessCodes));
        else
            tvJobInfo.append(todaysWorkOrders[currentWorkOrder].getAccessCodes());

        tvJobInfo.append(getString(R.string.jobProblem));

        if (todaysWorkOrders[currentWorkOrder].getProblem(currentWorkOrder) == null)
            tvJobInfo.append(getString(R.string.noProblem));
        else
            tvJobInfo.append(todaysWorkOrders[currentWorkOrder].getProblem(currentWorkOrder));
    }

    /**Calculates the total labor cost of the job. Called by jobClock(). Updates the labor and
     * total fields
     *
     * @param start LocalDateTime, start time
     * @param end LocalDateTime, end time
     */
    private void calculateLabor(LocalDateTime start, LocalDateTime end) {
        long minutes;
        String sLabor = "";
        minutes = ChronoUnit.MINUTES.between(start, end);

        long hours = minutes / 60;  // find how many hours spent on job
        minutes -= 60 * hours;  // deduct the hours in minutes from total minutes

        if (hours > 0) {  // for each full hour, assign to total
            laborTotal = hours * laborRate;

            if (minutes > 30)  // if the remaining minutes is over half hour, add full hour
                laborTotal += laborRate;
            else  // else add half hour rate
                laborTotal += laborRate / 2;
        }
        else
            laborTotal = laborRate;
        sLabor = "$" + priceFormat.format(laborTotal);
        tvLaborDisplay.setText(sLabor);
    }

    private String updateSubTotal(String price, String quantity) {
        double dPrice = Double.valueOf(price);
        int iQuantity = Integer.valueOf(quantity);
        double rowTotal = dPrice * iQuantity;
        subTotal += rowTotal;
        return "$" + priceFormat.format(subTotal);
    }

    private String updateTax(int currentWorkOrder) {
        Double taxRate = Double.valueOf(addresses.getTaxRate(currentWorkOrder));
        taxAmount = (taxRate/100) * subTotal;

        tvTaxText.setText("Tax (" + addresses.getTaxRate(currentWorkOrder) + "%):");
        return "$" + priceFormat.format(taxAmount);
    }

    private String updateTotal() {
        return priceFormat.format(subTotal + taxAmount + laborTotal + total);
    }
}


