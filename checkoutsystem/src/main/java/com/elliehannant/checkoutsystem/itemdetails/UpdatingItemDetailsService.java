package com.elliehannant.checkoutsystem.itemdetails;

import com.elliehannant.checkoutsystem.userinput.EditingChoice;
import com.elliehannant.checkoutsystem.userinput.UserInputService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdatingItemDetailsService {

    // Questions and outputs
    public static final String EDIT_ITEMS = "Would you like to edit the items";
    public static final String EDIT_ANOTHER_ITEM = "Would you like to edit another item";
    public static final String EDIT_ITEM_FURTHER = "Would you like to edit this item further";
    public static final String EDIT_WHICH_ITEM = "Which item do you wish to edit";
    public static final String WHICH_EDITING_CHOICE = "What would you like to change";
    public static final String WHAT_NEW_UNIT_PRICE = "What would you like the new price per unit to be for %s";
    public static final String WHAT_NEW_DISCOUNT_NUM = "How many items would you like to get as part of the discount";
    public static final String WHAT_NEW_DISCOUNT_PRICE = "How much would you like the total discounted cost to be";
    public static final String CONFIRM_REMOVE_DISCOUNT = "Are you sure you would like to remove the discount for %s";
    public static final String SUCCESSFULLY_REMOVED = "Item %s's discount has been successfully removed%n";

    private final ItemDetailsService itemDetailsService;
    private final UserInputService userInputService;

    public UpdatingItemDetailsService(ItemDetailsService itemDetailsService, UserInputService userInputService) {
        this.itemDetailsService = itemDetailsService;
        this.userInputService = userInputService;
    }

    public void runItemDetailsUpdate(List<ItemDetails> itemDetailsList) {
        boolean editingItems = userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEMS);
        do {
            if (editingItems) {     // run editor when editing items is true
                String itemEditing = userInputService.getStringResponse(EDIT_WHICH_ITEM, itemDetailsService.getItemNameList(itemDetailsList));
                ItemDetails editingItemDetails = itemDetailsService.getItemDetails(itemEditing, itemDetailsList);

                itemDetailsService.displayItemDetails(itemEditing, itemDetailsList);
                updateItemDetails(itemDetailsList, editingItemDetails);

                editingItems = userInputService.getYesOrNoResponseAsBoolean(EDIT_ANOTHER_ITEM);

                if (!editingItems) {
                    itemDetailsService.displayItemDetailsList(itemDetailsList);
                }
            }
        } while (editingItems);
    }

    void updateItemDetails(List<ItemDetails> itemDetailsList, ItemDetails editingItemDetails) {
        do {
            EditingChoice editChoice = userInputService.getEditingChoiceResponse(WHICH_EDITING_CHOICE);
            if (editChoice == EditingChoice.PRICE_PER_UNIT) {
                updatePricePerUnit(editingItemDetails);
            } else if (editChoice == EditingChoice.ADD_OR_EDIT_DISCOUNT) {
                updateDiscount(editingItemDetails);
            } else if (editChoice == EditingChoice.REMOVE_DISCOUNT) {
                removeDiscount(editingItemDetails);
            }
            itemDetailsService.displayItemDetails(editingItemDetails.getItem(), itemDetailsList);
        } while (userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEM_FURTHER));
    }

    void updatePricePerUnit(ItemDetails itemDetailsEditing) {
        int newPricePerUnit = userInputService.getIntegerPriceResponse(String.format(WHAT_NEW_UNIT_PRICE, itemDetailsEditing.getItem()));

        itemDetailsEditing.setPricePerUnit(newPricePerUnit);
        itemDetailsEditing.setDiscountedItemPrice(newPricePerUnit, itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
    }

    void updateDiscount(ItemDetails itemDetailsEditing) {
        int discountNum = userInputService.getIntegerResponseTwoOrAbove(WHAT_NEW_DISCOUNT_NUM);
        int discountPrice = userInputService.getIntegerPriceResponse(WHAT_NEW_DISCOUNT_PRICE);

        itemDetailsEditing.setDiscountNum(discountNum);
        itemDetailsEditing.setDiscountPrice(discountPrice);
        itemDetailsEditing.setDiscountedItemPrice(itemDetailsEditing.getPricePerUnit(), itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
        itemDetailsEditing.setItemDiscounted(true);
    }

    void removeDiscount(ItemDetails itemDetailsEditing) {
        if (userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_DISCOUNT, itemDetailsEditing.getItem()))) {
            itemDetailsEditing.setDiscountNum(null);
            itemDetailsEditing.setDiscountPrice(null);
            itemDetailsEditing.setItemDiscounted(false);
            itemDetailsEditing.setDiscountedItemPrice(itemDetailsEditing.getPricePerUnit(), itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
            System.out.printf(SUCCESSFULLY_REMOVED, itemDetailsEditing.getItem());
        }
    }
}
