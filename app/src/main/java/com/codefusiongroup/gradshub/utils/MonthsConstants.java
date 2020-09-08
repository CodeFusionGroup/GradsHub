package com.codefusiongroup.gradshub.utils;


public class MonthsConstants {

    public static int setMonths(String eventMonth) {

        String month = eventMonth.toUpperCase();
        int monthNumber = -1;

        // NOTE: months in Java Calendar are 0 indexed
        switch (month) {
            case "JANUARY":
                monthNumber = 0;
                break;

            case "FEBRUARY":
                monthNumber = 1;
                break;

            case "MARCH":
                monthNumber = 2;
                break;

            case "APRIL":
                monthNumber = 3;
                break;

            case "MAY":
                monthNumber = 4;
                break;

            case "JUNE":
                monthNumber = 5;
                break;

            case "JULY":
                monthNumber = 6;
                break;

            case "AUGUST":
                monthNumber = 7;
                break;

            case "SEPTEMBER":
                monthNumber = 8;
                break;

            case "OCTOBER":
                monthNumber = 9;
                break;

            case "NOVEMBER":
                monthNumber = 10;
                break;

            case "DECEMBER":
                monthNumber = 11;
                break;


        }

        return monthNumber;

    }

}
