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
    
    //private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    //private static final String DATABASE_URL    = "jdbc:mysql://localhost:3306/ERD";
    private static final String _PROPERTY_FILE = ".ERD.properties"; 
    private static Properties   _PROPS         = new Properties();
    private static Connection   connection;
    
    static {
        Locale.setDefault(new Locale("en", "EN"));
    }

    private static List<Date> getDateValues(String sql) throws SQLException {
        SQLCommunication com      = new SQLCommunication();
        Connection       con      = com.getConnection();
        List<Date>       alValues = new ArrayList<>();
        if (con != null) {
            try (Statement statement = con.createStatement()) {
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next()) {
                    alValues.add(rs.getDate(1));
                }
                rs.close();
            }
        }
        return alValues;
    }

    private static List<Report> getReport(String sql) throws SQLException  {
        SQLCommunication com      = new SQLCommunication();
        Connection       con      = com.getConnection();
        List<Report>     alValues = new ArrayList<>();
        if (con != null) {
            try (Statement statement = con.createStatement()) {
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next()) {
                    String title        = rs.getString(1);
                    String description  = rs.getString(2);
                    int    id           = rs.getInt(3);
                    Date   creationDate = rs.getDate(4);
                    String section      = rs.getString(5);
                    String info         = rs.getString(6);
                    String fileName     = rs.getString(7);
                    int    itsystem     = rs.getInt(8);
                    Report report       = new Report(id, title, description, info, fileName);
                    report.setITSystem(itsystem);
                    alValues.add(report);
                }
                rs.close();
            }
        }
        return alValues;
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
                Class.forName(_PROPS.getProperty("DATABASE_DRIVER"));
                connection = DriverManager.getConnection(_PROPS.getProperty("DATABASE_URL"), getProperties());
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
    
    public static void insertMaxBandwidth(int itSystemId, int reportId, double dIn, double dOut, String sDateIn, String sDateOut) throws Exception {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        if (con != null) {
            SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
            SimpleDateFormat format3 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");  
            Date             dateIn  = format3.parse(sDateIn);
            Date             date    = format3.parse(sDateIn);         
            Date             dateOut = format3.parse(sDateOut);            
            java.sql.Timestamp timestamp    = new java.sql.Timestamp(date.getTime());
            java.sql.Timestamp timestampIn  = new java.sql.Timestamp(dateIn.getTime());
            java.sql.Timestamp timestampOut = new java.sql.Timestamp(dateOut.getTime());
            CallableStatement cs = (CallableStatement) con.prepareCall("{call insertMaxBandwidth(?,?,?,?,?,?,?)}");
            cs.setInt(1, itSystemId);
            cs.setInt(2, reportId);
            cs.setTimestamp(3, timestamp);
            cs.setDouble(4, dIn);
            cs.setDouble(5, dOut);
            cs.setTimestamp(6, timestampIn);
            cs.setTimestamp(7, timestampOut);
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
    
    public static void cleanBandwidth(String itSystem, String sDate) throws SQLException {
        SQLCommunication com = new SQLCommunication();
        Connection con = com.getConnection();
        if (con != null) {
            Statement statement = con.createStatement();
            String sql = "select * from bandwidth where ITSystem = (select ID from ITSystem where name ='" + itSystem + "') "
                    + "and Date(FormattedTime) > '" + sDate + "' order by FormattedTime;";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            Bandwidth bOld = null;
            int sizeOfDoubleddRows   = 0;
            int sizeOfDeletedRows   = 0;
            int sizeOfUndefinedRows = 0;
            while(rs.next()) {
                Bandwidth bNew = new Bandwidth(rs.getInt(1), rs.getDate(2), rs.getInt(5), rs.getInt(6));
                bNew.setTimestamp(rs.getTimestamp(2));
                if (bOld != null) {
                    if (bNew.isEqual(bOld)) {
                        String sql2 = "select count(*) from bandwidth where ITSystem=" + bOld.getItSystem() + 
                                      " and FormattedTime='" + bOld.getTimestamp() + "';";
                        Statement statement2 = con.createStatement();
                        ResultSet rs2 = statement2.executeQuery(sql2);
                        int size = 0;
                        while(rs2.next()) {
                            size = rs2.getInt(1);
                        }
                        rs2.close();
                        if (size > 1) {
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
                        } else if (size == 1){
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
    
    public static List<String> getNameITSystems() throws SQLException  {
        SQLCommunication com         = new SQLCommunication();
        Connection       con         = com.getConnection();
        List<String>     alITSysteme = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            String sql = "select NAME from ITSystem order by ID;";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                alITSysteme.add(rs.getString(1));
            }
            rs.close();
            statement.close();
        }
        return alITSysteme;
    }
    
    private static List<ITSystem> getAllITSystems() throws SQLException  {
        SQLCommunication com         = new SQLCommunication();
        Connection       con         = com.getConnection();
        List<ITSystem>   alITSysteme = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            String sql = "select * from ITSystem order by ID;";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                ITSystem its = new ITSystem(rs.getInt(2),rs.getString(1), rs.getString(3));
                alITSysteme.add(its);
            }
            rs.close();
            statement.close();
        }
        return alITSysteme;
    }
    
    private static List<Integer> getIntValues(String sql) throws SQLException {
        SQLCommunication com      = new SQLCommunication();
        Connection       con      = com.getConnection();
        List<Integer>    alValues = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                alValues.add(rs.getInt(1));
            }
            rs.close();
            statement.close();
        }
        return alValues;
    }
    
    public static List<Date> getReportDatesOfITSystem(int id) throws SQLException {
        List<Date> listOfDates  = getDateValues("select CreationDate from report where ITSystem=" + id + " order by CreationDate;");
        
        return listOfDates;
    }
    /**
     * Get information about IT-Systems
     * ID, Name, ICTO, size of reports and size of bandwidth 
     * @return
     * @throws SQLException 
     */
    public static List<ITSystem> getITSystems() throws SQLException  {
        List<ITSystem> alITSystems       = new ArrayList();
        List<ITSystem> alNames           = getAllITSystems();
        List<Integer>  sizeOfReports     = getIntValues("select count(*) from report group by ITSystem order by ITSystem;");
        List<Integer>  sizeOfBandwidth   = getIntValues("select count(*) from bandwidth group by ITSystem order by ITSystem;");
        List<Date>     listOfFirstDates  = getDateValues("select MIN(CreationDate) from report group by ITSystem order by ITSystem;");
        List<Date>     listOfLastDates   = getDateValues("select MAX(CreationDate) from report group by ITSystem order by ITSystem;");
        
        int i=0;
        for (ITSystem its : alNames) {
            i++;
            its.setSizeOfReports(sizeOfReports.get(i));
            its.setSizeOfBandwidth(sizeOfBandwidth.get(i));
            its.setFirstDate(listOfFirstDates.get(i));
            its.setLastDate(listOfLastDates.get(i));
            
            alITSystems.add(its);
        }
        return alITSystems;
    }
  
    public static List<Report>getReportsOfITSystem(int id) throws SQLException {
        String sql = "select * from report where ITSystem='" + id + "';";
        return getReport(sql);
    }
    
    public static List<Bandwidth> getBandwidthOfITSystem(String name) throws SQLException  {
       String sql = "select * from bandwidth where ITSystem = (select ID from ITSystem where name ='" + name + "') order by FormattedTime;";
       return getBandwidth(sql);
    }
    
    public static List<Bandwidth> getBandwidthOfITSystem(int id, String startDate) throws SQLException  {
        String sql = "select * from bandwidth where ITSystem = " + id + 
                     " and Date(FormattedTime) = '" + startDate + "' order by FormattedTime;";
        return getBandwidth(sql);
    }
    
    public static List<Bandwidth> getBandwidthOfITSystem(String name, String startDate) throws SQLException  {
        String sql = "select * from bandwidth where ITSystem = (select ID from ITSystem where name ='" + name + 
                     "') and Date(FormattedTime) = '" + startDate + "' order by FormattedTime;";
        return getBandwidth(sql);
    }
    
    public static List<Bandwidth> getBandwidthOfITSystem(String name, String startDate, String endDate) throws SQLException  {
        String sql = "select * from bandwidth where ITSystem = (select ID from ITSystem where name ='" + name + 
                     "') and Date(FormattedTime) between '" + startDate + "' and '" + endDate + "' order by FormattedTime;";
        return getBandwidth(sql);
    }
    
    public static List<Bandwidth> getMaxBandwidthOfITSystem(String name, String startDate, String endDate) throws SQLException  {
        String sql = "select Date(FormattedTime), Max(BitsPerSecIn), Max(BitsPerSecOut) from bandwidth "
                + "where ITSystem=(select ID from ITSystem where name ='" + name
                + "') and Date(FormattedTime) between '" + startDate + "' and '" + endDate 
                + "' group by Date(FormattedTime) order by Date(FormattedTime);";
        //System.out.println(sql);
        return getMaxBandwidth(sql);
    }
    public static List<Bandwidth> getMaxBandwidthOfITSystem(int id, String startDate, String endDate) throws SQLException  {
        String sql = "select Date(FormattedTime), Max(BitsPerSecIn), Max(BitsPerSecOut) from bandwidth "
                + "where ITSystem=" + id + " and Date(FormattedTime) between '" + startDate + "' and '" + endDate 
                + "' group by Date(FormattedTime) order by Date(FormattedTime);";
        //System.out.println(sql);
        return getMaxBandwidth(sql);
    }
    
    public static List<Bandwidth> getMaxBandwidthOfITSystem(int id, String startDate) throws SQLException {
        String[] column = {"BitsPerSecIn", "BitsPerSecOut"};        
        String sql = "select distinct id from report where ITSystem = " + id + " and CreationDate='" + startDate + "'";
        int reportId = getReportID(sql);
        sql = "select Min(UnixTime), Max(UnixTime) from bandwidth where reportid=" + reportId + ";";
        int[] extrem = getTimeslot(sql);
        sql = "select * from bandwidth where ITSystem = " + id + 
               " and unixtime between " + extrem[0] + " and " + extrem[1] + " order by " + column[0] + " desc limit 1;";
        List<Bandwidth> lIn = getBandwidth(sql);
        sql = "select * from bandwidth where ITSystem = " + id + 
               " and unixtime between " + extrem[0] + " and " + extrem[1] + " order by " + column[1] + " desc limit 1;";
        List<Bandwidth> lOut = getBandwidth(sql);
        for (Bandwidth b : lOut) {
            lIn.add(b);
        }
        return lIn;
    }
    
    public static List<Bandwidth> getMaxBandwidthOfITSystems() throws SQLException {
        String sql = "select date, sum(maxbandwidthin), sum(maxbandwidthout) from maxbandwidth group by date order by date desc;";
        List<Bandwidth> lB = getMaxBandwidth(sql);
        return lB;
    }
    
    public static List<Bandwidth> getBandwidthOfITSystems(String startDate) throws SQLException {   
        String sql = "select min(unixtime), max(unixtime) from bandwidth where date(formattedtime) = '" + startDate + "';";
        int[] minmax = SQLCommunication.getTimeslot(sql);
        String sql2 = "select unixtime, sum(Bitspersecin), sum(bitspersecout) from bandwidth where unixtime between " 
                      + minmax[0] + " and " + minmax[1] + " group by unixtime";
        List<Bandwidth> lB = getTotalBandwidth(sql2);
        return lB;
    }
    
    private static List<Bandwidth> getBandwidth(String sql) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        List<Bandwidth> alBandwidth = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            //System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                Bandwidth b = new Bandwidth(rs.getInt(1), rs.getDate(2), rs.getInt(5), rs.getInt(6));
                b.setBpsIn(rs.getDouble(3));
                b.setBpsOut(rs.getDouble(4));
                b.setTimestamp(rs.getTimestamp(2));
                b.setId(rs.getInt(7));
                
                alBandwidth.add(b);
            }
        }
        return alBandwidth;
    }
    
    private static List<Bandwidth> getTotalBandwidth(String sql) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        List<Bandwidth> alBandwidth = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            //System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                Bandwidth b = new Bandwidth();
                b.setUnixTime(rs.getInt(1));
                b.setBpsIn(rs.getDouble(2));
                b.setBpsOut(rs.getDouble(3));
                
                alBandwidth.add(b);
            }
        }
        return alBandwidth;
    }
    
    private static List<Bandwidth> getMaxBandwidth(String sql) throws SQLException  {
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        List<Bandwidth> alBandwidth = new ArrayList<>();
        if (con != null) {
            Statement statement = con.createStatement();
            //System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                Bandwidth b = new Bandwidth();
                b.setTimestamp(rs.getTimestamp(1));
                b.setBpsIn(rs.getDouble(2));
                b.setBpsOut(rs.getDouble(3));
                alBandwidth.add(b);
            }
        }
        return alBandwidth;
    }
    
    private static int getReportID(String sql) throws SQLException {
        int id = 0;
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        if (con != null) {
            Statement statement = con.createStatement();
            // System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                id = rs.getInt(1);
            }
        }
        return id;
    }
    
    private static int[] getTimeslot(String sql) throws SQLException {
        int[] extrem = new int[2];
        
        SQLCommunication com = new SQLCommunication();
        Connection       con = com.getConnection();
        if (con != null) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                extrem[0] = rs.getInt(1);
                extrem[1] = rs.getInt(2);
            }
        }
        return extrem;
    }
}
