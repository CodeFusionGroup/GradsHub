package com.codefusiongroup.gradshub.utils;

import org.junit.Test;

import static com.codefusiongroup.gradshub.utils.MonthsConstants.setMonths;
import static org.junit.Assert.*;

public class MonthsConstantsTest {

    private int[] monthNumber = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER",
                                "OCTOBER", "NOVEMBER", "DECEMBER"};
    @Test
    public void setMonthsTest() {
        for(int i = 0;i<12;i++){
            int actual = MonthsConstants.setMonths(months[i]);
            assertEquals(i, actual);
        }
    }

}