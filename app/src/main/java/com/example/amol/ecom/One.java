package com.example.amol.ecom;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.amol.ecom.login.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class One extends Fragment implements SearchView.OnQueryTextListener, QuantityChangedListener {

    private ArrayList<FoodItemList> foodItemLists = new ArrayList<>();
    public RecyclerView mRecyclerView;
    public RecyclerView.LayoutManager mLayoutManager;
    private ItemOneAdapter itemOneAdapter;
    public ArrayList<FoodItemList> itemForCheckout = null;
    private ArrayList<FoodItemList> selectedCartItems;

    public LinearLayout linearLayout;

    // private static final String url_all_items = "http://10.0.2.2/xampp/ecom/Read_food_items.php";
    private static String url_all_items = "https://www.food2all.in/apps/food2all/Read_Food_Item.php";
    private static final String TAG_ITEMS = "Item_Details";
    private static final String TAG_IMG_URL = "image_url";
    private static final String TAG_ITEM_NAME = "food_name";
    private static final String TAG_ITEM_DESCRIPTION = "food_description";
    private static final String TAG_PRICE = "food_price";
    public JSONArray items;
    public Toolbar toolbar;
    public TextView TextViewPrice;
    private double total;
    private ArrayList<FoodItemList> itemsForAdapter = new ArrayList<>();


    public One() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);
        setHasOptionsMenu(true);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearlayoutfragmentone);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_proceed);
        TextViewPrice = (TextView) toolbar.findViewById(R.id.textView_Item_Price);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
                SessionManager.currentSelectedItemPosition = 0;

            }
        });

        setCheckOutLayout();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SQLiteDatabase database = new SQLiteDatabase(getActivity());
        itemsForAdapter = database.getAllItems();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ItemOneParsing();
        itemOneAdapter = new ItemOneAdapter(foodItemLists, getActivity());
        System.out.println("fooditemlists" + foodItemLists.size());
        itemOneAdapter.setQuantityChangedListener(this);
        mRecyclerView.setAdapter(itemOneAdapter);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        System.out.println("Inside onCreate Option Menu after search ");

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                itemOneAdapter.setFilter(foodItemLists);
                System.out.println("Inside onMenuItemActionExpand ");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                System.out.println("Inside onMenuItemActionCollapse ");
                return true;
            }
        });

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toggle_button).setVisible(false);
    }


    public void setCheckOutLayout() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SessionManager.ProccedFlag == 1) {
                            toolbar.setVisibility(View.VISIBLE);
                        } else {
                            toolbar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }, 5, 5);
    }


    public void ItemOneParsing() {

        final ProgressDialog loading = ProgressDialog.show(getActivity(), null, "Fetching Menu", true, false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setContentView(R.layout.remove_border);

        JsonObjectRequest fooditemreq = new JsonObjectRequest(Request.Method.GET, url_all_items, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                JsonItemOneParsing(response);

            }
        }, new Response.ErrorListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loading.dismiss();
                System.out.println("volley item error message response" + volleyError.getMessage());
                if (volleyError instanceof NoConnectionError) {
                    linearLayout.setBackground(getResources().getDrawable(R.mipmap.noconnect));
                    Snackbar snackbar = Snackbar.make(getView(), "Unable to Connect", Snackbar.LENGTH_SHORT)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ItemOneParsing();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Slow Internet connection, Please try again", Toast.LENGTH_SHORT).show();
                    System.out.println("volley item error message response" + volleyError.getMessage());
                }


            }
        });
        AppController.getInstance().addToRequestQueue(fooditemreq);

    }


    private void JsonItemOneParsing(JSONObject response) {
        System.out.println("Volley Success response" + response.toString());
        try {

            int success = response.getInt("success");

            if (success == 1) {
                System.out.println("volley item entering after success");
                items = response.getJSONArray((TAG_ITEMS));
                System.out.println("volley item entering success loop==" + items);

                JSONObject c = null;
                for (int i = 0; i < items.length(); i++) {

                    FoodItemList itemList = new FoodItemList();
                    //  SQLiteDatabase database=new SQLiteDatabase(getActivity());
                    c = items.getJSONObject(i);
                    System.out.println("volley item entering success loop==" + c);

                    String itemimage = c.getString(TAG_IMG_URL);
                    itemList.setImageurl(itemimage);
                    System.out.println("volley item image==" + itemimage);

                    String itemName = c.getString(TAG_ITEM_NAME);
                    itemList.setItemName(itemName);
                    System.out.println("volley item name==" + itemName);

                    String itemDesc = c.getString(TAG_ITEM_DESCRIPTION);
                    itemList.setItemdesciption(itemDesc);
                    System.out.println("volley item descroption==" + itemDesc);

                    String itemPrice = c.getString(TAG_PRICE);
                    itemList.setItemprice(itemPrice);
                    System.out.println("volley item price==" + itemPrice);

                    foodItemLists.add(itemList);
                    //database.AddFoodItemsToDatabase(itemList);
                    itemOneAdapter.notifyDataSetChanged();
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onQuantityChanged() {
        SQLiteDatabase dbHelper = new SQLiteDatabase(getActivity().getApplicationContext());
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
            // TextViewPrice.setText(String.valueOf(SessionManager.GrandTotalOfAllItems));
            System.out.println("Grand total is" + SessionManager.GrandTotalOfAllItems);
        }

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        System.out.println("Inside onQueryTextChange ");
        final ArrayList<FoodItemList> filtereditemlist = filter(foodItemLists, newText);
        itemOneAdapter.setFilter(filtereditemlist);
        return true;
    }

    private ArrayList<FoodItemList> filter(ArrayList<FoodItemList> ItemLists, String newText) {
        newText = newText.toLowerCase();
        System.out.println("Inside filter ");
        final ArrayList<FoodItemList> filtereditemlist = new ArrayList<>();
        for (FoodItemList List : ItemLists) {
            final String text = List.getItemName().toLowerCase();
            if (text.contains(newText)) {
                filtereditemlist.add(List);
            }
        }
        System.out.println("Inside onQueryTextChange " + filtereditemlist);
        return filtereditemlist;
    }
}


