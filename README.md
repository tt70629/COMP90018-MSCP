# COMP90018-MSCP

// Motivation and introduction of the application

When hanging out to buy food, people usually remember all the goods they are going to buy in their mind or write them down on mobile note applications because they don't have a particular space to record them. Also, if they bought too much, someone probably would gradually forget what they had bought and when they bought them after putting the food into the fridge. Moreover, they might also be suffering from not knowing what to cook when they want to use ingredients in the fridge as possible as they can.  

This is the motivator of using Icooking. Icooking offers three main functions to solve this problem. Initially, Icooking provides a list for users to record what they will buy before they go out. Secondly, Icooking provides an inventory list to remind users what they still have got in their fridge. Lastly, users can match recipes based on what they have in the inventory by simply tapping ingredients and clicking the search button, and then Icooking will recommend suggestions for them.

// Functions details

The mentioned three functions are allocated respectively in three pages. They are buy-list, inventory, and match recipes. Moreover, there's one more page for holding the detailed recipe to guide the people while cooking.

On the buy-list page, people can either add, delete, and move bought ingredients to the inventory.

On the inventory page, people can add, update, and delete the ingredients that they have. The system will check the expiry date for the user and remind users to use the ingredients that are close to being expired as soon as possible. 

On the match-recipe page, people can select ingredients from inventory and search for recipes. Three recipes would be displayed, and people can click the search button to search again. To make it more interesting, we also applied a sensor here so that people can shake the phone instead of clicking the button if they want to re-roll the recipes. The ingredients that are going to expire are highlighted on this page to remind people to use these ingredients priorly.

On the recipe page, people can cook by following the guide. If people find missing ingredients while reading this page, they can add all the missing ingredients to the buy-list by clicking the button. They also can click the finish button to remove all the used ingredients from the inventory.

More details will be demonstrated by the video.

// Future development

Initially, we have to split data from each user. Currently, users are sharing the same data from the database. Therefore, we only can serve one user in this version.

We should have recorded more recipes in our database so that it could be more helpful to users.

There's still plenty of room to improve the UI.

We need to develop searching algorithms to increase the usabilities and make the user feel more comfortable while using this function. 

We need to record the expiry date for different ingredients in our database because each ingredient has a different expiry date. 
