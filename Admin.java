package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Scanner;



public class Admin  {
	
	Scanner scanner=new Scanner(System.in);
	public void updateProduct() {
		
		
		PreparedStatement preparedStatement=null;
		try {
			
			int exit;
			
			do {
				this.showProductsByCategory();
				int ch;
				int product_exit;
				do {
					System.out.println("\tEnter product id of the product to be update");
					int product_id=scanner.nextInt();
					
					
					System.out.println("select product property to be changed from the below list");
					System.out.println("\t1.product_name");
					System.out.println("\t2.product Description");
					System.out.println("\t3.Product price");
					System.out.println("\t4.Product Quantity");
					System.out.println("\t5.Product Category");
					ch=scanner.nextInt();
					String column_name="";
					String value="";
					
					
					switch(ch) {
					case 1:
						
						column_name="Product_name";
						System.out.println("Enter new name of the product");
						scanner.nextLine();
						value=scanner.nextLine();
						 
						
						break;
					case 2:
						column_name="Description";
						System.out.println("Enter new Description of the product");
						scanner.nextLine();
						 value=scanner.nextLine();
						break;
					case 3:
						column_name="Price";
						System.out.println("Enter new price of the product");
						 value=scanner.next();
						break;
					case 4:
						column_name="Quantity";
						System.out.println("Enter new quantity of the product");
						 value=scanner.next();
						break;
					case 5:
						column_name="Category";
						System.out.println("Enter new category of the product");
						 value=scanner.next();
						break;
					 default:
						 System.out.println("Plese Enter valid choice");
						
					}
					
					
					
					DatabaseConnection.createConnection();
					preparedStatement=DatabaseConnection.conn.prepareStatement("Update products set " + column_name + " = ? where product_id=?" );
					
					preparedStatement.setString(1, value);
					preparedStatement.setInt(2, product_id);
					
					preparedStatement.executeUpdate();
					
					System.out.println("Success");
					System.out.println("Do you want to update more properties?(0/1): ");
					product_exit=scanner.nextInt();
					
				}
				while(product_exit!=0);
				
				
				System.out.println("Do you want to continue?(0/1):");
				exit=scanner.nextInt();
				
				
				
			
			}
			while(exit!=0);
		} 
		catch (SQLSyntaxErrorException e) {
			
		}
		catch (SQLException  e) {
			
			e.printStackTrace();
		}
		
		finally {
			try {
				preparedStatement.close();
			DatabaseConnection.conn.close();
			} catch (SQLException |NullPointerException e) {
				
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	
	public void addProduct() {
		DatabaseConnection.createConnection();
		
        PreparedStatement preparedStatement = null;
        String query = "Insert into Products(PRODUCT_NAME,DESCRIPTION,PRICE,CATEGORY,QUANTITY) values(?,?,?,?,?)";
        
        try {
        	int doYouWantToConti=1;
        	do {
        		System.out.println("Enter product description: ");
                String p_desc = scanner.nextLine();
                
                System.out.println("Enter product name: ");
                
                String p_name = scanner.nextLine();
                System.out.println("Enter product price: ");
                float p_price = scanner.nextFloat();
                System.out.println("Enter product category: ");
                String p_category = scanner.next();
                System.out.println("Enter product quantity: ");
                int p_quantity = scanner.nextInt();
      
                preparedStatement =DatabaseConnection.conn.prepareStatement(query);
                preparedStatement.setString(1, p_name);
                preparedStatement.setString(2, p_desc);
                preparedStatement.setFloat(3, p_price);
                preparedStatement.setString(4, p_category.toUpperCase()); //converting category name to uppercase to remove case sensitive
                preparedStatement.setInt(5, p_quantity);
                preparedStatement.addBatch();
                
                System.out.println("Do you want to add more product?(0/1): ");
                doYouWantToConti = scanner.nextInt();
                scanner.nextLine();
                
			} while (doYouWantToConti==1);
        	preparedStatement.executeBatch();
            System.out.println("\n\t"+" Recordes inserted Successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
               DatabaseConnection.conn.close();
            } catch (SQLException | NullPointerException e) {
               System.out.println(e.getMessage());
            }
        }
    }
    public void updateStatus() {
        PreparedStatement preparedStatement = null;
        String updateQuery = "Update orders set order_status='Delivered' where order_id=?";
        int selectOrdId;
        try {
            showOrdersForAdmin();
            System.out.println("\tSelect order id to change status: ");
            selectOrdId = scanner.nextInt();
            DatabaseConnection.createConnection();
            preparedStatement =DatabaseConnection.conn.prepareStatement(updateQuery);
            preparedStatement.setInt(1, selectOrdId);
            System.out.println("\t"+preparedStatement.executeUpdate()+" Recorde changed\n\n\tUpdated order table records\n");
            showOrdersForAdmin();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
            } catch (SQLException | NullPointerException e) {
               System.out.println(e.getMessage());
            }
        }
    }
    
    public void goToAdminPage() {
        int choice;
        do {
            System.out.println("\n\t*******Menu For Admin*******\n"+"\t1)Add product\n"+"\t2)Update product\n"+"\t3)Update Status of delivery\n"+"\t4)To Stop");
            System.out.print("\nEnter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            if(choice==1) {
            	addProduct();            
            }
            else if(choice==2) {
            	updateProduct();            
            }
            else if(choice==3){
                updateStatus();
            }
            else if(choice==4){
                System.out.println("Thank you!!!");
                OrderManagement.userInterface();
                break;
            }
            else {
                System.out.println("\n\tPlease select valid choice :(");
            }
        }while(choice!=4);
    }
    
    public void showOrdersForAdmin() {
    	DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "Select * from orders";
        try {
            preparedStatement =DatabaseConnection.conn.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1)+" | "+resultSet.getInt(2)+" | "+resultSet.getString(3)+" | "+resultSet.getDate(4)+" | "+resultSet.getFloat(5)+" | "+"\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                resultSet.close();
                preparedStatement.close();
               DatabaseConnection.conn.close();
            } catch (SQLException |NullPointerException e) {
               System.out.println(e.getMessage());
            }
        }
    }
    
    public void showProductsByCategory() {
		 
		 DatabaseConnection.createConnection();
		 ResultSet resultSet=null;
		 System.out.println("\n********----Welcome to Interface----********");
	     String query = "Select Distinct(category) from products";
	     String catquery = "Select * from Products where category=?";

	     PreparedStatement preparedStatement=null;
	     HashMap<Integer, String>categoryName = new HashMap<>();
	     try {
	    	 
	                        preparedStatement =DatabaseConnection.conn.prepareStatement(query);
	            resultSet = preparedStatement.executeQuery(query);
	           
	            System.out.print("\n\t*****Category Available*****\n\t\t");
	            int count=0;
	            while(resultSet.next()) {
	            	
	                System.out.print((count)+")"+resultSet.getString(1)+"\n\t\t");
	                categoryName.put(count, resultSet.getString(1));
	                count++;
	            }
	            int selectCat;
	            //for printing all the records having same category selected by user            
	            System.out.println("\nPlease Select a Category:");
	            selectCat = scanner.nextInt();
	            preparedStatement =DatabaseConnection.conn.prepareStatement(catquery);
	            preparedStatement.setString(1, categoryName.get(selectCat));
	            resultSet = preparedStatement.executeQuery();
	            while(resultSet.next()) {
	                System.out.println(resultSet.getInt(1)+" | "+resultSet.getString(2)+" | "+resultSet.getString(3)+" | "+                                   resultSet.getFloat(4)+" | "+                                   resultSet.getString(5)+" | "+                                   resultSet.getInt(6)+"\n");
	            }
	        } catch (SQLException e) {
	        	
	            e.printStackTrace();
	        }finally {
	            try {
	                resultSet.close();
	                preparedStatement.close();
	              DatabaseConnection.conn.close();
	            } catch (SQLException | NullPointerException e) {
	                System.out.println(e.getMessage());
	            }
	        }
	 }
	
	
	
}
