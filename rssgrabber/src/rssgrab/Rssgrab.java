/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rssgrab;

//IMPORT PACKAGES

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author n00822782
 */
public class Rssgrab {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String command = null;
        String sitesIn = null;     
        String[] sites = null;
        String rssData = null;
        item[] masterItems = null;
                
        //PROGRAM LOGICAL FLOW START
        
        // READ LIST OF SITES FROM URL CSV FILE , STORE INTO ARRAY
        
        sites = siteRipper.rip();
        
        // Clear existing construct file data
        command = "truncate -s 0 construct";
        runCommand.run(command);
                
        // RUN WGET FOR EACH SITE AND PIPE INTO CONSTRUCT FILE
        for (String site : sites) {
            // Build the command
            command = "curl " + site + " >> //home//armitage//bin//rssgrabber//construct";
            runCommand.run(command);
        }
        
        // Get data from construct file and store into variable 
        rssData = constructor.construct();
                
        // PARSE rssData FOR item(title,link,pubDate) AND STORE INTO ITEM OBJECT
        masterItems = buildItems.initItems(rssData);
        
        // CONSCTRUCT OUTPUT AS HTML ENABLED EMAIL        
        String htmlOut = mailBuilder.makeMail(masterItems);
        
        // CREATE/OVERWRITE OUTPUT FILE
        command = "truncate -s 0 emailForm";
        runCommand.run(command);
        
        try
        {
            PrintWriter out = new PrintWriter("emailForm");
            
            out.println(htmlOut);
            
            out.close();
        }
        catch(FileNotFoundException e){}
        
        // SEND EMAIL 
        command = "./mailrss.sh";
        runCommand.run(command);
        
        // PROGRAM LOCICAL FLOW END      
        System.exit(0);
                
    }
    
}

// regex for <item></item>
// \<item\>.*?\<\/item\>

// Grabs list of sites from conf file
class siteRipper 
{
    
    static String sitesOut = null;
    
    public static String[] rip()
    {
    
        try
        {
            sitesOut = new Scanner(new File("conf")).useDelimiter("\\Z").next();
            System.out.println("Sites In String: " + sitesOut);
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Unknown error occurred.");
            System.exit(-1);
        }
        String[] sites = sitesOut.split(",");   
        
        return sites;
    }
}

// Runs provided bash command
class runCommand 
{
    static String command; 
    static String outputOfCmd = null;
    
    public static String run(String commandIn)
    {
        
        command = commandIn; 
        
         // Create the runtime 
        Runtime runtime = Runtime.getRuntime();

        try
        {
            // Run the command 
            Process process = runtime.exec(new String[] { "bash", "-c", command});

            // Create readers to output command results
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // String var to hold output 
            String s = null;

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                outputOfCmd += s;
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                outputOfCmd += s;
                System.out.println(s);
            }
                        
        }
        catch(IOException e)
        {
            System.out.println("IOException occurred");
            System.exit(-1);
        }
        
        return outputOfCmd;
    }
}

// Returns string with complete RSS data from construct file
class constructor
{
    static String conString = null;
    
    public static String construct()
    {
        String command = "cat construct";
        
        conString = runCommand.run(command);
        
        return conString;
    }
    
}

// item object class 
class item
{
    
    String title = null;
    String link = null;
    String pubDate = null; 
    
    item(String titleIn, String linkIn, String pubDateIn)
    {
        title = titleIn;
        link = linkIn;
        pubDate = pubDateIn;
    }
        
    public String getTitle()
    {
        return title;
    }
    
    public String getLink()
    {
        return link;
    }
    
    public String getPubDate()
    {
        return pubDate;
    }
    
    @Override
    public String toString()
    {
        String out = null; 
        
        out = "\nTITLE: " + title
                + "\nLINK: " + link
                + "\nDATE: " + pubDate;
        
        return out;
    }
    
    public String toHTML()
    {
        String html = null; 
        
        html = "<li><p><a href=\"" + link + "\">" + title + "</a>"
                + "<br>" + pubDate + "</p></li>";
        
        return html;
    }
}

// Creates item object array and populates each item with relative data
class buildItems
{
    // Declare array to hold items once constructed
        
    // Declare strings to hold regexs
    static String itemDataIn = null; 
    static String itemRegex = "\\<item\\>(.*?)(?=\\<\\/item\\>)";
    static String titleRegex = "\\<title\\>(.*?)(?=\\<\\/title\\>)";
    static String linkRegex = "\\<link\\>(.*?)(?=\\<\\/link\\>)";
    static String pubDateRegex = "\\<pubDate\\>(.*?)(?=\\<\\/pubDate\\>)";
    
    public static item[] initItems(String constructString)
    {
                
        // Get the RSS data in 
        itemDataIn = constructString;
        
        // Run regex matches to build arrays of every item,title,link,pubDate        
        Pattern itemPattern = Pattern.compile(itemRegex);
        Matcher matcher = itemPattern.matcher(itemDataIn);
        List<String> matches = new ArrayList<>();
        while(matcher.find())
        {
            matches.add(matcher.group(1));
        }
        
        String[] itemArray = matches.toArray(new String[0]);
        
        matches.clear();
        
        Pattern titlePattern = Pattern.compile(titleRegex);
        matcher = titlePattern.matcher(itemDataIn);
        while(matcher.find())
        {
            matches.add(matcher.group(1));
        }
        
        String[] titleArray = matches.toArray(new String[0]);
        
        matches.clear();
        
        Pattern linkPattern = Pattern.compile(linkRegex);
        matcher = linkPattern.matcher(itemDataIn);
        while(matcher.find())
        {
            matches.add(matcher.group(1));
        }
        
        String[] linkArray = matches.toArray(new String[0]);
        
        matches.clear();
        
        Pattern pubDatePattern = Pattern.compile(pubDateRegex);
        matcher = pubDatePattern.matcher(itemDataIn);
        while(matcher.find())
        {
            matches.add(matcher.group(1));
        }
        
        String[] pubDateArray = matches.toArray(new String[0]);
        
        matches.clear();
        matches = null; // Reset the matches list
        
        // Build out the individual items
        // If each array has the same number of elements, then no input 
        // mismatches occurred and each array value at [x] will belong 
        // to the correct item ... I hope
        
        //boolean arrayMatch = false; 
        
        List<item> items = new ArrayList<>();
       
        int numItems = itemArray.length;
                    
        for(int x = 0; x < numItems; x++)
        {
            try
            {
                item tmpItem = new item(titleArray[x],linkArray[x],pubDateArray[x]);   
                              
                items.add(tmpItem);                
            }
            catch(NullPointerException e)
            {
                e.printStackTrace();
            }
        }
        
        item[] itemsOut = items.toArray(new item[items.size()]);
        
        return itemsOut;
    }
}

class mailBuilder
{
    static String bodyOut; 
    static item[] items; 
    
    public static String makeMail(item[] itemArrayIn)
    {
        items = itemArrayIn;
        
        bodyOut = "<!DOCTYPE html><HTML><head><title></title></head><body>";
        bodyOut += "<p><h2>Daily RSS Feed</h2></p><br><ul>";
        
        for(int x = 0; x < items.length; x++)
        {
            bodyOut += items[x].toHTML();
        }
        
        bodyOut += "</ul></body></HTML>";
        
        return bodyOut; 
    }

}