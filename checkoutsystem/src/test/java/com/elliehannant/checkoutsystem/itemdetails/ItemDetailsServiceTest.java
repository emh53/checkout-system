package com.elliehannant.checkoutsystem.itemdetails;

import com.elliehannant.checkoutsystem.userinput.UserInputService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ItemDetailsServiceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    List<ItemDetails> sampleItemDetailsList;
    List<ItemDetails> itemDetailsBAndC;
    ItemDetails itemA;
    ItemDetails itemB;
    ItemDetails itemC;
    ItemDetails itemD;

    String expectedDisplayForA;
    String expectedDisplayForB;
    String expectedDisplayForC;
    String expectedDisplayForD;


    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemA = new ItemDetails("A", 50, 3, 130);
        itemB = new ItemDetails("B", 30, 2, 45);
        itemC = new ItemDetails("C", 20);
        itemD = new ItemDetails("D", 15);

        sampleItemDetailsList = new ArrayList<>();
        sampleItemDetailsList.add(itemA);
        sampleItemDetailsList.add(itemB);
        sampleItemDetailsList.add(itemC);
        sampleItemDetailsList.add(itemD);

        itemDetailsBAndC = new ArrayList<>();
        itemDetailsBAndC.add(itemB);
        itemDetailsBAndC.add(itemC);

        //Item Display
        expectedDisplayForA = "Item details for A\r\n Price per unit: 50p\r\n Discount: 3 for £1.30\r\n";
        expectedDisplayForB = "Item details for B\r\n Price per unit: 30p\r\n Discount: 2 for 45p\r\n";
        expectedDisplayForC = "Item details for C\r\n Price per unit: 20p\r\n No Discount\r\n";
        expectedDisplayForD = "Item details for D\r\n Price per unit: 15p\r\n No Discount\r\n";
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void editItemDetails_notEditedIfUserInputsNo() {
        provideInput("n");
        ItemDetailsService.editItemDetails(sampleItemDetailsList);
        Mockito.verifyNoInteractions(mock(UpdatingItemDetailsService.class));
        Mockito.verifyNoInteractions(mock(UserInputService.class));
    }

    @Test
    public void displayItemDetailsList_displayAllItems() {
        ItemDetailsService.displayItemDetailsList(itemDetailsBAndC);

        String expectedOutput = expectedDisplayForB + expectedDisplayForC;
        Assert.assertEquals("Both items displayed", expectedOutput, getOutput());
    }

    @Test
    public void displayItemDetails_itemWithDiscountDisplayedCorrectly() {
        ItemDetailsService.displayItemDetails("A", sampleItemDetailsList);

        String expectedOutput = expectedDisplayForA;
        Assert.assertEquals("Print details for A", expectedOutput, getOutput());
    }

    @Test
    public void displayItemDetails_itemWithoutDiscountDisplayedCorrectly() {
        ItemDetailsService.displayItemDetails("D", sampleItemDetailsList);

        String expectedOutput = expectedDisplayForD;
        Assert.assertEquals("Print details for D", expectedOutput, getOutput());
    }

    @Test
    public void getItemNameList_namesReturned() {
        List<String> response = ItemDetailsService.getItemNameList(sampleItemDetailsList);

        List<String> itemNames = Arrays.asList("A", "B", "C", "D");
        Assert.assertEquals("Item names", itemNames, response);
    }

    @Test
    public void getItemDetails_correctItemReturned() {
        ItemDetails response = ItemDetailsService.getItemDetails("A", sampleItemDetailsList);
        Assert.assertEquals("A returned", itemA, response);
    }

    @Test
    public void getAllSampleItemDetails_allSampleItemsReturned() {
        List<ItemDetails> actualResponse = ItemDetailsService.getAllSampleItemDetails();

        ItemDetails A = actualResponse.get(0);
        Assert.assertEquals("Item A - Name", itemA.getItem(), A.getItem());
        Assert.assertEquals("Item A - Price", itemA.getPricePerUnit(), A.getPricePerUnit());
        Assert.assertEquals("Item A - Discount Num", itemA.getDiscountNum(), A.getDiscountNum());
        Assert.assertEquals("Item A - Discount Price", itemA.getDiscountPrice(), A.getDiscountPrice());
        Assert.assertEquals("Item A - Discounted Item Price", Integer.valueOf(30), A.getDiscountedItemPrice());
        Assert.assertTrue("Item A   - Is Item Discounted", A.isItemDiscounted());

        ItemDetails B = actualResponse.get(1);
        Assert.assertEquals("Item B - Name", itemB.getItem(), B.getItem());
        Assert.assertEquals("Item B - Price", itemB.getPricePerUnit(), B.getPricePerUnit());
        Assert.assertEquals("Item B - Discount Num", itemB.getDiscountNum(), B.getDiscountNum());
        Assert.assertEquals("Item B - Discount Price", itemB.getDiscountPrice(), B.getDiscountPrice());
        Assert.assertEquals("Item B - Discounted Item Price", Integer.valueOf(15), B.getDiscountedItemPrice());
        Assert.assertTrue("Item B - Is Item Discounted", B.isItemDiscounted());

        ItemDetails C = actualResponse.get(2);
        Assert.assertEquals("Item C - Name", itemC.getItem(), C.getItem());
        Assert.assertEquals("Item C - Price", itemC.getPricePerUnit(), C.getPricePerUnit());
        Assert.assertNull("Item C - Discount Num", C.getDiscountNum());
        Assert.assertNull("Item C - Discount Price", C.getDiscountPrice());
        Assert.assertNull("Item C - Discounted Item Price", C.getDiscountedItemPrice());
        Assert.assertFalse("Item C - Is Item Discounted", C.isItemDiscounted());

        ItemDetails D = actualResponse.get(3);
        Assert.assertEquals("Item D - Name", itemD.getItem(), D.getItem());
        Assert.assertEquals("Item D - Price", itemD.getPricePerUnit(), D.getPricePerUnit());
        Assert.assertNull("Item D - Discount Num", D.getDiscountNum());
        Assert.assertNull("Item D - Discount Price", D.getDiscountPrice());
        Assert.assertNull("Item D - Discounted Item Price", D.getDiscountedItemPrice());
        Assert.assertFalse("Item D - Is Item Discounted", D.isItemDiscounted());
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }
}