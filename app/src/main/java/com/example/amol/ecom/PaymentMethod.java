package com.example.amol.ecom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.amol.ecom.login.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentMethod extends AppCompatActivity implements View.OnClickListener {

    public Button btn_cashondelivery, btn_onlinepayment;
    public TextView text_FinalTotalAmount;
    private TextView textViewTandC;
    private View promptView;
    private TextView TermsAndCondition;
    public String payment_method, paid_status, confirmed_status, delivery_status;

    private static final String ORDER_PRIMARY_FEEDBACK_ID = "Returned_OrderId_Response";
    private static final String TAG_ORDER_ID = "Order_Id_From_Server";

    public ArrayList<ProfileEntity> current_cust_profile_List = new ArrayList<ProfileEntity>();
    public ProfileEntity current_cust_profile = new ProfileEntity();
    private String customer_id;
    ProgressDialog dialog;

    JSONObject json = null;
    public static int successs;
    JSONArray orderid = null;
    public ArrayList<FoodItemList> items = null;
    private String order_id_secondary, customer_id_secondary;
    private String item_name_secondary, item_price_secondary, item_qty_secondary;
    private String url_sendOrderFeedBack = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_Payment);
        setSupportActionBar(toolbar);
        textViewTandC = (TextView) findViewById(R.id.textview_TermsAndConditions);
        btn_cashondelivery = (Button) findViewById(R.id.btn_CashOnDelivery);
        btn_onlinepayment = (Button) findViewById(R.id.btn_OnlinePayment);
        btn_cashondelivery.setOnClickListener(this);
        btn_onlinepayment.setOnClickListener(this);
        text_FinalTotalAmount = (TextView) findViewById(R.id.text_finalamount);
        text_FinalTotalAmount.setText(String.valueOf(SessionManager.cartNotificationTotal));

        dialog=new ProgressDialog(this);
        dialog.setMessage("Placing order...");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Make Payment");
        SpannableTextview();
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
            case R.id.btn_CashOnDelivery:
                dialog.show();
                System.out.println("in payment method in side clicked on Cash on Delivery ");
                CashOnDelivery();
                SQLiteDatabase database = new SQLiteDatabase(PaymentMethod.this);
                database.makeEmpty("food_table");
                SessionManager.ProccedFlag = 0;
                SessionManager.GrandTotalOfAllItems = 0;
                Intent intent = new Intent(PaymentMethod.this, ThanxScreen.class);
                startActivity(intent);
                break;

            case R.id.btn_OnlinePayment:
                SQLiteDatabase database1 = new SQLiteDatabase(PaymentMethod.this);
                database1.makeEmpty("food_table");
                SessionManager.ProccedFlag = 0;
                SessionManager.GrandTotalOfAllItems = 0;
                Intent i = new Intent(PaymentMethod.this, ThanxScreen.class);
                startActivity(i);
                break;
        }
    }

    private void CashOnDelivery() {

        payment_method = "On Delivery";
        paid_status = "false";
        confirmed_status = "false";
        delivery_status = "false";
        SaveOrderPrimaryDetails();
    }


    private void SaveOrderPrimaryDetails() {

        System.out.println("in payment method in side SaveOrderPrimaryDetails ");
        final OrderIds current_order_id = new OrderIds();
        SQLiteDatabase database = new SQLiteDatabase(PaymentMethod.this);
        current_cust_profile_List = database.getCustomerProfile(SessionManager.MobileNumber);
        System.out.println("in payment method in side SaveOrderPrimaryDetails current_cust_profile_List " + current_cust_profile_List.size());

        for (int i = 0; i < current_cust_profile_List.size(); i++) {
            current_cust_profile = current_cust_profile_List.get(i);
        }
        customer_id = String.valueOf(current_cust_profile.getCustId());

        System.out.println("in payment method in side SaveOrderPrimaryDetails customer_id " + customer_id);

        String url_SaveCustomerOrderPrimay = "http://10.0.2.2/xampp/ecom/save_primary_order_details.php";

        StringRequest request = new StringRequest(Request.Method.POST, url_SaveCustomerOrderPrimay, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveOrderSecondaryDetails();
                System.out.println("in payment method in side SaveOrderPrimaryDetails on Response " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    if (success == 1) {
                        System.out.println("in payment method in side SaveOrderPrimaryDetails on success");
                        SQLiteDatabase database = new SQLiteDatabase(PaymentMethod.this);
                        orderid = json.getJSONArray(ORDER_PRIMARY_FEEDBACK_ID);
                        System.out.println("in payment method in side SaveOrderPrimaryDetails on succes order id" + orderid);
                        for (int i = 0; i < orderid.length(); i++) {
                            JSONObject c = orderid.getJSONObject(i);
                            String OrderId = c.getString(TAG_ORDER_ID);
                            current_order_id.setOrder_id(Integer.parseInt(OrderId));
                            System.out.println("in payment method in side SaveOrderPrimaryDetails on order id " + current_order_id);
                            current_order_id.setCustomer_id(Integer.parseInt(OrderId));
                            System.out.println("in payment method in side SaveOrderPrimaryDetails on set customer id ");
                            database.SaveOrderId(current_order_id);
                        }
                        database.close();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("in payment in SaveOrderPrimaryDetails on error Response" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("customer_id", customer_id);
                map.put("order_total_amount", String.valueOf(text_FinalTotalAmount));
                map.put("order_payment_method", payment_method);
                map.put("order_paid_status", paid_status);
                map.put("order_confirmed_status", confirmed_status);
                map.put("order_delivery_status", delivery_status);
                return map;
            }

        };

        AppController.getInstance().addToRequestQueue(request);
    }


    private void saveOrderSecondaryDetails() {

        System.out.println("in payment method in side saveOrderSecondaryDetails");
        OrderIds recentOrder_id_from_Db = new OrderIds();
        OrderIds helperEntity = new OrderIds();
        helperEntity.setCustomer_id(Integer.parseInt(customer_id));

        SQLiteDatabase database = new SQLiteDatabase(PaymentMethod.this);

        ArrayList<FoodItemList> itemLists=new ArrayList<>();

        itemLists = database.getAllCartItem();

        System.out.println("in payment method in side saveOrderSecondaryDetails items list " + itemLists.size());

        for (int i = 0; i < itemLists.size(); i++) {

            item_name_secondary = itemLists.get(i).getItemName();
            System.out.println("in payment method in side saveOrderSecondaryDetails item name " + item_name_secondary);
            item_price_secondary = Float.toString(Float.parseFloat(itemLists.get(i).getItemprice()));
            System.out.println("in payment method in side saveOrderSecondaryDetails item price " + item_price_secondary);
            item_qty_secondary = Integer.toString(itemLists.get(i).getItemquantity());
            System.out.println("in payment method in side saveOrderSecondaryDetails item quantity " + item_qty_secondary);
            confirmed_status = "true";

        }
        recentOrder_id_from_Db = database.getRecentOrderId(helperEntity);
        order_id_secondary = Integer.toString(recentOrder_id_from_Db.getOrder_id());
        System.out.println("in payment method in side saveOrderSecondaryDetails order_id_secondary " + order_id_secondary);
        customer_id_secondary = Integer.toString(recentOrder_id_from_Db.getCustomer_id());
        database.close();

        String url_SaveCustomerOrderSecondary ="http://10.0.2.2/xampp/ecom/save_secondary_order_details.php";

        StringRequest request = new StringRequest(Request.Method.POST, url_SaveCustomerOrderSecondary, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                System.out.println("in payment method in side saveOrderSecondaryDetails on Response");

                try {
                    JSONObject object = new JSONObject(response);
                    int success = object.getInt("success");
                    if (success == 1) {

                        System.out.println("in payment method in side saveOrderSecondaryDetails on success");
                        SQLiteDatabase dbHelper = new SQLiteDatabase(PaymentMethod.this);
                        orderid = json.getJSONArray(ORDER_PRIMARY_FEEDBACK_ID);
                        dbHelper.close();
                        //           sendFeedBacktoCustomer();
                        Intent intent = new Intent(PaymentMethod.this, ThanxScreen.class);
                        startActivity(intent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("order_id_secondary", order_id_secondary);
                map.put("customer_id_secondary", customer_id_secondary);
                map.put("item_name_secondary", item_name_secondary);
                map.put("item_price_secondary", item_price_secondary);
                map.put("item_qty_secondary", item_qty_secondary);
                map.put("confirm_status_secondary", confirmed_status);
                map.put("current_date", getDate());
                map.put("current_time", getTime());
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void sendFeedBacktoCustomer() {

        int succ = 0;
        int successflag;
        final String custid = null, orderid;
        OrderIds current_order_id = new OrderIds();

        SQLiteDatabase help_for_get_cust_id = new SQLiteDatabase(PaymentMethod.this);
        current_cust_profile_List = help_for_get_cust_id.getCustomerProfile(SessionManager.MobileNumber);

        for (int i = 0; i < current_cust_profile_List.size(); i++) {


            // custid = String.valueOf(current_cust_profile_List.get(i).getCustId());

        }

        //customerID=String.valueOf(custid);

        OrderIds togetRecentOrderId = new OrderIds();
        OrderIds OrderId = new OrderIds();
        togetRecentOrderId.setCustomer_id(Integer.parseInt(custid));
        OrderId = help_for_get_cust_id.getRecentOrderId(togetRecentOrderId);
        help_for_get_cust_id.close();
        orderid = Integer.toString(OrderId.getOrder_id());


        StringRequest request = new StringRequest(Request.Method.POST, url_sendOrderFeedBack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                ClearAllPreviousOrderDetails();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("customer_mobile_number", SessionManager.MobileNumber);
                map.put("customer_id", custid);
                map.put("order_id", orderid);

                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }

    private void ClearAllPreviousOrderDetails() {
        SQLiteDatabase database = new SQLiteDatabase(this);
        database.makeEmpty("food_table");
        database.makeEmpty("Order_Ids");
        database.close();
    }


    public void SpannableTextview() {
        SpannableString ss = new SpannableString("By confirming order, You agree to \n the Terms and Conditions.");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Toast.makeText(CartActivity.this, "Clicked on String", Toast.LENGTH_SHORT).show();
                LayoutInflater li = LayoutInflater.from(PaymentMethod.this);
                promptView = li.inflate(R.layout.terms_and_conditions, null);
                TermsAndCondition = (TextView) promptView.findViewById(R.id.textview_termsdisplay);
                TermsAndCondition.setText(R.string.terms_condition);
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethod.this);
                builder.setView(promptView);
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 39, 61, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewTandC.setText(ss);
        textViewTandC.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTandC.setHighlightColor(Color.TRANSPARENT);
    }


    public String getDate() {
        String Date = "";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date = dateFormat.format(c.getTime());
        return Date;
    }

    public String getTime() {
        String Time = "";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
        Time = dateFormat.format(c.getTime());

        return Time;
    }

}
