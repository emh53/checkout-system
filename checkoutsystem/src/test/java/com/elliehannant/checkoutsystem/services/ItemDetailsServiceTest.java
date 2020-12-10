package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.SampleItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ItemDetailsServiceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    @InjectMocks
    private ItemDetailsService itemDetailsService;

    List<ItemDetails> sampleItemDetailsList;
    ItemDetails itemA;
    ItemDetails itemB;
    ItemDetails itemC;
    ItemDetails itemD;

    String expectedDisplayForA;
    String expectedDisplayForD;
    String expectedDisplayForAllSampleItems;


    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemA = SampleItem.getSampleItemDetails(SampleItem.A);
        itemB = SampleItem.getSampleItemDetails(SampleItem.B);
        itemC = SampleItem.getSampleItemDetails(SampleItem.C);
        itemD = SampleItem.getSampleItemDetails(SampleItem.D);

        sampleItemDetailsList = new ArrayList<>();
        sampleItemDetailsList.add(itemA);
        sampleItemDetailsList.add(itemB);
        sampleItemDetailsList.add(itemC);
        sampleItemDetailsList.add(itemD);

        //Item Display
        expectedDisplayForA = "Item details for A\r\n Price per unit: 50p\r\n Discount: 3 for £1.30\r\n";
        expectedDisplayForD = "Item details for D\r\n Price per unit: 15p\r\n No Discount\r\n";
        String expectedDisplayForB = "Item details for B\r\n Price per unit: 30p\r\n Discount: 2 for 45p\r\n";
        String expectedDisplayForC = "Item details for C\r\n Price per unit: 20p\r\n No Discount\r\n";

        expectedDisplayForAllSampleItems = "\r\n----------------------------------------------------------";
        expectedDisplayForAllSampleItems += "\r\n ITEM DETAILS:";
        expectedDisplayForAllSampleItems += "\r\n----------------------------------------------------------\r\n";
        expectedDisplayForAllSampleItems += expectedDisplayForA + expectedDisplayForB + expectedDisplayForC + expectedDisplayForD;
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void displayItemDetailsList_displayAllItems() {
        itemDetailsService.displayItemDetailsList(sampleItemDetailsList);
        Assert.assertEquals("All items should be displayed", expectedDisplayForAllSampleItems, getOutput());
    }

    @Test
    public void displayItemDetails_itemWithDiscountDisplayedCorrectly() {
        itemDetailsService.displayItemDetails("A", sampleItemDetailsList);

        String expectedOutput = expectedDisplayForA;
        Assert.assertEquals("Print details for A", expectedOutput, getOutput());
    }

    @Test
    public void displayItemDetails_itemWithoutDiscountDisplayedCorrectly() {
        itemDetailsService.displayItemDetails("D", sampleItemDetailsList);

        String expectedOutput = expectedDisplayForD;
        Assert.assertEquals("Print details for D", expectedOutput, getOutput());
    }

    @Test
    public void getItemNameList_namesReturned() {
        List<String> response = itemDetailsService.getItemNameList(sampleItemDetailsList);

        List<String> itemNames = Arrays.asList("A", "B", "C", "D");
        Assert.assertEquals("Item names", itemNames, response);
    }

    @Test
    public void getItemDetails_correctItemReturned() {
        ItemDetails response = itemDetailsService.getItemDetails("A", sampleItemDetailsList);
        Assert.assertEquals("A returned", itemA, response);
    }

    @Test
    public void getItemDetails_itemNotInList() {
        try {
            itemDetailsService.getItemDetails("Z", sampleItemDetailsList);
        } catch (Exception e) {
            Assert.assertEquals("Item details missing", e.getMessage());
        }
    }

    @Test
    public void formatPrice_penceReturned() {
        String response = itemDetailsService.formatPrice(99);
        Assert.assertEquals("Format price", "99p", response);
    }

    @Test
    public void formatPrice_poundsReturned() {
        String response = itemDetailsService.formatPrice(100);
        Assert.assertEquals("Format Price", "£1.00", response);

        String response2 = itemDetailsService.formatPrice(1000000);
        Assert.assertEquals("Format Price", "£10000.00", response2);

        String response3 = itemDetailsService.formatPrice(43943);
        Assert.assertEquals("Format Price", "£439.43", response3);
    }

    private String getOutput() {
        return testOut.toString();
    }
}