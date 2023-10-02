
// An example class that uses the secure server socket class

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;


public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "server/LIUkeystore.ks";
	static final String TRUSTSTORE = "server/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
	public void run() throws IOException {
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
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );


			System.out.println("\n>>>> SecureAdditionServer: active ");
			SSLSocket incoming = (SSLSocket)sss.accept();

      		BufferedReader in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			PrintWriter out = new PrintWriter( incoming.getOutputStream(), true );

			String str;
			while ( !(str = in.readLine()).equals("") ) {
				double result = 0;
				StringTokenizer st = new StringTokenizer(str);
				try {
					while (st.hasMoreTokens()) {
						Double d = new Double(st.nextToken());
						result += d;
					}
					out.println("The result is " + result);

				} catch (NumberFormatException nfe) {
					out.println("Sorry, your list contains an invalid number");
				}
			}

				while(true) {
					String choice = in.readLine();


					//Download, Upload, Delete
					switch (choice) {
						case "1" -> {
							// Read the requested filename from the client
							String filename = in.readLine();

							// Check if the file exists

							File file = new File("server/files/" + filename);

							try {
								if (file.exists()) {
									BufferedReader fileReader = new BufferedReader(new FileReader(file));
									String line;

									FileWriter w = new FileWriter("client/files/downloaded");

									// Send the file content to the client
									while ((line = fileReader.readLine()) != null) {
										w.write(line);
										out.println(line);
									}

									// Signal the end of file transfer
									out.println("downloaded");
									fileReader.close();
									w.close();
									System.out.println("File sent to client: " + filename);
								} else {
									// Handle the case when the file doesn't exist
									System.out.println("File not found on the server: " + filename);
									out.println("File not found");
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						case "2" -> {
							String fileName = in.readLine();
							String content = in.readLine();

							//Create a new file
							try {
								File myFile = new File("server/files/" + fileName);
								if (myFile.createNewFile()) {

									//Fill the file
									FileWriter myWriter = new FileWriter("server/files/" + fileName);
									myWriter.write(content);
									myWriter.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							out.println("Succesfully uploaded file");

						}
						case "3" -> {
							String filename = in.readLine();
							// Check if the file exists
							File file = new File("server/files/" + filename);

							try {
								if (file.exists()) {
									// Attempt to delete the file
									if (file.delete()) {
										out.println("File deleted");
										System.out.println("File deleted on the server: " + filename);
									} else {
										out.println("Failed to delete file");
										System.out.println("Failed to delete file on the server: " + filename);
									}
								} else {
									// If the file doesn't exist, inform the client
									out.println("File not found");
									System.out.println("File not found on the server: " + filename);
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							}

						}
						case "4" -> {


						}
					}

				}



		} catch (Exception x) {
			x.printStackTrace();
			System.out.println("exception thrown");
			System.out.println( x );
		}

	}
	
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) throws IOException {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}

