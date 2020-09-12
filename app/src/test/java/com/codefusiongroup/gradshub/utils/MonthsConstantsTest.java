package com.codefusiongroup.gradshub.utils;

import org.junit.Test;

import static com.codefusiongroup.gradshub.utils.MonthsConstants.setMonths;
import static org.junit.Assert.*;

public class MonthsConstantsTest {

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