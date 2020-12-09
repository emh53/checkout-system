package com.elliehannant.checkoutsystem.itemdetails;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemDetailsService {

    public ItemDetailsService() {
    }

    public void displayItemDetailsList(List<ItemDetails> itemDetailsList) {
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println(" ITEM DETAILS:");
        System.out.println("----------------------------------------------------------");
        for (ItemDetails itemDetails : itemDetailsList) {
            displayItemDetails(itemDetails.getItem(), itemDetailsList);
        }
    }

    public void displayItemDetails(String item, List<ItemDetails> itemDetailsList) {
        ItemDetails itemDetails = getItemDetails(item, itemDetailsList);

        System.out.printf("Item details for %s%n", itemDetails.getItem());
        System.out.printf(" Price per unit: %s%n", formatPrice(itemDetails.getPricePerUnit()));

        if (itemDetails.isItemDiscounted()) {
            System.out.printf(" Discount: %d for %s%n", itemDetails.getDiscountNum(), formatPrice(itemDetails.getDiscountPrice()));
        } else {
            System.out.println(" No Discount");
        }
    }

    public List<String> getItemNameList(List<ItemDetails> itemDetailsList) {
        List<String> listOfItemNames = new ArrayList<>();
        for (ItemDetails itemDetails : itemDetailsList) {
            listOfItemNames.add(itemDetails.getItem());
        }
        return listOfItemNames;
    }

    public ItemDetails getItemDetails(String item, List<ItemDetails> itemDetailsList) {
        for (ItemDetails itemDetails : itemDetailsList) {
            if (itemDetails.getItem().equalsIgnoreCase(item)) {
                return itemDetails;
            }
        }
        return null;
    }

    public String formatPrice(int pence) {
        if (pence < 100) {
            return pence + "p";
        } else {
            double pounds = (double) pence / 100;
            return new DecimalFormat("£###0.00").format(pounds);
        }
    }
}
