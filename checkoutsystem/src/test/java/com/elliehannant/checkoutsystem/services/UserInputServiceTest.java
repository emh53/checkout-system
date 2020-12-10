package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.EditingChoice;
import com.elliehannant.checkoutsystem.enums.SampleItem;
import com.elliehannant.checkoutsystem.handlers.UserInputHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserInputServiceTest {

    private static final String YES_OR_NO_EXPECTED_OUTPUT = "\n%s? [y/n]\r\n";
    private static final String ENTER_ITEM_OUTPUT = "\nItem (enter if done): ";
    private static final String STRING_RESPONSE_A_OR_B = "\nQuestion? [A, B]\r\n";
    private static final String EDITING_ITEM_QUESTION = String.format("\nQuestion? %s\r\n", EditingChoice.displayOptions(true));
    private static final String ADD_EDIT_REMOVE_QUESTION = String.format("\nQuestion? %s\r\n", EditingChoice.displayOptions(false));
    private static final String PRICE_QUESTION = "\nQuestion? (in pence)\r\n";
    private static final String QUESTION = "Question";

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    @InjectMocks
    private UserInputService userInputService;

    @Mock
    private ItemDetailsService itemDetailsService;

    private ByteArrayOutputStream testOut;

    private List<String> itemNameList;
    private List<ItemDetails> itemDetailsList;
    private ItemDetails itemDetails;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemNameList = new ArrayList<>();
        itemNameList.add(SampleItem.A.getItemName());
        itemNameList.add(SampleItem.B.getItemName());
        itemNameList.add(SampleItem.C.getItemName());
        itemNameList.add(SampleItem.D.getItemName());

        itemDetailsList = SampleItem.getAllSampleItemDetails();

        itemDetails = SampleItem.getSampleItemDetails(SampleItem.A);
        given(itemDetailsService.getItemDetails(SampleItem.A.getItemName(), itemDetailsList)).willReturn(itemDetails);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    //-----------------------------------------------------------------
    // Get Item For Checkout Transaction Tests
    //-----------------------------------------------------------------
    @Test
    public void getItemForCheckoutTransaction_newLineInputAndConfirm() {
        provideInput("\ny");      //  new line then 'y'(yes)

        ItemDetails response = userInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertNull("Checkout Transaction", response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, "Are you sure you are done");
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_newLineInputAndCancel() {
        provideInput("\nn\nA");      //  new line then 'n'(no) the 'A'

        ItemDetails response = userInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemDetails, response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, "Are you sure you are done");
        expectedOutput += ENTER_ITEM_OUTPUT;
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_validItemInput() {
        provideInput("A");        //  'A'

        ItemDetails response = userInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemDetails, response);
        Assert.assertEquals("Output", ENTER_ITEM_OUTPUT, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_invalidInput() {
        provideInput("1\nA");     // invalid 1 then 'A'

        given(itemDetailsService.getItemNameList(itemDetailsList)).willReturn(itemNameList);
        ItemDetails response = userInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemDetails, response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(UserInputHandler.INVALID_ITEM_SUPPLIED, "[A, B, C, D]");
        expectedOutput += ENTER_ITEM_OUTPUT;
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Yes Or No Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getYesOrNoResponse_yesInput() {
        provideInput("y");    //  yes entered

        boolean response = userInputService.getYesOrNoResponseAsBoolean(QUESTION);
        Assert.assertTrue("YesOrNo", response);

        String expectedOutput = String.format(YES_OR_NO_EXPECTED_OUTPUT, QUESTION);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getYesOrNoResponse_noInput() {
        provideInput("n");    //  no entered

        boolean response = userInputService.getYesOrNoResponseAsBoolean(QUESTION);
        Assert.assertFalse("YesOrNo", response);

        String expectedOutput = String.format(YES_OR_NO_EXPECTED_OUTPUT, QUESTION);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getYesOrNoResponse_invalidInput() {
        provideInput("1\ny");    //  invalid input then yes entered

        boolean response = userInputService.getYesOrNoResponseAsBoolean(QUESTION);
        Assert.assertTrue("YesOrNo", response);

        String expectedOutput = String.format(YES_OR_NO_EXPECTED_OUTPUT, QUESTION);
        expectedOutput += UserInputHandler.INVALID_YES_OR_NO_RESPONSE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Integer Response (2 or above) Tests
    //-----------------------------------------------------------------
    @Test
    public void getIntegerResponseTwoOrAbove_notInteger() {
        provideInput("A\n2");   // invalid String then int

        int response = userInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_validInput() {
        provideInput("2");   // int

        int response = userInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_oneSupplied() {
        provideInput("1\n2");   // invalid value then int

        int response = userInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_TWO + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_zeroSupplied() {
        provideInput("0\n2");   // invalid value then int

        int response = userInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_TWO + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_negativeSupplied() {
        provideInput("-1\n2");   // invalid value then int

        int response = userInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_TWO + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get String Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getStringResponse_validInput() {
        provideInput("A");      //  valid response

        List<String> validResponseList = Arrays.asList("A", "B");
        String response = userInputService.getStringResponse(QUESTION, validResponseList);
        Assert.assertEquals("String response", "A", response);

        Assert.assertEquals("Output", STRING_RESPONSE_A_OR_B, getOutput());
    }

    @Test
    public void getStringResponse_invalidInput() {
        provideInput("1\nA");       // response not in list then valid response

        List<String> validResponseList = Arrays.asList("A", "B");
        String response = userInputService.getStringResponse(QUESTION, validResponseList);
        Assert.assertEquals("String response", "A", response);

        String expectedOutput = STRING_RESPONSE_A_OR_B;
        expectedOutput += UserInputHandler.INVALID_CHOICE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Editing Choice Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getEditingChoiceResponse_notIntegerAndEditingSelectedItem() {
        provideInput("A\n" + EditingChoice.PRICE_PER_UNIT.getValue());    // invalid option then valid

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, true);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        String expectedOutput = EDITING_ITEM_QUESTION;
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_notIntegerAndNotEditingSelectedItem() {
        provideInput("A\n" + EditingChoice.ADD_ITEM.getValue());    // invalid option then valid

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, false);
        Assert.assertEquals("Editing Choice", EditingChoice.ADD_ITEM, response);

        String expectedOutput = ADD_EDIT_REMOVE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_validInputWhenEditingSelectedItemAndCorrectOptionsDisplayed() {
        provideInput(String.valueOf(EditingChoice.PRICE_PER_UNIT.getValue()));    // valid option

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, true);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        Assert.assertEquals("Output", EDITING_ITEM_QUESTION, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_validInputWhenNotEditingSelectedItemAndCorrectOptionsDisplayed() {
        provideInput(String.valueOf(EditingChoice.ADD_ITEM.getValue()));    // valid option

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, false);
        Assert.assertEquals("Editing Choice", EditingChoice.ADD_ITEM, response);

        Assert.assertEquals("Output", ADD_EDIT_REMOVE_QUESTION, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_notAGivenChoiceWhenEditingSelectedItem() {
        provideInput("6\n" + EditingChoice.PRICE_PER_UNIT.getValue());    // invalid option then valid

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, true);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        String expectedOutput = EDITING_ITEM_QUESTION;
        expectedOutput += UserInputHandler.INVALID_CHOICE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_notAGivenChoiceWhenNotEditingSelectedItem() {
        provideInput("6\n" + EditingChoice.ADD_ITEM.getValue());    // invalid option then valid

        EditingChoice response = userInputService.getEditingChoiceResponse(QUESTION, false);
        Assert.assertEquals("Editing Choice", EditingChoice.ADD_ITEM, response);

        String expectedOutput = ADD_EDIT_REMOVE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_CHOICE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Integer Price Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getIntegerPriceResponse_notInteger() {
        provideInput("A\n1");       // not int then valid

        int response = userInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_negativeInput() {
        provideInput("-1\n1");       // negative int then valid

        int response = userInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_0 + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_zeroInputCancel() {
        provideInput("0\nn\n1");       // zero then no (to cancel) then valid

        int response = userInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, UserInputHandler.ZERO_PENCE_WARNING);
        expectedOutput += UserInputHandler.SUPPLY_ANOTHER_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_zeroInputConfirmed() {
        provideInput("0\ny");       // zero then yes (to confirm) then valid

        int response = userInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 0, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, UserInputHandler.ZERO_PENCE_WARNING);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_validInput() {
        provideInput("1");       //  valid input

        int response = userInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        Assert.assertEquals("Output", PRICE_QUESTION, getOutput());
    }

    @Test
    public void getIntegerResponse_catchEnters() {
        provideInput("\n\n\n\n\n\n\n\n2");

        int response = userInputService.getIntegerResponse(new Scanner(System.in));
        Assert.assertEquals(2, response);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }
}