PA5 WRITEUP
---------------
The following files provided by Robert Senser was modified by Justin Shapiro for PA5 in the class CSCI 3320:
	- HttpServer.java
	- Request.java
	- Response.java (only changes to formatting to match my code style)
---------------

I am happy to say that my pyrmont web server is fully complete and fully tested. The server is multithreaded, allowing many index.html pages to be loaded while the *spy page is being retrieved from the SecretServer. Significant changes to HttpServer.java and Request.java were made in order to make this possible. Here are the changes I made to those files in order to complete the assignment:

HttpServer.java: 
	In the while loop in await(), a new thread was started which invoked a separate class that implemented Runnable. It took me a very long time to realize that I needed to do this. At first, I imported java.lang.Thread and extended the HttpServer class, putting the code that called the Request and Response classes in the overridden run() function, but that did not work. A multithreaded server, as I found out, needs a class that implements Runnable in order to create new threads independent of the main function of the server (that is, the HttpServer.class). Therefore, the code that called Request and Response were placed in the overridden run() function of the HttpServerThreaded class that implements Runnable. At the end of the run() function, the boolean value of the SHUTDOWN_COMMAND was passed back to a new function setShutdown() in the HttpServer class that allowed for proper setting of the shutdown boolean value that would terminate the while loop and hence the program. 

Request.java:
	In the parse() function, the resulting uri string was checked to see if it was equal to "/*spy" or "/SHUTDOWN". If the uri string was equal to neither of these, then it was most likely equal to "index.html" and the thread would be allowed to exit the function, then the class, and proceed to processing in the Response class. 

If the uri string was equal to "/SHUTDOWN", I included a prompt that would tell the user that shutting down the server might take a while. I experimented with putting a Thread interrupt() here, but that did not speed up the process. I believe it is normal for several seconds to pass after "/SHUTDOWN" is given to the server before it shuts down. I have experience working with real servers, and real servers take a while to shut down because they have to safely end critical processes. Likewise, remaining Threads have to end here before the program shuts down.

If the uri string was equal to "/*spy", the uri string name was changed to "/spy.html" due to the fact that Response looks for a file of this format in order to send it to the browser screen. If the uri string was not changed, the data retrieved from the SecretServer would have never made it to the browser window. 

After the name of the uri string was changed, the function createSpyHTML() was called. This function resides in the Request.java file and is responsible for running the SecretServer in order to obtain the secret number and subsequently create the HTML file that is eventually sent to the browser window. 

Using the Process class, I had the command 'java -classpath \"spyServer\" SecretServer' run so the SecretServer was executed. This assumes that the grader has already compiled the SecretServer.java file. This is a reasonable assumption due to the fact that in real life, the FBI would not have to worry about compiling North Korea's secret server. The thread was put to sleep for one second after the execution of this Process to compensate for any additional time it might take relative to the running time of the program. 

Data was retrieved from the SecretServer by creating a socket with the IP address of 127.0.0.1 (localhost) and port 9090. These are the only credentials the server will accept, and will either not connect or deny connection if any other credentials were provided. I took the usage of the BufferedReader class in the SecretClient class in order to save the secret number to a string. Using the string template provided, a new String was created that would serve as the contents of the eventual HTML file. The HTML file was created in the webroot path by specifying the working directory (System.getPropery("user.dir")) and the concatenation of "\\webroot\\spy.html". PrintWriter was then used to write the string containing the HTML information for the file.

--------------------

That is all of the changes that I made in order to fully complete the assignment. The grader should not experience any issues with my submission unless he hasn't already compiled the SecretServer.java file or if he for some reason has a different file structure in the working directory. I've done the best I could in the design of the program to be sure everything will run smoothly during the grading process.

Overall, I spent around 15 hours working on this assignment. This assignment was the most challenging of any so far in the class. The most challenging part was getting the multithreading to work. I was on a wild goose chase around the internet trying to find out about different methods of how to multithread a server after my initial idea of simply extending the Thread class in HttpServer did not work. I ended up using the instructor's hint on the PPT to get my multithreading to work. I did not expect I would need to write a new class for this assignment. 

Along with this assignment being the most challenging of all of the assignments so far in this class, it is my opinion that it is too far out of scope for this class. After many hours researching network programming, I came to the conclusion that this is a really complex topic, and this assignment did very little in helping my understanding of network programming along with the TCP/IP protocol. I actually have a background in network administration: I possess a Network+ certification and work in an IT Department that manages servers and routers. Therefore, I was familiar with all the network terminology before I started this assignment and I am disappointed to say that my knowledge of networks did not help me at all in this assignment. I am still confused to what a Socket really is and how it relates to a connection. I hope to gain more experience in the future with network programming, as this is a subject I am interested in very much.
