package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService;
import com.elliehannant.checkoutsystem.userinput.UserInputService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckoutTransactionService {

    public static String checkout(List<ItemDetails> itemDetailsList) {
        int totalPrice = 0;
        List<String> itemsInCheckout = new ArrayList<>();

        while (true) {
            ItemDetails itemDetails = UserInputService.getItemForCheckoutTransaction(itemDetailsList);      // returns null when done

            if (itemDetails == null) {
                break;
            } else {
                itemsInCheckout.add(itemDetails.getItem());
                totalPrice = checkoutItem(totalPrice, itemsInCheckout, itemDetails);
            }
        }
        return String.format("\nFinal total: %s - Items: %s", ItemDetailsService.formatPrice(totalPrice), itemsInCheckout.toString());
    }

    static int checkoutItem(int totalPrice, List<String> itemsInCheckout, ItemDetails itemDetails) {
        int price = getPriceWithDiscountCheck(itemDetails, itemsInCheckout);
        String formattedPrice = ItemDetailsService.formatPrice(price);

        int updatedTotalPrice = totalPrice + price;
        String formattedUpdatedTotalPrice = ItemDetailsService.formatPrice(updatedTotalPrice);

        if (price == itemDetails.getPricePerUnit()) {
            System.out.printf("+ %s (Total: %s)%n", formattedPrice, formattedUpdatedTotalPrice);
        } else {
            System.out.printf("(discount added) + %s (Total: %s)%n", formattedPrice, formattedUpdatedTotalPrice);
        }
        return updatedTotalPrice;
    }

    static int getPriceWithDiscountCheck(ItemDetails itemDetails, List<String> itemsInCheckout) {
        if (itemDetails.isItemDiscounted()) {
            int numberOfCurrentItemInCheckout = Collections.frequency(itemsInCheckout, itemDetails.getItem());
            if (numberOfCurrentItemInCheckout % itemDetails.getDiscountNum() == 0) {
                return itemDetails.getDiscountedItemPrice();
            }
        }
        return itemDetails.getPricePerUnit();
    }
}
