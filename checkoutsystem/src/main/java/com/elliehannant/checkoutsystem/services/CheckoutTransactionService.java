package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CheckoutTransactionService {

    public static final String FINAL_TOTAL_DISPLAY = "Final total: %s - Items: %s";
    public static final String RUNNING_TOTAL_DISPLAY = "+ %s (Total: %s)%n";
    public static final String RUNNING_TOTAL_DISCOUNT_DISPLAY = "(discount added) + %s (Total: %s)%n";
    private final UserInputService userInputService;
    private final ItemDetailsService itemDetailsService;

    public CheckoutTransactionService(UserInputService userInputService,
                                      ItemDetailsService itemDetailsService) {
        this.userInputService = userInputService;
        this.itemDetailsService = itemDetailsService;
    }

    public String runCheckoutTransaction(List<ItemDetails> itemAvailableForCheckout) {
        int totalPrice = 0;
        List<String> itemsInCheckout = new ArrayList<>();

        while (true) {
            ItemDetails itemDetails = userInputService.getItemForCheckoutTransaction(itemAvailableForCheckout);      // returns null when done

            if (itemDetails == null) {
                break;
            } else {
                itemsInCheckout.add(itemDetails.getItem());
                totalPrice = updateTotalPrice(totalPrice, itemsInCheckout, itemDetails);
            }
        }
        return String.format(FINAL_TOTAL_DISPLAY, itemDetailsService.formatPrice(totalPrice), itemsInCheckout.toString());
    }

    int updateTotalPrice(int totalPrice, List<String> itemsInCheckout, ItemDetails itemDetails) {
        int price = getPriceWithDiscountCheck(itemDetails, itemsInCheckout);
        String formattedPrice = itemDetailsService.formatPrice(price);

        int updatedTotalPrice = totalPrice + price;
        String formattedUpdatedTotalPrice = itemDetailsService.formatPrice(updatedTotalPrice);

        if (price == itemDetails.getPricePerUnit()) {
            System.out.printf(RUNNING_TOTAL_DISPLAY, formattedPrice, formattedUpdatedTotalPrice);
        } else {
            System.out.printf(RUNNING_TOTAL_DISCOUNT_DISPLAY, formattedPrice, formattedUpdatedTotalPrice);
        }
        return updatedTotalPrice;
    }

    int getPriceWithDiscountCheck(ItemDetails itemDetails, List<String> itemsInCheckout) {
        if (itemDetails.isItemDiscounted()) {
            int numberOfCurrentItemInCheckout = Collections.frequency(itemsInCheckout, itemDetails.getItem());
            if (numberOfCurrentItemInCheckout % itemDetails.getDiscountNum() == 0) {
                return itemDetails.getDiscountedItemPrice();
            }
        }
        return itemDetails.getPricePerUnit();
    }
}
