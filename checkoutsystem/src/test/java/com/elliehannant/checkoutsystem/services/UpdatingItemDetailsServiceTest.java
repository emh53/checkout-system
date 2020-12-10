package com.elliehannant.checkoutsystem.services;

import com.elliehannant.checkoutsystem.dtos.ItemDetails;
import com.elliehannant.checkoutsystem.enums.EditingChoice;
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

import static com.elliehannant.checkoutsystem.services.UpdatingItemDetailsService.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdatingItemDetailsServiceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    @InjectMocks
    private UpdatingItemDetailsService updatingItemDetailsService;

    @Mock
    private UserInputService userInputService;

    @Mock
    private ItemDetailsService itemDetailsService;

    private ItemDetails itemWithDiscount;
    private ItemDetails itemWithoutDiscount;
    private List<ItemDetails> allSampleItems;
    private List<String> itemNames;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        itemWithDiscount = SampleItem.getSampleItemDetails(SampleItem.A);
        itemWithoutDiscount = SampleItem.getSampleItemDetails(SampleItem.C);

        allSampleItems = SampleItem.getAllSampleItemDetails();
        itemNames = Arrays.asList("A", "B", "C", "D");
        given(itemDetailsService.getItemNameList(allSampleItems)).willReturn(itemNames);
        given(userInputService.getStringResponse(EDIT_WHICH_ITEM, itemDetailsService.getItemNameList(allSampleItems))).willReturn(SampleItem.A.getItemName());
        given(itemDetailsService.getItemDetails(SampleItem.A.getItemName(), allSampleItems)).willReturn(SampleItem.getSampleItemDetails(SampleItem.A));
        given(userInputService.getEditingChoiceResponse(WHICH_EDITING_CHOICE, true)).willReturn(EditingChoice.PRICE_PER_UNIT);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void runItemDetailsUpdate_notEditedIfUserDoesNotEdit() {
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEMS)).willReturn(false);

        updatingItemDetailsService.runItemDetailsUpdate(allSampleItems);
        verify(userInputService, times(0)).getEditingChoiceResponse(WHICH_EDITING_CHOICE, false);
    }

    @Test
    public void runItemDetailsUpdate_editedOnceIfUserEditsOnce() {
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEMS)).willReturn(true);
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ANOTHER_ITEM)).willReturn(false);

        updatingItemDetailsService.runItemDetailsUpdate(allSampleItems);
        verify(userInputService, times(1)).getEditingChoiceResponse(WHICH_EDITING_CHOICE, false);
    }

    @Test
    public void runItemDetailsUpdate_editedOnceIfUserEditsTwice() {
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEMS)).willReturn(true);
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ANOTHER_ITEM)).willReturn(true, false);

        updatingItemDetailsService.runItemDetailsUpdate(allSampleItems);
        verify(userInputService, times(2)).getEditingChoiceResponse(WHICH_EDITING_CHOICE, false);
    }

    @Test
    public void addItem_itemAddedWithoutDiscount() {
        given(userInputService.getStringResponse(ADD_WHICH_ITEM, updatingItemDetailsService.availableItemsToAdd(itemNames))).willReturn("E");
        given(userInputService.getIntegerPriceResponse(String.format(WHAT_NEW_UNIT_PRICE, "E"))).willReturn(20);
        given(userInputService.getYesOrNoResponseAsBoolean(ADD_A_DISCOUNT)).willReturn(false);

        updatingItemDetailsService.addItem(allSampleItems);

        ItemDetails lastItem = allSampleItems.get(allSampleItems.size() - 1);
        Assert.assertEquals("Item added", "E", lastItem.getItem());
        Assert.assertEquals("Item added", 20, lastItem.getPricePerUnit());
        Assert.assertNull("Item added", lastItem.getDiscountNum());
        Assert.assertNull("Item added", lastItem.getDiscountPrice());
        Assert.assertNull("Item added", lastItem.getDiscountedItemPrice());
        Assert.assertFalse("Item added", lastItem.isItemDiscounted());
    }

    @Test
    public void addItem_itemAddedWithDiscount() {
        given(userInputService.getStringResponse(ADD_WHICH_ITEM, updatingItemDetailsService.availableItemsToAdd(itemNames))).willReturn("E");
        given(userInputService.getIntegerPriceResponse(String.format(WHAT_NEW_UNIT_PRICE, "E"))).willReturn(20);
        given(userInputService.getYesOrNoResponseAsBoolean(ADD_A_DISCOUNT)).willReturn(true);
        given(userInputService.getIntegerResponseTwoOrAbove(WHAT_NEW_DISCOUNT_NUM)).willReturn(3);
        given(userInputService.getIntegerPriceResponse(WHAT_NEW_DISCOUNT_PRICE)).willReturn(45);

        updatingItemDetailsService.addItem(allSampleItems);

        ItemDetails lastItem = allSampleItems.get(allSampleItems.size() - 1);
        Assert.assertEquals("Item added", "E", lastItem.getItem());
        Assert.assertEquals("Item added", 20, lastItem.getPricePerUnit());
        Assert.assertEquals("Item added", Integer.valueOf(3), lastItem.getDiscountNum());
        Assert.assertEquals("Item added", Integer.valueOf(45), lastItem.getDiscountPrice());
        Assert.assertEquals("Item added", Integer.valueOf(5), lastItem.getDiscountedItemPrice());
        Assert.assertTrue("Item added", lastItem.isItemDiscounted());
    }

    @Test
    public void editItem_editItemOnce() {
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEM_FURTHER)).willReturn(false);

        updatingItemDetailsService.editItem(allSampleItems);
        verify(userInputService, times(1)).getEditingChoiceResponse(WHICH_EDITING_CHOICE, true);
    }

    @Test
    public void editItem_editItemTwice() {
        given(userInputService.getYesOrNoResponseAsBoolean(EDIT_ITEM_FURTHER)).willReturn(true, false);

        updatingItemDetailsService.editItem(allSampleItems);
        verify(userInputService, times(2)).getEditingChoiceResponse(WHICH_EDITING_CHOICE, true);
    }

    @Test
    public void removeItem_confirmSuccessfulRemove() {
        given(userInputService.getStringResponse(REMOVE_WHICH_ITEM, itemNames)).willReturn("A");
        given(userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_ITEM, "A"))).willReturn(true);

        Assert.assertEquals("Item not yet removed", 4, allSampleItems.size());
        updatingItemDetailsService.removeItem(allSampleItems);

        Assert.assertEquals("Item removed", 3, allSampleItems.size());
        for (ItemDetails itemDetails : allSampleItems) {
            Assert.assertNotEquals("Item removed", "A", itemDetails.getItem());
        }
    }

    @Test
    public void removeItem_cancelledSuccessfulRemove() {
        given(userInputService.getStringResponse(REMOVE_WHICH_ITEM, itemNames)).willReturn("A");
        given(userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_ITEM, "A"))).willReturn(false);

        Assert.assertEquals("Item not yet removed", 4, allSampleItems.size());
        updatingItemDetailsService.removeItem(allSampleItems);

        Assert.assertEquals("Item removed", 4, allSampleItems.size());
    }

    @Test
    public void removeItem_unSuccessfulRemove() {
        Assert.assertEquals("Item not yet removed", 4, allSampleItems.size());
        updatingItemDetailsService.removeItem(Collections.singletonList(SampleItem.getSampleItemDetails(SampleItem.A)));

        Assert.assertEquals("Not removed", 4, allSampleItems.size());

        String expectedOutput = MUST_HAVE_AT_LEAST_ONE_ITEM + "\r\n";
        Assert.assertEquals("Not removed", expectedOutput, getOutput());
    }

    @Test
    public void updatePricePerUnit_itemWithNoDiscount() {
        int originalUnitPrice = 20;
        int newUnitPrice = 40;
        given(userInputService.getIntegerPriceResponse(String.format(WHAT_NEW_UNIT_PRICE, itemWithoutDiscount.getItem()))).willReturn(newUnitPrice);

        Assert.assertEquals("Original price per unit", originalUnitPrice, itemWithoutDiscount.getPricePerUnit());
        Assert.assertNull("Original discounted price", itemWithoutDiscount.getDiscountedItemPrice());

        updatingItemDetailsService.updatePricePerUnit(itemWithoutDiscount);
        Assert.assertEquals("New price per unit", newUnitPrice, itemWithoutDiscount.getPricePerUnit());
        Assert.assertNull("New discounted price", itemWithoutDiscount.getDiscountedItemPrice());
    }

    @Test
    public void updatePricePerUnit_itemWithDiscount() {
        int originalUnitPrice = 50;
        Integer originalDiscountPrice = 30;
        int newUnitPrice = 60;
        Integer newDiscountPrice = 10;
        given(userInputService.getIntegerPriceResponse(String.format(WHAT_NEW_UNIT_PRICE, itemWithDiscount.getItem()))).willReturn(newUnitPrice);

        Assert.assertEquals("Original price per unit", originalUnitPrice, itemWithDiscount.getPricePerUnit());
        Assert.assertEquals("Original discounted price", originalDiscountPrice, itemWithDiscount.getDiscountedItemPrice());

        updatingItemDetailsService.updatePricePerUnit(itemWithDiscount);
        Assert.assertEquals("New price per unit", newUnitPrice, itemWithDiscount.getPricePerUnit());
        Assert.assertEquals("New discounted price", newDiscountPrice, itemWithDiscount.getDiscountedItemPrice());
    }

    @Test
    public void updateDiscount_changeExistingDiscount() {
        Integer newDiscountNum = 4;
        Integer newDiscountPrice = 155;

        given(userInputService.getIntegerResponseTwoOrAbove(WHAT_NEW_DISCOUNT_NUM)).willReturn(newDiscountNum);
        given(userInputService.getIntegerPriceResponse(WHAT_NEW_DISCOUNT_PRICE)).willReturn(newDiscountPrice);

        updatingItemDetailsService.updateDiscount(itemWithDiscount);
        Assert.assertEquals("Discount Num", newDiscountNum, itemWithDiscount.getDiscountNum());
        Assert.assertEquals("Discount Price", newDiscountPrice, itemWithDiscount.getDiscountPrice());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(5), itemWithDiscount.getDiscountedItemPrice());
        Assert.assertTrue("Discounted", itemWithDiscount.isItemDiscounted());
    }

    @Test
    public void updateDiscount_addADiscount() {
        Integer newDiscountNum = 2;
        Integer newDiscountPrice = 30;

        given(userInputService.getIntegerResponseTwoOrAbove(WHAT_NEW_DISCOUNT_NUM)).willReturn(newDiscountNum);
        given(userInputService.getIntegerPriceResponse(WHAT_NEW_DISCOUNT_PRICE)).willReturn(newDiscountPrice);

        updatingItemDetailsService.updateDiscount(itemWithoutDiscount);
        Assert.assertEquals("Discount Num", newDiscountNum, itemWithoutDiscount.getDiscountNum());
        Assert.assertEquals("Discount Price", newDiscountPrice, itemWithoutDiscount.getDiscountPrice());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(10), itemWithoutDiscount.getDiscountedItemPrice());
        Assert.assertTrue("Discounted", itemWithoutDiscount.isItemDiscounted());
    }

    @Test
    public void removeDiscount_confirmRemove() {
        given(userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_DISCOUNT, itemWithDiscount.getItem()))).willReturn(true);

        updatingItemDetailsService.removeDiscount(itemWithDiscount);
        Assert.assertNull("Discount Num", itemWithDiscount.getDiscountNum());
        Assert.assertNull("Discount Price", itemWithDiscount.getDiscountPrice());
        Assert.assertNull("Discounted Item Price", itemWithDiscount.getDiscountedItemPrice());
        Assert.assertFalse("Discounted", itemWithDiscount.isItemDiscounted());

        String expectedOutput = "Item A's discount has been successfully removed\r\n";
        Assert.assertEquals("Output", expectedOutput, getOutput());
    }

    @Test
    public void removeDiscount_cancelRemove() {
        given(userInputService.getYesOrNoResponseAsBoolean(String.format(CONFIRM_REMOVE_DISCOUNT, itemWithDiscount.getItem()))).willReturn(false);

        updatingItemDetailsService.removeDiscount(itemWithDiscount);
        Assert.assertEquals("Discount Num", Integer.valueOf(3), itemWithDiscount.getDiscountNum());
        Assert.assertEquals("Discount Price", Integer.valueOf(130), itemWithDiscount.getDiscountPrice());
        Assert.assertEquals("Discounted Item Price", Integer.valueOf(30), itemWithDiscount.getDiscountedItemPrice());
        Assert.assertTrue("Discounted", itemWithDiscount.isItemDiscounted());

        Assert.assertEquals("No Output", "", getOutput());
    }

    @Test
    public void availableItemsToAdd_listReturned() {
        List<String> response = updatingItemDetailsService.availableItemsToAdd(itemNames);
        List<String> expectedResponse = new ArrayList<>();
        for (char ch = 'E'; ch <= 'Z'; ++ch) {
            expectedResponse.add(String.valueOf(ch));
        }

        Assert.assertEquals("Items available to add", expectedResponse, response);
    }

    private String getOutput() {
        return testOut.toString();
    }
}