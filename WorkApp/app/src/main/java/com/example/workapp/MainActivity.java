package com.example.workapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnWorkOrder, btnInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnInventory = findViewById(R.id.btnInventory);
        btnWorkOrder = findViewById(R.id.btnWorkOrder);

        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInventory();
            }
        });

        btnWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWorkOrder();
            }
        });
    }

    /** goToInventory: goes to the inventory activity
     *
     */
    public void goToInventory() {
        Intent i = new Intent(getApplicationContext(), Inventory.class);
        startActivity(i);
    }

    /** goToWorkOrder: goes to the work order activity
     *
     */
    public void goToWorkOrder() {
        Intent i = new Intent(getApplicationContext(), WorkOrders.class);
        startActivity(i);
    }
}