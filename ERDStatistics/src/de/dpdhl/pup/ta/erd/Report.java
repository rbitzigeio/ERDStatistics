/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

/**
 *
 * @author cyqjefe0019
 */
public class Report {
    
    private int    _id;
    private String _title;
    private String _description;
    private String _info;
    private String _fileName;
    private String _section;
    
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
    
}
