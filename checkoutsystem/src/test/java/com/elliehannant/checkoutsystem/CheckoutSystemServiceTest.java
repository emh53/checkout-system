package com.elliehannant.checkoutsystem;

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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutSystemServiceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    private List<ItemDetails> itemDetailsList;
    private ItemDetails itemDetails;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemDetailsList = new ArrayList<>();
        itemDetails = new ItemDetails("A", 10, 3, 20);
        itemDetailsList.add(itemDetails);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void displayItemDetails_userRespondedYesAndDetailsDisplayed() {
        provideInput("y");
        CheckoutSystemService.displayItemDetails(itemDetailsList);

        String expectedOutput = "Would you like to display the items? [y/n]";
        expectedOutput += "\r\n";
        expectedOutput += "\r\n----------------------------------------------------------";
        expectedOutput += "\r\n ITEM DETAILS:";
        expectedOutput += "\r\n----------------------------------------------------------";
        expectedOutput += String.format("\r\nItem details for %s", itemDetails.getItem());
        expectedOutput += String.format("\r\n Price per unit: %sp", itemDetails.getPricePerUnit());
        expectedOutput += String.format("\r\n Discount: %s for %sp", itemDetails.getDiscountNum(), itemDetails.getDiscountPrice());

        Assert.assertEquals(expectedOutput, getOutput().trim());
    }

    @Test
    public void displayItemDetails_userRespondedNoAndNothingDisplayed() {
        provideInput("n");
        CheckoutSystemService.displayItemDetails(itemDetailsList);

        String expectedOutput = "Would you like to display the items? [y/n]";
        Assert.assertEquals(expectedOutput, getOutput().trim());
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }
}