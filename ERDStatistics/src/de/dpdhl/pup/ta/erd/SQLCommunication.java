/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import com.mysql.cj.jdbc.CallableStatement;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author rbit
 */
public class SQLCommunication {
    
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL    = "jdbc:mysql://localhost:3306/ERD";
    private static final String _PROPERTY_FILE = ".ERD.properties"; 
    private static Properties   _PROPS         = new Properties();
    private static Connection   connection;
    
    private void SQLConnection() {
    }
    
    public SQLCommunication getInstance() {
       return this;    
    }
    
    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("user", _PROPS.getProperty("USERNAME"));
        properties.setProperty("password", _PROPS.getProperty("PASSWORD"));
        properties.setProperty("MaxPooledStatements", _PROPS.getProperty("MAX_POOL"));
        return properties;
    }
    
    // connect database
    private Connection getConnection() {
        if (connection == null) {
            String home = System.getenv("USERPROFILE");
            if (home == null) {
                home = System.getenv("HOME");
            }
            String propFile = home + "/" + _PROPERTY_FILE;
            try {
                FileInputStream in = new FileInputStream(propFile);
                _PROPS.load(in);
                in.close();
                //Class.forName(_PROPS.getProperty(DATABASE_DRIVER));
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // disconnect database
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void insertEntity(int uTime, String sDate, double dIn, double dOut, int reportId) throws Exception{
        SQLCommunication com = new SQLCommunication();
        com.getConnection();
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        Date date = format.parse(sDate);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        CallableStatement cs = (CallableStatement) connection.prepareCall("{call insertBandwidth(?,?,?,?,?)}");
        cs.setInt(1, uTime);
        cs.setTimestamp(2, timestamp);
        cs.setDouble(3, dIn);
        cs.setDouble(4, dOut);
        cs.setInt(5, reportId);
        cs.execute();
    }
    
    
 public static void insertReport(String title, String description, int id, String section, String lineChart, String fileName, String icto) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        com.getConnection();
        CallableStatement cs = (CallableStatement) connection.prepareCall("{call insertReport(?,?,?,?,?,?, ?)}");
        cs.setString(1, title);
        cs.setString(2, description);
        cs.setInt(3, id);
        cs.setString(4, section);
        cs.setString(5, lineChart);
        cs.setString(6, fileName);
        cs.setString(7, icto);
        cs.execute();
    }
}
