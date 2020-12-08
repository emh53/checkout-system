package com.elliehannant.checkoutsystem.userinput;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService;

import java.util.List;
import java.util.Scanner;

public class UserInputService {

    private static final String QUESTION_WITH_OPTIONS = "\n%s? %s%n";

    public static ItemDetails getItemForCheckoutTransaction(List<ItemDetails> itemDetailsList) {
        Scanner userInput = new Scanner(System.in);
        while (true) {
            System.out.print("\nItem (enter if done): ");
            String response = userInput.nextLine();

            if (response == null || response.equals("")) {
                if (getYesOrNoResponseAsBoolean("Are you sure you are done", userInput)) {
                    return null;
                } else {
                    continue;
                }
            }

            ItemDetails itemDetails = ItemDetailsService.getItemDetails(response, itemDetailsList);
            if (itemDetails == null) {
                System.out.printf(UserInputHandler.INVALID_ITEM_SUPPLIED, ItemDetailsService.getItemNameList(itemDetailsList).toString());
            } else {
                return itemDetails;
            }
        }
    }

    public static boolean getYesOrNoResponseAsBoolean(String question, Scanner userInput) {
        System.out.printf("\n%s? [y/n]%n", question);

        while (true) {
            String response = userInput.nextLine();
            if (response.equalsIgnoreCase("y")) {
                return true;
            } else if (response.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println(UserInputHandler.INVALID_YES_OR_NO_RESPONSE);
            }
        }
    }

    public static boolean getYesOrNoResponseAsBoolean(String question) {
        Scanner userInput = new Scanner(System.in);
        return getYesOrNoResponseAsBoolean(question, userInput);
    }

    public static int getIntegerResponseTwoOrAbove(String question) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf("\n%s?%n", question);
        while (true) {
            if (UserInputHandler.isUserInputInteger(userInput)) {
                int response = getIntegerResponse(userInput);
                if (response >= 2) {
                    return response;
                } else {
                    System.out.println(UserInputHandler.INVALID_VALUE_BELOW_TWO);
                }
            }
        }
    }

    public static String getStringResponse(String question, List<String> validResponseList) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf(QUESTION_WITH_OPTIONS, question, validResponseList.toString());

        while (true) {
            String response = userInput.nextLine();
            for (String validResponse : validResponseList) {
                if (validResponse.equals(response)) {
                    return validResponse;
                }
            }
            System.out.println(UserInputHandler.INVALID_CHOICE);
        }
    }

    public static EditingChoice getEditingChoiceResponse(String question) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf(QUESTION_WITH_OPTIONS, question, EditingChoice.displayAllOptionsInList());

        while (true) {
            if (UserInputHandler.isUserInputInteger(userInput)) {
                int response = getIntegerResponse(userInput);
                for (EditingChoice choice : EditingChoice.values()) {
                    if (response == choice.getValue()) {
                        return choice;
                    }
                }
                System.out.println(UserInputHandler.INVALID_CHOICE);
            }
        }

    }

    public static int getIntegerPriceResponse(String question) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf("\n%s? (in pence)%n", question);

        while (true) {
            if (UserInputHandler.isUserInputInteger(userInput)) {
                int response = getIntegerResponse(userInput);
                if (response == 0) {
                    if (getYesOrNoResponseAsBoolean(UserInputHandler.ZERO_PENCE_WARNING, userInput)) {
                        return response;
                    } else {
                        System.out.println(UserInputHandler.SUPPLY_ANOTHER_VALUE);
                    }
                } else if (response < 0) {
                    System.out.println(UserInputHandler.INVALID_VALUE_BELOW_0);
                } else {
                    return response;
                }
            }
        }
    }

    private static int getIntegerResponse(Scanner userInput) {
        while (true) {
            String input = userInput.nextLine();
            if (input != null && !input.equals("")) {
                return Integer.parseInt(input);
            }
        }
    }
}
