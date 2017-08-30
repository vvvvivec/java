#!/bin/bash

# Creates necessary files for program at desired path

#get path from cmd line
path=$1

#copy scripts to path
cp runmailer.sh $path
cp mailrss.sh $path

#cd to path 
cd $path

#create the necessary files
touch emailForm
touch conf
touch construct 

#change the template files to writable 
#change scripts to executable
sudo chmod 666 emailForm
sudo chmod 666 conf
sudo chmod 666 construct
sudo chmod 777 runmailer.sh
sudo chmod 777 mailrss.sh 

#state success
echo "emailForm, conf, and construct files created successfully" 
echo "runmailer.sh and mailrss.sh created successfully"
echo "permissions set" 
