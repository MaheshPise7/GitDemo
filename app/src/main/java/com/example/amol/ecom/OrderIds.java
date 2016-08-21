package com.example.amol.ecom;

/**
 * Created by Amol on 21-Mar-16.
 */
public class OrderIds {

    public int id;
    public int order_id;
    public int customer_id;


    public OrderIds(){
        this.id=0;
        this.order_id=0;
        this.customer_id=0;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
}


