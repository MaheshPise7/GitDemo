package com.example.amol.ecom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.amol.ecom.login.SessionManager;

import java.util.ArrayList;

public class ItemOneAdapter extends RecyclerView.Adapter<ItemOneAdapter.ViewHolder> {

    private ArrayList<FoodItemList> foodItemLists;
    public Context context;
    QuantityChangedListener quantityChangedListener;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ItemOneAdapter(ArrayList<FoodItemList> foodItemList, Context context) {

        this.foodItemLists = foodItemList;
        this.context = context;
    }


    public void setQuantityChangedListener(QuantityChangedListener quantityChangedListener) {
        this.quantityChangedListener = quantityChangedListener;
    }


    @Override
    public ItemOneAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_items, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final FoodItemList list = foodItemLists.get(position);
        System.out.println("fooditemlist size" + list);
        System.out.println("fooditemlist size" + foodItemLists.get(position));
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        holder.networkImageView.setImageUrl(list.getImageurl(), imageLoader);
        holder.ItemName.setText(list.getItemName());
        holder.ItemDescription.setText(list.getItemdesciption());
        holder.ItemPrice.setText(list.getItemprice());
        holder.ItemQuantity.setText(list.getItemquantity().toString());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Clicked on Items",Toast.LENGTH_SHORT).show();
              //  Intent intent = new Intent(context, ScrollingActivity.class);
                //context.startActivity(intent);
            }
        });


        holder.AddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previouspos = SessionManager.currentSelectedItemPosition;
                SessionManager.currentSelectedItemPosition = position;
                FoodItemList selectedlist = foodItemLists.get(position);
                int current = selectedlist.getItemquantity();
                current = current + 1;
                selectedlist.setItemquantity(current);
                SessionManager.ProccedFlag = 1;
                SQLiteDatabase database = new SQLiteDatabase(context);
                database.AddSelectedFoodItems(selectedlist);
                database.UpdateFoodItemSelectedQty(selectedlist);
                database.close();
                notifyDataSetChanged();
                quantityChangedListener.onQuantityChanged();
                quantityChangedListener.updateNotificationsBadge(SessionManager.cartNotificationTotal);

            }

        });

        holder.SubtractItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousPos = SessionManager.currentSelectedItemPosition;
                SessionManager.currentSelectedItemPosition = position;
                FoodItemList selecteditemList = foodItemLists.get(position);
                int current = selecteditemList.getItemquantity();
                if (current <= 0) {
                    SessionManager.ProccedFlag = 0;
                } else {
                    current = current - 1;
                    selecteditemList.setItemquantity(current);
                    SQLiteDatabase dbHelper = new SQLiteDatabase(context);
                    dbHelper.AddSelectedFoodItems(selecteditemList);
                    dbHelper.UpdateFoodItemSelectedQty(selecteditemList);
                    dbHelper.close();
                    notifyDataSetChanged();
                    quantityChangedListener.onQuantityChanged();
                    quantityChangedListener.updateNotificationsBadge(SessionManager.cartNotificationTotal);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        System.out.println("fooditemlist size" + foodItemLists.size());
        return foodItemLists.size();


    }

    public void setFilter(ArrayList<FoodItemList> newfoodItemLists) {
        System.out.println("Inside Adapter ");
        foodItemLists = new ArrayList<>();
        foodItemLists.addAll(newfoodItemLists);
        System.out.println("Inside Adapter " + foodItemLists);
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView networkImageView;
        public TextView ItemName, ItemDescription, ItemPrice, ItemQuantity;
        public ImageButton AddItem, SubtractItem;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.network_image);
            networkImageView.setDefaultImageResId(R.mipmap.networkimageview);
            ItemName = (TextView) itemView.findViewById(R.id.item_name);
            ItemDescription = (TextView) itemView.findViewById(R.id.item_description);
            ItemPrice = (TextView) itemView.findViewById(R.id.item_price);
            ItemQuantity = (TextView) itemView.findViewById(R.id.item_quantity);
            AddItem = (ImageButton) itemView.findViewById(R.id.add_item);
            SubtractItem = (ImageButton) itemView.findViewById(R.id.minus_item);
        }
    }
}
