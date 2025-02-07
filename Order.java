package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Order {
	Scanner scanner=new Scanner(System.in);
	
	public void buyNow(HashSet<Integer> products) {


		
		Payment payment=new Payment();
		Shipments shipments=new Shipments();
		
		
		HashMap<Integer,ArrayList<Integer>> productMap=new HashMap<>();
		Statement statement=null;
		ResultSet resultSet=null;
		try {
			statement = DatabaseConnection.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			int exit;
			do {
				System.out.println("Enter product ID to Buy");
				int id=scanner.nextInt();
				if(! products.contains(id) ) {
					System.out.println("Please Enter valid product id");
					
				}
				else {
					if(productMap.containsKey(id)) {
						System.out.println("You have already bought product");
					}
					else {
						System.out.println("Enter Quantity of the product");
						int quantity=scanner.nextInt();
						resultSet = statement.executeQuery("Select quantity,price from products where product_id= " + id);
						resultSet.next();
						int actual_quantity=resultSet.getInt(1);
						int price=resultSet.getInt(2);
						if(actual_quantity<quantity) {
							System.out.println("Sorry! Out of stock");
							System.out.println("Please try later");
							
						}
						else {
							
							productMap.put(id,new ArrayList<>());
							productMap.get(id).add(quantity);
							productMap.get(id).add(price);
							resultSet.updateInt(1, actual_quantity-quantity);
							resultSet.updateRow();
							products.remove(Integer.valueOf(id));
						}
					}
					
				}
				
				
				System.out.println("Press 1 to buy more products else 0");
				exit=scanner.nextInt();
				
				
				
				
			}
			while(exit!=0);
			
			if(productMap.size()!=0) {
				float total_price=calculateTotalPrice(productMap);
				this.addToOrderTable(User.customer_id, total_price); //adds order to order table
				
				
				resultSet=statement.executeQuery("select max(order_id) from orders where customer_id="+User.customer_id);
				resultSet.next();
				int order_id=resultSet.getInt(1);
				
				if(!payment.payNow(total_price, order_id)) {
					statement.executeUpdate("delete from orders where order_id="+ order_id );
				}
				else {
					
					shipments.addToShipmentTable(order_id);
					
					
					int product_id;
					int quantity;
					int product_price;
					for(Map.Entry entry:productMap.entrySet()) {
						ArrayList<Integer> list=(ArrayList<Integer>)entry.getValue();
						product_id=(int)entry.getKey();
						quantity=list.get(0);
						product_price=list.get(1);
						
						this.addToOrderItemTable(order_id, product_id, quantity, product_price);
					}
					productMap.clear();
				}
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				
				resultSet.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException e) {
				
				System.out.println(e.getMessage());
			}
			
		}
		
	}
	
	public float calculateTotalPrice(HashMap<Integer,ArrayList<Integer>> productMap) {
		float total_price=0;
		for(Map.Entry l:productMap.entrySet()) {
			ArrayList<Integer> list=(ArrayList<Integer>)l.getValue();
			total_price+=list.get(0)*list.get(1);
		}
		return total_price;
	}
	
	public void addToOrderItemTable(int order_id,int product_id,int quantity,float product_price) {
		DatabaseConnection.createConnection();
		Statement statement=null;
	
		PreparedStatement preparedStatement=null;
		
		try {
			statement=DatabaseConnection.conn.createStatement();
			preparedStatement=DatabaseConnection.conn.prepareStatement("insert into order_items(order_id,product_id,quantity,price_per_unit) values(?,?,?,?)");
			
			preparedStatement.setInt(1, order_id);
			preparedStatement.setInt(2, product_id);
			preparedStatement.setInt(3, quantity);
			preparedStatement.setFloat(4, product_price);
			preparedStatement.execute();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			try {
				preparedStatement.close();
			DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException  e) {
				
				System.out.println(e.getMessage());
			}
		}
		
		
	}
	
	public void addToOrderTable(int customer_id,float total_price) {
		DatabaseConnection.createConnection();
		PreparedStatement preparedStatement=null;
	try {
		preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into orders(customer_id,order_status,order_date,total_price) values(?,'processing',sysdate,?)");
		preparedStatement.setInt(1, customer_id);
		preparedStatement.setFloat(2, total_price);
		System.out.println("customer id " +customer_id);
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
	
	
	public int cancleItemFromOrder(int order_item_id) {
		DatabaseConnection.createConnection();
        PreparedStatement preparedStatement=null;
        String query = "Delete from order_items where order_item_id=?";
        int countOfDelete=0;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, order_item_id);
            countOfDelete = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
			try {
				preparedStatement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException  e) {
				
				System.out.println(e.getMessage());
			}
		}
        return countOfDelete;
    }
    public int FindCountOfOrderItems(int order_id){
    	DatabaseConnection.createConnection();
        int count=1000;
        String query = "SELECT COUNT(*) FROM order_items WHERE order_id = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, order_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt(1);
        } catch (SQLException e) {
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
        return count;
    }
    public int cancleWholeOrder(int order_id) {
    	DatabaseConnection.createConnection();
        int count=0;
        String query = "Update orders set order_status='Cancelled' WHERE order_id = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, order_id);
            count = preparedStatement.executeUpdate();
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
        return count;
    }
    public void updateQuantity(int prod_id, int prod_quantity) {
    	DatabaseConnection.createConnection();
        String query = "Update products set QUANTITY=QUANTITY+? where product_id=?";
        PreparedStatement preparedStatement= null;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, prod_quantity);
            preparedStatement.setInt(2, prod_id);
            System.out.println(preparedStatement.executeUpdate()+" Record updated in product table");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
                DatabaseConnection.conn.close();
            } catch (SQLException | NullPointerException  e) {
               System.out.println(e.getMessage());
            }
        }
    }
    
    //Return order
    
    public void showDeliveredProducts() {
    	DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int order_id;
        int prod_id;
        int prod_quantity;
        int return_item_id;
        String query = "SELECT p.PRODUCT_NAME, p.PRODUCT_ID, oi.price_per_unit, o.ORDER_ID, oi.ORDER_ITEM_ID,o.order_status,oi.quantity FROM orders o JOIN order_items oi ON o.ORDER_ID = oi.order_id JOIN products p ON oi.product_id = p.PRODUCT_ID WHERE o.customer_id = ? and o.order_status='Delivered'";
        HashMap<Integer,ArrayList<Integer>>orderItemIdMap = new HashMap<>();
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1,User.customer_id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()==false) {
                System.out.println("\n\tYou haven't placed any order!!!!\n");
                return;
            }
            else {
                System.out.println("Order_item Id->Product Id->Product Name->Price->Status->Quantity\n\n");
                resultSet.beforeFirst();
                while(resultSet.next()) {
                    ArrayList<Integer>dataOfOrderItem = new ArrayList<>();
                    System.out.println(resultSet.getInt(5)+" | "+                               resultSet.getInt(2)+" | "+                               resultSet.getString(1)+" | "+                               resultSet.getFloat(3)+" | "+                               resultSet.getString(6)+" | "+                               resultSet.getInt(7)+"\n");
                    dataOfOrderItem.add(resultSet.getInt(4)); //stores order_id                    
                    dataOfOrderItem.add(resultSet.getInt(2)); //stores prod_id                    
                    dataOfOrderItem.add(resultSet.getInt(7)); //stores prod_quantity                    
//                    System.out.println("dataOfOrderItem: "+dataOfOrderItem);
                    orderItemIdMap.put(resultSet.getInt(5),dataOfOrderItem);
                }
                System.out.println(orderItemIdMap+" :Map");
                System.out.println("Do you want to return any item?(0/1): ");
                int choice = scanner.nextInt();
                while(choice==1){
                    System.out.println("Please enter order_item_id you want to return: ");
                    return_item_id = scanner.nextInt();
                    if(orderItemIdMap.containsKey(return_item_id)) {
                        ArrayList<Integer>tempArrayList = orderItemIdMap.get(return_item_id);
                        if(tempArrayList.isEmpty()) {
                            System.out.println("You have returned all your orders!!!");
                            break;
                        }
                        else {
                            System.out.println("Array: "+tempArrayList);
                            order_id = tempArrayList.get(0);
                            prod_id = tempArrayList.get(1);
                            prod_quantity = tempArrayList.get(2);
                            System.out.println(returnItem(return_item_id)+" item returned successfully!!");
                            int count = FindCountOfOrderItems(order_id);
                            if(count==0) {
                                System.out.println("You returned the whole order\n"+returnWholeOrder(order_id)+" Record updated in order table");
                            }
                            updateQuantity(prod_id,prod_quantity);
                            orderItemIdMap.remove(return_item_id);
                        }
                    }
                    else {
                        System.out.println("This order item is not valid!!");
                    }
                    System.out.println("Do you want to return more items?(0/1): ");
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public int returnItem(int return_item_id) {
    	DatabaseConnection.createConnection();
        PreparedStatement preparedStatement=null;
        String query = "Delete from order_items where order_item_id=?";
        int countOfDelete=0;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, return_item_id);
            countOfDelete = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
                DatabaseConnection.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return countOfDelete;
    }
    public int returnWholeOrder(int order_id) {
    	DatabaseConnection.createConnection();
        int count=0;
        String query = "Update orders set order_status='RETURNED' WHERE order_id = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseConnection.conn.prepareStatement(query);
            preparedStatement.setInt(1, order_id);
            count = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
                DatabaseConnection.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    
    
}
