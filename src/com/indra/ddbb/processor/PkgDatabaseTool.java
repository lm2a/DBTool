package com.indra.ddbb.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class PkgDatabaseTool {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	static String mDatabase;
	static String mTable;
	static String mPath;
	//fields
	static String mFinger_b64;
	static String mFinger_byte;
	static String mFinger_number;
	static String mRecord_id;
	static String mSys_date;
	static String mVoter_id;
	
	
	
	
	public static void main(String[] args) {
		PkgDatabaseTool tool = new PkgDatabaseTool();
		tool.readProperties();
		tool.process();
	}
	
	private void readProperties(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);
			
			mDatabase = prop.getProperty("database");
			mTable = prop.getProperty("table");
			mPath = getPathInTheRightWay(prop.getProperty("path"))+mDatabase;
			
			
			mFinger_b64 = prop.getProperty("finger_b64");
			mFinger_byte = prop.getProperty("finger_byte");
			mFinger_number = prop.getProperty("finger_number");
			mRecord_id = prop.getProperty("record_id");
			mSys_date = prop.getProperty("sys_date");
			mVoter_id = prop.getProperty("voter_id");
			
			// get the property value and print it out
			System.out.println(mDatabase);
			System.out.println(mTable);
			System.out.println(mPath+mDatabase);
			
			System.out.println(mFinger_b64);
			System.out.println(mFinger_byte);
			System.out.println(mFinger_number);
			System.out.println(mRecord_id);
			System.out.println(mSys_date);
			System.out.println(mVoter_id);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	  
	  /**
	   * Check path ends with / or add it
	   * @param path
	   * @return
	   */
	  private static String getPathInTheRightWay(String path){
		  if(path==null||(path.length()<3)){
			 path = "./"; 
		  }else{
		  if(path.charAt(path.length()-1) != '/'){
			 path = path+'/'; 
		  }
		  }
		 return path; 
	  }
	  
	    /**
	     * Connect to the test.db database
	     * @return the Connection object
	     */
	    private Connection getDDBBConnectiont() {
	        // SQLite connection string
	    	String url = String.format("jdbc:sqlite:%s", mPath); 	        
	    	Connection conn = null;
	        try {
	            conn = DriverManager.getConnection(url);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return conn;
	    }
	    
	    

	    public void process(){
	        String sql = String.format("SELECT * FROM %s", mTable);
	        int n = 0;
	   

	        Timestamp timestamp0 = new Timestamp(System.currentTimeMillis());
	        
	        try (Connection conn = getDDBBConnectiont();
	             Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql)){
	        	 System.out.println("Comienza el proceso: "+getFormattedTimestamp());
	            // loop through the result set
	            while (rs.next()) {
	            	
	            	String f64 = getBase64FromBiteArray(rs.getBytes(mFinger_byte));
	            	
	            	System.out.println(++n +" - "+ rs.getString(mFinger_b64) +  "\t" + 
	            			"->"+f64.substring(0, 20) + "\t" +
	            			rs.getInt(mFinger_number) + "\t" +
	            			rs.getString(mSys_date) + "\t" +
	            			rs.getInt(mVoter_id) + "\t" +
	            			rs.getInt(mRecord_id));
	            	update(conn, rs.getInt(mRecord_id), f64);
	            }
		        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		        long diff = timestamp1.getTime() - timestamp0.getTime();
		        
	       	 System.out.println("Termina el proceso. Demoro: "+diff/1000+" seconds");
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    
	    
	    private String getFormattedTimestamp(){
	        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        return sdf.format(timestamp);
	    }
	    
	    private String getBase64FromBiteArray(byte[] array){
	    	return Base64.encodeToString(array, Base64.DEFAULT);
	    }
	    
	/**
	 * update TFINGERPRINTS
	 * @param id
	 * @param finger64
	 */
	    public void update(Connection conn, int id, String finger64) {
	    	byte[] blob = null;
	        String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?", mTable, mFinger_b64, mFinger_byte, mRecord_id);
	        try (
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	 
	            // set the corresponding param
	            pstmt.setString(1, finger64);
	            pstmt.setBytes(2, blob);
	            pstmt.setInt(3, id);
	            // update 
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    
	    

}
