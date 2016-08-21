package com.example.amol.ecom;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.amol.ecom.login.SessionManager;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public ArrayList<FoodItemList> foodItemLists;
    public ArrayList<FoodItemList> itemsForCheckouts = null;
    public ArrayList<FoodItemList> selectedItems = new ArrayList<>();
    public ArrayList<FoodItemList> allFoodItems = new ArrayList<>();
    public Context context;
    QuantityChangedListener quantityChangedListener;

    public CartAdapter(ArrayList<FoodItemList> foodItemLists, Context context) {
        this.foodItemLists = foodItemLists;
        this.context = context;
    }

    public void setQuantityChangedListener(QuantityChangedListener quantityChangedListener) {
        this.quantityChangedListener = quantityChangedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_final_items_selected, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        FoodItemList itemList = foodItemLists.get(position);
        holder.textViewName.setText(itemList.getItemName());
        holder.textViewPrice.setText(itemList.getItemprice());
        holder.textviewQuantity.setText(Integer.toString(itemList.getItemquantity()));
        int qunty = Integer.parseInt(holder.textviewQuantity.getText().toString());
        Float price = Float.valueOf(holder.textViewPrice.getText().toString());
        Float totalcost = price * qunty;
        holder.textViewGrandPrice.setText(Float.toString(totalcost));

        holder.addmorequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodItemList slecteditem = foodItemLists.get(position);
                int quantity = slecteditem.getItemquantity();
                quantity = quantity + 1;
                if (quantity <= 1) {
                    quantity = 1;
                    notifyDataSetChanged();
                }
                slecteditem.setItemquantity(quantity);
                SQLiteDatabase database = new SQLiteDatabase(context);
                database.AddSelectedFoodItems(slecteditem);
                // int totalquantity = database.getSelectedItemQty();
                database.close();

                holder.textviewQuantity.setText("X" + slecteditem.getItemquantity().toString() + "");
                holder.textViewGrandPrice.setText(String.valueOf(Integer.valueOf(slecteditem.getItemquantity()) * Integer.valueOf(slecteditem.getItemprice())));
                quantityChangedListener.onQuantityChanged();

            }
        });

        holder.removequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FoodItemList slecteditem = foodItemLists.get(position);
                int quantity = slecteditem.getItemquantity();
                quantity = quantity - 1;
                if (quantity <= 1) {
                    quantity = 1;
                    notifyDataSetChanged();
                }
                slecteditem.setItemquantity(quantity);
                SQLiteDatabase database = new SQLiteDatabase(context);
                database.AddSelectedFoodItems(slecteditem);
                int totalquantity = database.getSelectedItemQty();
                database.close();

                holder.textviewQuantity.setText(slecteditem.getItemquantity().toString());
                holder.textViewGrandPrice.setText(String.valueOf(Integer.valueOf(slecteditem.getItemquantity()) * Integer.valueOf(slecteditem.getItemprice())));
                quantityChangedListener.onQuantityChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return foodItemLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView textViewName, textviewQuantity, textViewPrice, textViewGrandPrice;
        public ImageButton addmorequantity, removequantity;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.SelectedItemNameTextView);
            textviewQuantity = (TextView) itemView.findViewById(R.id.SelectedItemQuantity);
            textViewPrice = (TextView) itemView.findViewById(R.id.SelectedItemPriceTextView);
            textViewGrandPrice = (TextView) itemView.findViewById(R.id.SelectedItemGrandPriceTextView);
            addmorequantity = (ImageButton) itemView.findViewById(R.id.AddMoreQuantity);
            removequantity = (ImageButton) itemView.findViewById(R.id.RemoveQuantity);

        }
    }
}
