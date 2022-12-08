/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 *
 * @author cyqjefe0019
 */
public class ERDStatistic {
    
    private static Logger _LOGGER = Logger.getInstance();
    
    private File _fileName;
    
    Series  _seriesIn  = new XYChart.Series();
    Series  _seriesOut = new XYChart.Series();
    int     _unit      = 0; // Default Unit Bit/s
    String  _day       = "";
     
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
    
     public File getFile() {
        return _fileName;
    }
    
    public Series[] getSeries() {
        Series[] series = {_seriesIn, _seriesOut};
        return series;
    }
    
    public Series getSeriesIn() {
        return _seriesIn;
    }
    
    public Series getSeriesOut() {
        return _seriesOut;
    }
    
    public int getUnit() {
        return _unit;
    }
    
    public void add(ERDStatistic erd) {
        log("Concat data of " + erd._day + " to " + _day);    
        Series seriesIn  = erd.getSeriesIn();
        Series seriesOut = erd.getSeriesOut();
        ObservableList olSeriesIn  = seriesIn.getData();
        ObservableList olSeriesOut = seriesOut.getData();
        int size  = olSeriesIn.size();
        int size0 =_seriesIn.getData().size() + 1;
        for (int i=0; i<size; i++) {
            XYChart.Data dataIn  = (XYChart.Data)olSeriesIn.get(i);
            XYChart.Data dataOut = (XYChart.Data)olSeriesOut.get(i);
            Integer xIn  = Integer.valueOf(dataIn.getXValue().toString());
            Integer xOut = Integer.valueOf(dataOut.getXValue().toString());   
            Integer yIn  = Integer.valueOf(dataIn.getYValue().toString());
            Integer yOut = Integer.valueOf(dataOut.getYValue().toString());
            _seriesIn.getData().add(new XYChart.Data(size0 + i,yIn)); 
            _seriesOut.getData().add(new XYChart.Data(size0 + i, yOut));
        }
        log("Size of Series : " + _seriesIn.getData().size());
    }
    
    private void readFile(File f) {  
        String  inString  = "";
        boolean bStart    = false;
        int     i         = 0;
        int     iSum      = 0;
        try {
            FileReader inFile = new FileReader(f);                  
            BufferedReader inStream = new BufferedReader(inFile);
            //long sumValueIn  = 0;
            //long sumValueOut = 0;
            boolean bBreak   = false;
            while ((inString = inStream.readLine()) != null) { // Read data until line with unix time is passed
                if (bStart) {
                    String[] s = inString.split(",");
                    if (s.length > 3) {
                        String[] s0  = s[3].split("\\.");
                        String[] s1  = s[4].split("\\.");
                        String day = s[1].substring(1) + s[2].substring(0,11);
                        Double dValueIn;
                        Double dValueOut;
                        long valueIn;
                        long valueOut;
                        try {
                            dValueIn = Double.parseDouble(s0[0])  / 1000 / 1000; // MBits/s;
                            valueIn  = dValueIn.intValue();
                        } catch (NumberFormatException ex) {
                            log("Max value set for line " + i + " in data file  " + f.getName());
                            valueIn = Integer.MAX_VALUE / 1000 / 1000; // MBits/s;
                        }
                        try {
                            dValueOut = Double.parseDouble(s1[0])  / 1000 / 1000; // MBits/s;
                            valueOut  = dValueOut.intValue();
                        } catch (NumberFormatException ex) {
                            log("Max value set for line " + i + " in data file  " + f.getName());
                            valueOut = Integer.MAX_VALUE / 1000 / 1000; // MBits/s;
                        }
                        iSum++; 
                        _seriesIn.getData().add(new XYChart.Data(iSum, valueIn)); // Average
                        _seriesOut.getData().add(new XYChart.Data(iSum, valueOut)); // Average
                        if (_day.isEmpty()) {
                            _day = day.substring(0, day.length()-6);
                        }
                    } 
                } // First empty line after data defines end of statistic section
                if (bStart && inString.length() == 0) {
                    log(i + " Lines available for date " + _day);
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
       _LOGGER.log(s);
    }

    
}
