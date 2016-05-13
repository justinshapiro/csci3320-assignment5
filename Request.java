// Programming Assignment #5 for CSCI3320 - Advanced Programming
// Contributors: Robert Senser & Justin Shapiro


/* Changed code style (indents and spaces) to match my coding style
 *    Yes, this was necessary
 */

package ex01.pyrmont;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.lang.Process;

public class Request {

   private InputStream input;
   private String uri;
   
   public Request(InputStream input) {
      this.input = input;
   }

   public void parse() {
      try {
         // Read a set of characters from the socket
         StringBuffer request = new StringBuffer(2048);
         int i;
         byte[] buffer = new byte[2048];
         
         try {
            i = input.read(buffer);
         } catch (IOException e) { e.printStackTrace(); i = -1; }
         
         for (int j=0; j<i; j++) 
            request.append((char) buffer[j]);
   
         System.out.print(request.toString());
         uri = parseUri(request.toString());
         
         if (uri.equals("/*spy")) { // if uri == /*spy, change to /spy.html and generate file
            uri = "/spy.html";
            createSpyHTML();
         } 
         else if (uri.equals("/SHUTDOWN")) // Tell user that server will shut down after a small wait
            System.out.println("SHUTTING DOWN SERVER... Please wait for threads to terminate.");
      } catch (NullPointerException e) { /* do nothing */ }     
   }

   private String parseUri(String requestString) {
      int index1, index2;
      index1 = requestString.indexOf(' ');
      if (index1 != -1) {
         index2 = requestString.indexOf(' ', index1 + 1);
         if (index2 > index1)
            return requestString.substring(index1 + 1, index2);
      }
      
      return null;
  }

   public String getUri() { return uri; }
  
   public void createSpyHTML() { // creates a spy.html to return in webroot
      try {
         // Start SecretServer
         Process startSpyServer = Runtime.getRuntime().exec("java -classpath \"spyServer\" SecretServer");
         
         // Sleep for one second just to be safe
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) { /* do nothing */ }
         
         // Communicate with server to grab number   
         Socket spy_socket = new Socket("127.0.0.1", 9090);
         BufferedReader input = new BufferedReader(new InputStreamReader(spy_socket.getInputStream()));
         String answer = input.readLine();  
         
         // Create HTML file contents
         String message = "<html><head><title>*spy</title></head><body>Secret: " +
                           answer + "<br></body></html>";
         
         // Create HTML file path                  
         String spy_file_path = System.getProperty("user.dir") + "\\webroot\\spy.html";   
          
         // Create HTML File            
         File spy_file = new File(spy_file_path); 
         spy_file.createNewFile();
         
         // Write the HTML file contents to the file
         PrintWriter out = new PrintWriter(spy_file_path);
         out.println(message);    
         out.close();
      } catch (IOException e) { e.printStackTrace(); }  
   }
}