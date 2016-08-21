package com.example.amol.ecom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Amol on 01-Feb-16.
 */
public class SQLiteDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ecom.db";
    public static final String FOODTABLENAME = "food_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "QUANTITY";
    public static final String COL4 = "PRICE";

    public static final String CREATE_CUSTOMER_PROFILE_TABLE = "Customer_Profile";
    public static final String COL_CUST_ID = "Id";
    public static final String COL_CUST_NAME = "cust_name";
    public static final String COL_CUST_Contact = "cust_contact";
    public static final String customer_email = "cust_email";
    public static final String customer_address = "cust_address";

    public static final String TABLE_ORDER_ID = "Order_Ids";
    public static final String ID = "Id";
    public static final String order_id = "order_id";
    public static final String customer_id = "customer_id";

    public static final String Food_item_TABLE = "food_item_table";
    public static final String Food_ID = "id";
    public static final String Food_Name = "food_name";
    public static final String Food_Description = "food_Description";
    public static final String Food_Price = "food_price";


    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        String sql_selected_string = ("CREATE TABLE food_table(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, QUANTITY INTEGER, PRICE INTEGER)");
        String CREATE_ORDERID_TABLE = "CREATE TABLE IF NOT EXISTS Order_Ids(Id INTEGER PRIMARY KEY AUTOINCREMENT,order_id INTEGER,customer_id INTEGER)";
        String CREATE_CUSTOMER_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS Customer_Profile(Id INTEGER,cust_name TEXT,cust_contact VARCHAR PRIMARY KEY,cust_email VARCHAR,cust_address VARCHAR)";
        String sql_food_Table = ("CREATE TABLE IF NOT EXISTS food_item_table(id INTEGER PRIMARY KEY AUTOINCREMENT, food_name VARCHAR, food_Description VARCHAR, food_price VARCHAR )");

        db.execSQL(sql_selected_string);
        db.execSQL(CREATE_ORDERID_TABLE);
        db.execSQL(CREATE_CUSTOMER_PROFILE_TABLE);
        db.execSQL(sql_food_Table);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + FOODTABLENAME);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ORDER_ID);
        db.execSQL("DROP TABLE IF EXISTS" + CREATE_CUSTOMER_PROFILE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS" + Food_item_TABLE);
        onCreate(db);

    }

    public void AddSelectedFoodItems(FoodItemList selectedlist) {

        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + FOODTABLENAME + " WHERE " + COL2 + " = '" + selectedlist.getItemName() + "' ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            UpdateCart(selectedlist);

        } else {

            ContentValues values = new ContentValues();
            values.put(COL2, selectedlist.getItemName());
            System.out.println("Adding item name::" + selectedlist.getItemName());
            values.put(COL3, selectedlist.getItemquantity());
            System.out.println("Adding item quantity::" + selectedlist.getItemquantity());
            values.put(COL4, selectedlist.getItemprice());
            System.out.println("Adding item price::" + selectedlist.getItemprice());

            db.insertWithOnConflict(FOODTABLENAME, "NULL", values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE);

        }
        cursor.close();
        db.close();
    }


    private void UpdateCart(FoodItemList selectedlist) {

        android.database.sqlite.SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL3, selectedlist.getItemquantity());
        System.out.println("Updating Cart Table qty :" + selectedlist.getItemquantity());
        database.update("food_table", values, "NAME" + "='" + selectedlist.getItemName() + "'", null);
        database.close();


    }

    public void UpdateFoodItemSelectedQty(FoodItemList selectedlist) {

        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL3, selectedlist.getItemquantity());
        db.update("food_table", values, "NAME" + "='" + selectedlist.getItemName() + "'", null);
        System.out.println("Upadated FoodItem  Cart Qty---" + selectedlist.getItemquantity());
        db.close();
    }

    public ArrayList<FoodItemList> getAllCartItem() {

        ArrayList<FoodItemList> fooditemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM food_table";
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FoodItemList itemList = new FoodItemList();

                String itemid = cursor.getString(cursor.getColumnIndex(COL1));
                System.out.println("Reading fooditems in helper" + itemid);
                itemList.setId(Integer.valueOf(itemid));

                String itemname = cursor.getString(cursor.getColumnIndex(COL2));
                System.out.println("Reading fooditems in helper" + itemname);
                itemList.setItemName(itemname);

                String itemQuantity = cursor.getString(cursor.getColumnIndex(COL3));
                System.out.println("Reading fooditems in helper" + itemQuantity);
                itemList.setItemquantity(Integer.valueOf(itemQuantity));

                String itemPrice = cursor.getString(cursor.getColumnIndex(COL4));
                System.out.println("Reading fooditems in helper" + itemPrice);
                itemList.setItemprice(itemPrice);


                fooditemList.add(itemList);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return fooditemList;
    }

    public void makeEmpty(String TableName) {
        android.database.sqlite.SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from " + TableName);
        System.out.println("............Deleting Records of table.......... " + TableName);
        database.close();
    }


    public int getSelectedItemQty() {

        int quantity = 0;
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorgetQty = db.rawQuery("SELECT SUM(QUANTITY) FROM food_table", null);
        if (cursorgetQty.moveToFirst()) {
            quantity = cursorgetQty.getInt(0);
        }
        return quantity;
    }


    public OrderIds getRecentOrderId(OrderIds current_customer_id) {
        OrderIds recentOrderId = new OrderIds();
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select * from Order_Ids where customer_id='" + current_customer_id.getCustomer_id() + "'";
        Cursor CursorOrderId = db.rawQuery(selectQuery, null);

        if (CursorOrderId.getCount() > 0) {
            if (CursorOrderId.moveToNext()) {
                do {
                    recentOrderId.setOrder_id(CursorOrderId.getInt(1));
                    recentOrderId.setCustomer_id(CursorOrderId.getInt(2));

                } while (CursorOrderId.moveToNext());
            }
        }
        return recentOrderId;
    }

    public ArrayList<ProfileEntity> getCustomerProfile(String mobileNumber) {

        ArrayList<ProfileEntity> entities = new ArrayList<>();
        //  String selectquery = "SELECT *FROM" + CREATE_CUSTOMER_PROFILE_TABLE + "WHERE" + COL_CUST_Contact + "='" + mobileNumber + "'";
        String selectQuery = "SELECT  * FROM " + CREATE_CUSTOMER_PROFILE_TABLE + " WHERE " + COL_CUST_Contact + " = '" + mobileNumber + "' ";
        android.database.sqlite.SQLiteDatabase database = this.getWritableDatabase();
        Cursor profile = database.rawQuery(selectQuery, null);
        if (profile.moveToFirst()) {
            do {
                ProfileEntity profileEntity = new ProfileEntity();
                profileEntity.setCustId(Integer.parseInt(Integer.toString(profile.getInt(0))));
                System.out.println("Reading customer Id::" + Integer.parseInt(Integer.toString(profile.getInt(0))));
                profileEntity.setCustName(profile.getString(1));
                System.out.println("Reading customer name::" + profile.getString(1));
                profileEntity.setCustContact(profile.getString(2));
                System.out.println("Reading customer contact::" + profile.getString(2));
                profileEntity.setCustEmail(profile.getString(3));
                System.out.println("Reading customer email::" + profile.getString(3));
                profileEntity.setCustAddress(profile.getString(4));
                System.out.println("Reading customer address::" + profile.getString(4));

                entities.add(profileEntity);
            } while (profile.moveToNext());
        }
        profile.close();
        database.close();

        return entities;
    }

    public void SaveOrderId(OrderIds current_order_id) {
        android.database.sqlite.SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ORDER_ID + " WHERE " + customer_id + " = '" + current_order_id.getCustomer_id() + "' ";


        //  String selectQuery = "select * from Order_Ids where order_id='"+current_order_id.getCurrentOrderId()+"' and customer_id='"+current_order_id.getCustomerId()+"'";
        Cursor CursorNotification = database.rawQuery(selectQuery, null);
        if (CursorNotification.getCount() > 0) {

            String deletequry = "delete from " + TABLE_ORDER_ID;
            // String deletequry = "delete from Order_Ids where order_id='"+current_order_id.getCurrentOrderId()+"' and customer_id='"+current_order_id.getCustomerId()+"'";
            database.execSQL(deletequry);
            ContentValues values = new ContentValues();


            values.put("order_id", current_order_id.getOrder_id());
            System.out.println("Adding current order Id::" + current_order_id.getOrder_id());


            values.put("customer_id", current_order_id.getCustomer_id());
            System.out.println("Adding Customer Id::" + current_order_id.getCustomer_id());


            database.insert("Order_Ids", null, values);

            System.out.println("Order ID removed and added");


        } else {


            ContentValues values = new ContentValues();

            values.put("order_id", current_order_id.getOrder_id());
            System.out.println("Adding current order Id::" + current_order_id.getOrder_id());


            values.put("customer_id", current_order_id.getCustomer_id());
            System.out.println("Adding Customer ID::" + current_order_id.getCustomer_id());


            database.insert("Order_Ids", null, values);

            System.out.println("new Order Id Added");
        }
        database.close();
    }

    public void SaveCurrentCustomerProfile(ProfileEntity cust_profile) {

        android.database.sqlite.SQLiteDatabase dbcurrent = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM Customer_Profile";

        Cursor cursor = dbcurrent.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {

            makeEmpty("Customer_Profile");

            //  db.delete(TABLE_CURRENT_CUSTOMER, COLUMN_CUST_CONTACT+"="+cust_profile.getCustContact(), null);

            ContentValues values = new ContentValues();

            values.put("Id", cust_profile.getCustId());
            System.out.println("Adding Customer ID::" + cust_profile.getCustId());

            values.put("cust_name", cust_profile.getCustName());
            System.out.println("Adding Customer Name::" + cust_profile.getCustName());

            values.put("cust_contact", cust_profile.getCustContact());
            System.out.println("Adding Customer Contact::" + cust_profile.getCustContact());

            values.put("cust_email", cust_profile.getCustEmail());
            System.out.println("Adding Customer Email::" + cust_profile.getCustEmail());

            values.put("cust_address", cust_profile.getCustAddress());
            System.out.println("Adding Customer Address::" + cust_profile.getCustAddress());

            android.database.sqlite.SQLiteDatabase tempdb = this.getWritableDatabase();
            tempdb.insert("Customer_Profile", null, values);
            tempdb.close();
            System.out.println("Removed recent And added new Updated Current Customer :" + cust_profile.getCustOtp());


        } else {
            ContentValues values = new ContentValues();

            values.put("Id", cust_profile.getCustId());
            System.out.println("Adding Customer ID::" + cust_profile.getCustId());

            values.put("cust_name", cust_profile.getCustName());
            System.out.println("Adding Customer Name::" + cust_profile.getCustName());

            values.put("cust_contact", cust_profile.getCustContact());
            System.out.println("Adding Customer Contact::" + cust_profile.getCustContact());

            values.put("cust_email", cust_profile.getCustEmail());
            System.out.println("Adding Customer Email::" + cust_profile.getCustEmail());

            values.put("cust_address", cust_profile.getCustAddress());
            System.out.println("Adding Customer Address::" + cust_profile.getCustAddress());

            android.database.sqlite.SQLiteDatabase tempdb = this.getWritableDatabase();
            tempdb.insert("Customer_Profile", null, values);
            tempdb.close();
            System.out.println("  New Current Customer Profile Added SuccessFully::" + cust_profile.getCustOtp());
        }
        dbcurrent.close();
    }


    public void AddFoodItemsToDatabase(FoodItemList itemList) {

        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM food_item_table ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {

            ContentValues values = new ContentValues();
            // values.put(Food_URL, itemList.getImageurl());
            // System.out.println("Adding item name in database::" + itemList.getImageurl());
            values.put(Food_Name, itemList.getItemName());
            System.out.println("Adding item quantity database::" + itemList.getItemName());
            values.put(Food_Description, itemList.getItemdesciption());
            System.out.println("Adding item price database::" + itemList.getItemdesciption());
            values.put(Food_Price, itemList.getItemprice());
            System.out.println("Adding item price database::" + itemList.getItemprice());

            db.insertWithOnConflict(Food_item_TABLE, "NULL", values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE);

        }
        cursor.close();
        db.close();
    }

    public ArrayList<FoodItemList> getAllItems() {
        ArrayList<FoodItemList> itemList = new ArrayList<FoodItemList>();
        // Select All Query
        String selectQuery = "SELECT * FROM food_item_table";
        //  String selectQuery = "SELECT  * FROM " + TABLE_ITEMS +" WHERE " + COLUMN_FOOD_TYPE + " = VEG" ;
        // glob.myDatabase.rawQuery("SELECT * FROM contactlist where groupname='"+glob.showgroup+"' ",null);
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("Cursor Returned ------------     :  " + cursor.isBeforeFirst());
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FoodItemList itemList1 = new FoodItemList();
                // Item item = new Item();
                itemList1.setId(Integer.valueOf(cursor.getString(0)));
                System.out.println("Special Offer.....         image  :  " + cursor.getString(0));
                itemList1.setItemName(cursor.getString(1));
                System.out.println("Special Offer        Name  :  " + cursor.getString(1));
                itemList1.setItemdesciption(cursor.getString(2));
                System.out.println("Special Offer        description  :  " + cursor.getString(2));
                itemList1.setItemprice(String.valueOf(cursor.getFloat(3)));
                System.out.println("Special Offer        price  :  " + cursor.getString(3));
                itemList.add(itemList1);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemList;
    }


}
