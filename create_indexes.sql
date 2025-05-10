-- Many of our queries need to find the Users' login, hence why we have an index for it.
DROP INDEX IF EXISTS User_Login_Index;
CREATE UNIQUE INDEX User_Login_Index ON Users (login);

-- The query we use for logging in will be used very often, so we made an index for retrieving the login and password.
DROP INDEX IF EXISTS User_Login_Password_Index;
CREATE INDEX User_Login_Password_Index ON Users (login, password);

-- This index is for speeding up queries that filter the menu based on a specific item type.
DROP INDEX IF EXISTS Type_Of_Item_Index;
CREATE INDEX Type_Of_Item_Index ON Items (typeOfItem);

-- We frequently access orders by their order id to either update order status or creating a new, unique order id.
DROP INDEX IF EXISTS Order_ID_Index;
CREATE INDEX Order_ID_Index ON ItemsInOrder (orderID);

-- Item name is used frequently for when the user wants to order an item.
DROP INDEX IF EXISTS Item_Name_Index;
CREATE INDEX Item_Name_Index ON Items (itemName);