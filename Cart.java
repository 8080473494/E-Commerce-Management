package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Cart extends DatabaseConnection{
	
	Scanner scanner=new Scanner(System.in);
	public HashSet<Integer> addProductsToCart(int choice,HashMap<Integer, String>mapofCategory,HashSet<Integer>selectedProductList) {
      	super.createConnection();
	 	
	 	PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;
        String catquery = "Select * from Products where category=?";
        HashSet<Integer> availableProdId = new HashSet<>();
        int doYouWantToContinue=1;
        try {
            preparedStatement = conn.prepareStatement(catquery);
            preparedStatement.setString(1, mapofCategory.get(choice));
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1)+" | "+resultSet.getString(2)+" | "+ resultSet.getString(3)+" | "+ resultSet.getFloat(4)+" | "+resultSet.getString(5)+" | "+ resultSet.getInt(6)+"\n");
                availableProdId.add(resultSet.getInt(1));
            }
            do {
                System.out.println("\nPlease Select a Product Id you want to add to your cart: ");
                int prod_id = scanner.nextInt();
                if(availableProdId.contains(prod_id)) {
                    if(selectedProductList.contains(prod_id)) {
                        System.out.println("\tThis product is already present in your cart....");
                    }
                    else {
                        selectedProductList.add(prod_id);
                    }
                }
                else {
                    System.out.println("\tThis product id does not exist...\n\tPlease enter valid product id from above display product details!!");;
                }
                System.out.println("Do you want to add more products with same category?(0/1): ");
                doYouWantToContinue = scanner.nextInt();
            } while (doYouWantToContinue==1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
			try {
				preparedStatement.close();
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
        return selectedProductList;
    }
 
}
