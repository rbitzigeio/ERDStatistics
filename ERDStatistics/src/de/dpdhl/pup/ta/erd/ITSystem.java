/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author RBO-VS-Admin
 */
public class ITSystem {
    
    private int    _id;
    private String _name;
    private String _icto;
    private int    _sizeOfReports;
    private int    _sizeOfBandwidth;
    private Date   _firstDate;
    private Date   _lastDate;
    private Date   _actDate;
    
    
    public static List<ITSystem> _listOfITSystems = new ArrayList();
    
    private ITSystem() {
        
    }
    public ITSystem(int id, String name) {
        _id   = id;
        _name = name;
         _listOfITSystems.add(this);
    }
    public ITSystem(int id, String name, String icto) {
        this(id, name);
        _icto = icto;
    }
    
    public static void init() throws SQLException {
        _listOfITSystems = SQLCommunication.getITSystems();
    }
    
    public static List<ITSystem> getAllITSystems() {
       return _listOfITSystems;
    }

    public static ITSystem getITSystemByID(int id) {
       for (ITSystem its : _listOfITSystems) {
           if (its.getId() == id) {
               return its;
           }
       }
       return null;
    }
    
    public static ITSystem getITSystemByName(String name) {
       for (ITSystem its : _listOfITSystems) {
           if (its.getName().equals(name)) {
               return its;
           }
       }
       return null;
    }
    
    /**
     * @return the _id
     */
    public int getId() {
        return _id;
    }

    /**
     * @return the _name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the _icto
     */
    public String getIcto() {
        return _icto;
    }

    /**
     * @return the _sizeOfReports
     */
    public int getSizeOfReports() {
        return _sizeOfReports;
    }

    /**
     * @param _sizeOfReports the _sizeOfReports to set
     */
    public void setSizeOfReports(int _sizeOfReports) {
        this._sizeOfReports = _sizeOfReports;
    }

    /**
     * @return the _sizeOfBandwidth
     */
    public int getSizeOfBandwidth() {
        return _sizeOfBandwidth;
    }

    /**
     * @param _sizeOfBandwidth the _sizeOfBandwidth to set
     */
    public void setSizeOfBandwidth(int _sizeOfBandwidth) {
        this._sizeOfBandwidth = _sizeOfBandwidth;
    }

    /**
     * @return the _firstDate
     */
    public Date getFirstDate() {
        return _firstDate;
    }

    /**
     * @param _firstDate the _firstDate to set
     */
    public void setFirstDate(Date _firstDate) {
        this._firstDate = _firstDate;
    }

    /**
     * @return the _lastDate
     */
    public Date getLastDate() {
        return _lastDate;
    }

    /**
     * @param _lastDate the _actDate to set
     */
    public void setLastDate(Date _lastDate) {
        this._lastDate = _lastDate;
    }
    
    /**
     * @return the _actDate
     */
    public Date getActDate() {
        return _actDate;
    }

    /**
     * @param _actDate the _actDate to set
     */
    public void setActDate(Date _actDate) {
        this._actDate = _actDate;
    }
}