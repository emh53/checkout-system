package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService;
import com.elliehannant.checkoutsystem.userinput.UserInputService;

import java.util.List;

public class CheckoutSystemService {

    public static void runCheckoutSystemService() {
        List<ItemDetails> itemDetailsList = getItems();
        displayItemDetails(itemDetailsList);
        runCheckout(itemDetailsList);
    }

    private static List<ItemDetails> getItems() {
        List<ItemDetails> itemDetailsList = ItemDetailsService.getAllSampleItemDetails();
        displayItemDetails(itemDetailsList);
        ItemDetailsService.editItemDetails(itemDetailsList);
        return itemDetailsList;
    }

    static void displayItemDetails(List<ItemDetails> itemDetailsList) {
        boolean displayItems = UserInputService.getYesOrNoResponseAsBoolean("Would you like to display the items");
        if (displayItems) {
            System.out.println();
            System.out.println("----------------------------------------------------------");
            System.out.println(" ITEM DETAILS:");
            System.out.println("----------------------------------------------------------");
            ItemDetailsService.displayItemDetailsList(itemDetailsList);
        }
    }

    private static void runCheckout(List<ItemDetails> itemDetailsList) {
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println(" CHECKOUT");
        System.out.println("----------------------------------------------------------");
        System.out.println(CheckoutTransactionService.checkout(itemDetailsList));
    }
}
