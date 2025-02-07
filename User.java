package e_commerce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

public class User  {
	static int customer_id;
	Scanner scanner=new Scanner(System.in);
	public boolean signUp() {
		System.out.println("\n\t********Welcome to signup page************");
		System.out.println();
		
		DatabaseConnection.createConnection();
		PreparedStatement preparedStatement=null;
		
		
		System.out.println("\tEnter First name");
		String firstName=scanner.next();
		
		System.out.println("\tEnter last name");
		String lastName=scanner.next();
		
		System.out.println("\tEnter your email");
		String email=scanner.next();
		String password;
		
		
		while(true) {
			System.out.println("\tEnter  password");
			 password=scanner.next();
			System.out.println("\tEnter password again");
			String confirmPassword=scanner.next();
			if(password.equals(confirmPassword)){
				break;
			}
			else {
				System.out.println("\n\t\tpasswords are not equal");
			}
		}
		scanner.nextLine();
		System.out.println("\tEnter Address");
		String address=scanner.nextLine();
		
		System.out.println("\tEnter City");
		String City=scanner.next();
		System.out.println("\tEnter state");
		String State=scanner.next();
		System.out.println("\tEnter zip_code ");
		String zip_code=scanner.next();
		System.out.println("\tEnter phone_no ");
		String phone_no=scanner.next();
		
		try {
			preparedStatement=DatabaseConnection.conn.prepareStatement("Insert into Customers(First_Name,Last_Name,Email,Password,Address,city,state,zip_code,phone_no) values(?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, password);
			preparedStatement.setString(5, address);
			preparedStatement.setString(6, City);
			preparedStatement.setString(7, State);
			preparedStatement.setString(8, zip_code);
			preparedStatement.setString(9,phone_no);
			
			preparedStatement.execute();
			
			System.out.println("\n\t\t*****SignUp SucessFull*****");
			return true;
			
		} 
		catch(SQLIntegrityConstraintViolationException e){
			System.out.println("\n\tEmail id is already present! \n\tPlease register with different email id.");
			this.signUp();
		}
		catch (  SQLException   e) {
			
			
			e.printStackTrace();
			return false;
	
		}
		finally {
			
			
			try {
				preparedStatement.close();
				DatabaseConnection.conn.close();
			} catch (SQLException | NullPointerException  e) {
			
				System.out.println(e.getMessage());
			}
		}
		return false;
	}
	
	
	public Boolean custlogIn() {
		DatabaseConnection.createConnection();
        System.out.println("\n\tWelcome to Customer LogIn :)");
        System.out.println();
        String username;
        String password;
        String query = "Select email,password,customer_id from customers";
        Statement stmt = null;
        ResultSet resultSet=null;
        boolean isPresent=false;
        try {
            stmt = DatabaseConnection.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery(query);
            do {
                System.out.println("Enter email(username): ");
                username = scanner.next();
                System.out.println("Enter Password: ");
                password = scanner.next();
                while (resultSet.next()) {
                    if(username.equals(resultSet.getString(1))) {
                        if(password.equals(resultSet.getString(2))) {
                            customer_id = resultSet.getInt(3);
                            System.out.println("customer id " +customer_id);
                            System.out.println("Log in successfull...");
                            isPresent=true;
                            return true;
                        }
                        
                    }
                    
                }
                System.out.println("\n\tusername or password incorrect! Please Enter valid Details.");
                resultSet.beforeFirst();
            }while(isPresent==false);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                resultSet.close();
                stmt.close();
              DatabaseConnection.conn.close();
            } catch (SQLException | NullPointerException  e) {
            	System.out.println(e.getMessage());
            }
        }
        return false;
    }
    public Boolean adminLogIn() {
    	DatabaseConnection.createConnection();
        System.out.println("\n\t*****Welcome to Admin LogIn*****");
        System.out.println();
        String username;
        String password;
        String query = "Select username,password from admin";
        Statement stmt = null;
        ResultSet resultSet=null;
        boolean isPresent=false;
        try {
            stmt =DatabaseConnection.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery(query);
            do {
                System.out.println("\tEnter username: ");
                username = scanner.next();
                System.out.println("\tEnter Password: ");
                password = scanner.next();
                while (resultSet.next()) {
                    if(username.equals(resultSet.getString(1))) {
                    	
                        if(password.equals(resultSet.getString(2))) {
                            System.out.println("\n\tAdmin Log in successfull :)");
                            isPresent=true;
                            return true;
                        }
                        else {
                            System.out.println("\n\tPassword incorrect :(");
                        }
                    }
                    else {
                        System.out.println(username+" Username is not present");
                    }
                }
                resultSet.beforeFirst();
            }while(isPresent==false);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                resultSet.close();
                stmt.close();
               DatabaseConnection.conn.close();
            } catch (SQLException | NullPointerException  e) {
            	System.out.println(e.getMessage());
            }
        }
        return false;
    }
    
    public void logOut() {
    	customer_id=0;
    	
    }
}
