/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rssgrab;

//IMPORT PACKAGES

import java.io.*;
import java.util.*;

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
        
        //PROGRAM LOGICAL FLOW START
        
        // READ LIST OF SITES FROM URL CSV FILE , STORE INTO ARRAY
        try
        {
            sitesIn = new Scanner(new File("conf")).useDelimiter("\\Z").next();
            System.out.println("Sites In String: " + sitesIn);
        }
        catch(Exception e)
        {
            System.out.println("Unknown error occurred.");
            System.exit(-1);
        }
        String[] sites = sitesIn.split(",");
        System.out.println("Sites array: " + Arrays.toString(sites));
        
        // RUN WGET FOR EACH SITE AND PIPE INTO CONSTRUCT FILE 
        for(int x = 0; x < sites.length; x++)
        {
            // Build the command 
            command = "truncate =s 0 construct;curl " + sites[x] + " >> //home//armitage//apps//rssgrabber//construct";            
            
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
                    System.out.println(s);
                }
            
                // Read any errors from the attempted command
                System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }
            catch(IOException e)
            {
                System.out.println("IOException occurred");
                System.exit(-1);
            }
        }
        
        // PARSE FILE FOR item(title,link,pubDate) AND STORE INTO ARRAY
        
        // CONSCTRUCT OUTPUT AS HTML ENABLED EMAIL
        
        // CREATE/OVERWRITE OUTPUT FILE
        
        // SEND EMAIL 
        
        // PRGORAM LOCICAL FLOW END        
    }
    
}

// regex for <item></item>
// \<item\>.*?\<\/item\>