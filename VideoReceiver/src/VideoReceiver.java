import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoReceiver implements Runnable {
	
	// Ŭ���̾�Ʈ�� ������ �� �ֵ��� ����� ��Ʈ ��ȣ�Դϴ�.
	int port = 5555;
	
	public static void main(String[] args) {
		Thread vr = new Thread(new VideoReceiver());
		vr.start();
	}

	@Override
	public void run() {
		try {
			System.out.println("VideoReceiver Start");
			// �ش� ��Ʈ�� server socket�� ���ϴ�.
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true) {
				// Ŭ���̾�Ʈ���� ������ ��ٸ��ϴ�.
				Socket socket =serverSocket.accept();
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				// Ŭ���̾�Ʈ�κ��� ���� ������ �����ϱ����� ����� ������ �޽��ϴ�.
				String reTitle = dis.readUTF();
				// Ŭ���̾�Ʈ�κ��� ���������� �� ���̸� �޽��ϴ�.
				String tmplength = dis.readUTF();
				System.out.println("Receiving Video File Name : " + reTitle);
				System.out.println("Receiving Video File size : " + Integer.parseInt(tmplength));
				// ���� ������ �޾� ������ buffer�� �����մϴ�.
				byte[] buffer = new byte[Integer.parseInt(tmplength)];
				int length = buffer.length;
				System.out.println("Maked Buffer Size : "+length);
				
				// ���� ������ ���������� �޴� �κ��Դϴ�.
				// stream�� �о buffer�� �����մϴ�.
				// current�� buffer(array)�� current�� ���� �����ϸ� �ȴٴ� ���� �˷��ֱ� ���� ����մϴ�.
				// bytesRead�� stream���κ��� �� byte�� �о����� Ȯ���ϰ� 
				// current�� �߰��Ͽ� �������� ���� stream�� buffer�� ��𿡼����� �����ϸ� �Ǵ��� �����ϱ����� ����մϴ�. 
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
				
				// �о�� stream�� file�� ����մϴ�.
				FileOutputStream fos = new FileOutputStream("/uploaded_video/"+reTitle);
				fos.write(buffer, 0, length);
				fos.flush();
				
				// �۾��� ���� ��� �ݾ��ݴϴ�.
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
