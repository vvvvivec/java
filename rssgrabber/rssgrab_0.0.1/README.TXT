To-Do: update java code to get current directory and store it locally to make code portable 

RSSGRAB v0.0.1 
Brian D

This utility grabbs rss feed data from a list of provided rss feed URLs and emails the data as an HTML
list to the desired email

SETUP:
	run setup.sh and provide the path you want to install rss grab into , e.g. ~/bin
        enter desired rss feed URLS into the conf file deliminating with ","
	call runmailer.sh to utilize the program 
	the scripts and template files must be located in the same directory
	edit the email in mailrss.sh to desired address         
