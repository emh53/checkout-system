package com.elliehannant.checkoutsystem.itemdetails;

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

@RunWith(MockitoJUnitRunner.class)
public class UpdatingItemDetailsServiceTest {

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

    @Test
    public void updatePricePerUnit_withNoDiscount() {
        int newPricePerUnit = 60;
        ItemDetails itemDetails = new ItemDetails("A", 50);

        provideInput(String.valueOf(newPricePerUnit));
        UpdatingItemDetailsService.updatePricePerUnit(itemDetails);
        Assert.assertEquals("Price Per Unit", newPricePerUnit, itemDetails.getPricePerUnit());
    }

    @Test
    public void updatePricePerUnit_withDiscount() {
        int newPricePerUnit = 60;
        ItemDetails itemDetails = new ItemDetails("A", 50, 3, 130);

        provideInput(String.valueOf(newPricePerUnit));

        UpdatingItemDetailsService.updatePricePerUnit(itemDetails);
        Assert.assertEquals("Price Per Unit", newPricePerUnit, itemDetails.getPricePerUnit());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(10), itemDetails.getDiscountedItemPrice());
    }

    @Test
    public void updateDiscount() {
        Integer newDiscountNum = 4;
        Integer newDiscountPrice = 155;
        ItemDetails itemDetails = new ItemDetails("A", 50, 3, 130);

        UpdatingItemDetailsService.updateDiscount(itemDetails, newDiscountNum, newDiscountPrice);
        Assert.assertEquals("Discount Num", newDiscountNum, itemDetails.getDiscountNum());
        Assert.assertEquals("Discount Price", newDiscountPrice, itemDetails.getDiscountPrice());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(5), itemDetails.getDiscountedItemPrice());
        Assert.assertTrue("Discounted", itemDetails.isItemDiscounted());
    }

    @Test
    public void removeDiscount_confirmRemove() {
        provideInput("y");
        ItemDetails itemDetails = new ItemDetails("A", 50, 3, 130);

        UpdatingItemDetailsService.removeDiscount(itemDetails);
        Assert.assertNull("Discount Num", itemDetails.getDiscountNum());
        Assert.assertNull("Discount Price", itemDetails.getDiscountPrice());
        Assert.assertNull("Discounted Item Price", itemDetails.getDiscountedItemPrice());
        Assert.assertFalse("Discounted", itemDetails.isItemDiscounted());

        String expectedOutput = "\nAre you sure you would like to remove the discount for A? [y/n]\r\n";
        expectedOutput += "Item A's discount has been successfully removed\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void removeDiscount_cancelRemove() {
        provideInput("n");
        ItemDetails itemDetails = new ItemDetails("A", 50, 3, 130);

        UpdatingItemDetailsService.removeDiscount(itemDetails);
        Assert.assertEquals("Discount Num", Integer.valueOf(3), itemDetails.getDiscountNum());
        Assert.assertEquals("Discount Price", Integer.valueOf(130), itemDetails.getDiscountPrice());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(30), itemDetails.getDiscountedItemPrice());
        Assert.assertTrue("Discounted", itemDetails.isItemDiscounted());

        String expectedOutput = "\nAre you sure you would like to remove the discount for A? [y/n]\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }
}