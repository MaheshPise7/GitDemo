package com.example.amol.ecom;

public class FoodItemList {

    private Integer id;
    private Integer Itemquantity;
    private String imageurl;
    private String ItemName;
    private String itemdesciption;
    private String itemprice;

    public FoodItemList() {
        this.id = 0;
        this.Itemquantity = 0;
        this.imageurl = "";
        this.ItemName = "";
        this.itemdesciption = "";
        this.itemprice = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemquantity() {
        return Itemquantity;
    }

    public void setItemquantity(Integer itemquantity) {
        Itemquantity = itemquantity;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }


    public String getItemdesciption() {
        return itemdesciption;
    }

    public void setItemdesciption(String itemdesciption) {
        this.itemdesciption = itemdesciption;
    }

    public String getItemprice() {
        return itemprice;
    }

    public void setItemprice(String itemprice) {
        this.itemprice = itemprice;
    }


}
