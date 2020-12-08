package com.elliehannant.checkoutsystem.itemdetails;

import com.elliehannant.checkoutsystem.userinput.EditingChoice;
import com.elliehannant.checkoutsystem.userinput.UserInputService;

import java.util.List;

import static com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService.displayItemDetails;
import static com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService.getItemDetails;

public class UpdatingItemDetailsService {

    public static void updateItemDetails(List<ItemDetails> itemDetailsList) {
        String itemEditing = UserInputService.getStringResponse("Which item do you wish to edit", ItemDetailsService.getItemNameList(itemDetailsList));
        displayItemDetails(itemEditing, itemDetailsList);
        ItemDetails editingItemDetails = getItemDetails(itemEditing, itemDetailsList);
        assert editingItemDetails != null;

        do {
            EditingChoice editChoice = UserInputService.getEditingChoiceResponse("What would you like to change");
            if (editChoice == EditingChoice.PRICE_PER_UNIT) {
                updatePricePerUnit(editingItemDetails);
            } else if (editChoice == EditingChoice.ADD_OR_EDIT_DISCOUNT) {
                updateDiscount(editingItemDetails);
            } else if (editChoice == EditingChoice.REMOVE_DISCOUNT) {
                removeDiscount(editingItemDetails);
            }
            displayItemDetails(editingItemDetails.getItem(), itemDetailsList);
        } while (UserInputService.getYesOrNoResponseAsBoolean("Would you like to edit this item further"));
    }

    static void updatePricePerUnit(ItemDetails itemDetailsEditing) {
        int newPricePerUnit = UserInputService.getIntegerPriceResponse(String.format("What would you like the new price per unit to be for %s", itemDetailsEditing.getItem()));
        itemDetailsEditing.setPricePerUnit(newPricePerUnit);
        itemDetailsEditing.setDiscountedItemPrice(newPricePerUnit, itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
    }

    private static void updateDiscount(ItemDetails itemDetailsEditing) {
        int discountNum = UserInputService.getIntegerResponseTwoOrAbove("How many items would you like to get as part of the discount");
        int discountPrice = UserInputService.getIntegerPriceResponse("How much would you like the total discounted cost to be");
        updateDiscount(itemDetailsEditing, discountNum, discountPrice);
    }

    static void updateDiscount(ItemDetails itemDetailsEditing, Integer discountNum, Integer discountPrice) {
        itemDetailsEditing.setDiscountNum(discountNum);
        itemDetailsEditing.setDiscountPrice(discountPrice);
        itemDetailsEditing.setDiscountedItemPrice(itemDetailsEditing.getPricePerUnit(), itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
        itemDetailsEditing.setItemDiscounted(true);
    }

    static void removeDiscount(ItemDetails itemDetailsEditing) {
        if (UserInputService.getYesOrNoResponseAsBoolean(String.format("Are you sure you would like to remove the discount for %s", itemDetailsEditing.getItem()))) {
            itemDetailsEditing.setDiscountNum(null);
            itemDetailsEditing.setDiscountPrice(null);
            itemDetailsEditing.setItemDiscounted(false);
            itemDetailsEditing.setDiscountedItemPrice(itemDetailsEditing.getPricePerUnit(), itemDetailsEditing.getDiscountNum(), itemDetailsEditing.getDiscountPrice());
            System.out.printf("Item %s's discount has been successfully removed%n", itemDetailsEditing.getItem());
        }
    }

    //TODO: Adding and removing items
}
