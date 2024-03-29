import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    //Initializes instance variables
    private Scanner in; 
    private BankAccount activeAccount; 
    private Bank bank;
    private long accountNo;

    //Gives value to switch statement on line 35
    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int DELETE = 5;
    public static final int LOGOUT = 6;

    //Gives value to deposit / withdraw / transfer statements in BankAccount.java
    public static final int INVALIDLOW = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    public static final int INVALIDMAX = 3;
    
    public static final long MAXAMOUNT = 1000000000000L;
    
    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 30;
    boolean needNextLine = false;
    boolean logoutDuringSession = false;
    
    //Constructs a new instance of the ATM class.
    
    public ATM() {
        this.in = new Scanner(System.in);
        
        try {
        	this.bank = new Bank();
        } catch (IOException e) {
	    // cleanup any resources (i.e., the Scanner) and exit
        }       
    }
    
    //Application execution begins here.
    public void startup() {
        System.out.println("Welcome to the AIT ATM!\n"); //Friendly greeting
        
        //Login loop
        while (true) {
        	
            //Account No check
        	
        	if(needNextLine) {
        		in.nextLine();
        	}
        	
        	String accountNoString;
            do {
    			System.out.print("Account No.: ");
    			accountNoString = in.nextLine();
    		} while(!accountNoString.equals("+") && (accountNoString.equals("") || Long.valueOf(accountNoString) < 100000001L
    				  || Long.valueOf(accountNoString) > 999999999L) && !accountNoString.equals("-1"));
            
            if (accountNoString.equals("+")) { //Create Account Check
            	createAccount();
            } else {
            	//Continue with login
	            accountNo = Long.valueOf(accountNoString);
	            
	            System.out.print("PIN        : ");
	            int pin = in.nextInt();
	            
	            //Check for login validity
	            if (accountNo != -1 && isValidLogin(accountNo, pin)) {
	            	
	                //Valid
	                System.out.print("\nHello again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
	                
	                //Selection loop
	                boolean validLogin = true;
	                while (validLogin) {
	                    switch (getSelection()) {
	                        case VIEW: showBalance(); break;
	                        case DEPOSIT: deposit(); break;
	                        case WITHDRAW: withdraw(); break;
	                        case TRANSFER: transfer(); break;
	                        case DELETE: delete();
	                        case LOGOUT: validLogin = false; logoutDuringSession = true; break;
	                        default: System.out.println("\nInvalid selection.\n"); break;
	                    }
	                }
	            } else if (accountNo == -1 && pin == -1) {
	            	shutdown();
	            } else {
	              System.out.println("\nInvalid account number and/or PIN.\n");
	              needNextLine = true;
	            }
            }
        }
    }
    
    public void createAccount() {
		String newFirstName;
		String newLastName;
	    int newPin;
	    long longPin;
	    User newUser;
		
		do { //Infinite prompt til proper first name is entered
			System.out.print("First Name: ");
			newFirstName = in.nextLine();
		} while(newFirstName.length() > 20 || newFirstName.equals(""));

		do { //Infinite prompt til proper last name is entered
			System.out.print("Last Name: ");
			newLastName = in.nextLine();
		} while (newLastName.length() > 30 || newLastName.equals(""));
		
		do { //Infinite prompt til proper pin is entered
			System.out.print("Pin: ");
	    	newPin = in.nextInt();
	    	longPin = Long.valueOf(newPin);
		} while (longPin > 9999L || longPin < 1000L);
		
		//Giving values to variables
		newUser = new User(newFirstName, newLastName);
		BankAccount newAccount = bank.createAccount(newPin, newUser);
	
		bank.update(newAccount);
		bank.save();
		
		System.out.println("\nThank you. Your account number is " + newAccount.getAccountNo());
		System.out.println("Please login to access your newly created account.");
		needNextLine = true;
    }
    
    public boolean isValidLogin(long accountNo, int pin) {
        activeAccount = bank.login(accountNo, pin);
        return activeAccount != null;  
    }
    
    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer funds");
        System.out.println("[5] Delete account");
        System.out.println("[6] Logout");
        
        return in.nextInt();
    }
    
    //Switch statement cases
    public void showBalance() { 
        System.out.println("\nCurrent balance: " + activeAccount.getBalance() + "\n");
    }
    
    public void delete() {
    	System.out.println("You can only delete your own account. Are you absolutely sure?");
        System.out.println("[1] Yes");
        System.out.println("[2] No");
        int choice =  in.nextInt();
        if (choice == 1) {
        	System.out.print("Please provide your PIN: ");
        	int pin = in.nextInt();
        	
        	if (bank.login(activeAccount.getAccountNo(), pin) != null) {
            	System.out.println("One last time- are you absolutely sure?");
                System.out.println("[1] Yes");
                System.out.println("[2] No");
                choice =  in.nextInt();
                if (choice == 1) {
                	bank.deleteAccount(bank.getIndexOfAccount(activeAccount.getAccountNo()));
                	System.out.println("Understood. Account deleted. Redirecting to login screen.");
                	bank.save();
                } else {
                	System.out.println("Deletion aborted.");
                }
        	} else {
        		System.out.println("Invalid combination inputted. Deletion aborted.");
        	}
        } else if (choice == 2) {
        	System.out.println("Deletion aborted.");
        } else {
        	System.out.println("Invalid selection inputted. Deletion aborted.");
        }
    }
    
    public void deposit() { 
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALIDLOW) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nDeposit accepted.\n");
            bank.update(activeAccount);
            bank.save();
        }
    }
      
    public void transfer() {
    	System.out.print("Enter account: ");
    	long transferAccountNo = in.nextLong();
    	
    	if (bank.getAccount(transferAccountNo) != null && transferAccountNo != activeAccount.getAccountNo()) { //if account exists
            
    		System.out.print("\nEnter amount: ");
            double amount = in.nextDouble();
            
            int status = activeAccount.withdraw(amount);
            if (status == ATM.INVALIDLOW) {
                System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
            } else if (status == ATM.INSUFFICIENT) {
                System.out.println("\nTransfer rejected. Insufficient funds.\n");
            } else if (status == ATM.SUCCESS) { //Withdrawal is successful
            	BankAccount transferAccount = bank.getAccount(transferAccountNo);
                int transferStatus = transferAccount.deposit(amount);
                if (transferStatus == ATM.INVALIDMAX) {
                	System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.\n");
                } else if (transferStatus == ATM.SUCCESS) {
                	System.out.println("\nTransfer accepted. Balance is now " + activeAccount.getBalance() + ".\n");
                }
            	bank.update(transferAccount);
            }
            
    	} else if (transferAccountNo == activeAccount.getAccountNo()) {
    		System.out.println("\nTransfer rejected. Destination account cannot be same as origin account.\n");
    	} else {
    		System.out.println("\nTransfer rejected. Destination account not found.\n");
    	}
    	
    	bank.update(activeAccount);
    	bank.save();
    }
    
    public void withdraw() { 
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALIDLOW) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
            bank.update(activeAccount);
            bank.save();
        }
    }
    
    public void shutdown() {
        if (in != null) {
            in.close();
        }
        
        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.startup();
    }
}
