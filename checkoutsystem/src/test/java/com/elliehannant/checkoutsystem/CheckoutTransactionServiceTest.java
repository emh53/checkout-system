package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutTransactionServiceTest {

    public static final int PRICE_PER_UNIT = 50;
    public static final int DISCOUNT_PRICE = 30;

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    ItemDetails itemA;
    List<ItemDetails> availableCheckoutItems;
    List<String> oneItemInCheckout;
    List<String> twoItemsInCheckout;
    List<String> threeItemsInCheckout;
    List<String> sixItemsInCheckout;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemA = new ItemDetails("A", PRICE_PER_UNIT, 3, 130);

        availableCheckoutItems = Collections.singletonList(itemA);
        oneItemInCheckout = Collections.singletonList("A");

        twoItemsInCheckout = Arrays.asList("A", "A");
        threeItemsInCheckout = Arrays.asList("A", "A", "A");
        sixItemsInCheckout = Arrays.asList("A", "A", "A", "A", "A", "A");
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void checkoutItem_itemCheckedOutAndNoDiscountAdded() {
        int expectedUpdatedTotal = 2 * PRICE_PER_UNIT;
        String expectedOutput = "+ 50p (Total: £1.00)\r\n";

        int updatedTotal = CheckoutTransactionService.checkoutItem(PRICE_PER_UNIT, twoItemsInCheckout, itemA);
        Assert.assertEquals("Total price updated", expectedUpdatedTotal, updatedTotal);
        Assert.assertEquals("Total output", expectedOutput, getOutput());
    }

    @Test
    public void checkoutItem_itemCheckedOutAndDiscountAdded() {
        int totalBeforeItemAdded = 2 * PRICE_PER_UNIT;
        int expectedUpdatedTotal = totalBeforeItemAdded + DISCOUNT_PRICE;
        String expectedOutput = "(discount added) + 30p (Total: £1.30)\r\n";

        int updatedTotal = CheckoutTransactionService.checkoutItem(totalBeforeItemAdded, threeItemsInCheckout, itemA);
        Assert.assertEquals("Total price updated", expectedUpdatedTotal, updatedTotal);
        Assert.assertEquals("Total output", expectedOutput, getOutput());
    }

    @Test
    public void getPriceWithDiscountCheck_pricePerUnitReturnedForOneItem() {
        int response = CheckoutTransactionService.getPriceWithDiscountCheck(itemA, oneItemInCheckout);
        Assert.assertEquals("Price per unit", PRICE_PER_UNIT, response);
    }

    @Test
    public void getPriceWithDiscountCheck_pricePerUnitReturnedForTwoItems() {
        int response = CheckoutTransactionService.getPriceWithDiscountCheck(itemA, twoItemsInCheckout);
        Assert.assertEquals("Price per unit", PRICE_PER_UNIT, response);
    }

    @Test
    public void getPriceWithDiscountCheck_pricePerUnitReturnedForDiscountedItem() {
        int response = CheckoutTransactionService.getPriceWithDiscountCheck(itemA, threeItemsInCheckout);
        Assert.assertEquals("Price per unit", DISCOUNT_PRICE, response);
    }

    @Test
    public void getPriceWithDiscountCheck_pricePerUnitReturnedForSecondDiscountedItem() {
        int response = CheckoutTransactionService.getPriceWithDiscountCheck(itemA, sixItemsInCheckout);
        Assert.assertEquals("Price per unit", DISCOUNT_PRICE, response);
    }

    private String getOutput() {
        return testOut.toString();
    }
}