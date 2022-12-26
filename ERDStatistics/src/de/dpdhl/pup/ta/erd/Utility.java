package de.dpdhl.pup.ta.erd;

import java.time.Month;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rbit
 */
public class Utility {
    
    public String getsortableDate(String yyyy, String mmm, String dd) {
        StringBuilder sortableDate = new StringBuilder("");
        sortableDate.append(yyyy);
        sortableDate.append(".");
        switch (mmm) {
            case "Jan" : sortableDate.append("01"); break;
            case "Feb" : sortableDate.append("02"); break;
            case "Mar" : sortableDate.append("03"); break;
            case "Apr" : sortableDate.append("04"); break;
            case "May" : sortableDate.append("05"); break;
            case "Jun" : sortableDate.append("06"); break;
            case "Jul" : sortableDate.append("07"); break;
            case "Aug" : sortableDate.append("08"); break;
            case "Sep" : sortableDate.append("09"); break;
            case "Oct" : sortableDate.append("10"); break;
            case "Nov" : sortableDate.append("11"); break;
            case "Dec" : sortableDate.append("12"); break;
            default    : break;
        }
        sortableDate.append(".");
        if (dd.length()==1) {
            sortableDate.append("0");
        } 
        sortableDate.append(dd);
        
        return sortableDate.toString();
    }
    
    public static Month getMonth(String m) {
        Month mmm = null;
        switch (m) {
            case "Jan" : mmm = Month.JANUARY;   break;
            case "Feb" : mmm = Month.FEBRUARY;  break;
            case "Mar" : mmm = Month.MARCH;     break;
            case "Apr" : mmm = Month.APRIL;     break;
            case "May" : mmm = Month.MAY;       break;
            case "Jun" : mmm = Month.JUNE;      break;
            case "Jul" : mmm = Month.JULY;      break;
            case "Aug" : mmm = Month.AUGUST;    break;
            case "Sep" : mmm = Month.SEPTEMBER; break;
            case "Oct" : mmm = Month.OCTOBER;   break;
            case "Nov" : mmm = Month.NOVEMBER;  break;
            case "Dec" : mmm = Month.DECEMBER;  break;
        }
        return mmm;
    }
    
    public static int getMonthAsInt(String mmm) {
        int m = 0;
        switch (mmm) {
            case "Jan" : m =  1; break;
            case "Feb" : m =  2; break;
            case "Mar" : m =  3; break;
            case "Apr" : m =  4; break;
            case "May" : m =  5; break;
            case "Jun" : m =  6; break;
            case "Jul" : m =  7; break;
            case "Aug" : m =  8; break;
            case "Sep" : m =  9; break;
            case "Oct" : m = 10; break;
            case "Nov" : m = 11; break;
            case "Dec" : m = 12; break;
        }
        return m;
    }
}
