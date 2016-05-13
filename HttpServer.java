// Programming Assignment #5 for CSCI3320 - Advanced Programming
// Contributors: Robert Senser & Justin Shapiro

/* Changed code style (indents and spaces) to match my coding style
 *    Yes, this was necessary
 */

package ex01.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

   /** WEB_ROOT is the directory where our HTML and other files reside.
   *  For this package, WEB_ROOT is the "webroot" directory under the working
   *  directory.
   *  The working directory is the location in the file system
   *  from where the java command was invoked.
   */
   public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator  + "webroot";
   
   private static boolean shutdown = false; // the shutdown command received
   public Socket socket = null;
   
   public static void main(String[] args) {
      HttpServer server = new HttpServer();
      server.await();
   }

   public void await() {
      ServerSocket serverSocket = null;
      int port = 8080;
      
      try {
         serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
      } catch (IOException e) { e.printStackTrace(); System.exit(1); }

      // Loop waiting for a request
      while (!shutdown) {
         Socket socket = null;
         InputStream input = null;
         OutputStream output = null;
         
         try {
            socket = serverSocket.accept();
            input = socket.getInputStream();
            output = socket.getOutputStream();
            
            // Start a new thread
            Thread thread = new Thread(new HttpServerThreaded(socket, input, output));
            thread.start();
         } catch (IOException e) { e.printStackTrace(); }          
      }
   }
   
   public static void setShutdown(boolean isShutdown) { // Needed to process if SHUTDOWN_COMMAND was invoked
      Thread.currentThread().interrupt();
      shutdown = isShutdown; 
   }
}

class HttpServerThreaded implements Runnable { // This class is needed to handle multiple requests
   private Socket socket = null;
   protected InputStream input = null;
   protected OutputStream output = null;
   private static final String SHUTDOWN_COMMAND = "/SHUTDOWN"; // shutdown command
   
   public HttpServerThreaded(Socket _socket, InputStream _input, OutputStream _output) { 
      this.socket = _socket; 
      this.input = _input;
      this.output = _output;
   }   

   public void run() {
       try {
         // create Request object and parse
         Request request = new Request(input);
         request.parse();
   
         // create Response object
         Response response = new Response(output);
         response.setRequest(request);
         response.sendStaticResource();
         
         // Close the socket
         socket.close();
            
         try {
            //check if the previous URI is a shutdown command
            HttpServer.setShutdown(request.getUri().equals(SHUTDOWN_COMMAND));
         } catch (NullPointerException e) { /* do nothing */ }   
      } catch (IOException e) { e.printStackTrace(); } 
   }
}
