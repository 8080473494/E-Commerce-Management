package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class Customer{
	Scanner scanner=new Scanner(System.in);
	Order order=new Order();
	public void goToCustPage() {
		User user=new User();
	
        int choice;
        HashSet<Integer>selectedProductList = new HashSet<>();
        do {
            System.out.println("\n\t********Welcome to Customer Interface********");
                System.out.println("\t1]Want to see your cart\n\t2]Add Products to your cart\n\t3]See Order histroy\n\t4]Return product\n\t5]Log Out");
                choice = scanner.nextInt();
                if(choice==2) {
                selectedProductList=availableCategory(selectedProductList);                
                }
                else if(choice==1) {
                wantToSeeYourCart(selectedProductList); 
                System.out.println(selectedProductList); 
                }
                else if(choice==3) {
                    showOrdersForCust();
                }
                else if(choice==4) {
                	order.showDeliveredProducts();
                }
                else {
                	
                    System.out.println("\n\tThank you!!!\n\tLog out Successfull :)");
                    user.logOut();
                    OrderManagement.userInterface();
                    
                    
                    break;
                }
        } while (choice!=5);
    }
	
	public HashSet<Integer> wantToSeeYourCart(HashSet<Integer>selectedProdList){
		Order order =new Order();
	 	DatabaseConnection.createConnection();
        if(selectedProdList.isEmpty()) {
            System.out.println("Your cart is empty!!!");
        }
        else {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet=null;
            String catquery = "Select * from Products where product_id=?";
            int wantToBuyProd=1;
            try {
                preparedStatement =DatabaseConnection.conn.prepareStatement(catquery);
                for(Integer prod:selectedProdList) {
                    preparedStatement.setInt(1, prod);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        System.out.println(resultSet.getInt(1)+" | "+resultSet.getString(2)+" | "+resultSet.getString(3)+" | "+resultSet.getFloat(4)+" | "+resultSet.getString(5)+" | "+resultSet.getInt(6)+"\n");
                    }
                }
                while(wantToBuyProd==1) {
                    System.out.println("\n\tWant to buy product?(0/1): ");
                    wantToBuyProd= scanner.nextInt();
                    if(wantToBuyProd==1) {
                        order.buyNow(selectedProdList);
                    }
                    else {
                        break;
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    resultSet.close();
                    preparedStatement.close();
                    DatabaseConnection.conn.close();
                } catch (SQLException | NullPointerException  e) {
                	System.out.println(e.getMessage());
                }
            }
        }
        return selectedProdList;
    }
	
	
	
	public HashSet<Integer> availableCategory(HashSet<Integer>selectedProductList) {
		 
		 DatabaseConnection.createConnection();
		 Cart cart=new Cart();
	        String query = "Select Distinct(category) from products";
	        ResultSet resultSet = null;
	        PreparedStatement preparedStatement=null;
	        int wantToContinue=1;
	        HashMap<Integer, String>categoryName = new HashMap<>();
	        try {
	            preparedStatement =DatabaseConnection.conn.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	            resultSet = preparedStatement.executeQuery(query);
	            do {
	                System.out.print("\n\t*****Category Available*****\n\t\t");
	                int count=0;
	                while(resultSet.next()) {
	                    System.out.print((count)+")"+resultSet.getString(1)+"\n\t\t");
	                    categoryName.put(count, resultSet.getString(1));
	                    count++;
	                }
	                resultSet.beforeFirst();
	                int selectCat;
	                System.out.println("\nPlease Select a Category: ");
	                selectCat = scanner.nextInt();
	                while(categoryName.containsKey(selectCat)==false) {
	                    System.out.println("\tInvalid category...");
	                    System.out.println("\tPlease Select correct Category: ");
	                    selectCat = scanner.nextInt();
	                }
	                selectedProductList = cart.addProductsToCart(selectCat, categoryName,selectedProductList);
	                System.out.println("\n\tDo you want to select more products?(0/1): ");
	                wantToContinue = scanner.nextInt();
	            } while (wantToContinue==1);
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
	        return selectedProductList;
	    }
	
	public void showOrdersForCust() {
		DatabaseConnection.createConnection();
		Order order=new Order();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int order_id;
        int prod_id;
        int prod_quantity;
        int prod_status;
        int cancle_item_id;
        String query = "SELECT p.PRODUCT_NAME, p.PRODUCT_ID, oi.price_per_unit, o.ORDER_ID, oi.ORDER_ITEM_ID,o.order_status,oi.quantity FROM  orders o JOIN order_items oi ON o.ORDER_ID = oi.order_id JOIN products p ON oi.product_id = p.PRODUCT_ID WHERE o.customer_id = ?";
        HashMap<Integer,ArrayList<Integer>>orderItemIdMap = new HashMap<>();
        ArrayList<Integer>dataOfOrderItem = new ArrayList<>();
        ArrayList<Integer> tempArrayList = new ArrayList<>();
        try {
            preparedStatement =DatabaseConnection.conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1, User.customer_id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()==false) {
                System.out.println("\n\tYou haven't placed any order!!!!\n");
                return;
            }
            else {
                System.out.println("Order_item Id->Product Id->Product Name->Price->Status->Quantity\n\n");
                resultSet.beforeFirst();
                while(resultSet.next()) {
                    System.out.println(resultSet.getInt(5)+" | "+ resultSet.getInt(2)+" | "+ resultSet.getString(1)+" | "+resultSet.getFloat(3)+" | "+ resultSet.getString(6)+" | "+ resultSet.getInt(7)+"\n");
                    int status = changeStatusToInt(resultSet.getString(6));
                    System.out.println("Product status is " + status);
                    dataOfOrderItem.add(resultSet.getInt(4)); //stores order_id                    
                    dataOfOrderItem.add(resultSet.getInt(2)); //stores prod_id                    
                    dataOfOrderItem.add(status); //stores prod_status                    
                    dataOfOrderItem.add(resultSet.getInt(7)); //stores prod_quantity                    
                    orderItemIdMap.put(resultSet.getInt(5),dataOfOrderItem);
                }
                System.out.println("\n\tDo you want to cancle any item?(0/1): ");
                int choice = scanner.nextInt();
                int countOfItemsInOrder;
                while(choice==1){
                    System.out.println("\tPlease enter order_item_id you want to cancle: ");
                    cancle_item_id = scanner.nextInt();
                    if(orderItemIdMap.containsKey(cancle_item_id)) {
                        tempArrayList = orderItemIdMap.get(cancle_item_id);
                        order_id = tempArrayList.get(0);
                        prod_id = tempArrayList.get(1);
                        prod_status =  tempArrayList.get(2);
                        prod_quantity = tempArrayList.get(3);
                        if(prod_status==1) {
                            System.out.println(order.cancleItemFromOrder(cancle_item_id)+" item canclled successfully!!");
                            countOfItemsInOrder =order.FindCountOfOrderItems(order_id);
                            if(countOfItemsInOrder==0) {
                                System.out.println("\t"+order.cancleWholeOrder(order_id)+"Record updated from orders table");
                                System.out.println("\n\tWhole order is cancelled....");
                            }
                            order.updateQuantity(prod_id,prod_quantity);
                        }
                        else if(prod_status==0) {
                            System.out.println("\n\tyou can't cancle this order..\n\t this order is deliverd");
                        }
                        else if(prod_status==-1) {
                            System.out.println("\n\tYou already cancelled this product");
                        }
                        tempArrayList.clear();
                    }
                    else {
                        System.out.println("\tThis order item is not valid!!");
                    }
                    System.out.println("\tDo you want to cancle more items?(0/1): ");
                    choice = scanner.nextInt();
                }
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
	
	public int changeStatusToInt(String status) {
        if(status.equals("pending")) {
            return 1;
        } else if(status.equals("Delivered")) {
            return 0;
        }
        return -1; 
	}
	
	
	
	 
	 
	 
	 
	 
}
