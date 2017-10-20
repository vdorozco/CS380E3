//Valerie Orozco
//CS380
//EX3

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

//This program connects to a server, the  first byte that it reads corresponds vto the number of bytes that is
// to be saved into an array. After reading the whole, array, we calculate the checksum of the array of bytes
//The client will then send the checksum as a sequence of two bytes back to the server. The server will respond
// with 1 then the checksum was calculated correctly otherwise it will respond with a 0.

public class Ex3Client{
	
	public static void main (String [] args){
		
		
		try{
			Socket socket = new Socket("codebank.xyz", 38103);
			
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			System.out.println("Connected to server.");
			
			int numToRec = is.read();
			System.out.printf("Reading %d Bytes. \n", numToRec);
			
			byte[] received = new byte[numToRec];
			
			for(int i = 0; i < numToRec; i++){
				
				received[i] = (byte) is.read();
			}
			
			System.out.printf("Data received: ");
			
			for(int i = 0; i < received.length; i++){
				
				if((i) % 10 == 0){
					
					System.out.print(String.format("%02X", received[i]));
				}
				
				System.out.print(String.format("%02X", received[i]));
			}
			
		System.out.println();
			
		short checksum = checksum(received);
		
		System.out.printf("Checksum calculated: 0x%s\n", String.format("%04X", checksum));

			
		for(int i = 1; i >= 0; i--){
			
			os.write((byte) (checksum >>> (8*i)));
			
		}
		
		if(is.read() == 1){
			
			System.out.println("Response good.");
		} else {
			
			System.out.println("Response bad.");
		}
		
		socket.close();
		
		} catch (UnknownHostException uhe){
			
			System.out.print("[M] Could not connect to server.");
		
		} catch(IOException ioe){
			
			System.out.println("[M] Unexpected IO error occurred.");
			ioe.printStackTrace();
		} finally {
			
			System.out.println("Disconnected from server.");
		}
		
	}
	
    //checksum implements the interent checksum algorithm. it traverses the array b passed in as an argument
    //two bytes at a time, combining these two bytes vinto a 16-bit value that is added to the sum. Every time
    //the new sum is calculated it must pass an overflow check. If an overflow occurred, then we logic- and the
    //sum with 0xFFF and then add 1, functioning as a wrap-around. Finally, when the sum has been calculated, the
    // compliment of vthe sum is calculated and only the rightmost 16 bits are returned.
    
    
	public static short checksum(byte[] b){
		
		long sum = 0;
		int i = 0;
		
		while(i < b.length){
			
			
			long left = (byteToUnsignedLong(b[i++])) << 8;
			
			long right = 0;
			
			if(i < b.length){
				
				right = byteToUnsignedLong(b[i++]);
			}
			
			long nextValue = left + right;
			
			sum+= nextValue;
			
			if((sum & 0xFFFF0000) != 0){
				
				sum &= 0xFFFF;
				sum++;
			}
		}
		
		return (short) (~(sum & 0xFFFF));
	}
	
	
	private static long byteToUnsignedLong(byte b){
		
		return (b & 0xFF);
	}
}