package com.elliehannant.checkoutsystem.userinput;

public enum EditingChoice {
    PRICE_PER_UNIT(1, "1. Price Per Unit"),
    ADD_OR_EDIT_DISCOUNT(2, "2. Add or Edit Discount"),
    REMOVE_DISCOUNT(3, "3. Remove Discount");

    private final int value;
    private final String optionDisplay;

    EditingChoice(int value, String optionDisplay) {
        this.value = value;
        this.optionDisplay = optionDisplay;
    }

    public int getValue() {
        return value;
    }

    public String getOptionDisplay() {
        return optionDisplay;
    }

    public static String displayAllOptionsInList() {
        StringBuilder optionDisplay = new StringBuilder();
        for (EditingChoice editingChoice : values()) {
            optionDisplay.append("\n ").append(editingChoice.getOptionDisplay());
        }
        return optionDisplay.toString();
    }
}
