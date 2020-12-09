package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.itemdetails.ItemDetails;
import com.elliehannant.checkoutsystem.itemdetails.ItemDetailsService;
import com.elliehannant.checkoutsystem.itemdetails.SampleItem;
import com.elliehannant.checkoutsystem.userinput.UserInputService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.elliehannant.checkoutsystem.CheckoutSystemService.DISPLAY_ITEMS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutSystemServiceTest {

    @InjectMocks
    private CheckoutSystemService checkoutSystemService;

    @Mock
    private UserInputService userInputService;

    @Mock
    private ItemDetailsService itemDetailsService;

    private List<ItemDetails> itemDetailsList;

    @Before
    public void setUp() {
        itemDetailsList = new ArrayList<>();
        ItemDetails itemDetails = SampleItem.getSampleItemDetails(SampleItem.A);
        itemDetailsList.add(itemDetails);
    }

    @Test
    public void displayItemDetails_userRespondedYesAndDetailsDisplayed() {
        given(userInputService.getYesOrNoResponseAsBoolean(DISPLAY_ITEMS)).willReturn(true);

        checkoutSystemService.displayItemDetails(itemDetailsList);
        verify(itemDetailsService, times(1)).displayItemDetailsList(itemDetailsList);
    }

    @Test
    public void displayItemDetails_userRespondedNoAndNothingDisplayed() {
        given(userInputService.getYesOrNoResponseAsBoolean(DISPLAY_ITEMS)).willReturn(false);

        checkoutSystemService.displayItemDetails(itemDetailsList);
        verify(itemDetailsService, times(0)).displayItemDetailsList(itemDetailsList);
    }
}