package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class Payment{
	
	Scanner scanner = new Scanner(System.in);
			
	
	public boolean payNow(float total_price,int order_id) {
		
		
		System.out.println("\n\t*****Please Select Your payment method*****");
		System.out.println("\t1.UPI Payment");
		System.out.println("\t2.Card Payment");
		Statement statement=null;
		ResultSet resultSet=null;
		int payment_id;
		int choice=scanner.nextInt();
		try {
			
			if(choice==1) {
				System.out.println("Enter Your UPI ID");
				String upi_id=scanner.next();
			
				System.out.println("\n\tPayment_Sucessfull :)");
				System.out.println("\tRs." + total_price +"Paid");
				this.addToPaymentTable(order_id, "UPI", total_price, "Paid");
				DatabaseConnection.createConnection();
			
				statement=DatabaseConnection.conn.createStatement();
				resultSet=statement.executeQuery("Select max(p.payment_id) from payments p inner join orders o on p.order_id=o.order_id where o.customer_id = "+ User.customer_id);
				resultSet.next();
				payment_id=resultSet.getInt(1);
				this.addToUPITable(payment_id,upi_id);
				return true;
				
			 } 
			
		
			else {
				System.out.println("Enter Your Card Number");
				String card_number=scanner.next();
			
				System.out.println("\tPayment_Sucessfull :)");
				System.out.println("\tRs." + total_price +"Paid");
				this.addToPaymentTable(order_id, "Card", total_price, "Paid");
				DatabaseConnection.createConnection();
		
				statement=DatabaseConnection.conn.createStatement();
				resultSet=statement.executeQuery("Select max(p.payment_id) from payments p inner join orders o on p.order_id=o.order_id where o.customer_id ="+ User.customer_id);
				resultSet.next();
				payment_id=resultSet.getInt(1);
				this.addToCardTable(payment_id,card_number);
				return true;
			}	
			
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try {
				statement.close();
				resultSet.close();
				DatabaseConnection.conn.close();
			} catch (SQLException  | NullPointerException e) {
			
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void addToUPITable(int payment_id,String upi_id) {
		DatabaseConnection.createConnection();
		PreparedStatement preparedStatement=null;
		
		try {
			preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into UPI(payment_id,upi_id) values(?,?)");
			
			preparedStatement.setInt(1, payment_id);
			preparedStatement.setString(2, upi_id);
			preparedStatement.execute();
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				preparedStatement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public void addToCardTable(int payment_id,String card_number) {
		DatabaseConnection.createConnection();
		PreparedStatement preparedStatement=null;
		
		try {
			preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into card(payment_id,card_number) values(?,?)");
			
			preparedStatement.setInt(1, payment_id);
			preparedStatement.setString(2, card_number);
			preparedStatement.execute();
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				preparedStatement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	
	public void addToPaymentTable(int order_id,String payment_method,float total_price,String status) {
		DatabaseConnection.createConnection();
		PreparedStatement preparedStatement=null;
		try {
			preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into payments(order_id,payment_method,amount,status,payment_date) values(?,?,?,?,sysdate)");
			preparedStatement.setInt(1, order_id);
			preparedStatement.setString(2, payment_method);
			preparedStatement.setFloat(3, total_price);
			preparedStatement.setString(4, status);
		
			preparedStatement.execute();
		
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				preparedStatement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException e) {
			
				System.out.println(e.getMessage());
			}
		}
	}
	
}
