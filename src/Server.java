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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Server 
{

	private static Socket socket;
	private static String sha1 = "";
	private final static String SFM_PATH = "C:\\Users\\iancamp\\Documents\\VisualSFM_windows_64bit\\";

	public static void main(String[] args) throws IOException 
	{	
				receive(); //download images from client
		process(" sfm " + System.getProperty("user.dir") + "\\" + sha1 + "\\in\\ " + sha1 + "\\sparse.nvm"); 
		process(" sfm+loadnvm+pmvs " + System.getProperty("user.dir") + "\\" + sha1 + "\\sparse.nvm " + sha1 + "\\dense.nvm"); 

		System.out.println("Done!");
	}

	private static void process(String args) throws IOException{
		//windows only
		try{
			System.out.println(SFM_PATH + "VisualSFM.exe " + args);
			
			Process proc = Runtime.getRuntime().exec(SFM_PATH + "VisualSFM.exe" + args);
			BufferedReader in = new BufferedReader(  
					new InputStreamReader(proc.getInputStream()));
			String line = null;  
			while ((line = in.readLine()) != null) {  
				System.out.println(line);  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  

		}
		//		System.out.println(System.getProperty("user.dir"));
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
			ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();


			//get hash

			socket = serverSocket.accept();	

			InputStream is = socket.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);

			//			DataInputStream din = new DataInputStream(socket.getInputStream());
			//			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(isr);




			sha1 = Client.sha1sum(br.readLine());
			System.out.println("hash: " + sha1);
			//dout.writeUTF("Server received hash");
			//			System.out.println(din.readUTF());

			while(true) 
			{

				try{

					//Reading the message from the client


					socket = serverSocket.accept();


					is = socket.getInputStream();

					//
					//System.out.println(br.readLine());
					//					System.out.println(din.readUTF());

					//					img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
					//					System.out.println("Image received!!!!");
					//					output = new File(sha1 + "/in/" + "img" + i + ".jpg");
					//					output.mkdirs();
					//					System.out.println(img);
					//					if(System.getProperty("os.name").equalsIgnoreCase("win"))
					//						Thread.sleep(1000);
					//					if(img == null)
					//						break;
					//					ImageIO.write(img, "jpg", output);
					//					System.out.println("Image written to file!");
					//					i++;

					img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
					System.out.println("Image received!!!!");


					if(img == null)
						break;
					images.add(img);



				}
				catch(SocketTimeoutException st)
				{
					System.out.println("Socket timed out!");
					break;
				}
			}
			for(BufferedImage image : images)
			{
				output = new File(sha1 + "/in/" + "img" + i + ".jpg");
				output.mkdirs();
				ImageIO.write(image, "jpg", output);
				System.out.println("Image written to file!");
				i++;
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