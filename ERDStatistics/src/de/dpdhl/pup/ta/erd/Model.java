/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

/**
 *
 * @author cyqjefe0019
 */
public class Model {
    
    private static final String             _PROPERTIES = ".ERD.properties";
    private static final String             _CSVDIR     = "CSVDIR";
    private static final String[]           _MONTH    = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                                         "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    
    private String                          _propertiesFile;
    private String                          _csvDir;
    private File                            _selectedFile; 
    private List                            _lCsv = new ArrayList();
    private HashMap<String, File>           _mCsv = new HashMap<>(); // List of CSV files
    private static HashMap<String,String>   _mCSVDirs     = new HashMap<>(); // List of ERD Consumers
    
    
    public Model() throws IOException {
        this.init();
    }
    
    public Model(String dir) throws IOException {
        _csvDir = dir;
    }
    
    public void setSelectedFile(File selectedFile) {
        _selectedFile = selectedFile;
    }
    
    public File getSelectedFile() {
        return _selectedFile;
    }
    
    public String getPropertiesFile() {
        return _propertiesFile;
    }
    
    public String getCsvDir() {
        return _csvDir;
    }
    
    public void setCsvDir(String dir) {
        for (String key : _mCSVDirs.keySet()) {
            String v = _mCSVDirs.get(key);
            if (v.equals(dir)) {
                _csvDir = key;    
                _lCsv.clear();
                _mCsv.clear();
                getERDDataFiles();
                getSortedERDDataFiles();
            }
        }
    }
    
    public HashMap getCsvDirs() {
        return _mCSVDirs;
    }
    
    public List<String> getSortedERDDataFiles() {
        getERDDataFiles();
        Collections.sort(_lCsv);
        return _lCsv;
    }
    
    public File getCsvDataFiles(String key) {
        if (_mCsv.containsKey(key)) {
            return _mCsv.get(key);
        }
        return null;
    }
    
    public ArrayList<String> getERDDataFiles() {
        ArrayList<String> allFiles = new ArrayList<>();
        final File folder = new File(_csvDir);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                allFiles.add(fileEntry.getName());
                String[] fileName =  fileEntry.getName().split(" ");
                if (fileName.length > 3) {
                    Month     m  = getMonth(fileName[1]);
                    LocalDate ld = LocalDate.of(Integer.parseInt(fileName[3]), m, Integer.parseInt(fileName[2].replace(',',' ').trim()));
                    ld = ld.minusDays(1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy.MM.dd" );
                    String sDate = formatter.format( ld );
                    _lCsv.add(sDate);
                    _mCsv.put(sDate, fileEntry);
                }
            }
        }
        return allFiles;
    }
    
    public ArrayList<String> getERDDataFiles(String filter) {
        ArrayList<String> filteredFiles = new ArrayList<>();
        ArrayList<String> allFiles = this.getERDDataFiles();
        for (String fileName : allFiles) {
            if (fileName.contains(filter)) {
                filteredFiles.add(fileName);
            }
        }
        return filteredFiles;
    }
    
    private void init() throws IOException {
        String home = System.getenv("USERPROFILE");
        if (home == null) {
            home = System.getenv("HOME");
        }
        String propFile = home + "/" + _PROPERTIES;
        Logger.getInstance().log("Home : " + propFile);
        FileReader inFile = new  FileReader(propFile);
        _propertiesFile = propFile;
        BufferedReader inStream = new BufferedReader(inFile);
        String inString;
        while ((inString = inStream.readLine()) != null) {
            if (!inString.startsWith("#")) {
                String[] info = inString.split("=");
                switch (info[0]) {
                    case _CSVDIR : String[] s = info[1].split(";");
                                   if (s.length == 2) {  
                                       if (_csvDir == null) {
                                          _csvDir = s[0];
                                       }
                                       _mCSVDirs.put(s[0], s[1]);
                                   }
                                   break;
                    default      : break;
                }
            }
        }
        inStream.close();   
    }

    private String getsortableDate(String yyyy, String mmm, String dd) {
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
    
    private static Month getMonth(String m) {
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
}