/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 *
 * @author cyqjefe0019
 */
public class ERDStatistic {
    
    private File _fileName;
    
    Series  _seriesIn  = new XYChart.Series();
    Series  _seriesOut = new XYChart.Series();
    int     _unit      = 0; // Default Unit Bit/s
     
    public ERDStatistic() {
        _seriesIn.setName("In-Bound");
        _seriesOut.setName("Out-Bound");
    }
    
    public ERDStatistic(File f) {
        this();
        setFile(f);
    }
    
    public void setFile(File f) {
        _fileName = f;   
        readFile(f);
    }
    
    public Series[] getSeries() {
        Series[] series = {_seriesIn, _seriesOut};
        return series;
    }
    
    public Series getSeriesIn() {
        return _seriesIn;
    }
    
    public Series getSeriesOut() {
        return _seriesIn;
    }
    
    public int getUnit() {
        return _unit;
    }
    
    private void readFile(File f) {  
        String  inString  = "";
        boolean bStart    = false;
        int     i         = 0;
        int     iSum      = 0;
        try {
            FileReader inFile = new FileReader(f);                  
            BufferedReader inStream = new BufferedReader(inFile);
            long sumValueIn  = 0;
            long sumValueOut = 0;
            boolean bBreak   = false;
            while ((inString = inStream.readLine()) != null) { // Read data until line with unix time is passed
                if (bStart) {
                    String[] s = inString.split(",");
                    if (s.length > 3) {
                        i++;
                        String[] s0  = s[3].split("\\.");
                        String[] s1  = s[4].split("\\.");
                        String day = s[1].substring(1) + s[2].substring(0,11);
                        int valueIn  = Integer.parseInt(s0[0]);
                        int valueOut = Integer.parseInt(s1[0]);
                        sumValueIn   = sumValueIn  + valueIn; 
                        sumValueOut  = sumValueOut + valueOut;
                        iSum         = iSum + i;    
                        sumValueIn   = sumValueIn / 1000 / 1000; // MBits/s
                        sumValueOut = sumValueOut / 1000 / 1000; // MBits/s
                        _seriesIn.getData().add(new XYChart.Data(iSum, sumValueIn)); // Average
                        _seriesOut.getData().add(new XYChart.Data(iSum, sumValueOut)); // Average
                    } 
                } // First empty line after data defines end of statistic section
                if (inString.length() == 0) {
                    log(i + " Lines in data file  " + f.getName());
                    bBreak = true;
                } else {
                    if (inString.startsWith("[Line chart: Traffic Volume by Avg % Util (In)")) { 
                        _unit = 1; // % Unit or Bit/s (0 = default)
                    }
                    if (inString.startsWith("\"unix time\"")) {
                        bStart = true;
                    }
                }
                if (bBreak) {
                    break; // WHILE
                }
            }
        } catch (Exception ex) {  
                log("Line : " + i + inString);
                log("Error reading file: " + ex.getMessage()) ;
        }   
    }
    
    private void log(String s) {
       
    }

    
}
