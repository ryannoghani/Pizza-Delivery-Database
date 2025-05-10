CS 166 Project Report
Group Information
Include group number.
Include each team member’s name and netid
Ryan Noghani: rnogh001
Nashwaan Ali Khan: nkhan040


Implementation Description
Include high-level description of your implementation (1 paragraph max)

We consider what the role and login of the current user is, and based on that give them certain permissions for what they can do in each function. Each function typically consists of taking input from the user, creating an sql query, executing it, and displaying the results in a way that’s readable for the user.

Include screenshots and/or code snippets for each query. In addition, explain how you implemented each query and what purpose it fulfills. (no more than 3-4 sentences per query)

So for this inserts into what we are doing in this query we make a new user and a corresponding table after we give customer to the user, and we do the prompts that we need to do basically and for implementation we get login details and make sure it's right; contact details, and through esql object

So for this query all we are doing purpose wise is checking, whenever a user logs in, if that log in attempt is connected to a user who actually exists into the system, and LogIn with esql object as a parameter
So what this query does is as shown gets favorite items in addition and in simultaneously with the phone number of the user and it has to match the user who’s logging in and the implementation is in viewProfile which takes in esql and login.

So these 3 updates are for updating the favoriteItems, phoneNum and passwords respectively. We have to make sure that it lines up with the login, so that no malicious stuff happens. Then we do this implementation in updateProfile which takes the esql object, login and role (so nobody does funny stuff).

So here we do these select queries to get the menu. The where clauses help guide us in terms of filtering by getting a specific item type or specific price. We use the ORDER BY for ordering our menu in descending or ascending order. The implementation is in the viewMenu function which only takes our esql object as a param.

So here we do these inserts into queries, primarily focusing on putting an order which will correspondingly update the FoodOrder, and the second one, it updates ItemsInOrder, by the respective parameters. ItemsInOrder can experience multiple insertions during a user’s order. This implementation is carried out in the placeOrder function which takes an esql object and login as params.

Basically we have to select queries. One of them will get all the FoodOrders that the user has ever ordered. The other one is for managers to access a specific user’s (or their own) orders. This second query is making sure that the inputted login aligns with a user who has an order history. We pass in the role to make sure some funny stuff doesn’t occur, like the customer trying to snoop other people’s order history. Also the implementation is for viewAllOrders which takes the esql object, login, and role as parameters.

As you can see this select query will get the FoodOrders assuming the login aligns with the user currently trying to enter the system. The orderTimeStamp gets the history and makes sure it's in the last 5 orders, and our implementation is in viewRecentOrders, which takes the esql object, login, and role.

The first query checks whether the user has a specific order under their name. If they do, then that order information is displayed to them. Otherwise, they are told the order ID they’re looking for wasn’t found.


This query is pretty straight forward. As you can see it just selects every single item from the Store table, the implementation is in viewStores function which only takes the esql object as a parameter.

Essentially what we are doing here is that we update the status of a specific order by using the orderID to track it. Our implementation can be seen in updateOrderStatus which takes the esql object as a parameter. The first query is for checking if the order even exists, while the second query does the actual updating when we know the order exists.

These queries for updating items are all about updating the menu by choosing to update one of its attributes. For example we give the user the choice to set the price, type, description, ingredients etc. We want to implement this with the updateMenu function which only takes the esql object as a parameter. As with our previous examples, the SELECT queries are being used to check for the existence of a certain Item, and the UPDATE is being used once we know the item exists.









If you did any extra credit, provide screenshots and/or code snippets. Explain how you implemented the extra credit. (triggers/stored procedures, performance tuning, etc)



	

We made indexes for our queries. We chose these indexes based on which queries we thought would most be used in this database. For example, we know logging in will be one of the most used features, so we created an index for the login and password. Although we didn’t notice any major improvements in the runtime of our queries after adding indexes, we know this isn’t due to the indexes being flawed, but rather the database being so small that indexes aren’t really necessary. However, we know that if the database were to grow, these indexes would make a noticeable improvement on our queries’ runtime.





Some Fancy Stuff

One of the fancy features we have is maintaining a hierarchy in our system. This means for example that even if you are the manager, you are not able to have power over other managers. One of the abilities a manager has is to change the role of users. But what if the other user is a manager? Realistically, it would be bad if a rogue manager was able to demote every single user in the system to a customer. The example above shows a manager with login mfarrears0 promoting a customer with login lbeldom3 to a manager. However, when mfarrears0 tries changing lbeldom3’s role again, our system prevents this from happening, since now both of them are equals and should not be able to demote each other.

We also hide certain features from the drivers and customers in our menu. This keeps the functions that are strictly for the manager more simple with less cases checking for the role of the logged in user.



Good User Interface

Our system is also pretty user friendly. We implemented a lot of error checking, whether that be limiting the roles a user inputs to “customer”, “driver”, and “manager”, or preventing a manager from creating a new menu item that already exists on the menu. A good example of our error checking is shown above. The code above is able to limit a user from entering 0 or less as the quantity for an item they’re ordering. Our system will keep prompting them for a quantity until they enter a valid one. Another user friendly feature we have is allowing the user to go back or return to the menu after they begin using some feature. This is good for when the user presses on a feature on accident.


Problems/Findings
Include problems/findings you encountered while working on the project (1-2 paragraphs max)

-The functions were not designed to account for the role and login of the current user, which made it difficult to impose restrictions on using certain queries. Our workaround was to modify the functions’ headers by adding login and role parameters.

-Dealing with hierarchical issues in our system, such as preventing a manager from abusing their power. Our workaround was adding more sophisticated conditions that check to see the role of the user that a manager is trying to manipulate.

Contributions
Include descriptions of what each member worked on (1 paragraph max)

Ryan: Functions having to do with viewing/modifying customer data and menus.
Nashwaan: Order adding and menu viewing functions and querying (update, insert into)
