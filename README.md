# COMP90018-MSCP

################################################################################                     
                    COMP90018 Programming Project Submission
################################################################################

Group No: #T01/10-3

Group Members:

1: [wzhang10 (GitHub username: kionzhang), wzhang10@student.unimelb.edu.au] 
2: [chaoz5 (GitHub username: Chaoszzz), chaoz5@student.unimelb.edu.au] 
3: [yazhao5 (GitHub username: Irene-zy), yangzhao5@student.unimelb.edu.au] 
4: [migu (GitHub username: gmy0516), migu@student.unimelb.edu.au] 
5: [sinhengw (GitHub username: tt70629), sinhengw@student.unimelb.edu.au] 

YouTube Link: https://youtu.be/qVA1ifuH-tQ

Publicity statement: We authorise the University of Melbourne to use material from our submission for publicity.


## Project Overview

Have you ever been left with following difficult position: What should I cook for today's dinner? What else ingredients should I buy to cook this dish? What are the freshness of  ingredients in my fridge? I don't want to go for a grocery run, what can I cook? If you have, we present you the awesome android application, ICooking!

ICooking offers three main functions to users. Firstly, it provides an inventory list to remind users what they still got in their fridge and the freshness of each ingredient. Secondly, users can match recipes based on what they have in the inventory by simply tapping ingredients and clicking the search button or shaking the phone, and ICooking will recommend recipes for them. Once user choose a recipe, ICooking will produce a shopping list for the grocery run and automatically move bought ingredient to inventory list when users put them in fridge. 
  
## Tips to run the application

- Replace the path of sdk.dir in the "local.properties" file to your path of sdk directory before you run the App.

- Please watch [this presentation video](https://youtu.be/qVA1ifuH-tQ) as instructions for this App.

- The notification functions are set to launch at 2pm by default, if you want to see whether the function work properly, you might need to set your machine's time to 1:59pm then run the App. You will see the notifications if you have any ingredient is gonna be expired in 3 days or has been expired

## Future development

- Currently, users are sharing the same data from the database. Therefore, we only can serve one user in this version. We will try to split the database for multiple users and create log in page for authentication. 

- We will extend the recipes' database.

- There's still plenty of room to improve the UI.

- We will develop searching algorithms to increase the usabilities and make the user feel more comfortable while using this function. 

- For now, we set all ingredients having 10 days duration by default and allowing users to edit the expiry date manually.To provide better user experience, we will create a set of predefined ingredients with corresponding duration, such as milk with 7 days duration and steak with 30 days duration.

- We will create a setting page for user to define the notification time, background, and  other configurations. 
