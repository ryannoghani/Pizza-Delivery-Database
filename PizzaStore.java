/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              
              while(usermenu) {

                  String query = String.format("SELECT role FROM Users WHERE login = '%s';", authorisedUser);
                  List<List<String>> result = esql.executeQueryAndReturnResult(query);
                  String role = result.get(0).get(0).trim();
                

                if (role.equals("manager")) {

                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Profile");
                  System.out.println("2. Update Profile");
                  System.out.println("3. View Menu");
                  System.out.println("4. Place Order"); //make sure user specifies which store
                  System.out.println("5. View Full Order ID History");
                  System.out.println("6. View Past 5 Order IDs");
                  System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                  System.out.println("8. View Stores"); 

                  //**the following functionalities should only be able to be used by drivers & managers**
                  System.out.println("9. Update Order Status");

                  //**the following functionalities should ony be able to be used by managers**
                  System.out.println("10. Update Menu");
                  //System.out.println("11. Update User");

                  System.out.println(".........................");
                  System.out.println("20. Log out");
                  

                  switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser, role); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser, role); break;
                   case 6: viewRecentOrders(esql, authorisedUser, role); break;
                   case 7: viewOrderInfo(esql, authorisedUser, role); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   case 10: updateMenu(esql); break;
                   //case 11: updateUser(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                  }
                }

                else if (role.equals("driver")) {

                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Profile");
                  System.out.println("2. Update Profile");
                  System.out.println("3. View Menu");
                  System.out.println("4. Place Order"); //make sure user specifies which store
                  System.out.println("5. View Full Order ID History");
                  System.out.println("6. View Past 5 Order IDs");
                  System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                  System.out.println("8. View Stores"); 
                  //**the following functionalities should only be able to be used by drivers & managers**
                  System.out.println("9. Update Order Status");

                  //System.out.println("11. Update User");

                  System.out.println(".........................");
                  System.out.println("20. Log out");
                 
                  //dont include updateMenu

                  switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser, role); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser, role); break;
                   case 6: viewRecentOrders(esql, authorisedUser, role); break;
                   case 7: viewOrderInfo(esql, authorisedUser, role); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   //case 11: updateUser(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                  }
                }

                else if (role.equals("customer")) {

                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Profile");
                  System.out.println("2. Update Profile");
                  System.out.println("3. View Menu");
                  System.out.println("4. Place Order"); //make sure user specifies which store
                  System.out.println("5. View Full Order ID History");
                  System.out.println("6. View Past 5 Order IDs");
                  System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                  System.out.println("8. View Stores"); 
                  //System.out.println("11. Update User");

                  System.out.println(".........................");
                  System.out.println("20. Log out");
              
                     //dont include updateOrderStatus
                     //dont include updateMenu
                     switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser, role); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser, role); break;
                   case 6: viewRecentOrders(esql, authorisedUser, role); break;
                   case 7: viewOrderInfo(esql, authorisedUser, role); break;
                   case 8: viewStores(esql); break;
                   //case 11: updateUser(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                  }
                }  
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user. when a new user comes to the system, he/she can setup a new
account through your interface, by providing necessary information. The user will
automatically be a customer. Their favorite item will be empty.
    **/
   public static void CreateUser(PizzaStore esql){

      try {
        System.out.print("Enter login: ");
        String login = in.readLine();

        System.out.print("Enter password: ");
        String password = in.readLine();

        String role = "customer";

        String favoriteItems = null;

        System.out.print("Enter phone number: ");
        String phoneNum = in.readLine();

        String query = String.format(
            "INSERT INTO Users (login, password, role, favoriteItems, phoneNum) VALUES ('%s', '%s', '%s', '%s', '%s');",
            login, password, role, favoriteItems, phoneNum
        );

        esql.executeUpdate(query);
        System.out.println("User successfully created!");

      } 
      catch (Exception e) {
        System.err.println("Error creating user: " + e.getMessage());
      }
   }//end CreateUser





   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist.
    *
    * user can use his/her login and password to login into the system.
Once he/she logs in, a session will be maintained for him/her until logout (an example
has been provided in the Java source code).
    **/
     

   public static String LogIn(PizzaStore esql) {
       try {
         System.out.print("Enter login: ");
         String login = in.readLine();

         System.out.print("Enter password: ");
         String password = in.readLine();

         // Query to check if the user exists with the given credentials
         String query = String.format(
            "SELECT Users.* FROM Users WHERE login = '%s' AND password = '%s';",
            login, password
         );

         // Execute the query and check if a result is returned
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
            System.out.println("Login failed: Invalid username or password.");
            return null;
         } 
	      else {
            System.out.println("Login successful! Welcome, " + login);
            return login; // Return the username if login is successful
         }
      } 
      catch (Exception e) {
        System.err.println("Error during login: " + e.getMessage());
        return null;
      }
   }
 

// Rest of the functions definition go in here


  /*
   Profile: Users should be able to view and update their favoriteItem. Users should also be
   able to view their phoneNum. Users should be able to change their password &
   phoneNum. Only managers can edit a user’s login and role.
   */

   public static void viewProfile(PizzaStore esql, String login) {

      try {
         System.out.print("YOUR PROFILE\n");
         String query = String.format("SELECT favoriteItems, phoneNum FROM Users WHERE login = '%s';", login);
         esql.executeQueryAndPrintResult(query);
      } 
      catch (Exception e) {
         System.err.println("Error retrieving profile: " + e.getMessage());
      }


   }

   public static void updateProfile(PizzaStore esql, String login, String role) {
      
      try {

         String newFavoriteItems;
         String newPhoneNumber;
         String newPassword;
         String newLogin;
         String newRole;
         String updateQuery;

         role = role.trim();
         if (!role.equals("manager"))
         {
            System.out.println("Which part of your profile would you like to update?");
            System.out.println("1. Favorite Items");
            System.out.println("2. Phone Number");
            System.out.println("3. Password");
            System.out.print("Enter your choice: ");
            int input = Integer.parseInt(in.readLine());

            switch (input) {
               case 1:
                  System.out.print("Enter new favorite items: ");
                  newFavoriteItems = in.readLine();
                  updateQuery = String.format("UPDATE Users SET favoriteItems = '%s' WHERE login = '%s';", newFavoriteItems, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               case 2:
                  System.out.print("Enter new phone number: ");
                  newPhoneNumber = in.readLine();
                  updateQuery = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s';", newPhoneNumber, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               case 3:
                  System.out.print("Enter new password: ");
                  newPassword = in.readLine();
                  updateQuery = String.format("UPDATE Users SET password = '%s' WHERE login = '%s';", newPassword, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               default:
                  System.out.println("Invalid choice.");
                  return;
            }
         }

         else
         {
            System.out.println("Enter the login of the profile you would like to update: ");
            String update_login = in.readLine();
            String existsQuery = String.format("SELECT * FROM Users WHERE login = '%s';", update_login);
            int count = esql.executeQuery(existsQuery);
            if (count == 0) {
               System.out.println("Login not found! Returning to menu.");
               return;
            }

            System.out.println("Which part of the profile would you like to update?");
            System.out.println("1. Favorite Items");
            System.out.println("2. Phone Number");
            System.out.println("3. Password");
            System.out.println("4. Login");
            System.out.println("5. Role");
            System.out.print("Enter your choice: ");

            int input = Integer.parseInt(in.readLine());

            switch (input) {
               case 1:
                  System.out.print("Enter new favorite items: ");
                  newFavoriteItems = in.readLine();
                  updateQuery = String.format("UPDATE Users SET favoriteItems = '%s' WHERE login = '%s';", newFavoriteItems, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               case 2:
                  System.out.print("Enter new phone number: ");
                  newPhoneNumber = in.readLine();
                  updateQuery = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s';", newPhoneNumber, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               case 3:
                  System.out.print("Enter new password: ");
                  newPassword = in.readLine();
                  updateQuery = String.format("UPDATE Users SET password = '%s' WHERE login = '%s';", newPassword, login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;

               case 4:
                  System.out.print("Enter new login: ");
                  newLogin = in.readLine();
                  updateQuery = String.format("UPDATE Users SET login = '%s' WHERE login = '%s';", newLogin, update_login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
               case 5:
                  String checkQuery = String.format("SELECT * FROM Users WHERE login = '%s' AND role = 'manager';", update_login);
                  int count2 = esql.executeQuery(checkQuery);
                  if (count2 != 0) {
                     System.out.println("Update denied. Cannot demote managers!");
                     return;
                  }
                  System.out.print("Enter new role: ");
                  newRole = in.readLine();

                  if (!(newRole.equals("manager") || newRole.equals("driver") || newRole.equals("customer"))) {
                     System.out.println("Not a valid role. Returning to menu.");
                     return;
                  }
                  updateQuery = String.format("UPDATE Users SET role = '%s' WHERE login = '%s';", newRole, update_login);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Profile updated successfully!");
                  break;
                  
               default:
                  System.out.println("Invalid choice.");
                  return;
            }
         }
            
      } 
      catch (Exception e) {
         System.err.println("Error updating profile: " + e.getMessage());
      }
   }

  /* 
   Browse Menu: allows the user to view all the items on the menu. User should be able to
   filter their search based on type and price (they can search for food items under a certain
   type (such as “drinks” or “sides”), or search for food items under a certain price). User
   should also be able to sort the menu based on price from highest to lowest price and
   from lowest to highest price.
   */

   public static void viewMenu(PizzaStore esql) {
      try {
         System.out.print("BROWSE MENU\n");
	 System.out.print("-----------\n");
	 System.out.print("1. View All Items\n");
	 System.out.print("2. Filter Based On Type\n");
	 System.out.print("3. Filter Based On Price\n");
	 System.out.print("4. View All Items Sorted From Highest To Lowest Price\n");
	 System.out.print("5. View All Items Sorted From Lowest To Highest Price\n");
	 System.out.print(".....................................................\n");
	 System.out.print("6. Go Back\n");
	 System.out.print("Please make your choice: ");

	 int input = Integer.parseInt(in.readLine());
	 String query;
	 int rowCount;
	 switch(input) {

	    case 1:
	       query = "SELECT * FROM ITEMS;";
               rowCount = esql.executeQueryAndPrintResult(query);
               System.out.println("Total items found: " + rowCount);
	       break;

	    case 2:
	       System.out.print("Enter Type\n");
	       String typeOfItem = in.readLine();
	       query = String.format("SELECT * FROM ITEMS WHERE typeOfItem = '%s';", typeOfItem);
	       rowCount = esql.executeQueryAndPrintResult(query);
               System.out.println("Total items found: " + rowCount);
               break;

	    case 3:
	       System.out.print("Enter Price\n");
               double price = Double.parseDouble(in.readLine());
               query = String.format("SELECT * FROM ITEMS WHERE price = %.2f;", price);
               rowCount = esql.executeQueryAndPrintResult(query);
               System.out.println("Total items found: " + rowCount);
               break;

	    case 4:
	       query = "SELECT * FROM ITEMS ORDER BY price DESC;";
	       rowCount = esql.executeQueryAndPrintResult(query);
               System.out.println("Total items found: " + rowCount);
               break;

	    case 5:
	       query = "SELECT * FROM ITEMS ORDER BY price ASC;";
               rowCount = esql.executeQueryAndPrintResult(query);
               System.out.println("Total items found: " + rowCount);
               break;

	    case 6: break;

	    default : System.out.println("Unrecognized choice!"); break;
	 }


      }

      catch (Exception e) {
        System.err.println("Error creating user: " + e.getMessage());
      }
   }


  /*
   Place Order: user can order any item from the menu. User should first be asked which
   store they want to order from. User will be asked to input every itemName and quantity
   (the amount of each item they want) for each item they want to order. The total price of
   their order should be returned and output to the user. After placing the order, the order
   information needs to be inserted in the FoodOrder table with a unique orderID (and
   make sure you include the store they ordered at). Each itemName, orderID, and the
   corresponding quantity should be inserted into the ItemsInOrder table for every item in
   the order.
   */


   public static void placeOrder(PizzaStore esql, String login) {
      try {
         System.out.print("Enter Store ID: ");
         int storeID = Integer.parseInt(in.readLine());
      
         String storeQuery = String.format("SELECT * FROM Store WHERE storeID = %d;", storeID);
         if (esql.executeQuery(storeQuery) == 0) {
               System.out.println("Store ID does not exist! Returning to menu.");
               return;
         }

         List<String> itemNames = new ArrayList<>();
         List<Integer> quantities = new ArrayList<>();
         double totalPrice = 0.0;
         
         while (true) {
               System.out.print("Enter Item Name (or type '0' to finish): ");
               String itemName = in.readLine();
               if (itemName.equals("0")) {
                  break;
               }

               String itemQuery = String.format("SELECT price FROM Items WHERE itemName = '%s';", itemName);
               List<List<String>> result = esql.executeQueryAndReturnResult(itemQuery);
               if (result.isEmpty()) {
                  System.out.println("Item does not exist! Try again.");
                  continue;
               }

               System.out.print("Enter Quantity: ");
               int quantity = Integer.parseInt(in.readLine());
               while (quantity <= 0) {
                  System.out.println("Invalid quantity entered. Try again!");
                  quantity = Integer.parseInt(in.readLine());
               }

               double price = Double.parseDouble(result.get(0).get(0));
               totalPrice += price * quantity;
               itemNames.add(itemName);
               quantities.add(quantity);
         }

         if (itemNames.isEmpty()) {
               System.out.println("No items selected. Order canceled.");
               return;
         }

         LocalDateTime now = LocalDateTime.now();
         String orderTimestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

         String orderStatus = "incomplete";


         int orderID = 1 + Integer.parseInt(esql.executeQueryAndReturnResult("SELECT MAX(orderID) FROM FoodOrder").get(0).get(0));


         String orderQuery = String.format("INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) VALUES (%d, '%s', %d, %.2f, '%s', '%s');", orderID, login, storeID, totalPrice, orderTimestamp, orderStatus);
         esql.executeUpdate(orderQuery);

         // Insert each item into ItemsInOrder table
         for (int i = 0; i < itemNames.size(); i++) {
               String insertItemQuery = String.format(
                  "INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES (%d, '%s', %d);",
                  orderID, itemNames.get(i), quantities.get(i));
               esql.executeUpdate(insertItemQuery);
         }

         System.out.println("Order placed successfully! Total Price: $" + totalPrice);

      } 
      catch (Exception e) {
         System.err.println("Error placing order: " + e.getMessage());
      }
   }




   /*
   See OrderID History: Customers will be able to see their order history. They should
   be able to see a list of all their past orderIDs. A customer is not allowed to see the
   order history of other customers. Managers & Drivers are allowed to see all
   orders from anyone.
   */

   
   public static void viewAllOrders(PizzaStore esql, String login, String role) {
    try {
        String query;
        
        if (role.equals("customer")) {
            query = String.format("SELECT * FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC;", login);
        } 
        else {
            System.out.println("Enter login of user who's order history you want to see: ");
            String update_login = in.readLine();
            query = String.format("SELECT * FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC;", update_login);
        }

        int rowCount = esql.executeQueryAndPrintResult(query);
        if (rowCount == 0) {
            System.out.println("No orders found.");
        }
        

    } catch (Exception e) {
        System.err.println("Error retrieving order history: " + e.getMessage());
    }
}

   /*
   See Recent 5 OrderIDs: Similar to the last section, customers can see their order
   history, but limit the output to the 5 most recent orders.
   */

   public static void viewRecentOrders(PizzaStore esql, String login, String role) {

      try {
         String query;
        
         if (role.equals("customer")) {
            query = String.format("SELECT * FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC LIMIT 5;", login);
         } 
         else {
            System.out.println("Enter login of user who's order history you want to see: ");
            String update_login = in.readLine();
            query = String.format("SELECT * FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC LIMIT 5;", update_login);
         }

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No orders found.");
         }
        

    } 
    catch (Exception e) {
        System.err.println("Error retrieving order history: " + e.getMessage());
    }
   }


   /*
   Lookup Specific Order: Customers will be able to view details about a specific order.
   They should be able to see their orderTimestamp, totalPrice, orderStatus, and list of
   items in that order (along with the quantity). A customer is not allowed to see the
   order information of orders that belong to other customers. Managers & Drivers
   are allowed to see all orders from anyone.
   */

   public static void viewOrderInfo(PizzaStore esql, String login, String role) {
      try {
         String existsQuery = "";
         role = role.trim();
         int orderID;
         int count;
         if (role.equals("customer")) {

            System.out.print("Enter Order ID: ");
            orderID = Integer.parseInt(in.readLine());
            existsQuery = String.format("SELECT * FROM FoodOrder WHERE orderID = %d AND login = '%s';", orderID, login);
            count = esql.executeQuery(existsQuery);

            if (count == 0) {
               System.out.println("Order ID not found! Returning to menu.");
               return;
            }

            esql.executeQueryAndPrintResult(existsQuery);


            existsQuery = String.format("SELECT * FROM itemsinorder WHERE orderID = %d;", orderID);
            esql.executeQueryAndPrintResult(existsQuery);
         }

         else {

            System.out.println("Enter the login of the person who's food order you want to see: ");
            String update_login = in.readLine();
            existsQuery = String.format("SELECT * FROM FoodOrder WHERE login = '%s';", update_login);
            count = esql.executeQuery(existsQuery);

            if (count == 0) {
               System.out.println("Orders under specified login not found! Returning to menu.");
               return;
            }


            System.out.println("Enter Order ID: ");
            orderID = Integer.parseInt(in.readLine());
            existsQuery = String.format("SELECT * FROM FoodOrder WHERE orderID = %d AND login = '%s';", orderID, update_login);
            count = esql.executeQuery(existsQuery);

            if (count == 0) {
               System.out.println("Order ID not found! Returning to menu.");
               return;
            }

            esql.executeQueryAndPrintResult(existsQuery);

            existsQuery = String.format("SELECT * FROM itemsinorder WHERE orderID = %d;", orderID);
            esql.executeQueryAndPrintResult(existsQuery);




         }

      }

      catch (Exception e) {
        System.err.println("Error looking up order: " + e.getMessage());
      }
   }

   /*
   View Stores: Customers should be able to view the list of all stores. They should see all
   information about the location of the store, the storeID, the review score, and whether or
   not it is open.
   */

   public static void viewStores(PizzaStore esql) {

      try {
         String query = "SELECT * FROM Store;";
         esql.executeQueryAndPrintResult(query);
      }

      catch (Exception e) {
        System.err.println("Error viewing stores: " + e.getMessage());
      }
   }
   
   /*
   Drivers: Drivers should be able to update the order status field of any given order.
   Managers should be able to do this as well.
   */

   public static void updateOrderStatus(PizzaStore esql) {

      try {
         System.out.println("Enter Order ID: ");
         int orderID = Integer.parseInt(in.readLine());
         String existsQuery = String.format("SELECT * FROM FoodOrder WHERE orderID = %d;", orderID);
         int count = esql.executeQuery(existsQuery);
         if (count == 0) {
            System.out.println("OrderID not found! Returning to menu.");
            return;
         }


         System.out.println("Enter New Order Status: ");
         String newStatus = in.readLine();
         String updateQuery = String.format("UPDATE FoodOrder SET orderStatus = '%s' WHERE orderID = %d;", newStatus, orderID);
         esql.executeQuery(updateQuery);
         System.out.println("Status Updated Successfully!");


      }

      catch (Exception e) {
        System.err.println("Error viewing stores: " + e.getMessage());
      }

   }

   /*
    Update Food Item Information: For Managers, they can update the information of any
    item in the menu given the itemName. They should also be able to add new items.
    */

   public static void updateMenu(PizzaStore esql) {
      try {
         System.out.print("UPDATE FOOD ITEM INFORMATION\n");
         System.out.print("----------------------------\n");
	      System.out.print("1. Update item\n");
	      System.out.print("2. Add new item\n");
         System.out.print(".....................................................\n");
         System.out.print("3. Go Back\n");
         System.out.print("Please make your choice: ");

	 int input = Integer.parseInt(in.readLine());
	 String itemName;
	 String existsQuery;
	 switch(input) {
	   case 1:
	      System.out.print("Enter the name of the item to update: ");
            itemName = in.readLine();

               existsQuery = String.format("SELECT * FROM Items WHERE itemName = '%s';", itemName);
               int count = esql.executeQuery(existsQuery);

               if (count == 0) {
                  System.out.println("Item not found! Returning to menu.");
                  return;
               }

               System.out.println("What would you like to update?");
               System.out.println("1. Price");
               System.out.println("2. Type");
               System.out.println("3. Description");
	       System.out.println("4. Ingredients");
	       System.out.println("5. Name");
               System.out.print("Enter choice: ");
               int option = Integer.parseInt(in.readLine());

               String updateQuery = "";
               switch (option) {
                  case 1:
                     System.out.print("Enter new price: ");
                     double newPrice = Double.parseDouble(in.readLine());
                     updateQuery = String.format("UPDATE Items SET price = %.2f WHERE itemName = '%s';", newPrice, itemName);
                     break;
                  case 2:
                     System.out.print("Enter new type: ");
                     String newType = in.readLine();
                     updateQuery = String.format("UPDATE Items SET type = '%s' WHERE itemName = '%s';", newType, itemName);
                     break;
                  case 3:
                     System.out.print("Enter new description: ");
                     String newDesc = in.readLine();
                     updateQuery = String.format("UPDATE Items SET description = '%s' WHERE itemName = '%s';", newDesc, itemName);
                     break;

                  case 4:
                     System.out.print("Enter new ingredients: ");
                     String newIngredients = in.readLine();
                     updateQuery = String.format("UPDATE Items SET ingredients = '%s' WHERE itemName = '%s';", newIngredients, itemName);
                     break;

                  case 5:
                     System.out.print("Enter new name: ");
                     String newName = in.readLine();
                     String checkQuery = String.format("SELECT * FROM Items WHERE itemName = '%s';", newName);
                     if (esql.executeQuery(checkQuery) > 0) {
                        System.out.println("Item name already taken! Returning to menu.");
                        break;
                     }
		               updateQuery = String.format("UPDATE Items SET itemName = '%s' WHERE itemName = '%s';", newName, itemName);
                     break;

                  default:
                     System.out.println("Invalid choice! Returning to menu.");
                     break;
               }
               esql.executeUpdate(updateQuery);
               System.out.println("Item updated successfully!");
	            break;

	     case 2:
	        System.out.print("Enter Item Name\n");
	        itemName = in.readLine();
	        existsQuery = String.format("SELECT * FROM Items WHERE itemName = '%s';", itemName);

	       if (esql.executeQuery(existsQuery) > 0) {
	          System.out.print("Item already exists!\n");
		  break;
	       }

	       System.out.print("Enter Ingredients\n");
               String ingredients = in.readLine();

	       System.out.print("Enter Type Of Item\n");
               String typeOfItem = in.readLine();

	       System.out.print("Enter Price\n");
	       double price = Double.parseDouble(in.readLine());

	       System.out.print("Enter Description\n");
               String description = in.readLine();


	       String insertQuery = String.format("INSERT INTO Items (itemName, ingredients, typeOfItem, price, description) VALUES ('%s', '%s', '%s', %.2f, '%s');", itemName, ingredients, typeOfItem, price, description);

               esql.executeUpdate(insertQuery);
               System.out.println("New item added successfully!");
	       break;

	    default:
	       break;
	 }
      }

      catch (Exception e) {
        System.err.println("Error creating user: " + e.getMessage());
      }
   }

   /*
   Managers: Managers will be able view and update the information of all users (as well
   as change their role) and update menu information.
   */

  //We didn't really need this function since other functions like update profile and update menu do its job.

   // public static void updateUser(PizzaStore esql) {
   // }


}//end PizzaStore


