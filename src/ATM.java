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
    public static final int LOGOUT = 4;

    //Gives value to deposit / withdraw statements in BankAccount.java
    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    
    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 20;
    int needNextLine = 0;
    
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
        	
        	if(needNextLine != 0) {
        		in.nextLine();
        	}
        	
            System.out.print("Account No.: ");
            String accountNoString = in.nextLine();
            
            if (accountNoString.equals("+")) { //Create Account Check
            	//Create account
            	String newFirstName;
            	String newLastName;
                int newPin;
                User newUser;
            	
            	System.out.print("First name: ");
            	newFirstName = in.nextLine();
            	System.out.print("Last name: ");
            	newLastName = in.nextLine();
            	System.out.print("PIN: ");
            	newPin = in.nextInt();
            	
            	newUser = new User(newFirstName, newLastName);
            	BankAccount newAccount = bank.createAccount(newPin, newUser);
            	
            	bank.update(newAccount);
            	bank.save();
            	
            	System.out.println("Thank you. Your account number is " + newAccount.getAccountNo());
            	System.out.println("Please login to access your newly created account.");
            	needNextLine++;
            	
            } else {
            	//Continue with login
	            accountNo = Long.valueOf(accountNoString);
	            
	            System.out.print("PIN        : ");
	            int pin = in.nextInt();
	            
	            if (isValidLogin(accountNo, pin)) {
	                //Valid
	                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
	                
	                //Selection loop
	                boolean validLogin = true;
	                while (validLogin) {
	                    //User selects desired action
	                    switch (getSelection()) {
	                        case VIEW: showBalance(); break;
	                        case DEPOSIT: deposit(); break;
	                        case WITHDRAW: withdraw(); break;
	                        case LOGOUT: validLogin = false; break;
	                        default: System.out.println("\nInvalid selection.\n"); break;
	                    }
	                }
	            } else if (accountNo == -1 && pin == -1) {
	            	shutdown();
	            } else {
	              System.out.println("\nInvalid account number and/or PIN.\n");
	            }
            }
        }
    }
    
    public boolean isValidLogin(long accountNo, int pin) {
        return accountNo == activeAccount.getAccountNo() && pin == activeAccount.getPin();
    }
    
    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Logout");
        
        return in.nextInt();
    }
    
    //Switch statement cases
    public void showBalance() { 
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() { 
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nDeposit accepted.\n");
        }
    }
    
    public void withdraw() { 
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
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
