
import java.io.*; 
import java.net.*;
public class TCPsender extends Thread {
	public cdht_ex m_cdht;
	public static int goal_port;
	public static InetAddress inet ;
	public static InetAddress source_address;
	public static int source_port;
	public TCPsender(cdht_ex t){
		m_cdht = t;
	}
	@Override
	public void run(){
		super.run();
		try {
			send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean check_function(int get_file_name){
		int file_name = (get_file_name + 1) % 256;
		if(m_cdht.node.id <= file_name && file_name< m_cdht.node.get_id_of_fir_suc()){
			return true;	
		}
		else if(m_cdht.node.id <= file_name && file_name > m_cdht.node.get_id_of_fir_suc() && m_cdht.node.id > m_cdht.node.get_id_of_fir_suc()){
			return true;
		}
		else{
			return false;
		}
	}
	public void send() throws IOException{
		for(;;){
			suspend();
			inet = InetAddress.getByName("localhost");
			int type = m_cdht.tcp_read();
			int file_number = m_cdht.tcp_read();
			int dest_port = m_cdht.tcp_read();
			if(type == m_cdht.COMM_FROM_QUIT){
				String mess_to_pre = m_cdht.COMM_FROM_QUIT+ " "+ m_cdht.node.get_id_of_fir_suc()+ " "+ m_cdht.node.get_id_of_sec_suc()+" "+m_cdht.node.id ;
				int fir_pre = m_cdht.node.get_id_of_fir_pre();
				int sec_pre = m_cdht.node.get_id_of_sec_pre();
				Socket clientsocket1 = new Socket(inet,50000+fir_pre);
				Socket clientsocket2 = new Socket(inet,50000+sec_pre);
				DataOutputStream outToServer1 = new DataOutputStream(clientsocket1.getOutputStream());
				outToServer1.writeBytes(mess_to_pre);
				DataOutputStream outToServer2 = new DataOutputStream(clientsocket2.getOutputStream());
				outToServer2.writeBytes(mess_to_pre);
				clientsocket1.close();
				clientsocket2.close();
			}
			else if(type == m_cdht.COMM_FROM_REQU){
				if(check_function(file_number)){
					if(dest_port == m_cdht.node.id){
						System.out.println("File is already here");
					}
					String send = "File "+file_number+" is here."+
				"A response message, destined for peer "+dest_port+", has been sent.";
					System.out.println(send);
			    Socket socket = new Socket(inet,dest_port+50000);
			    String data_send = m_cdht.CONFIRM+" "+file_number+" "+ m_cdht.node.id ;
			    DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				outToServer.writeBytes(data_send);
				socket.close();
				}
				else{
					System.out.println("File " + file_number+ " is not stored here.");
					System.out.println("File request message has been forwarded to my successor.");
					Socket socket = new Socket(inet,m_cdht.node.get_id_of_fir_suc()+50000);
					String send = m_cdht.COMM_FROM_REQU+" "+file_number+" "+dest_port;
					DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					output.writeBytes(send);
					socket.close();
				}
			}
			else if(type == m_cdht.COMM_FROM_TYPE){
				System.out.println("File request message for "+file_number+" has been sent to my successor.");
				Socket socket = new Socket(inet,m_cdht.node.get_id_of_fir_suc()+50000);
				String send = m_cdht.COMM_FROM_REQU+" "+file_number+" "+dest_port;
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeBytes(send);
				socket.close();
			}
			else{
				if(type >=  1000){
					Socket socket = new Socket(inet,50000+(type / 1000));
					String send = m_cdht.QUIT_CONFIRM1 + " ";
					DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					output.writeBytes(send);
					System.out.println("sending quit confirm to my fir succ");
					socket.close();
				}
				else{
					Socket socket = new Socket(inet,50000+type);
					String send = m_cdht.QUIT_CONFIRM2 + " ";
					DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					output.writeBytes(send);
					System.out.println("sending quit confirm to my sec succ");
					socket.close();
				}
			}
		}
		
	}
	
}
