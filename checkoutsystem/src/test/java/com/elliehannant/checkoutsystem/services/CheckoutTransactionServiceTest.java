package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.SampleItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.elliehannant.checkoutsystem.services.CheckoutTransactionService.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutTransactionServiceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    @InjectMocks
    private CheckoutTransactionService checkoutTransactionService;

    @Mock
    private UserInputService userInputService;

    @Mock
    private ItemDetailsService itemDetailsService;

    List<ItemDetails> itemsAvailableForCheckout;
    ItemDetails itemWithDiscount;
    ItemDetails itemWithoutDiscount;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemWithDiscount = SampleItem.getSampleItemDetails(SampleItem.A);
        itemWithoutDiscount = SampleItem.getSampleItemDetails(SampleItem.C);

        itemsAvailableForCheckout = new ArrayList<>();
        itemsAvailableForCheckout.add(itemWithDiscount);
        itemsAvailableForCheckout.add(itemWithoutDiscount);

        given(itemDetailsService.formatPrice(0)).willReturn("0p");
        given(itemDetailsService.formatPrice(50)).willReturn("50p");
        given(itemDetailsService.formatPrice(30)).willReturn("30p");
        given(itemDetailsService.formatPrice(100)).willReturn("£1.00");
        given(itemDetailsService.formatPrice(130)).willReturn("£1.30");
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void runCheckoutTransaction_noItemsCheckedOut() {
        given(userInputService.getItemForCheckoutTransaction(itemsAvailableForCheckout)).willReturn(null);

        String expectedResponse = String.format(FINAL_TOTAL_DISPLAY, "0p", "[]");
        String response = checkoutTransactionService.runCheckoutTransaction(itemsAvailableForCheckout);
        Assert.assertEquals("Final total response", expectedResponse, response);
    }

    @Test
    public void runCheckoutTransaction_oneItemCheckedOut() {
        given(userInputService.getItemForCheckoutTransaction(itemsAvailableForCheckout)).willReturn(itemWithDiscount, (ItemDetails) null);

        String expectedResponse = String.format(FINAL_TOTAL_DISPLAY, "50p", "[A]");
        String response = checkoutTransactionService.runCheckoutTransaction(itemsAvailableForCheckout);
        Assert.assertEquals("Final total response", expectedResponse, response);
    }

    @Test
    public void runCheckoutTransaction_moreThanOneItemCheckedOut() {
        given(userInputService.getItemForCheckoutTransaction(itemsAvailableForCheckout)).willReturn(itemWithDiscount, itemWithDiscount, null);

        String expectedResponse = String.format(FINAL_TOTAL_DISPLAY, "£1.00", "[A, A]");
        String response = checkoutTransactionService.runCheckoutTransaction(itemsAvailableForCheckout);
        Assert.assertEquals("Final total response", expectedResponse, response);
    }

    @Test
    public void updateTotalPrice_totalUpdatedWithoutDiscountAndStartingTotalZero() {
        List<String> itemsInCheckout = Collections.singletonList("A");

        int expectedResponse = 50;
        int response = checkoutTransactionService.updateTotalPrice(0, itemsInCheckout, itemWithDiscount);
        Assert.assertEquals("Updated total price", expectedResponse, response);

        String expectedOutput = String.format(RUNNING_TOTAL_DISPLAY, "50p", "50p");
        Assert.assertEquals("System output", expectedOutput, getOutput());
    }

    @Test
    public void updateTotalPrice_totalUpdatedWithoutDiscountAndStartingTotalNotZero() {
        List<String> itemsInCheckout = Arrays.asList("A", "A");

        int expectedResponse = 100;
        int response = checkoutTransactionService.updateTotalPrice(50, itemsInCheckout, itemWithDiscount);
        Assert.assertEquals("Updated total price", expectedResponse, response);

        String expectedOutput = String.format(RUNNING_TOTAL_DISPLAY, "50p", "£1.00");
        Assert.assertEquals("System output", expectedOutput, getOutput());
    }

    @Test
    public void updateTotalPrice_totalUpdatedWithDiscount() {
        List<String> itemsInCheckout = Arrays.asList("A", "A", "A");

        int expectedResponse = 130;
        int response = checkoutTransactionService.updateTotalPrice(100, itemsInCheckout, itemWithDiscount);
        Assert.assertEquals("Updated total price", expectedResponse, response);

        String expectedOutput = String.format(RUNNING_TOTAL_DISCOUNT_DISPLAY, "30p", "£1.30");
        Assert.assertEquals("System output", expectedOutput, getOutput());
    }

    @Test
    public void getPriceWithDiscountCheck_noDiscountAvailable() {
        List<String> itemsInCheckout = Arrays.asList("C", "C");

        int response = checkoutTransactionService.getPriceWithDiscountCheck(itemWithoutDiscount, itemsInCheckout);
        Assert.assertEquals("Checkout price", SampleItem.C.getUnitPrice(), response);
    }

    @Test
    public void getPriceWithDiscountCheck_discountAvailableAndNotDiscountedItem() {
        List<String> itemsInCheckout = Arrays.asList("A", "A");

        int response = checkoutTransactionService.getPriceWithDiscountCheck(itemWithDiscount, itemsInCheckout);
        Assert.assertEquals("Checkout price", SampleItem.A.getUnitPrice(), response);
    }

    @Test
    public void getPriceWithDiscountCheck_discountAvailableAndDiscountedItem() {
        List<String> itemsInCheckout = Arrays.asList("A", "A", "A");

        int response = checkoutTransactionService.getPriceWithDiscountCheck(itemWithDiscount, itemsInCheckout);
        Assert.assertEquals("Checkout price", 30, response);
    }

    private String getOutput() {
        return testOut.toString();
    }
}