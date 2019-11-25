import java.text.NumberFormat;

public class BankAccount {
        
    //Defines account number generation
    private static long prevAccountNo = 100000000L;

    //Initializes instance variables
    private int pin;
    private long accountNo;
    private User accountHolder;
    private double balance;

    //Constructor, creates instances of class
    public BankAccount(int pin, long accountNo, double balance, User accountHolder) {
        this.pin = pin;
        this.accountNo = ++BankAccount.prevAccountNo;
        this.balance = 0.0;
        this.accountHolder = accountHolder;
    }

    //Getters to retrieve info from BankAccount class
    public int getPin() {
        return pin;
    }
    
    public long getAccountNo() {
        return accountNo;
    }
    
    public String getBalance() {
        NumberFormat currency = NumberFormat.getCurrencyInstance();
    
        return currency.format(balance); //Solves "view balance" action
    }
    
    public User getAccountHolder() {
        return accountHolder;
    }

    //Solves "deposit / withdraw cash" action

    public int deposit(double amount) {
        if (amount <= 0) {
            return ATM.INVALIDLOW;    
        } else if (amount + balance > ATM.MAXAMOUNT) {
        	return ATM.INVALIDMAX;
        } else {
            balance = balance + amount;
        }
            
        return ATM.SUCCESS;
    }
     
//    public int transfer(long accountNo, double amount) {
//    	if (balance + amount >= ATM.MAXAMOUNT) {
//    		return ATM.INVALIDMAX;
//    	} else if (amount > balance) {
//    		return ATM.INSUFFICIENT;
//    	} else if (amount - balance < 0) {
//    		return ATM.INSUFFICIENT;
//    	} else if (wOD.equals("deposit")){
//    		balance = balance + amount;
//    	} else if (wOD.equals("withdraw")) {
//    		balance = balance - amount;
//    	}
//    	return ATM.SUCCESS;
//    }
    
    public int withdraw(double amount) {
        if (amount <= 0) {
            return ATM.INVALIDLOW;
        } else if (amount > balance) {
            return ATM.INSUFFICIENT;
        } else {
            balance = balance - amount;
        }
        
        return ATM.SUCCESS;
    }
    
    /*
     * Formats the account balance in preparation to be written to the data file.
     * 
     * @return a fixed-width string in line with the data file specifications.
     */
    
    private String formatBalance() {
    	return String.format("%.2f", balance);
    }
    
    /*
     * Converts this BankAccount object to a string of text in preparation to
     * be written to the data file.
     * 
     * @return a string of text formatted for the data file
     */
    
    @Override
    public String toString() {
        return String.valueOf(accountNo) +
            String.valueOf(pin) +
            accountHolder.serialize() +
            formatBalance();
    }
}
