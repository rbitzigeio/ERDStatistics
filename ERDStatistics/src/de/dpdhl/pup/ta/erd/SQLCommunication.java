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
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    
    public static void insertEntity(int uTime, String sDate, double dIn, double dOut, int reportId, int itSystemId) throws Exception{
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        if (con != null) {
            DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
            Date date = format.parse(sDate);
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            CallableStatement cs = (CallableStatement) con.prepareCall("{call insertBandwidth(?,?,?,?,?,?)}");
            cs.setInt(1, uTime);
            cs.setTimestamp(2, timestamp);
            cs.setDouble(3, dIn);
            cs.setDouble(4, dOut);
            cs.setInt(5, reportId);
            cs.setInt(6, itSystemId);
            cs.execute();
        }
    }
    
    
    public static boolean insertReport(String title, String description, int id, LocalDate date, String section, String lineChart, String fileName, int icto) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        boolean isInserted = false;
        if (con != null) {
            if (!checkReport(id)) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(date);
                CallableStatement cs = (CallableStatement) con.prepareCall("{call insertReport(?,?,?,?,?,?,?,?)}");
                cs.setString(1, title);
                cs.setString(2, description);
                cs.setInt(3, id);
                cs.setDate(4, sqlDate);
                cs.setString(5, section);
                cs.setString(6, lineChart);
                cs.setString(7, fileName);
                cs.setInt(8, icto);
                cs.execute();
                isInserted = true;
            } 
        }
        return isInserted;
    }
    
    public static boolean checkReport(int id) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        boolean idExists = false;
        if (con != null) {
            CallableStatement cs = (CallableStatement) con.prepareCall("{? = call checkReport(?)}");
            cs.setInt(2, id);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.execute();
            idExists = cs.getBoolean(1);
        }
        return idExists;
    }
    
    public static int getITSystemID(String name) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        int id = 0;
        if (con != null) {
            CallableStatement cs = (CallableStatement) con.prepareCall("{? = call getIDofITSystem(?)}");
            cs.setString(2, name);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            id = cs.getInt(1);
        }
        return id;
    }
    
    public static boolean isConnected() {
        return connection != null;
    }
}
