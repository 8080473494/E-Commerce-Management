package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Shipments{
	
	Scanner scanner=new Scanner(System.in);
	public void addToShipmentTable(int order_id) {
		DatabaseConnection.createConnection();
		System.out.println("press 1 for deliver to your actual address else press any key");
		Statement statement=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		
		
		int default_address=scanner.nextInt();
		String address="";
		String state="";
		String city="";
		int zip_code=0;
		
		
		try {
			statement=DatabaseConnection.conn.createStatement();
			
			preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into shipments(order_id,DILIVERY_BOY_ID,shipment_date,address,city,state,zip_code) values(?,?,SYSDATE,?,?,?,?)");
			if(default_address==1) {
				resultSet=statement.executeQuery("select address,city,state,zip_code from customers where customer_id="+User.customer_id);
				resultSet.next();
				address=resultSet.getString(1);
				city=resultSet.getString(2);
				state=resultSet.getString(3);
				zip_code=resultSet.getInt(4);
				
				
			}
			else {
				System.out.println("Enter Address");
				scanner.nextLine();
				address=scanner.nextLine();
				System.out.println("Enter city");
				city=scanner.next();
				System.out.println("Enter state");
				state=scanner.next();
				System.out.println("Enter zip code");
				zip_code=scanner.nextInt();
			}
			int d=setDiliveryBoy();
			preparedStatement.setInt(1, order_id);
			preparedStatement.setInt(2, d);
			preparedStatement.setString(3, address);
			preparedStatement.setString(4, city);
			preparedStatement.setString(5, state);
			preparedStatement.setInt(6, zip_code);
			System.out.println("Order ID" + order_id );
			System.out.println("D "+ d);
			preparedStatement.execute();
			System.out.println("Order Placed Successfully");
			
			setOrderStatusToShipped(order_id);
			
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				preparedStatement.close();
				resultSet.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException e) {
				
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	private int setDiliveryBoy() {
		DatabaseConnection.createConnection();
		String sql = "SELECT DILIVERY_BOY_ID FROM delivery_boys";
		Statement stmt=null;
		int dilivery_boy_id=0;
		try{
            
            
            stmt = DatabaseConnection.conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

           
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while (rs.next()) {
                int id = rs.getInt("DILIVERY_BOY_ID");
                ids.add(id);
            }

            Random rand = new Random();
             dilivery_boy_id = ids.get(rand.nextInt(ids.size()));

            

            
           
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
		finally{
			try {
				stmt.close();
				DatabaseConnection.conn.close();
			} catch (SQLException e) {
		
				System.out.println(e.getMessage());
			}
			
		}
		System.out.println("Random ID"+ dilivery_boy_id);
		return dilivery_boy_id;
	}
	
	private void setOrderStatusToShipped(int order_id) {
		DatabaseConnection.createConnection();
		Statement statement=null;
		
		try {
			statement=DatabaseConnection.conn.createStatement();
			statement.executeUpdate("Update orders set order_status='shipped' where order_id="+order_id);
			System.out.println("Order is shipped");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException e) {
				
				System.out.println(e.getMessage());
			}
		}
	}
	
}
