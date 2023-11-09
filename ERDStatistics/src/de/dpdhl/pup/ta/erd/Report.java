/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author cyqjefe0019
 */
public class Report {
    
    private int       _id;
    private String    _title;
    private String    _description;
    private String    _info;
    private String    _fileName;
    private String    _section;
    private String    _date;
    private int       _itsystem;
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "dd.MM.yyyy" );
    
    public Report() {  
    }

    public Report(int id) {  
       this.setId(id);
    }
    
    public Report(String fileName) {  
       this.setFileName(fileName);
    }
     
    public Report(int id, String title, String description, String info) {  
       this.setId(id);
       this.setTitle(title);
       this.setDescription(description);
       this.setInfo(info);
    }
    
    public Report(int id, String title, String description, String info, String fileName) {  
       this.setId(id);
       this.setTitle(title);
       this.setDescription(description);
       this.setInfo(info);
       this.setFileName(fileName);
    }
    
    /**
     * @return the _id
     */
    public int getId() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void setId(int id) {
        this._id = id;
    }

    /**
     * @return the _title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * @param title the _title to set
     */
    public void setTitle(String title) {
        this._title = title;
        int pos = title.indexOf('(');
        String s[] = (title.substring(pos+1, title.length()-3)).split(" ");
        String datum = s[0] + " " + s[1] + " " + s[2];
        this.setDate(datum);
    }

     /**
     * @return the _section
     */
    public String getSection() {
        return _section;
    }

    /**
     * @param title the _title to set
     */
    public void setSection(String section) {
        this._section = section;
    }
    
    /**
     * @return the _description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description the _description to set
     */
    public void setDescription(String description) {
        this._description = description;
    }

    /**
     * @return the _info
     */
    public String getInfo() {
        return _info;
    }

    /**
     * @param info the _info to set
     */
    public void setInfo(String _info) {
        this._info = _info;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * @param fileName the _fileName to set
     */
    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    /**
     * @return the _date
     */
    public String getDate() {
        return _date;
    }
    
    public LocalDate getLocalDate() {
        String[] s     = _date.split("\\.");
        int      year  = Integer.parseInt(s[2]); 
        int      month = Integer.parseInt(s[1]); 
        int      day   = Integer.parseInt(s[0]);
        return LocalDate.of(year, month, day);
    }

    /**
     * @param _date the _date to set
     */
    public void setDate(LocalDate ld) {
        this._date = formatter.format( ld );
    }
    public void setDate(String sDate) {
        String s[]   = sDate.split(" ");
        int year     = Integer.parseInt(s[2]); 
        int month    = Utility.getMonthAsInt(s[0]);
        int day      = Integer.parseInt(s[1].replace(",", "").trim());
        LocalDate ld = LocalDate.of(year, month, day);
        this._date   = formatter.format( ld );
    }
    
    public void setITSystem(int itsystem) {
        this._itsystem = itsystem;
    }
    public int getITSystem() {
        return this._itsystem;
    }
}
