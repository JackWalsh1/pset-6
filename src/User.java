public class User {
    
	  //Initializes instance variables
	  private String firstName;
	  private String lastName;

	  //Constructor, creates instances of class
	  public User(String firstName, String lastName) {
	    this.firstName = firstName;
	    this.lastName = lastName;
	    
	  }

	  //Getters to retrieve info from User class
	  public String getFirstName() {
	    return firstName;
	  }

	  public String getLastName() {
	    return lastName;
	  }
	  
    /**
     * Formats the first and last name in preparation to be written to the data file.
     * 
     * @return a fixed-width string in line with the data file specifications.
     */
    
    public String serialize() {
        return String.format("%1$-" + ATM.FIRST_NAME_WIDTH + "s", firstName) +
            String.format("%1$-" + ATM.LAST_NAME_WIDTH + "s", lastName);
    }
}

