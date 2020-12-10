package com.elliehannant.checkoutsystem.enums;

import java.util.ArrayList;
import java.util.List;

public enum EditingChoice {
    ADD_ITEM(1, "1. Add New Item", false),
    EDIT_ITEM(2, "2. Edit Item", false),
    REMOVE_ITEM(3, "3. Remove Item", false),
    PRICE_PER_UNIT(1, "1. Price Per Unit", true),
    ADD_OR_EDIT_DISCOUNT(2, "2. Add or Edit Discount", true),
    REMOVE_DISCOUNT(3, "3. Remove Discount", true);


    private final int value;
    private final String optionDisplay;
    private final boolean editingSelectedItem;

    EditingChoice(int value, String optionDisplay, boolean editingSelectedItem) {
        this.value = value;
        this.optionDisplay = optionDisplay;
        this.editingSelectedItem = editingSelectedItem;
    }

    public int getValue() {
        return value;
    }

    public String getOptionDisplay() {
        return optionDisplay;
    }

    public boolean isEditingSelectedItem() {
        return editingSelectedItem;
    }

    public static List<EditingChoice> getOptions(boolean editingSelectedItem) {
        List<EditingChoice> options = new ArrayList<>();
        for (EditingChoice editingChoice : values()) {
            if (editingChoice.isEditingSelectedItem() == editingSelectedItem) {
                options.add(editingChoice);
            }
        }
        return options;
    }

    public static String displayOptions(boolean editingSelectedItem) {
        StringBuilder optionDisplay = new StringBuilder();
        for (EditingChoice editingChoice : values()) {
            if (editingChoice.isEditingSelectedItem() == editingSelectedItem) {
                optionDisplay.append("\n ").append(editingChoice.getOptionDisplay());
            }
        }
        return optionDisplay.toString();
    }
}
