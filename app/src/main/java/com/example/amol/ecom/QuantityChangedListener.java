package com.example.amol.ecom;

/**
 * Created by Amol on 14-Mar-16.
 */
public interface QuantityChangedListener {
    void onQuantityChanged();
    void updateNotificationsBadge(double grandTotal);
    void updateTotalQty(int totalQty);
    void onBillLayoutClicked(FoodItemList foodItemList);
}
