package LoadData;

/*
 * Copyright (C) 2004-2006, Denis Lussier
 *
 * LoadData - Load Sample Data directly into database tables or create CSV files for
 *            each table that can then be bulk loaded (again & again & again ...)  :-)
 *
 *    Two optional parameter sets for the command line:
 *
 *                 numWarehouses=9999
 *
 *                 fileLocation=c:/temp/csv/
 *
 *    "numWarehouses" defaults to "1" and when "fileLocation" is omitted the generated
 *    data is loaded into the database tables directly.
 *
 */

import static client.jTPCCConfig.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbc.jdbcIO;

import pojo.Customer;
import pojo.District;
import pojo.History;
import pojo.Item;
import pojo.NewOrder;
import pojo.Oorder;
import pojo.OrderLine;
import pojo.Stock;
import pojo.Warehouse;
import client.jTPCCUtil;

public class LoadData {

  
  
  static boolean fastLoad;

  /**
   * slow load: 1m38.463s
   */
  public static class FastPreparedStatementConn {
    static Pattern pat = Pattern.compile("INSERT INTO (\\w+) +(\\([^\\)]+\\)) +VALUES +.+");
    String baseDir;
    public FastPreparedStatementConn(String baseDir) {
      this.baseDir = baseDir;
    }
    FastPreparedStatement prepareStatement(String s) throws Exception {
      Matcher m = pat.matcher(s);
      if (!m.matches()) throw new Exception();
      return new FastPreparedStatement(
          m.group(1),
          baseDir + m.group(1),
          m.group(2),
          s.replaceAll("[^\\?]+", "").length());
    }
  }

  public static class FastPreparedStatement implements PreparedStatement {

    ArrayList<Object> fields;
    String table, path, fieldNames;
    PrintWriter out;

    FastPreparedStatement(String table, String path, String fieldNames, int nfields) throws Exception {
      System.out.println(path + " " + nfields);
      fields = new ArrayList<Object>(nfields);
      for (int i = 0; i < nfields; i++)
        fields.add(null);
      System.out.println(fields.size());

      out = new PrintWriter(new FileWriter(path));

      this.table = table;
      this.path = path;
      this.fieldNames = fieldNames;
    }

    private void set(int pos, Object obj) {
      fields.set(pos - 1, obj);
    }

    @Override
    public void addBatch() throws SQLException {
      // write it out immediately
      StringBuilder builder = new StringBuilder();
      for (Object field : fields) {
        if (field == null) {
          field = "\\N";
        } else if (field instanceof String) {
          field = ((String) field).replaceAll("\n", "\\\\\n")
                                  .replaceAll("\t", "\\\\\t")
                                  .replaceAll("\0", "\\\\0")
                                  .replaceAll("\\\\", "\\\\\\\\");
        }
        builder.append(field).append('\t');
      }
      out.println(builder.deleteCharAt(builder.length() - 1));
    }

    @Override
    public void clearParameters() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean execute() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int executeUpdate() throws SQLException {
      // write it out immediately
      StringBuilder builder = new StringBuilder();
      for (Object field : fields) {
        if (field == null) {
          field = "\\N";
        } else if (field instanceof String) {
          field = ((String) field).replaceAll("\n", "\\\\\n")
                                  .replaceAll("\t", "\\\\\t")
                                  .replaceAll("\0", "\\\\0")
                                  .replaceAll("\\\\", "\\\\\\\\");
        }
        builder.append(field).append('\t');
      }
      out.println(builder.deleteCharAt(builder.length() - 1));
      return 0;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setArray(int arg0, Array arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1, int arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1, int arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBlob(int arg0, Blob arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBlob(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setBlob(int arg0, InputStream arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBoolean(int arg0, boolean arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setByte(int arg0, byte arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setBytes(int arg0, byte[] arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1, int arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setClob(int arg0, Clob arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setClob(int arg0, Reader arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setDate(int arg0, Date arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setDouble(int arg0, double arg1) throws SQLException {
      set(arg0, arg1);
    }

    @Override
    public void setFloat(int arg0, float arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setInt(int arg0, int arg1) throws SQLException {
      set(arg0, arg1);
    }

    @Override
    public void setLong(int arg0, long arg1) throws SQLException {
      set(arg0, arg1);
    }

    @Override
    public void setNCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNClob(int arg0, NClob arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNClob(int arg0, Reader arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNString(int arg0, String arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNull(int arg0, int arg1, String arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setNull(int arg0, int arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setObject(int arg0, Object arg1, int arg2, int arg3)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setObject(int arg0, Object arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setRef(int arg0, Ref arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setRowId(int arg0, RowId arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setShort(int arg0, short arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setString(int arg0, String arg1) throws SQLException {
      set(arg0, arg1);
    }

    @Override
    public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setTime(int arg0, Time arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
      set(arg0, arg1);
    }

    @Override
    public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
        throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setURL(int arg0, URL arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void addBatch(String arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void cancel() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void clearBatch() throws SQLException {
      // nothing to clear
    }

    @Override
    public void clearWarnings() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void close() throws SQLException {
      out.close();
    }

    @Override
    public boolean execute(String arg0, int arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean execute(String arg0, int[] arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean execute(String arg0, String[] arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean execute(String arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int[] executeBatch() throws SQLException {
      // already written in addBatch()
      return null;
    }

    @Override
    public ResultSet executeQuery(String arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int executeUpdate(String arg0, int arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int executeUpdate(String arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public Connection getConnection() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getFetchSize() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getMaxRows() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean getMoreResults(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getResultSetType() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public int getUpdateCount() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean isClosed() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean isPoolable() throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setCursorName(String arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setEscapeProcessing(boolean arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setFetchDirection(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setFetchSize(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setMaxFieldSize(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setMaxRows(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setPoolable(boolean arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public void setQueryTimeout(int arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
      throw new NotImplementedException();
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException {
      throw new NotImplementedException();
    }
    
  }

  // *********** JDBC specific variables ***********************
  private static Connection conn = null;
  private static Statement stmt = null;
  private static Timestamp sysdate = null;
  private static PreparedStatement custPrepStmt;
  private static PreparedStatement distPrepStmt;
  private static PreparedStatement histPrepStmt;
  private static PreparedStatement itemPrepStmt;
  private static PreparedStatement nworPrepStmt;
  private static PreparedStatement ordrPrepStmt;
  private static PreparedStatement orlnPrepStmt;
  private static PreparedStatement stckPrepStmt;
  private static PreparedStatement whsePrepStmt;

  // ********** general vars **********************************
  private static java.util.Date now = null;
  private static java.util.Date startDate = null;
  private static java.util.Date endDate = null;

  private static Random gen;
  private static int numWarehouses = 0;
  private static String fileLocation = "";
  private static boolean outputFiles = false;
  private static PrintWriter out = null;
  private static long lastTimeMS = 0;

  private static final int FIRST_UNPROCESSED_O_ID = 2101; 

  public static void main(String[] args) throws Exception {

    // #################### INITIALIZATION #####################################

    System.out.println("----------------- Initialization -------------------");

    numWarehouses = configWhseCount;
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
      String str = args[i];
      if (str.toLowerCase().startsWith("numwarehouses")) {
        String val = args[i + 1];
        System.out.println("Setting the number of warehouses to: " + val);
        numWarehouses = Integer.parseInt(val);
      }

      if (str.toLowerCase().startsWith("filelocation")) {
        fileLocation = args[i + 1];
        System.out.println("Setting the output file location to: "
                           + fileLocation);
        outputFiles = true;
      }
    }

    if (outputFiles == false) {
      initJDBC();

      // Clearout the tables
      truncateTable("item");
      truncateTable("warehouse");
      truncateTable("stock");
      truncateTable("district");
      truncateTable("customer");
      truncateTable("history");
      truncateTable("oorder");
      truncateTable("order_line");
      truncateTable("new_order");
    }

    // seed the random number generator
    gen = new Random(System.currentTimeMillis());

    // ######################### MAINLINE ######################################
    startDate = new java.util.Date();
    System.out.println("------------- LoadData Start Date = " + startDate
                       + "-------------");

    long startTimeMS = new java.util.Date().getTime();
    lastTimeMS = startTimeMS;

    long totalRows = loadWhse(numWarehouses);
    totalRows += loadItem(configItemCount);
    totalRows += loadStock(numWarehouses, configItemCount);
    totalRows += loadDist(numWarehouses, configDistPerWhse);
    totalRows += loadCust(numWarehouses, configDistPerWhse, configCustPerDist);
    totalRows += loadOrder(numWarehouses, configDistPerWhse, configCustPerDist);

    if (fastLoad) {
      PreparedStatement[] pss = new PreparedStatement[] {
        custPrepStmt,
        distPrepStmt,
        histPrepStmt,
        itemPrepStmt,
        nworPrepStmt,
        ordrPrepStmt,
        orlnPrepStmt,
        stckPrepStmt,
        whsePrepStmt
      };
      for (PreparedStatement ps : pss) {
        FastPreparedStatement fps = (FastPreparedStatement) ps;
        fps.close();
        System.out.println("load data infile '" + fps.path + "' into table " +
                     fps.table + " " + fps.fieldNames);
        stmt.execute("load data infile '" + fps.path + "' into table " +
                     fps.table + " " + fps.fieldNames);
        transCommit();
      }
    }

    long runTimeMS = (new java.util.Date().getTime()) + 1 - startTimeMS;
    endDate = new java.util.Date();
    System.out.println("");
    System.out
        .println("------------- LoadJDBC Statistics --------------------");
    System.out.println("     Start Time = " + startDate);
    System.out.println("       End Time = " + endDate);
    System.out.println("       Run Time = " + (int) runTimeMS / 1000
                       + " Seconds");
    System.out.println("    Rows Loaded = " + totalRows + " Rows");
    System.out.println("Rows Per Second = " + (totalRows / (runTimeMS / 1000))
                       + " Rows/Sec");
    System.out
        .println("------------------------------------------------------");

    // exit Cleanly
    try {
      if (outputFiles == false) {
        if (conn != null)
          conn.close();
      }
    } catch (SQLException se) {
      se.printStackTrace();
    } // end try

  } // end main

  static void transRollback() {
    if (outputFiles == false) {
      try {
        conn.rollback();
      } catch (SQLException se) {
        System.out.println(se.getMessage());
      }
    } else {
      out.close();
    }
  }

  static void transCommit() {
    if (outputFiles == false) {
      try {
        conn.commit();
      } catch (SQLException se) {
        System.out.println(se.getMessage());
        transRollback();
      }
    } else {
      out.close();
    }
  }

  static void truncateTable(String strTable) {

    System.out.println("Truncating '" + strTable + "' ...");
    try {
      stmt.execute("TRUNCATE TABLE " + strTable);
      transCommit();
    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();
    }

  }

  static void initJDBC() {

    try {
      
      // load the ini file
      String propsPath = System.getProperty("prop", "");
      Properties ini;
      if (propsPath.equals("")) {
        ini = System.getProperties();
      } else {
        ini = new Properties();
        ini.load(new FileInputStream(System.getProperty("prop")));
      }

      // display the values we need
      System.out.println("driver=" + ini.getProperty("driver"));
      System.out.println("conn=" + ini.getProperty("conn"));
      System.out.println("user=" + ini.getProperty("user"));
      System.out.println("password=******");

      // Register jdbcDriver
      Class.forName(ini.getProperty("driver"));

      // make connection
      conn = DriverManager.getConnection(ini.getProperty("conn"), ini
          .getProperty("user"), ini.getProperty("password"));
      conn.setAutoCommit(false);

      // Create Statement
      stmt = conn.createStatement();
      
      fastLoad = ini.getProperty("fastLoad", "").equals("true");
      if (fastLoad) {
        FastPreparedStatementConn conn = new FastPreparedStatementConn(ini.getProperty("fastLoaderBaseDir", ""));
        distPrepStmt = conn
            .prepareStatement("INSERT INTO district "
                              + " (d_id, d_w_id, d_ytd, d_tax, d_next_o_id, d_name, d_street_1, d_street_2, d_city, d_state, d_zip) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        itemPrepStmt = conn
            .prepareStatement("INSERT INTO item "
                              + " (i_id, i_name, i_price, i_data, i_im_id) "
                              + "VALUES (?, ?, ?, ?, ?)");
  
        custPrepStmt = conn
            .prepareStatement("INSERT INTO customer "
                              + " (c_id, c_d_id, c_w_id, "
                              + "c_discount, c_credit, c_last, c_first, c_credit_lim, "
                              + "c_balance, c_ytd_payment, c_payment_cnt, c_delivery_cnt, "
                              + "c_street_1, c_street_2, c_city, c_state, c_zip, "
                              + "c_phone, c_since, c_middle, c_data) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        histPrepStmt = conn.prepareStatement("INSERT INTO history "
                                             + " (h_c_id, h_c_d_id, h_c_w_id, "
                                             + "h_d_id, h_w_id, "
                                             + "h_date, h_amount, h_data) "
                                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
  
        ordrPrepStmt = conn
            .prepareStatement("INSERT INTO oorder "
                              + " (o_id, o_w_id,  o_d_id, o_c_id, "
                              + "o_carrier_id, o_ol_cnt, o_all_local, o_entry_d) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
  
        orlnPrepStmt = conn
            .prepareStatement("INSERT INTO order_line "
                              + " (ol_w_id, ol_d_id, ol_o_id, "
                              + "ol_number, ol_i_id, ol_delivery_d, "
                              + "ol_amount, ol_supply_w_id, ol_quantity, ol_dist_info) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        nworPrepStmt = conn.prepareStatement("INSERT INTO new_order "
                                             + " (no_w_id, no_d_id, no_o_id) "
                                             + "VALUES (?, ?, ?)");
  
        stckPrepStmt = conn
            .prepareStatement("INSERT INTO stock "
                              + " (s_i_id, s_w_id, s_quantity, s_ytd, s_order_cnt, s_remote_cnt, s_data, "
                              + "s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, "
                              + "s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        whsePrepStmt = conn
            .prepareStatement("INSERT INTO warehouse "
                              + " (w_id, w_ytd, w_tax, w_name, w_street_1, w_street_2, w_city, w_state, w_zip) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      } else {

        distPrepStmt = conn
            .prepareStatement("INSERT INTO district "
                              + " (d_id, d_w_id, d_ytd, d_tax, d_next_o_id, d_name, d_street_1, d_street_2, d_city, d_state, d_zip) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        itemPrepStmt = conn
            .prepareStatement("INSERT INTO item "
                              + " (i_id, i_name, i_price, i_data, i_im_id) "
                              + "VALUES (?, ?, ?, ?, ?)");
  
        custPrepStmt = conn
            .prepareStatement("INSERT INTO customer "
                              + " (c_id, c_d_id, c_w_id, "
                              + "c_discount, c_credit, c_last, c_first, c_credit_lim, "
                              + "c_balance, c_ytd_payment, c_payment_cnt, c_delivery_cnt, "
                              + "c_street_1, c_street_2, c_city, c_state, c_zip, "
                              + "c_phone, c_since, c_middle, c_data) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        histPrepStmt = conn.prepareStatement("INSERT INTO history "
                                             + " (h_c_id, h_c_d_id, h_c_w_id, "
                                             + "h_d_id, h_w_id, "
                                             + "h_date, h_amount, h_data) "
                                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
  
        ordrPrepStmt = conn
            .prepareStatement("INSERT INTO oorder "
                              + " (o_id, o_w_id,  o_d_id, o_c_id, "
                              + "o_carrier_id, o_ol_cnt, o_all_local, o_entry_d) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
  
        orlnPrepStmt = conn
            .prepareStatement("INSERT INTO order_line "
                              + " (ol_w_id, ol_d_id, ol_o_id, "
                              + "ol_number, ol_i_id, ol_delivery_d, "
                              + "ol_amount, ol_supply_w_id, ol_quantity, ol_dist_info) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        nworPrepStmt = conn.prepareStatement("INSERT INTO new_order "
                                             + " (no_w_id, no_d_id, no_o_id) "
                                             + "VALUES (?, ?, ?)");
  
        stckPrepStmt = conn
            .prepareStatement("INSERT INTO stock "
                              + " (s_i_id, s_w_id, s_quantity, s_ytd, s_order_cnt, s_remote_cnt, s_data, "
                              + "s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, "
                              + "s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
  
        whsePrepStmt = conn
            .prepareStatement("INSERT INTO warehouse "
                              + " (w_id, w_ytd, w_tax, w_name, w_street_1, w_street_2, w_city, w_state, w_zip) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      }

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();

    } catch (Exception e) {
      e.printStackTrace();
      transRollback();

    } // end try

  } // end initJDBC()

  static int loadItem(int itemKount) {

    int k = 0;
    int t = 0;
    int randPct = 0;
    int len = 0;
    int startORIGINAL = 0;

    try {

      now = new java.util.Date();
      t = itemKount;
      System.out.println("\nStart Item Load for " + t + " Items @ " + now
                         + " ...");

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation + "item.csv"));
        System.out.println("\nWriting Item file to: " + fileLocation
                           + "item.csv");
      }

      Item item = new Item();

      for (int i = 1; i <= itemKount; i++) {

        item.i_id = i;
        item.i_name = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(14, 24, gen));
        item.i_price = (float) (jTPCCUtil.randomNumber(100, 10000, gen) / 100.0);

        // i_data
        randPct = jTPCCUtil.randomNumber(1, 100, gen);
        len = jTPCCUtil.randomNumber(26, 50, gen);
        if (randPct > 10) {
          // 90% of time i_data isa random string of length [26 .. 50]
          item.i_data = jTPCCUtil.randomStr(len);
        } else {
          // 10% of time i_data has "ORIGINAL" crammed somewhere in middle
          startORIGINAL = jTPCCUtil.randomNumber(2, (len - 8), gen);
          item.i_data = jTPCCUtil.randomStr(startORIGINAL - 1) + "ORIGINAL"
                        + jTPCCUtil.randomStr(len - startORIGINAL - 9);
        }

        item.i_im_id = jTPCCUtil.randomNumber(1, 10000, gen);

        k++;

        if (outputFiles == false) {
          itemPrepStmt.setLong(1, item.i_id);
          itemPrepStmt.setString(2, item.i_name);
          itemPrepStmt.setDouble(3, item.i_price);
          itemPrepStmt.setString(4, item.i_data);
          itemPrepStmt.setLong(5, item.i_im_id);
          itemPrepStmt.addBatch();

          if ((k % configCommitCount) == 0) {
            long tmpTime = new java.util.Date().getTime();
            String etStr = "  Elasped Time(ms): "
                           + ((tmpTime - lastTimeMS) / 1000.000)
                           + "                    ";
            System.out.println(etStr.substring(0, 30) + "  Writing record " + k
                               + " of " + t);
            lastTimeMS = tmpTime;
            itemPrepStmt.executeBatch();
            itemPrepStmt.clearBatch();
            transCommit();
          }
        } else {
          String str = "";
          str = str + item.i_id + ",";
          str = str + item.i_name + ",";
          str = str + item.i_price + ",";
          str = str + item.i_data + ",";
          str = str + item.i_im_id;
          out.println(str);

          if ((k % configCommitCount) == 0) {
            long tmpTime = new java.util.Date().getTime();
            String etStr = "  Elasped Time(ms): "
                           + ((tmpTime - lastTimeMS) / 1000.000)
                           + "                    ";
            System.out.println(etStr.substring(0, 30) + "  Writing record " + k
                               + " of " + t);
            lastTimeMS = tmpTime;
          }
        }

      } // end for

      long tmpTime = new java.util.Date().getTime();
      String etStr = "  Elasped Time(ms): "
                     + ((tmpTime - lastTimeMS) / 1000.000)
                     + "                    ";
      System.out.println(etStr.substring(0, 30) + "  Writing record " + k
                         + " of " + t);
      lastTimeMS = tmpTime;

      if (outputFiles == false) {
        itemPrepStmt.executeBatch();
      }

      transCommit();
      now = new java.util.Date();
      System.out.println("End Item Load @  " + now);

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();
    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
    }

    return (k);

  } // end loadItem()

  static int loadWhse(int whseKount) {

    try {

      now = new java.util.Date();
      System.out.println("\nStart Whse Load for " + whseKount + " Whses @ "
                         + now + " ...");

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation
                                                   + "warehouse.csv"));
        System.out.println("\nWriting Warehouse file to: " + fileLocation
                           + "warehouse.csv");
      }

      Warehouse warehouse = new Warehouse();
      for (int i = 1; i <= whseKount; i++) {

        warehouse.w_id = i;
        warehouse.w_ytd = 300000;

        // random within [0.0000 .. 0.2000]
        warehouse.w_tax = (float) ((jTPCCUtil.randomNumber(0, 2000, gen)) / 10000.0);

        warehouse.w_name = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(6, 10,
                                                                      gen));
        warehouse.w_street_1 = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10,
                                                                          20,
                                                                          gen));
        warehouse.w_street_2 = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10,
                                                                          20,
                                                                          gen));
        warehouse.w_city = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10, 20,
                                                                      gen));
        warehouse.w_state = jTPCCUtil.randomStr(3).toUpperCase();
        warehouse.w_zip = "123456789";

        if (outputFiles == false) {
          whsePrepStmt.setLong(1, warehouse.w_id);
          whsePrepStmt.setDouble(2, warehouse.w_ytd);
          whsePrepStmt.setDouble(3, warehouse.w_tax);
          whsePrepStmt.setString(4, warehouse.w_name);
          whsePrepStmt.setString(5, warehouse.w_street_1);
          whsePrepStmt.setString(6, warehouse.w_street_2);
          whsePrepStmt.setString(7, warehouse.w_city);
          whsePrepStmt.setString(8, warehouse.w_state);
          whsePrepStmt.setString(9, warehouse.w_zip);
          whsePrepStmt.executeUpdate();
        } else {
          String str = "";
          str = str + warehouse.w_id + ",";
          str = str + warehouse.w_ytd + ",";
          str = str + warehouse.w_tax + ",";
          str = str + warehouse.w_name + ",";
          str = str + warehouse.w_street_1 + ",";
          str = str + warehouse.w_street_2 + ",";
          str = str + warehouse.w_city + ",";
          str = str + warehouse.w_state + ",";
          str = str + warehouse.w_zip;
          out.println(str);
        }

      } // end for

      transCommit();
      now = new java.util.Date();

      long tmpTime = new java.util.Date().getTime();
      System.out.println("Elasped Time(ms): "
                         + ((tmpTime - lastTimeMS) / 1000.000));
      lastTimeMS = tmpTime;
      System.out.println("End Whse Load @  " + now);

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();
    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
    }

    return (whseKount);

  } // end loadWhse()

  static int loadStock(int whseKount, int itemKount) {

    int k = 0;
    int t = 0;
    int randPct = 0;
    int len = 0;
    int startORIGINAL = 0;

    try {

      now = new java.util.Date();
      t = (whseKount * itemKount);
      System.out.println("\nStart Stock Load for " + t + " units @ " + now
                         + " ...");

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation + "stock.csv"));
        System.out.println("\nWriting Stock file to: " + fileLocation
                           + "stock.csv");
      }

      Stock stock = new Stock();

      for (int i = 1; i <= itemKount; i++) {

        for (int w = 1; w <= whseKount; w++) {

          stock.s_i_id = i;
          stock.s_w_id = w;
          stock.s_quantity = jTPCCUtil.randomNumber(10, 100, gen);
          stock.s_ytd = 0;
          stock.s_order_cnt = 0;
          stock.s_remote_cnt = 0;

          // s_data
          randPct = jTPCCUtil.randomNumber(1, 100, gen);
          len = jTPCCUtil.randomNumber(26, 50, gen);
          if (randPct > 10) {
            // 90% of time i_data isa random string of length [26 .. 50]
            stock.s_data = jTPCCUtil.randomStr(len);
          } else {
            // 10% of time i_data has "ORIGINAL" crammed somewhere in middle
            startORIGINAL = jTPCCUtil.randomNumber(2, (len - 8), gen);
            stock.s_data = jTPCCUtil.randomStr(startORIGINAL - 1) + "ORIGINAL"
                           + jTPCCUtil.randomStr(len - startORIGINAL - 9);
          }

          stock.s_dist_01 = jTPCCUtil.randomStr(24);
          stock.s_dist_02 = jTPCCUtil.randomStr(24);
          stock.s_dist_03 = jTPCCUtil.randomStr(24);
          stock.s_dist_04 = jTPCCUtil.randomStr(24);
          stock.s_dist_05 = jTPCCUtil.randomStr(24);
          stock.s_dist_06 = jTPCCUtil.randomStr(24);
          stock.s_dist_07 = jTPCCUtil.randomStr(24);
          stock.s_dist_08 = jTPCCUtil.randomStr(24);
          stock.s_dist_09 = jTPCCUtil.randomStr(24);
          stock.s_dist_10 = jTPCCUtil.randomStr(24);

          k++;
          if (outputFiles == false) {
            stckPrepStmt.setLong(1, stock.s_i_id);
            stckPrepStmt.setLong(2, stock.s_w_id);
            stckPrepStmt.setLong(3, stock.s_quantity);
            stckPrepStmt.setDouble(4, stock.s_ytd);
            stckPrepStmt.setLong(5, stock.s_order_cnt);
            stckPrepStmt.setLong(6, stock.s_remote_cnt);
            stckPrepStmt.setString(7, stock.s_data);
            stckPrepStmt.setString(8, stock.s_dist_01);
            stckPrepStmt.setString(9, stock.s_dist_02);
            stckPrepStmt.setString(10, stock.s_dist_03);
            stckPrepStmt.setString(11, stock.s_dist_04);
            stckPrepStmt.setString(12, stock.s_dist_05);
            stckPrepStmt.setString(13, stock.s_dist_06);
            stckPrepStmt.setString(14, stock.s_dist_07);
            stckPrepStmt.setString(15, stock.s_dist_08);
            stckPrepStmt.setString(16, stock.s_dist_09);
            stckPrepStmt.setString(17, stock.s_dist_10);
            stckPrepStmt.addBatch();
            if ((k % configCommitCount) == 0) {
              long tmpTime = new java.util.Date().getTime();
              String etStr = "  Elasped Time(ms): "
                             + ((tmpTime - lastTimeMS) / 1000.000)
                             + "                    ";
              System.out.println(etStr.substring(0, 30) + "  Writing record "
                                 + k + " of " + t);
              lastTimeMS = tmpTime;
              stckPrepStmt.executeBatch();
              stckPrepStmt.clearBatch();
              transCommit();
            }
          } else {
            String str = "";
            str = str + stock.s_i_id + ",";
            str = str + stock.s_w_id + ",";
            str = str + stock.s_quantity + ",";
            str = str + stock.s_ytd + ",";
            str = str + stock.s_order_cnt + ",";
            str = str + stock.s_remote_cnt + ",";
            str = str + stock.s_data + ",";
            str = str + stock.s_dist_01 + ",";
            str = str + stock.s_dist_02 + ",";
            str = str + stock.s_dist_03 + ",";
            str = str + stock.s_dist_04 + ",";
            str = str + stock.s_dist_05 + ",";
            str = str + stock.s_dist_06 + ",";
            str = str + stock.s_dist_07 + ",";
            str = str + stock.s_dist_08 + ",";
            str = str + stock.s_dist_09 + ",";
            str = str + stock.s_dist_10;
            out.println(str);

            if ((k % configCommitCount) == 0) {
              long tmpTime = new java.util.Date().getTime();
              String etStr = "  Elasped Time(ms): "
                             + ((tmpTime - lastTimeMS) / 1000.000)
                             + "                    ";
              System.out.println(etStr.substring(0, 30) + "  Writing record "
                                 + k + " of " + t);
              lastTimeMS = tmpTime;
            }
          }

        } // end for [w]

      } // end for [i]

      long tmpTime = new java.util.Date().getTime();
      String etStr = "  Elasped Time(ms): "
                     + ((tmpTime - lastTimeMS) / 1000.000)
                     + "                    ";
      System.out.println(etStr.substring(0, 30) + "  Writing final records "
                         + k + " of " + t);
      lastTimeMS = tmpTime;
      if (outputFiles == false) {
        stckPrepStmt.executeBatch();
      }
      transCommit();

      now = new java.util.Date();
      System.out.println("End Stock Load @  " + now);

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();

    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
    }

    return (k);

  } // end loadStock()

  static int loadDist(int whseKount, int distWhseKount) {

    int k = 0;
    int t = 0;

    try {

      now = new java.util.Date();

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation
                                                   + "district.csv"));
        System.out.println("\nWriting District file to: " + fileLocation
                           + "district.csv");
      }

      District district = new District();

      t = (whseKount * distWhseKount);
      System.out.println("\nStart District Data for " + t + " Dists @ " + now
                         + " ...");

      for (int w = 1; w <= whseKount; w++) {

        for (int d = 1; d <= distWhseKount; d++) {

          district.d_id = d;
          district.d_w_id = w;
          district.d_ytd = 30000;

          // random within [0.0000 .. 0.2000]
          district.d_tax = (float) ((jTPCCUtil.randomNumber(0, 2000, gen)) / 10000.0);

          district.d_next_o_id = 3001;
          district.d_name = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(6, 10,
                                                                       gen));
          district.d_street_1 = jTPCCUtil
              .randomStr(jTPCCUtil.randomNumber(10, 20, gen));
          district.d_street_2 = jTPCCUtil
              .randomStr(jTPCCUtil.randomNumber(10, 20, gen));
          district.d_city = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10, 20,
                                                                       gen));
          district.d_state = jTPCCUtil.randomStr(3).toUpperCase();
          district.d_zip = "123456789";

          k++;
          if (outputFiles == false) {
            distPrepStmt.setLong(1, district.d_id);
            distPrepStmt.setLong(2, district.d_w_id);
            distPrepStmt.setDouble(3, district.d_ytd);
            distPrepStmt.setDouble(4, district.d_tax);
            distPrepStmt.setLong(5, district.d_next_o_id);
            distPrepStmt.setString(6, district.d_name);
            distPrepStmt.setString(7, district.d_street_1);
            distPrepStmt.setString(8, district.d_street_2);
            distPrepStmt.setString(9, district.d_city);
            distPrepStmt.setString(10, district.d_state);
            distPrepStmt.setString(11, district.d_zip);
            distPrepStmt.executeUpdate();
          } else {
            String str = "";
            str = str + district.d_id + ",";
            str = str + district.d_w_id + ",";
            str = str + district.d_ytd + ",";
            str = str + district.d_tax + ",";
            str = str + district.d_next_o_id + ",";
            str = str + district.d_name + ",";
            str = str + district.d_street_1 + ",";
            str = str + district.d_street_2 + ",";
            str = str + district.d_city + ",";
            str = str + district.d_state + ",";
            str = str + district.d_zip;
            out.println(str);
          }

        } // end for [d]

      } // end for [w]

      long tmpTime = new java.util.Date().getTime();
      String etStr = "  Elasped Time(ms): "
                     + ((tmpTime - lastTimeMS) / 1000.000)
                     + "                    ";
      System.out.println(etStr.substring(0, 30) + "  Writing record " + k
                         + " of " + t);
      lastTimeMS = tmpTime;
      transCommit();
      now = new java.util.Date();
      System.out.println("End District Load @  " + now);

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();
    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
    }

    return (k);

  } // end loadDist()

  static int loadCust(int whseKount, int distWhseKount, int custDistKount) {

    int k = 0;
    int t = 0;

    Customer customer = new Customer();
    History history = new History();
    PrintWriter outHist = null;

    try {

      now = new java.util.Date();

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation
                                                   + "customer.csv"));
        System.out.println("\nWriting Customer file to: " + fileLocation
                           + "customer.csv");
        outHist = new PrintWriter(new FileOutputStream(fileLocation
                                                       + "cust-hist.csv"));
        System.out.println("\nWriting Customer History file to: "
                           + fileLocation + "cust-hist.csv");
      }

      t = (whseKount * distWhseKount * custDistKount * 2);
      System.out.println("\nStart Cust-Hist Load for " + t + " Cust-Hists @ "
                         + now + " ...");

      for (int w = 1; w <= whseKount; w++) {

        for (int d = 1; d <= distWhseKount; d++) {

          for (int c = 1; c <= custDistKount; c++) {

            sysdate = new java.sql.Timestamp(System.currentTimeMillis());

            customer.c_id = c;
            customer.c_d_id = d;
            customer.c_w_id = w;

            // discount is random between [0.0000 ... 0.5000]
            customer.c_discount = (float) (jTPCCUtil.randomNumber(1, 5000, gen) / 10000.0);

            if (jTPCCUtil.randomNumber(1, 100, gen) <= 10) {
              customer.c_credit = "BC"; // 10% Bad Credit
            } else {
              customer.c_credit = "GC"; // 90% Good Credit
            }
            if (c <= 1000) {
              customer.c_last = jTPCCUtil.getLastName(c-1);
            } else {
              customer.c_last = jTPCCUtil.getNonUniformRandomLastNameForLoad(gen);
            }
            customer.c_first = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(8,
                                                                          16,
                                                                          gen));
            customer.c_credit_lim = 50000;

            customer.c_balance = -10;
            customer.c_ytd_payment = 10;
            customer.c_payment_cnt = 1;
            customer.c_delivery_cnt = 0;

            customer.c_street_1 = jTPCCUtil.randomStr(jTPCCUtil
                .randomNumber(10, 20, gen));
            customer.c_street_2 = jTPCCUtil.randomStr(jTPCCUtil
                .randomNumber(10, 20, gen));
            customer.c_city = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10,
                                                                         20,
                                                                         gen));
            customer.c_state = jTPCCUtil.randomStr(3).toUpperCase();
            // TPC-C 4.3.2.7: 4 random digits + "11111"
            customer.c_zip = jTPCCUtil.randomNStr(gen, 4) + "11111";

            customer.c_phone = jTPCCUtil.randomNStr(gen, 16);

            customer.c_since = sysdate;
            customer.c_middle = "OE";
            customer.c_data = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(300,
                                                                         500,
                                                                         gen));

            history.h_c_id = c;
            history.h_c_d_id = d;
            history.h_c_w_id = w;
            history.h_d_id = d;
            history.h_w_id = w;
            history.h_date = sysdate;
            history.h_amount = 10;
            history.h_data = jTPCCUtil.randomStr(jTPCCUtil.randomNumber(10, 24,
                                                                        gen));

            k = k + 2;
            if (outputFiles == false) {
              custPrepStmt.setLong(1, customer.c_id);
              custPrepStmt.setLong(2, customer.c_d_id);
              custPrepStmt.setLong(3, customer.c_w_id);
              custPrepStmt.setDouble(4, customer.c_discount);
              custPrepStmt.setString(5, customer.c_credit);
              custPrepStmt.setString(6, customer.c_last);
              custPrepStmt.setString(7, customer.c_first);
              custPrepStmt.setDouble(8, customer.c_credit_lim);
              custPrepStmt.setDouble(9, customer.c_balance);
              custPrepStmt.setDouble(10, customer.c_ytd_payment);
              custPrepStmt.setLong(11, customer.c_payment_cnt);
              custPrepStmt.setLong(12, customer.c_delivery_cnt);
              custPrepStmt.setString(13, customer.c_street_1);
              custPrepStmt.setString(14, customer.c_street_2);
              custPrepStmt.setString(15, customer.c_city);
              custPrepStmt.setString(16, customer.c_state);
              custPrepStmt.setString(17, customer.c_zip);
              custPrepStmt.setString(18, customer.c_phone);

              custPrepStmt.setTimestamp(19, customer.c_since);
              custPrepStmt.setString(20, customer.c_middle);
              custPrepStmt.setString(21, customer.c_data);

              custPrepStmt.addBatch();

              histPrepStmt.setInt(1, history.h_c_id);
              histPrepStmt.setInt(2, history.h_c_d_id);
              histPrepStmt.setInt(3, history.h_c_w_id);

              histPrepStmt.setInt(4, history.h_d_id);
              histPrepStmt.setInt(5, history.h_w_id);
              histPrepStmt.setTimestamp(6, history.h_date);
              histPrepStmt.setDouble(7, history.h_amount);
              histPrepStmt.setString(8, history.h_data);

              histPrepStmt.addBatch();

              if ((k % configCommitCount) == 0) {
                long tmpTime = new java.util.Date().getTime();
                String etStr = "  Elasped Time(ms): "
                               + ((tmpTime - lastTimeMS) / 1000.000)
                               + "                    ";
                System.out.println(etStr.substring(0, 30) + "  Writing record "
                                   + k + " of " + t);
                lastTimeMS = tmpTime;

                custPrepStmt.executeBatch();
                histPrepStmt.executeBatch();
                custPrepStmt.clearBatch();
                custPrepStmt.clearBatch();
                transCommit();
              }
            } else {
              String str = "";
              str = str + customer.c_id + ",";
              str = str + customer.c_d_id + ",";
              str = str + customer.c_w_id + ",";
              str = str + customer.c_discount + ",";
              str = str + customer.c_credit + ",";
              str = str + customer.c_last + ",";
              str = str + customer.c_first + ",";
              str = str + customer.c_credit_lim + ",";
              str = str + customer.c_balance + ",";
              str = str + customer.c_ytd_payment + ",";
              str = str + customer.c_payment_cnt + ",";
              str = str + customer.c_delivery_cnt + ",";
              str = str + customer.c_street_1 + ",";
              str = str + customer.c_street_2 + ",";
              str = str + customer.c_city + ",";
              str = str + customer.c_state + ",";
              str = str + customer.c_zip + ",";
              str = str + customer.c_phone;
              out.println(str);

              str = "";
              str = str + history.h_c_id + ",";
              str = str + history.h_c_d_id + ",";
              str = str + history.h_c_w_id + ",";
              str = str + history.h_d_id + ",";
              str = str + history.h_w_id + ",";
              str = str + history.h_date + ",";
              str = str + history.h_amount + ",";
              str = str + history.h_data;
              outHist.println(str);

              if ((k % configCommitCount) == 0) {
                long tmpTime = new java.util.Date().getTime();
                String etStr = "  Elasped Time(ms): "
                               + ((tmpTime - lastTimeMS) / 1000.000)
                               + "                    ";
                System.out.println(etStr.substring(0, 30) + "  Writing record "
                                   + k + " of " + t);
                lastTimeMS = tmpTime;

              }
            }

          } // end for [c]

        } // end for [d]

      } // end for [w]

      long tmpTime = new java.util.Date().getTime();
      String etStr = "  Elasped Time(ms): "
                     + ((tmpTime - lastTimeMS) / 1000.000)
                     + "                    ";
      System.out.println(etStr.substring(0, 30) + "  Writing record " + k
                         + " of " + t);
      lastTimeMS = tmpTime;
      custPrepStmt.executeBatch();
      histPrepStmt.executeBatch();
      custPrepStmt.clearBatch();
      histPrepStmt.clearBatch();
      transCommit();
      now = new java.util.Date();
      if (outputFiles == true) {
        outHist.close();
      }
      System.out.println("End Cust-Hist Data Load @  " + now);

    } catch (SQLException se) {
      System.out.println(se.getMessage());
      transRollback();
      if (outputFiles == true) {
        outHist.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
      if (outputFiles == true) {
        outHist.close();
      }
    }

    return (k);

  } // end loadCust()

  static int loadOrder(int whseKount, int distWhseKount, int custDistKount) {

    int k = 0;
    int t = 0;
    PrintWriter outLine = null;
    PrintWriter outNewOrder = null;

    try {

      if (outputFiles == true) {
        out = new PrintWriter(new FileOutputStream(fileLocation + "order.csv"));
        System.out.println("\nWriting Order file to: " + fileLocation
                           + "order.csv");
        outLine = new PrintWriter(new FileOutputStream(fileLocation
                                                       + "order-line.csv"));
        System.out.println("\nWriting Order Line file to: " + fileLocation
                           + "order-line.csv");
        outNewOrder = new PrintWriter(new FileOutputStream(fileLocation
                                                           + "new-order.csv"));
        System.out.println("\nWriting New Order file to: " + fileLocation
                           + "new-order.csv");
      }

      now = new java.util.Date();
      Oorder oorder = new Oorder();
      NewOrder new_order = new NewOrder();
      OrderLine order_line = new OrderLine();
      jdbcIO myJdbcIO = new jdbcIO();

      t = (whseKount * distWhseKount * custDistKount);
      t = (t * 11) + (t / 3);
      System.out.println("whse=" + whseKount + ", dist=" + distWhseKount
                         + ", cust=" + custDistKount);
      System.out.println("\nStart Order-Line-New Load for approx " + t
                         + " rows @ " + now + " ...");

      for (int w = 1; w <= whseKount; w++) {

        for (int d = 1; d <= distWhseKount; d++) {
          // TPC-C 4.3.3.1: o_c_id must be a permutation of [1, 3000]
          int[] c_ids = new int[custDistKount];
          for (int i = 0; i < custDistKount; ++i) {
            c_ids[i] = i + 1;
          }
          // Collections.shuffle exists, but there is no Arrays.shuffle
          for (int i = 0; i < c_ids.length - 1; ++i) {
            int remaining = c_ids.length - i - 1;
            int swapIndex = gen.nextInt(remaining) + i + 1;
            assert i < swapIndex;
            int temp = c_ids[swapIndex];
            c_ids[swapIndex] = c_ids[i];
            c_ids[i] = temp;
          }

          for (int c = 1; c <= custDistKount; c++) {

            oorder.o_id = c;
            oorder.o_w_id = w;
            oorder.o_d_id = d;
            oorder.o_c_id = c_ids[c - 1];
            // o_carrier_id is set *only* for orders with ids < 2101 [4.3.3.1]
            if (oorder.o_id < FIRST_UNPROCESSED_O_ID) {
              oorder.o_carrier_id = jTPCCUtil.randomNumber(1, 10, gen);
            } else {
              oorder.o_carrier_id = null;
            }
            oorder.o_ol_cnt = jTPCCUtil.randomNumber(5, 15, gen);
            oorder.o_all_local = 1;
            oorder.o_entry_d = System.currentTimeMillis();

            k++;
            if (outputFiles == false) {
              myJdbcIO.insertOrder(ordrPrepStmt, oorder);
            } else {
              String str = "";
              str = str + oorder.o_id + ",";
              str = str + oorder.o_w_id + ",";
              str = str + oorder.o_d_id + ",";
              str = str + oorder.o_c_id + ",";
              str = str + oorder.o_carrier_id + ",";
              str = str + oorder.o_ol_cnt + ",";
              str = str + oorder.o_all_local + ",";
              Timestamp entry_d = new java.sql.Timestamp(oorder.o_entry_d);
              str = str + entry_d;
              out.println(str);
            }

            // 900 rows in the NEW-ORDER table corresponding to the last
            // 900 rows in the ORDER table for that district (i.e., with
            // NO_O_ID between 2,101 and 3,000)

            if (c >= FIRST_UNPROCESSED_O_ID) {

              new_order.no_w_id = w;
              new_order.no_d_id = d;
              new_order.no_o_id = c;

              k++;
              if (outputFiles == false) {
                myJdbcIO.insertNewOrder(nworPrepStmt, new_order);
              } else {
                String str = "";
                str = str + new_order.no_w_id + ",";
                str = str + new_order.no_d_id + ",";
                str = str + new_order.no_o_id;
                outNewOrder.println(str);
              }

            } // end new order

            for (int l = 1; l <= oorder.o_ol_cnt; l++) {
              order_line.ol_w_id = w;
              order_line.ol_d_id = d;
              order_line.ol_o_id = c;
              order_line.ol_number = l; // ol_number
              order_line.ol_i_id = jTPCCUtil.randomNumber(1, 100000, gen);
              if (order_line.ol_o_id < FIRST_UNPROCESSED_O_ID) {
                order_line.ol_delivery_d = oorder.o_entry_d;
                order_line.ol_amount = 0;
              } else {
                order_line.ol_delivery_d = null;
                // random within [0.01 .. 9,999.99]
                order_line.ol_amount = (float) (jTPCCUtil.randomNumber(1,
                                                                       999999,
                                                                       gen) / 100.0);
              }

              order_line.ol_supply_w_id = order_line.ol_w_id;
              order_line.ol_quantity = 5;
              order_line.ol_dist_info = jTPCCUtil.randomStr(24);

              k++;
              if (outputFiles == false) {

                myJdbcIO.insertOrderLine(orlnPrepStmt, order_line);
              } else {
                String str = "";
                str = str + order_line.ol_w_id + ",";
                str = str + order_line.ol_d_id + ",";
                str = str + order_line.ol_o_id + ",";
                str = str + order_line.ol_number + ",";
                str = str + order_line.ol_i_id + ",";
                Timestamp delivery_d = new Timestamp(order_line.ol_delivery_d);
                str = str + delivery_d + ",";
                str = str + order_line.ol_amount + ",";
                str = str + order_line.ol_supply_w_id + ",";
                str = str + order_line.ol_quantity + ",";
                str = str + order_line.ol_dist_info;
                outLine.println(str);
              }

              if ((k % configCommitCount) == 0) {
                long tmpTime = new java.util.Date().getTime();
                String etStr = "  Elasped Time(ms): "
                               + ((tmpTime - lastTimeMS) / 1000.000)
                               + "                    ";
                System.out.println(etStr.substring(0, 30) + "  Writing record "
                                   + k + " of " + t);
                lastTimeMS = tmpTime;
                if (outputFiles == false) {

                  ordrPrepStmt.executeBatch();
                  nworPrepStmt.executeBatch();
                  orlnPrepStmt.executeBatch();
                  ordrPrepStmt.clearBatch();
                  nworPrepStmt.clearBatch();
                  orlnPrepStmt.clearBatch();
                  transCommit();
                }
              }

            } // end for [l]

          } // end for [c]

        } // end for [d]

      } // end for [w]

      System.out.println("  Writing final records " + k + " of " + t);
      if (outputFiles == false) {
        ordrPrepStmt.executeBatch();
        nworPrepStmt.executeBatch();
        orlnPrepStmt.executeBatch();
      } else {
        outLine.close();
        outNewOrder.close();
      }
      transCommit();
      now = new java.util.Date();
      System.out.println("End Orders Load @  " + now);

    } catch (Exception e) {
      e.printStackTrace();
      transRollback();
      if (outputFiles == true) {
        outLine.close();
        outNewOrder.close();
      }
    }

    return (k);

  } // end loadOrder()

  // This originally used org.apache.commons.lang.NotImplementedException
  // but I don't get why...
  public static final class NotImplementedException
      extends UnsupportedOperationException {
  }
} // end LoadData Class
