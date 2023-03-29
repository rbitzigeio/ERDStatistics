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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    
    static {
        Locale.setDefault(new Locale("en", "EN"));
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
    
    public static void insertEntity(int uTime, String sDate, double dIn, double dOut, int reportId, int itSystemId) throws Exception {
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
            if (!checkReport(id, date, icto)) {
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
    
    public static boolean checkReport(int id, LocalDate date, int icto) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        boolean idExists = false;
        if (con != null) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            CallableStatement cs = (CallableStatement) con.prepareCall("{? = call checkReport(?,?,?)}");
            cs.setInt(2, id);
            cs.setDate(3, sqlDate);
            cs.setInt(4, icto);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.execute();
            idExists = cs.getBoolean(1);
        }
        return idExists;
    }
    
    public static boolean checkReportById(int id) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        boolean idExists = false;
        if (con != null) {
            CallableStatement cs = (CallableStatement) con.prepareCall("{? = call checkReportById(?)}");
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
    
    public static void cleanBandwidth(String itSystem) throws SQLException {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        if (con != null) {
            Statement statement = con.createStatement();
            String sql = "select * from bandwidth where ITSystem = (select ID from ITSystem where name ='" + itSystem + "') order by FormattedTime;";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            Bandwidth bOld = null;
            int sizeOfDoubleddRows   = 0;
            int sizeOfDeletedRows   = 0;
            int sizeOfUndefinedRows = 0;
            while(rs.next()) {
                Bandwidth bNew = new Bandwidth(rs.getInt(1), rs.getDate(2), rs.getInt(5), rs.getInt(6));
                if (bOld != null) {
                    if (bNew.isEqual(bOld)) {
                        String sql2 = "select count(*) from bandwidth where ITSystem=" + bOld.getItSystem() + 
                                      " and UnixTime=" + bOld.getUnixTime() +
                                      " and ReportID=" + bOld.getReportID();
                        //System.out.println(sql2);
                        Statement statement2 = con.createStatement();
                        ResultSet rs2 = statement2.executeQuery(sql2);
                        int size = 0;
                        while(rs2.next()) {
                            size = rs2.getInt(1);
                        }
                        rs2.close();
                        if (size == 1) {
                            sizeOfDoubleddRows++;
                            String sql3 = "delete from bandwidth where ITSystem=" + bOld.getItSystem() + 
                                          " and UnixTime=" + bOld.getUnixTime() +
                                          " and ReportID=" + bOld.getReportID();
                            Statement statement3 = con.createStatement();
                            statement3.executeUpdate(sql3);
                            /*
                            ResultSet rs3 = statement3.executeQuery(sql3);
                            while(rs3.next()){
                                System.out.print("ID: " + rs3.getInt("UnixTime"));
                            }
                            rs3.close();
                            */
                            sizeOfDeletedRows++;
                        } else {
                            sizeOfUndefinedRows++;
                            System.out.println(sql2);
                        }
                    }
                }
                bOld = bNew;
            }
            System.out.println("IT-System: " + itSystem);
            System.out.println("   Eindeutige  doppelte Einträge: " + sizeOfDoubleddRows);
            System.out.println("   Gelöschte   doppelte Einträge: " + sizeOfDeletedRows);
            System.out.println("   Mehrdeutige doppelte Einträge: " + sizeOfUndefinedRows);
        }
    }
    
     public static List<String> getITSysteme() throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        List<String> alITSysteme = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            String sql = "select NAME from ITSystem order by ID;";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                alITSysteme.add(rs.getString(1));
            }
        }
        return alITSysteme;
    }
    
}
