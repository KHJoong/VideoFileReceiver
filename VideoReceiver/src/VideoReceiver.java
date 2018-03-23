import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoReceiver implements Runnable {
	
	// 클라이언트와 연결할 수 있도록 열어둔 포트 번호입니다.
	int port = 5555;
	
	public static void main(String[] args) {
		Thread vr = new Thread(new VideoReceiver());
		vr.start();
	}

	@Override
	public void run() {
		try {
			System.out.println("VideoReceiver Start");
			// 해당 포트에 server socket을 엽니다.
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true) {
				// 클라이언트와의 연결을 기다립니다.
				Socket socket =serverSocket.accept();
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				// 클라이언트로부터 비디오 파일을 저장하기위해 사용할 제목을 받습니다.
				String reTitle = dis.readUTF();
				// 클라이언트로부터 비디오파일의 총 길이를 받습니다.
				String tmplength = dis.readUTF();
				System.out.println("Receiving Video File Name : " + reTitle);
				System.out.println("Receiving Video File size : " + Integer.parseInt(tmplength));
				// 비디오 파일을 받아 저장할 buffer를 생성합니다.
				byte[] buffer = new byte[Integer.parseInt(tmplength)];
				int length = buffer.length;
				System.out.println("Maked Buffer Size : "+length);
				
				// 비디오 파일을 실질적으로 받는 부분입니다.
				// stream을 읽어서 buffer에 저장합니다.
				// current는 buffer(array)의 current번 부터 저장하면 된다는 것을 알려주기 위해 사용합니다.
				// bytesRead는 stream으로부터 몇 byte를 읽었는지 확인하고 
				// current에 추가하여 다음번에 읽은 stream을 buffer의 어디에서부터 저장하면 되는지 구분하기위해 사용합니다. 
				int current = 0;
				int bytesRead = dis.read(buffer, 0, length);
				current = bytesRead;
				System.out.println("Receiving : "+(current*100)/length+"%  ");
				do {
					bytesRead = dis.read(buffer, current, (length - current));
			        if (bytesRead > 0) {
			            current += bytesRead;
			        } else {
			        	break;
			        }
			        System.out.println("Receiving : "+(current*100)/length+"%  ");
				} while (bytesRead > -1);
				
				// 읽어온 stream을 file에 출력합니다.
				FileOutputStream fos = new FileOutputStream("/uploaded_video/"+reTitle);
				fos.write(buffer, 0, length);
				fos.flush();
				
				// 작업이 끝나 모두 닫아줍니다.
				fos.close();
				dis.close();
				socket.close();
				
				System.out.println("Receive End");
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
