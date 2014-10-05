import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Scanner;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;

public class Client
{
	Image newimg;
	static BufferedImage bimg;
	byte[] bytes;

//	static String serverName = "localhost";
	static String serverName = "";
	static int port = 1222;
	

	public static void main(String [] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.print("Please enter a server address: ");
		serverName = in.next();
		File dir = new File("/home/iancamp/Desktop/VisualSFM/Pedal/Source"); //temp
		upload(dir);
		
	}

	public static void upload(File folder){

		//get hash
		String sha1 = "";

		for (final File fileEntry : folder.listFiles()) {
			try {
				//System.out.println("sha1sum file: " + sha1sum(fileEntry));
				sha1 += sha1sum(fileEntry);
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}

		}
		try {
			sha1 = sha1sum(sha1);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("sha1: " + sha1);
		
		//upload files to server
		try
		{
			System.out.println("Connecting to " + serverName
					+ " on port " + port);
			Socket client = new Socket(serverName, port);

			System.out.println("Just connected to "
					+ client.getRemoteSocketAddress());

			DataInputStream in=new DataInputStream(client.getInputStream());
			
			//			System.out.println(in.readUTF());

//			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			//send hash
//			out.writeChars(sha1);
			System.out.println("hdiuasda");
			bw.write(sha1);
			System.out.println("hdiuasda");
			//System.out.println(in.readUTF()); //received message
			System.out.println("hdiuasda");
			
//			client = new Socket(serverName, port);

			System.out.println("test1");
			bw.close();
			client.close();
			client = new Socket(serverName, port);
			listFilesForFolder(folder, client);
			System.out.println("test2");
			client.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}


	public static String sha1sum(final File file) throws NoSuchAlgorithmException, IOException {
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

		try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
			final byte[] buffer = new byte[1024];
			for (int read = 0; (read = is.read(buffer)) != -1;) {
				messageDigest.update(buffer, 0, read);
			}
		}



		try (Formatter formatter = new Formatter()) {
			for (final byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}

	public static String sha1sum(final String str) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA1");
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		
		try (Formatter formatter = new Formatter()) {
			for (final byte b : md.digest(str.getBytes())) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
		//return new String(md.digest(str.getBytes()));
	}
	public static void listFilesForFolder(final File folder, Socket client) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, client);
			} else {
				System.out.println("Image sent: " + fileEntry.getName());
				try{
					bimg = ImageIO.read(fileEntry);
					System.out.println("sent1");
					ImageIO.write(bimg,"JPG",client.getOutputStream());
					System.out.println("sent2");
					client.close();
					System.out.println("sent3");
					client = new Socket(serverName, port);
					System.out.println("sent4");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}