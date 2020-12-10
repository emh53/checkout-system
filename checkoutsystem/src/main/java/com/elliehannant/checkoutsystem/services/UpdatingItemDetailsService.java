package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.EditingChoice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdatingItemDetailsService {

    // Questions and outputs
    public static final String EDIT_ITEMS = "Would you like to update the items list";
    public static final String EDIT_ANOTHER_ITEM = "Would you like to edit the item list further";
    public static final String EDIT_ITEM_FURTHER = "Would you like to edit this item further";
    public static final String EDIT_WHICH_ITEM = "Which item do you wish to edit";
    public static final String WHICH_EDITING_CHOICE = "What would you like to change";
    public static final String WHAT_NEW_UNIT_PRICE = "What would you like the new price per unit to be for %s";
    public static final String WHAT_NEW_DISCOUNT_NUM = "How many items would you like to get as part of the discount";
    public static final String WHAT_NEW_DISCOUNT_PRICE = "How much would you like the total discounted cost to be";
    public static final String CONFIRM_REMOVE_DISCOUNT = "Are you sure you would like to remove the discount for %s";
    public static final String SUCCESSFULLY_REMOVED = "Item %s's discount has been successfully removed%n";
    public static final String ADD_WHICH_ITEM = "What item would you like to add";
    public static final String ADD_A_DISCOUNT = "Would you like to add a discount";
    public static final String MUST_HAVE_AT_LEAST_ONE_ITEM = "You can not remove an item - you must have at least one item";
    public static final String REMOVE_WHICH_ITEM = "What item would you like to remove";
    public static final String CONFIRM_REMOVE_ITEM = "Are you sure you would like to remove item %s";

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
                EditingChoice editChoice = userInputService.getEditingChoiceResponse(WHICH_EDITING_CHOICE, false);
                if (editChoice == EditingChoice.ADD_ITEM) {
                    addItem(itemDetailsList);
                } else if (editChoice == EditingChoice.EDIT_ITEM) {
                    editItem(itemDetailsList);
                } else if (editChoice == EditingChoice.REMOVE_ITEM) {
                    removeItem(itemDetailsList);
                }
                editingItems = userInputService.getYesOrNoResponseAsBoolean(EDIT_ANOTHER_ITEM);
                if (!editingItems) {
                    itemDetailsService.displayItemDetailsList(itemDetailsList);
                }
            }
        } while (editingItems);
    }

    void addItem(List<ItemDetails> itemDetailsList) {
        List<String> currentItems = itemDetailsService.getItemNameList(itemDetailsList);
        String newItemName = userInputService.getStringResponse(ADD_WHICH_ITEM, availableItemsToAdd(currentItems));

        ItemDetails newItemDetails = new ItemDetails();
        newItemDetails.setItem(newItemName);

        updatePricePerUnit(newItemDetails);
        boolean addDiscount = userInputService.getYesOrNoResponseAsBoolean(ADD_A_DISCOUNT);
        if (addDiscount) {
            updateDiscount(newItemDetails);
        }
        itemDetailsList.add(newItemDetails);
        itemDetailsService.displayItemDetails(newItemName, itemDetailsList);
    }

    void editItem(List<ItemDetails> itemDetailsList) {
        String itemEditing = userInputService.getStringResponse(EDIT_WHICH_ITEM, itemDetailsService.getItemNameList(itemDetailsList));
        ItemDetails editingItemDetails = itemDetailsService.getItemDetails(itemEditing, itemDetailsList);
        itemDetailsService.displayItemDetails(itemEditing, itemDetailsList);

        do {
            EditingChoice editChoice = userInputService.getEditingChoiceResponse(WHICH_EDITING_CHOICE, true);
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

    void removeItem(List<ItemDetails> itemDetailsList) {
        if (itemDetailsList.size() <= 1) {
            System.out.println(MUST_HAVE_AT_LEAST_ONE_ITEM);
        } else {
            List<String> currentItems = itemDetailsService.getItemNameList(itemDetailsList);
            String deleteItemName = userInputService.getStringResponse(REMOVE_WHICH_ITEM, currentItems);
            boolean confirmDelete = userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_ITEM, deleteItemName));
            if (confirmDelete) {
                itemDetailsList.removeIf(itemDetails -> itemDetails.getItem().equalsIgnoreCase(deleteItemName));
            }
        }
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

    List<String> availableItemsToAdd(List<String> existingItems) {
        List<String> items = new ArrayList<>();
        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            String letter = String.valueOf(ch);
            if (!existingItems.contains(letter)) {
                items.add(letter);
            }
        }
        return items;
    }
}
