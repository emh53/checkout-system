package com.elliehannant.checkoutsystem.itemdetails;

import com.elliehannant.checkoutsystem.userinput.UserInputService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemDetailsService {

    public static void editItemDetails(List<ItemDetails> itemDetailsList) {
        boolean editingItems = UserInputService.getYesOrNoResponseAsBoolean("Would you like to edit the items");
        do {
            if (editingItems) {     // run editor when editing items is true
                UpdatingItemDetailsService.updateItemDetails(itemDetailsList);
                editingItems = UserInputService.getYesOrNoResponseAsBoolean("Would you like to edit another item");
            }
        } while (editingItems);
    }

    public static void displayItemDetailsList(List<ItemDetails> itemDetailsList) {
        for (ItemDetails itemDetails : itemDetailsList) {
            displayItemDetails(itemDetails.getItem(), itemDetailsList);
        }
    }

    public static void displayItemDetails(String item, List<ItemDetails> itemDetailsList) {
        ItemDetails itemDetails = getItemDetails(item, itemDetailsList);
        assert itemDetails != null;

        System.out.printf("Item details for %s%n", itemDetails.getItem());
        System.out.printf(" Price per unit: %s%n", formatPrice(itemDetails.getPricePerUnit()));

        if (itemDetails.isItemDiscounted()) {
            System.out.printf(" Discount: %d for %s%n", itemDetails.getDiscountNum(), formatPrice(itemDetails.getDiscountPrice()));
        } else {
            System.out.println(" No Discount");
        }
    }

    public static List<String> getItemNameList(List<ItemDetails> itemDetailsList) {
        List<String> listOfItemNames = new ArrayList<>();
        for (ItemDetails itemDetails : itemDetailsList) {
            listOfItemNames.add(itemDetails.getItem());
        }
        return listOfItemNames;
    }

    public static ItemDetails getItemDetails(String item, List<ItemDetails> itemDetailsList) {
        for (ItemDetails itemDetails : itemDetailsList) {
            if (itemDetails.getItem().equals(item)) {
                return itemDetails;
            }
        }
        return null;
    }

    public static String formatPrice(int pence) {
        if (pence < 100) {
            return pence + "p";
        } else {
            double pounds = (double) pence / 100;
            return new DecimalFormat("£###0.00").format(pounds);
        }
    }

    public static List<ItemDetails> getAllSampleItemDetails() {
        List<ItemDetails> sampleItemDetails = new ArrayList<>();

        sampleItemDetails.add(new ItemDetails("A", 50, 3, 130));
        sampleItemDetails.add(new ItemDetails("B", 30, 2, 45));
        sampleItemDetails.add(new ItemDetails("C", 20));
        sampleItemDetails.add(new ItemDetails("D", 15));
        return sampleItemDetails;
    }
}
