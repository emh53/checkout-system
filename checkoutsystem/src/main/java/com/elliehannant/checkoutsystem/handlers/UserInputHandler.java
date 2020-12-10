package com.elliehannant.checkoutsystem.handlers;

import java.util.Scanner;

public class UserInputHandler {

    public static final String INVALID_ITEM_SUPPLIED = " Please supply a valid item, %s";
    public static final String INVALID_YES_OR_NO_RESPONSE = " Please answer 'y for yes' or 'n for no'";
    public static final String INVALID_VALUE_BELOW_TWO = " Please enter a value above 1";
    public static final String INVALID_CHOICE = " Please supply one of the given choices above";
    public static final String SUPPLY_ANOTHER_VALUE = " Please supply another value";
    public static final String ZERO_PENCE_WARNING = " Are you sure you would like to set the price to 0p";
    public static final String INVALID_VALUE_BELOW_0 = " Please supply a positive value";
    public static final String INVALID_NON_NUMERICAL_VALUE = " Please enter a numerical value";

    public static boolean isUserInputInteger(Scanner userInput) {
        while (true) {
            if (!userInput.hasNextInt()) {
                String input = userInput.nextLine();
                if (input == null || input.equals("")) {    // handles 'enter'
                    continue;
                }
                System.out.println(INVALID_NON_NUMERICAL_VALUE);
                return false;
            }
            return true;
        }
    }
}
