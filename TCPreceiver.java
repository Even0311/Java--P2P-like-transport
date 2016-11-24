
import java.io.*; 
import java.net.*;
public class TCPreceiver extends Thread{
	private cdht_ex m_cdht;
	public  TCPreceiver(cdht_ex t){
		m_cdht = t;
	}
	@Override
	public void run(){
		super.run();
		try {
			receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receive() throws IOException{
		ServerSocket socket = new ServerSocket(m_cdht.node.id + 50000);
		while(true){
			Socket receive_socket = socket.accept();
			InputStreamReader ipsr = new InputStreamReader(receive_socket.getInputStream());
			BufferedReader bfr = new BufferedReader(ipsr);
			String receive_data = bfr.readLine();
			String[] rece = receive_data.split(" ");
			if(Integer.parseInt(rece[0]) == m_cdht.CONFIRM){
				System.out.println("Received a response message from peer "+rece[2] +", which has the file "+ rece[1]+".");
			}
			else if(Integer.parseInt(rece[0]) == m_cdht.COMM_FROM_REQU){
				m_cdht.tcp_write(m_cdht.COMM_FROM_REQU);
				m_cdht.tcp_write(Integer.parseInt(rece[1]));
				m_cdht.tcp_write(Integer.parseInt(rece[2]));
				m_cdht.tcp_se.resume();
			}
			else if(Integer.parseInt(rece[0]) == m_cdht.COMM_FROM_QUIT){
				int port_to_leave = Integer.parseInt(rece[3]);
				System.out.println("Peer "+ port_to_leave+" will depart from the network.");
				if(port_to_leave == m_cdht.node.get_id_of_fir_suc()){
					m_cdht.node.set_fir_suc(new Node(Integer.parseInt(rece[1])));
					m_cdht.node.set_sec_suc(new Node(Integer.parseInt(rece[2])));
					System.out.println("My first successor is now peer "+ m_cdht.node.get_id_of_fir_suc()+".");
					System.out.println("My second successor is now peer "+ m_cdht.node.get_id_of_sec_suc()+".");
					// if the node to leave is its first successor ,then the response times 1000 to show that is 
					//is the response for first node 
					m_cdht.tcp_write(port_to_leave * 1000);
					m_cdht.tcp_write(m_cdht.FLAG);
					m_cdht.tcp_write(m_cdht.FLAG);
					m_cdht.tcp_se.resume();
					m_cdht.write(m_cdht.PING_COMM);
					m_cdht.m_UDPs.resume();
				}
				else if(port_to_leave == m_cdht.node.get_id_of_sec_suc()){
					// this does not times 1000 to show it is its second successor
					m_cdht.node.set_sec_suc(new Node(Integer.parseInt(rece[1])));
					m_cdht.node.fir_suc.set_fir_suc(new Node(Integer.parseInt(rece[1])));
					m_cdht.node.fir_suc.set_sec_suc(new Node(Integer.parseInt(rece[2])));
					System.out.println("My first successor is now peer "+ m_cdht.node.get_id_of_fir_suc()+".");
					System.out.println("My second successor is now peer "+ m_cdht.node.get_id_of_sec_suc()+".");
					m_cdht.tcp_write(port_to_leave);
					m_cdht.tcp_write(m_cdht.FLAG);
					m_cdht.tcp_write(m_cdht.FLAG);
					m_cdht.tcp_se.resume();
					m_cdht.write(m_cdht.PING_COMM);
					m_cdht.m_UDPs.resume();
				}
			}
			else if(Integer.parseInt(rece[0]) == m_cdht.QUIT_CONFIRM1){
				m_cdht.quit_condition1 += 1;
				if(m_cdht.quit_condition2 == 1){
							System.out.println("Confirm has been received from two pre_successor.quit gracefully ");
							System.exit(0);
				}
			}
			else if(Integer.parseInt(rece[0]) == m_cdht.QUIT_CONFIRM2){
				m_cdht.quit_condition2 += 1;
				if(m_cdht.quit_condition1 == 1){
					System.out.println("Confirm has been received from two pre_successor.quit gracefully ");
					System.exit(0);
				}
			}
		}
	}
}
