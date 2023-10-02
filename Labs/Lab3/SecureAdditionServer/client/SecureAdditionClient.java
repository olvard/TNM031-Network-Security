// A client-side class that uses a secure TCP/IP socket

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Scanner;
import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	// This is not a reserved port number 
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "client/LIUkeystore.ks";
	static final String TRUSTSTORE = "client/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";


	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
	}
	
  // The method used to start a client object
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port);
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");

			
			BufferedReader socketIn;
			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			socketOut = new PrintWriter( client.getOutputStream(), true );
			
			String numbers = "1.2 3.4 5.6";
			System.out.println( ">>>> Sending the numbers " + numbers+ " to SecureAdditionServer" );
			socketOut.println( numbers );
			System.out.println( socketIn.readLine() );

			socketOut.println ( "" );



			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));


			while (true) {
				// Display menu options
				System.out.println("Menu:");
				System.out.println("1. Download");
				System.out.println("2. Upload");
				System.out.println("3. Delete");
				System.out.println("4. Exit");
				System.out.print("Enter your choice (1/2/3/4): ");

				// Get user input
				int choice = Integer.parseInt(userInput.readLine());

				// Send the selected choice to the server
				socketOut.println(choice);

				// Perform action based on user choice
				switch (choice) {
					case 1:
						System.out.print("Enter the filename to download: ");
						String filename = userInput.readLine();

						// Send the filename to the server
						socketOut.println(filename);

						// Receive and handle the file content from the server
						String line;
						while (!(line = socketIn.readLine()).equals("downloaded")) {
							System.out.println(line);
						}
						System.out.println("File downloaded");
						break;
					case 2:
						System.out.print("Enter the path of the file to upload: ");
						String uploadFilename = userInput.readLine();

						BufferedReader fileReader = new BufferedReader(new FileReader("client/files/" + uploadFilename));
						//Store the contents of the file
						String data = "";

						//File uploadFile = new File("client/files/"+uploadFilename);
						String line2;
						while ((line = fileReader.readLine()) != null) {
							data += line;
						}
						fileReader.close();
						//System.out.println(data);
						//Send contents to server
						socketOut.println(uploadFilename);
						socketOut.println(data);
						System.out.println(socketIn.readLine());

						break;
					case 3:
						System.out.println("You selected 'Delete'.");
						System.out.print("Enter the filename to delete: ");
						String deleteFilename = userInput.readLine();

						socketOut.println(deleteFilename);

						String deleteResponse = socketIn.readLine();
						System.out.println(deleteResponse);

						break;
					case 4:
						System.out.println("Exiting the program.");
						System.exit(0);
					default:
						System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
				}
				}


		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}


	}

	
	// The test method for the class @param args Optional port number and host name
	public static void main( String[] args ) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if ( args.length > 0 ) {
				port = Integer.parseInt( args[0] );
			}
			if ( args.length > 1 ) {
				host = InetAddress.getByName( args[1] );
			}
			SecureAdditionClient addClient = new SecureAdditionClient( host, port );
			addClient.run();
		}
		catch ( UnknownHostException uhx ) {
			System.out.println( uhx );
			uhx.printStackTrace();
		}
	}
}
