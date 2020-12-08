package com.elliehannant.checkoutsystem.userinput;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserInputServiceTest {

    private static final String YES_OR_NO_EXPECTED_OUTPUT = "\n%s? [y/n]\r\n";
    private static final String ENTER_ITEM_OUTPUT = "\nItem (enter if done): ";
    private static final String STRING_RESPONSE_A_OR_B = "\nQuestion? [A, B]\r\n";
    private static final String EDITING_CHOICE_QUESTION = String.format("\nQuestion? %s\r\n", EditingChoice.displayAllOptionsInList());
    private static final String PRICE_QUESTION = "\nQuestion? (in pence)\r\n";
    private static final String QUESTION = "Question";

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
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

        List<ItemDetails> itemDetailsList = new ArrayList<>();
        ItemDetails response = UserInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertNull("Checkout Transaction", response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, "Are you sure you are done");
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_newLineInputAndCancel() {
        provideInput("\nn\nA");      //  new line then 'n'(no) the 'A'

        ItemDetails itemA = new ItemDetails("A", 50, 3, 130);
        List<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemA);

        ItemDetails response = UserInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemA, response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, "Are you sure you are done");
        expectedOutput += ENTER_ITEM_OUTPUT;
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_validItemInput() {
        provideInput("A");        //  'A'

        ItemDetails itemA = new ItemDetails("A", 50, 3, 130);
        List<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemA);

        ItemDetails response = UserInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemA, response);

        Assert.assertEquals("Output", ENTER_ITEM_OUTPUT, getOutput());
    }

    @Test
    public void getItemForCheckoutTransaction_invalidInput() {
        provideInput("1\nA");     // invalid 1 then 'A'

        ItemDetails itemA = new ItemDetails("A", 50, 3, 130);
        List<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemA);

        ItemDetails response = UserInputService.getItemForCheckoutTransaction(itemDetailsList);
        Assert.assertEquals("Checkout Transaction", itemA, response);

        String expectedOutput = ENTER_ITEM_OUTPUT;
        expectedOutput += String.format(UserInputHandler.INVALID_ITEM_SUPPLIED, "[A]");
        expectedOutput += ENTER_ITEM_OUTPUT;
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Yes Or No Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getYesOrNoResponse_yesInput() {
        provideInput("y");    //  yes entered

        boolean response = UserInputService.getYesOrNoResponseAsBoolean(QUESTION);
        Assert.assertTrue("YesOrNo", response);

        String expectedOutput = String.format(YES_OR_NO_EXPECTED_OUTPUT, QUESTION);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getYesOrNoResponse_noInput() {
        provideInput("n");    //  no entered

        boolean response = UserInputService.getYesOrNoResponseAsBoolean(QUESTION);
        Assert.assertFalse("YesOrNo", response);

        String expectedOutput = String.format(YES_OR_NO_EXPECTED_OUTPUT, QUESTION);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getYesOrNoResponse_invalidInput() {
        provideInput("1\ny");    //  invalid input then yes entered

        boolean response = UserInputService.getYesOrNoResponseAsBoolean(QUESTION);
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

        int response = UserInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_validInput() {
        provideInput("2");   // int

        int response = UserInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_oneSupplied() {
        provideInput("1\n2");   // invalid value then int

        int response = UserInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_TWO + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_zeroSupplied() {
        provideInput("0\n2");   // invalid value then int

        int response = UserInputService.getIntegerResponseTwoOrAbove(QUESTION);
        Assert.assertEquals("Integer response", 2, response);

        String expectedOutput = "\n" + QUESTION + "?\r\n";
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_TWO + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerResponseTwoOrAbove_negativeSupplied() {
        provideInput("-1\n2");   // invalid value then int

        int response = UserInputService.getIntegerResponseTwoOrAbove(QUESTION);
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
        String response = UserInputService.getStringResponse(QUESTION, validResponseList);
        Assert.assertEquals("String response", "A", response);

        Assert.assertEquals("Output", STRING_RESPONSE_A_OR_B, getOutput());
    }

    @Test
    public void getStringResponse_invalidInput() {
        provideInput("1\nA");       // response not in list then valid response

        List<String> validResponseList = Arrays.asList("A", "B");
        String response = UserInputService.getStringResponse(QUESTION, validResponseList);
        Assert.assertEquals("String response", "A", response);

        String expectedOutput = STRING_RESPONSE_A_OR_B;
        expectedOutput += UserInputHandler.INVALID_CHOICE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Editing Choice Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getEditingChoiceResponse_notInteger() {
        provideInput("A\n" + EditingChoice.PRICE_PER_UNIT.getValue());    // invalid option then valid

        EditingChoice response = UserInputService.getEditingChoiceResponse(QUESTION);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        String expectedOutput = EDITING_CHOICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_validInput() {
        provideInput(String.valueOf(EditingChoice.PRICE_PER_UNIT.getValue()));    // valid option

        EditingChoice response = UserInputService.getEditingChoiceResponse(QUESTION);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        Assert.assertEquals("Output", EDITING_CHOICE_QUESTION, getOutput());
    }

    @Test
    public void getEditingChoiceResponse_invalidInput() {
        provideInput("6\n" + EditingChoice.PRICE_PER_UNIT.getValue());    // invalid option then valid

        EditingChoice response = UserInputService.getEditingChoiceResponse(QUESTION);
        Assert.assertEquals("Editing Choice", EditingChoice.PRICE_PER_UNIT, response);

        String expectedOutput = EDITING_CHOICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_CHOICE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    //-----------------------------------------------------------------
    // Get Integer Price Response Tests
    //-----------------------------------------------------------------
    @Test
    public void getIntegerPriceResponse_notInteger() {
        provideInput("A\n1");       // not int then valid

        int response = UserInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_NON_NUMERICAL_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_negativeInput() {
        provideInput("-1\n1");       // negative int then valid

        int response = UserInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += UserInputHandler.INVALID_VALUE_BELOW_0 + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_zeroInputCancel() {
        provideInput("0\nn\n1");       // zero then no (to cancel) then valid

        int response = UserInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, UserInputHandler.ZERO_PENCE_WARNING);
        expectedOutput += UserInputHandler.SUPPLY_ANOTHER_VALUE + "\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_zeroInputConfirmed() {
        provideInput("0\ny");       // zero then yes (to confirm) then valid

        int response = UserInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 0, response);

        String expectedOutput = PRICE_QUESTION;
        expectedOutput += String.format(YES_OR_NO_EXPECTED_OUTPUT, UserInputHandler.ZERO_PENCE_WARNING);
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void getIntegerPriceResponse_validInput() {
        provideInput("1");       //  valid input

        int response = UserInputService.getIntegerPriceResponse(QUESTION);
        Assert.assertEquals("Price", 1, response);

        Assert.assertEquals("Output", PRICE_QUESTION, getOutput());
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }
}