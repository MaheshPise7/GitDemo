package com.example.amol.ecom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.amol.ecom.login.SessionManager;

import java.util.ArrayList;
import java.util.Iterator;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, QuantityChangedListener {

    public Button btnaddMoreItems, btnCheckOut;
    public TextView textviewgrandprice;
    ArrayList<FoodItemList> items = new ArrayList<>();
    public ArrayList<FoodItemList> selectedCartItems;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    CartAdapter cartAdapter;

    private double grandTotal = 0;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCheckOut);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_final_selected_items);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnaddMoreItems = (Button) findViewById(R.id.btn_addMoreItems);
        btnaddMoreItems.setOnClickListener(this);
        btnCheckOut = (Button) findViewById(R.id.btn_CheckOut);
        btnCheckOut.setOnClickListener(this);
        textviewgrandprice = (TextView) findViewById(R.id.textview_grand_total);
        textviewgrandprice.setText(String.valueOf(SessionManager.GrandTotalOfAllItems));

        SQLiteDatabase database = new SQLiteDatabase(CartActivity.this);
        items = database.getAllCartItem();
        database.close();

        cartAdapter = new CartAdapter(items, getApplicationContext());
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.setQuantityChangedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addMoreItems:
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_CheckOut:
                Intent i = new Intent(CartActivity.this, PaymentMethod.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onQuantityChanged() {

        SQLiteDatabase dbHelper = new SQLiteDatabase(this.getApplicationContext());
        selectedCartItems = dbHelper.getAllCartItem();
        System.out.println("Grand total quantity changed" + selectedCartItems.size());
        calculateTotalBill(selectedCartItems);
        dbHelper.close();
    }

    private void calculateTotalBill(ArrayList<FoodItemList> selectedCartItems) {
        Iterator<FoodItemList> iterator = selectedCartItems.iterator();
        total = 0;
        while (iterator.hasNext()) {
            FoodItemList list = iterator.next();
            total += (Integer.valueOf(list.getItemprice()) * Integer.valueOf(list.getItemquantity()));
            SessionManager.cartNotificationTotal = total;
            System.out.println("Grand total is" + SessionManager.GrandTotalOfAllItems);
        }
        textviewgrandprice.setText(String.valueOf(total + ""));


    }


    @Override
    public void updateNotificationsBadge(double grandTotal) {

    }

    @Override
    public void updateTotalQty(int totalQty) {

    }

    @Override
    public void onBillLayoutClicked(FoodItemList foodItemList) {

    }
}
