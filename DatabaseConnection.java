package e_commerce;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConnection {
	
	static Connection conn=null;

	
	public static void createConnection() {
		 Properties props = new Properties();
	        try {
	            FileInputStream fis = new FileInputStream("C:\\Users\\PratikTanawade\\Documents\\properties.txt");
	            props.load(fis);
	            fis.close();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }

	        String url = props.getProperty("url");
	        String username = props.getProperty("username");
	        String password = props.getProperty("password");
	        String driver=props.getProperty("driver");

	
	        try {
	             Class.forName(driver);
	             conn = DriverManager.getConnection(url, username, password);
	             
	           
	        } 
	        catch (ClassNotFoundException | SQLException e) {
	        	System.out.println(e.getMessage());
	        }
	        
	    }
	

}
