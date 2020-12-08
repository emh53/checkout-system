package com.elliehannant.checkoutsystem.itemdetails;

public class ItemDetails {
    private String item;
    private int pricePerUnit;
    private boolean itemDiscounted;
    private Integer discountNum;
    private Integer discountPrice;
    private Integer discountedItemPrice;

    public ItemDetails(String item, int pricePerUnit) {
        this.item = item;
        this.pricePerUnit = pricePerUnit;
        this.itemDiscounted = false;
    }

    public ItemDetails(String item, int pricePerUnit, Integer discountNum, Integer discountPrice) {
        this.item = item;
        this.pricePerUnit = pricePerUnit;
        this.discountNum = discountNum;
        this.discountPrice = discountPrice;

        if (discountNum != null && discountPrice != null) {
            int numberOfFullPriceItems = discountNum - 1;
            this.discountedItemPrice = discountPrice - (numberOfFullPriceItems * pricePerUnit);
            this.itemDiscounted = true;
        } else {
            this.itemDiscounted = false;
        }
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public boolean isItemDiscounted() {
        return itemDiscounted;
    }

    public void setItemDiscounted(boolean itemDiscounted) {
        this.itemDiscounted = itemDiscounted;
    }

    public Integer getDiscountNum() {
        return discountNum;
    }

    public void setDiscountNum(Integer discountNum) {
        this.discountNum = discountNum;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getDiscountedItemPrice() {
        return discountedItemPrice;
    }

    public void setDiscountedItemPrice(Integer discountedItemPrice) {
        this.discountedItemPrice = discountedItemPrice;
    }

    public void setDiscountedItemPrice(int pricePerUnit, Integer discountNum, Integer discountPrice) {
        if (discountNum != null && discountPrice != null) {
            int numberOfFullPriceItems = discountNum - 1;
            this.discountedItemPrice = discountPrice - (numberOfFullPriceItems * pricePerUnit);
        } else {
            this.discountedItemPrice = null;
        }
    }
}
