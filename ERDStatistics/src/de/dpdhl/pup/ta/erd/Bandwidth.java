package de.dpdhl.pup.ta.erd;


import java.sql.Date;
import java.sql.Timestamp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author RBO-VS-Admin
 */
public class Bandwidth {
    private int       _unixTime;
    private Date      _date;
    private Double    _bpsIn;
    private Double    _bpsOut;
    private int       _reportID;
    private int       _itSystem;
    private Timestamp _time;
    
    public Bandwidth() {
        
    }
    
    public Bandwidth(int unixTime, Date date, int reportID, int itSystem) {
        _unixTime = unixTime;
        _date     = date;
        _reportID = reportID;
        _itSystem = itSystem;
    }
    
    public boolean isEqual(Bandwidth bandwidth) {
        if (bandwidth.getItSystem() == this.getItSystem() && bandwidth.getTimestamp().equals(this.getTimestamp())) {
            return true;
        }
        return false;
    }

    /**
     * @return the _unixTime
     */
    public int getUnixTime() {
        return _unixTime;
    }

    /**
     * @param _unixTime the _unixTime to set
     */
    public void setUnixTime(int _unixTime) {
        this._unixTime = _unixTime;
    }

    /**
     * @return the _date
     */
    public Date getDate() {
        return _date;
    }

    /**
     * @param _date the _date to set
     */
    public void setDate(Date _date) {
        this._date = _date;
    }

    /**
     * @return the _bpsIn
     */
    public Double getBpsIn() {
        return _bpsIn;
    }

    /**
     * @param _bpsIn the _bpsIn to set
     */
    public void setBpsIn(Double _bpsIn) {
        this._bpsIn = _bpsIn;
    }

    /**
     * @return the _bpsOut
     */
    public Double getBpsOut() {
        return _bpsOut;
    }

    /**
     * @param _bpsOut the _bpsOut to set
     */
    public void setBpsOut(Double _bpsOut) {
        this._bpsOut = _bpsOut;
    }

    /**
     * @return the _reportID
     */
    public int getReportID() {
        return _reportID;
    }

    /**
     * @param _reportID the _reportID to set
     */
    public void setReportID(int _reportID) {
        this._reportID = _reportID;
    }
    
    /**
     * @return the _reportID
     */
    public int getItSystem() {
        return _itSystem;
    }

    /**
     * @param _reportID the _reportID to set
     */
    public void setItSystem(int itSystem) {
        this._itSystem = itSystem;
    }
    
    /**
     * @return the _time
     */
    public Timestamp getTimestamp() {
        return _time;
    }

    /**
     * @param _time the _time to set
     */
    public void setTimestamp(Timestamp time) {
        this._time = time;
    }
}
