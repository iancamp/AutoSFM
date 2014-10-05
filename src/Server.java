/*
 * Ian Campbell
 * https://github.com/iancamp22
 * 
 */


//http://www.careerbless.com/samplecodes/java/beginners/socket/SocketBasic1.php
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.imageio.ImageIO;

public class Server 
{

	private static Socket socket;

	public static void main(String[] args) 
	{
		receive(); //download images from client
	}
	
	
	/*
	 * Downloads images from client
	 * Stores the images in a directory named the hash of all images (computed by the client)
	 */
	private static void receive(){
		try
		{

			int port = 1222;
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port " + port);

			//Server is running always. This is done using this while(true) loop
			int i = 0;
			File output;
			
			BufferedImage img;
			
			
			//get hash
			
			socket = serverSocket.accept();	

			InputStream is = socket.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);

//			DataInputStream din = new DataInputStream(socket.getInputStream());
//			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(isr);

			


			String sha1 = Client.sha1sum(br.readLine());
			System.out.println("hash: " + sha1);
			//dout.writeUTF("Server received hash");
//			System.out.println(din.readUTF());

			while(true) 
			{
				
				try{
					
					//Reading the message from the client
System.out.println("test1");
				
					socket = serverSocket.accept();
					System.out.println("test2");

					is = socket.getInputStream();
					System.out.println("test3");

//
					//System.out.println(br.readLine());
//					System.out.println(din.readUTF());
					
					img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
					System.out.println("Image received!!!!");
					output = new File(sha1 + "/" + "img" + i + ".jpg");
					output.mkdirs();
					System.out.println(img);
					if(System.getProperty("os.name").equalsIgnoreCase("win"))
						Thread.sleep(50);
					if(img == null)
						continue;
					ImageIO.write(img, "jpg", output);
					System.out.println("Image written to file!");
					i++;
				}
				catch(SocketTimeoutException st)
				{
					System.out.println("Socket timed out!");
					break;
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch(Exception e){}
		}
	}
}