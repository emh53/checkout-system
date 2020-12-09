package com.elliehannant.checkoutsystem.itemdetails;

import java.util.ArrayList;
import java.util.List;

public enum SampleItem {
    A("A", 50, 3, 130, true),
    B("B", 30, 2, 45, true),
    C("C", 20, null, null, false),
    D("D", 15, null, null, false);

    private final String itemName;
    private final int unitPrice;
    private final Integer discountNum;
    private final Integer discountPrice;
    private final boolean discounted;

    SampleItem(String itemName, int unitPrice, Integer discountNum, Integer discountPrice, boolean discounted) {
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.discountNum = discountNum;
        this.discountPrice = discountPrice;
        this.discounted = discounted;
    }

    public String getItemName() {
        return itemName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public Integer getDiscountNum() {
        return discountNum;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public static List<ItemDetails> getAllSampleItemDetails() {
        List<ItemDetails> sampleItemDetails = new ArrayList<>();

        for (SampleItem sampleItem : values()) {
            sampleItemDetails.add(getSampleItemDetails(sampleItem));
        }

        return sampleItemDetails;
    }

    public static ItemDetails getSampleItemDetails(SampleItem sampleItem) {
        ItemDetails itemDetails = new ItemDetails();

        itemDetails.setItem(sampleItem.getItemName());
        itemDetails.setPricePerUnit(sampleItem.getUnitPrice());
        itemDetails.setItemDiscounted(sampleItem.isDiscounted());
        itemDetails.setDiscountNum(sampleItem.getDiscountNum());
        itemDetails.setDiscountPrice(sampleItem.getDiscountPrice());
        itemDetails.setDiscountedItemPrice(sampleItem.getUnitPrice(), sampleItem.getDiscountNum(), sampleItem.getDiscountPrice());

        return itemDetails;
    }
}
