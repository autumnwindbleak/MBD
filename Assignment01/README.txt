**Assignment01**
**Mining Big Data**

**Group member**
Xueyang Wang(a1690260)
Yue Zhang(a1682285)

**Email**
Xueyang Wang: Xueyang.Wang@student.adelaide.edu.au
Yue Zhang: a1682285@student.adelaide.edu.au

**Folder output** 
contains serveral output of the Assignment01:
100_output : Exercise 3 running with the 100.txt as input
pg100_output : Exercise 3 running with the pg100.txt as input
friend_output : Exercise 4 output


**How to run the code**

**Exercise 3**

**step 1**
unzip in GUI to the folder: /home/#username#/workspace/
OR
using the command: tar -xvfz WordCount.tar.gz /home/#username#/workspace/

**step 2**
open Eclipse click on File-->Import
sellect General-->Existing Projects into Workspace and click on Next
Select root directory to /home/#username#/workspace/WordCount then click on finish

**run in stand alone mode**
Open Eclipse and right click on the project wordcount
select Run As--> Run Configurations...
in the pop-up dialog select Java Application node and click on New Launch configuration button in the upper left corner.
Enter a name in the Name field and WordCount in the Main class Field.
Switch to  the Arguments tab and put "pg100.txt output" in the Program arguments field. 
if you want to run with 100.txt change "pg100.txt" to "100.txt"
click on Apply and then Run
if a window "Select Java Apllication" appeared. find "WordCount-main.wordcount" select it and press OK

**run in pseudo-distributed mode**
Open Eclipse and right click on the project wordcount
select Export..
In the pop-up dialog, expand the Java node and select JAR file. Click Next
Enter /home/#username#/WordCount.jar in the JAR file field click on Finish
Open a terminal then type the following command:
cd /home/#username#
hadoop fs -put workspace/WordCount/100.txt
hadoop jar WordCount.jar main.wordcount 100.txt output
** to see the result**
enter the commands below after run in pseudo-distributed mode:
hadoops fs -ls output // this command showing the output file for each reducer.
hadoops fs -cat output/#part-r-00000# //change #part-r-00000# to the only file which have the "part" word in it showing with last command

**Exercise 4**

**step 1**
unzip in GUI to the folder: /home/#username#/workspace/
OR
using the command: tar -xvfz WordCount.tar.gz /home/#username#/workspace/

**step 2**
open Eclipse click on File-->Import
sellect General-->Existing Projects into Workspace and click on Next
Select root directory to /home/#username#/workspace/FriendRecommendation then click on finish

**step 3**
Open Eclipse and right click on the project wordcount
select Run As--> Run Configurations...
in the pop-up dialog select Java Application node and click on New Launch configuration button in the upper left corner.
Enter a name in the Name field and FriendRecommendation in the Main class Field.
Switch to  the Arguments tab and put "LiveJournal.txt output" in the Program arguments field. 
click on Apply and then Run
if a window "Select Java Apllication" appeared. find "FriendRecommendation-main.friendrecommendation" select it and press OK

**END**