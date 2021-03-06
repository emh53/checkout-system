package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.EditingChoice;
import com.elliehannant.checkoutsystem.handlers.UserInputHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class UserInputService {

    private static final String QUESTION_WITH_OPTIONS = "\n%s? %s%n";

    private final ItemDetailsService itemDetailsService;

    public UserInputService(ItemDetailsService itemDetailsService) {
        this.itemDetailsService = itemDetailsService;
    }

    public ItemDetails getItemForCheckoutTransaction(List<ItemDetails> itemDetailsList) {
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

            ItemDetails itemDetails = itemDetailsService.getItemDetails(response, itemDetailsList);
            if (itemDetails == null) {
                System.out.printf(UserInputHandler.INVALID_ITEM_SUPPLIED, itemDetailsService.getItemNameList(itemDetailsList).toString());
            } else {
                return itemDetails;
            }
        }
    }

    public boolean getYesOrNoResponseAsBoolean(String question, Scanner userInput) {
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

    public boolean getYesOrNoResponseAsBoolean(String question) {
        Scanner userInput = new Scanner(System.in);
        return getYesOrNoResponseAsBoolean(question, userInput);
    }

    public int getIntegerResponseTwoOrAbove(String question) {
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

    public String getStringResponse(String question, List<String> validResponseList) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf(QUESTION_WITH_OPTIONS, question, validResponseList.toString());

        while (true) {
            String response = userInput.nextLine();
            for (String validResponse : validResponseList) {
                if (validResponse.equalsIgnoreCase(response)) {
                    return validResponse;
                }
            }
            System.out.println(UserInputHandler.INVALID_CHOICE);
        }
    }

    public EditingChoice getEditingChoiceResponse(String question, boolean editingSelectedItem) {
        Scanner userInput = new Scanner(System.in);
        System.out.printf(QUESTION_WITH_OPTIONS, question, EditingChoice.displayOptions(editingSelectedItem));
        while (true) {
            if (UserInputHandler.isUserInputInteger(userInput)) {
                int response = getIntegerResponse(userInput);
                for (EditingChoice choice : EditingChoice.getOptions(editingSelectedItem)) {
                    if (response == choice.getValue()) {
                        return choice;
                    }
                }
                System.out.println(UserInputHandler.INVALID_CHOICE);
            }
        }

    }

    public int getIntegerPriceResponse(String question) {
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

    // catch when user enters instead of integer
    int getIntegerResponse(Scanner userInput) {
        while (true) {
            String input = userInput.nextLine();
            if (input != null && !input.equals("")) {
                return Integer.parseInt(input);
            }
        }
    }
}
