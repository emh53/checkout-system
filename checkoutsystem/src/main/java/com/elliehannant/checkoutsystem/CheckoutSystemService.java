package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService;
import com.elliehannant.checkoutsystem.itemdetails.SampleItem;
import com.elliehannant.checkoutsystem.itemdetails.UpdatingItemDetailsService;
import com.elliehannant.checkoutsystem.userinput.UserInputService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutSystemService {

    static final String DISPLAY_ITEMS = "Would you like to display the items";

    private final UpdatingItemDetailsService updatingItemDetailsService;
    private final CheckoutTransactionService transactionService;
    private final UserInputService userInputService;
    private final ItemDetailsService itemDetailsService;

    public CheckoutSystemService(UpdatingItemDetailsService updatingItemDetailsService,
                                 CheckoutTransactionService transactionService,
                                 UserInputService userInputService,
                                 ItemDetailsService itemDetailsService) {
        this.updatingItemDetailsService = updatingItemDetailsService;
        this.transactionService = transactionService;
        this.userInputService = userInputService;
        this.itemDetailsService = itemDetailsService;
    }

    public void runCheckoutSystem() {
        do {
            List<ItemDetails> itemDetailsList = getItemsForCheckoutTransaction();
            runCheckout(itemDetailsList);
        } while (reRunSystem());
    }

    private List<ItemDetails> getItemsForCheckoutTransaction() {
        List<ItemDetails> itemDetailsList = SampleItem.getAllSampleItemDetails();
        displayItemDetails(itemDetailsList);
        updatingItemDetailsService.runItemDetailsUpdate(itemDetailsList);
        return itemDetailsList;
    }

    void displayItemDetails(List<ItemDetails> itemDetailsList) {
        boolean displayItems = userInputService.getYesOrNoResponseAsBoolean(DISPLAY_ITEMS);
        if (displayItems) {
            itemDetailsService.displayItemDetailsList(itemDetailsList);
        }
    }

    private void runCheckout(List<ItemDetails> itemDetailsList) {
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println(" CHECKOUT");
        System.out.print("----------------------------------------------------------");

        String finalTotalDisplay = transactionService.runCheckoutTransaction(itemDetailsList);
        System.out.println("----------------------------------------------------------");
        System.out.println(finalTotalDisplay);
        System.out.println("----------------------------------------------------------");
        System.out.println("----------------------------------------------------------");
    }

    private boolean reRunSystem() {
        return userInputService.getYesOrNoResponseAsBoolean("Would you like to re run the checkout system?");
    }
}
