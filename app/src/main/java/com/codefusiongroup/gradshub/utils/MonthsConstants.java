package com.codefusiongroup.gradshub.utils;


public class MonthsConstants {

    public static int setMonths(String eventMonth) {

        String month = eventMonth.toUpperCase();
        int monthNumber = -1;

        // NOTE: months in Java Calendar are 0 indexed
        switch (month) {
            case "JANUARY":
            case "JAN":
                monthNumber = 0;
                break;

            case "FEBRUARY":
            case "FEB":
                monthNumber = 1;
                break;

            case "MARCH":
            case "MAR":
                monthNumber = 2;
                break;

            case "APRIL":
            case "APR":
                monthNumber = 3;
                break;

            case "MAY":
                monthNumber = 4;
                break;

            case "JUNE":
            case "JUN":
                monthNumber = 5;
                break;

            case "JULY":
            case "JUL":
                monthNumber = 6;
                break;

            case "AUGUST":
            case "AUG":
                monthNumber = 7;
                break;

            case "SEPTEMBER":
            case "SEP":
            case "SEPT":
                monthNumber = 8;
                break;

            case "OCTOBER":
            case "OCT":
                monthNumber = 9;
                break;

            case "NOVEMBER":
            case "NOV":
                monthNumber = 10;
                break;

            case "DECEMBER":
            case "DEC":
                monthNumber = 11;
                break;

        }

        return monthNumber;

    }

}
