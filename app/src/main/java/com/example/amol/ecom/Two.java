package com.example.amol.ecom;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class Two extends Fragment implements QuantityChangedListener, SearchView.OnQueryTextListener {

    private ArrayList<FoodItemList> foodItemLists = new ArrayList<>();
    public RecyclerView mRecyclerView;
    public RecyclerView.LayoutManager mlayoutManager;
    public ItemTwoAdapter itemTwoAdapter;
    public ArrayList<FoodItemList> itemForCheckout = null;
    private ArrayList<FoodItemList> selectedCartItems;

    //private static final String url_all_items = "http://10.0.2.2/xampp/ecom/Read_food_nonvej_items.php";
    private static String url_all_items = "https://www.food2all.in/apps/food2all/Read_Food_Item.php";
    private static final String TAG_ITEMS = "Item_Details";
    private static final String TAG_IMG_URL = "image_url";
    private static final String TAG_ITEM_NAME = "food_name";
    private static final String TAG_ITEM_DESCRIPTION = "food_description";
    private static final String TAG_PRICE = "food_price";
    public JSONArray items;
    public Toolbar toolbar;
    public TextView TextViewPrice;
    private LinearLayout linearLayout;
    private double total;
    private ArrayList<FoodItemList> itemsForAdapter;

    public Two() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        setHasOptionsMenu(true);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearlayoutfragmenttwo);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_proceed_two);
        TextViewPrice = (TextView) toolbar.findViewById(R.id.textView_Item_Price_two);

        SQLiteDatabase dbHelperForCheckout = new SQLiteDatabase(getContext());
        itemForCheckout = dbHelperForCheckout.getAllCartItem();
        dbHelperForCheckout.close();

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SQLiteDatabase database=new SQLiteDatabase(getActivity());
        itemsForAdapter=database.getAllItems();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_two);
        mlayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mlayoutManager);
        ItemTwoParsing();
        itemTwoAdapter = new ItemTwoAdapter(foodItemLists, getContext());
        itemTwoAdapter.setQuantityChangedListener(this);
        mRecyclerView.setAdapter(itemTwoAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        System.out.println("Inside onCreate option search..");

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                itemTwoAdapter.setFilter(foodItemLists);
                System.out.println("Inside onMenuItemActionExpand..");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
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


    private void ItemTwoParsing() {

        final ProgressDialog loading = ProgressDialog.show(getActivity(), null, "Fetching Menu", true, false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setContentView(R.layout.remove_border);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_all_items, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseDataforItemtwo(response);
                loading.dismiss();

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
                                    ItemTwoParsing();
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);


    }

    private void parseDataforItemtwo(JSONObject response) {
        System.out.println("Volley Success response" + response.toString());

        try {
            int success = response.getInt("success");

            if (success == 1) {

                items = response.getJSONArray(TAG_ITEMS);
                JSONObject jsonObject;

                for (int i = 0; i < items.length(); i++) {
                    FoodItemList itemList = new FoodItemList();
                  //  SQLiteDatabase database=new SQLiteDatabase(getActivity());
                    jsonObject = items.getJSONObject(i);

                    String itemimage = jsonObject.getString(TAG_IMG_URL);
                    itemList.setImageurl(itemimage);
                    System.out.println("volley item image==" + itemimage);

                    String itemname = jsonObject.getString(TAG_ITEM_NAME);
                    itemList.setItemName(itemname);
                    System.out.println("volley item image==" + itemimage);

                    String itemDescription = jsonObject.getString(TAG_ITEM_DESCRIPTION);
                    itemList.setItemdesciption(itemDescription);
                    System.out.println("volley item image==" + itemDescription);

                    String itemprice = jsonObject.getString(TAG_PRICE);
                    itemList.setItemprice(itemprice);
                    System.out.println("volley item image==" + itemprice);

                    foodItemLists.add(itemList);
                    //database.AddFoodItemsToDatabase(itemList);
                    itemTwoAdapter.notifyDataSetChanged();
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
        dbHelper.close();
        calculateTotalBill(selectedCartItems);
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
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<FoodItemList> filteredlist = filter(foodItemLists, newText);
        itemTwoAdapter.setFilter(filteredlist);
        System.out.println("Inside onTextChange..");
        return true;
    }

    private ArrayList<FoodItemList> filter(ArrayList<FoodItemList> foodItemLists, String newText) {
        newText = newText.toLowerCase();
        final ArrayList<FoodItemList> foodItemLists1 = new ArrayList<>();
        for (FoodItemList list : foodItemLists) {
            final String text = list.getItemName().toLowerCase();
            if (text.contains(newText)) {
                foodItemLists1.add(list);
            }
        }
        return foodItemLists1;
    }
}
