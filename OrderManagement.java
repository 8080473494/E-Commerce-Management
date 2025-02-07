package e_commerce;



import java.util.Scanner;

public class OrderManagement  {
	static Scanner scanner=new Scanner(System.in);
	
	public static void userInterface() {
		User user=new User();
		Customer customer=new Customer();
		Admin admin =new Admin();
		
		System.out.println("\n\t*****Welcome to our E-Commerce Website*****");
		System.out.println();
//		System.out.println("Select below options");
		System.out.println("\t1.Signup");
		System.out.println("\t2.Login");
		System.out.println("\t3.Admin Login");
		System.out.println();
		int choice=scanner.nextInt(); 
		switch (choice) {
		case 1: 
			
			if(user.signUp())
				if(user.custlogIn()) {
					customer.goToCustPage();
				}
			break;
		
		case 2:
			if(user.custlogIn()) {
				customer.goToCustPage();
			}
			
			break;
		case 3:
			if(user.adminLogIn()) {
				admin.goToAdminPage();
			}
			
			break;
		}
		
	}
	
	public static void main(String[] args) {
		
		userInterface();
				
	}

}
