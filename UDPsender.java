
import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class UDPsender extends Thread{
	private cdht_ex m_CDHT;
	public UDPsender(cdht_ex t) {
		// TODO Auto-generated constructor stub
		m_CDHT = t;
	}
	@Override
	public void run() {
		
		super.run();
		
		try {
			try {
				send_function();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void send_function() throws IOException, InterruptedException {
		for(;;){
			suspend();
			DatagramSocket socket = new DatagramSocket();
			int flag = m_CDHT.read();
			
			InetAddress inet = InetAddress.getByName("localhost") ;
			if(flag == m_CDHT.PING_COMM){
				if(m_CDHT.force_quit_condition1 >3){
					m_CDHT.force_quit_condition1 = 0;
					//m_CDHT.node.set_fir_suc(m_CDHT.node.sec_suc);
					String send = m_CDHT.node.id+ " "+ m_CDHT.LOSE_FIRST+" "+ m_CDHT.FLAG;
					byte[] bytes_to_send = send.getBytes();
					DatagramPacket dtp = new DatagramPacket(bytes_to_send,bytes_to_send.length,inet,50000+m_CDHT.node.get_id_of_sec_suc());
					socket.send(dtp);
					// CCCCCCCCCCCCCC HERE        
					//DSADASDSADSADA	
					System.out.println("My first successor quit suddenly ,Now my first successor is Peer" + m_CDHT.node.fir_suc.id);
					System.out.println("My first successor quit suddenly ,Now my second successor is Peer" + m_CDHT.node.sec_suc.id);
				}
				if(m_CDHT.force_quit_condition2 > 3){
					m_CDHT.force_quit_condition2 = 0;
					String send = m_CDHT.node.id + " " + m_CDHT.LOSE_SECOND + " "+ m_CDHT.FLAG;
					byte[] bytes_to_send = send.getBytes();
					DatagramPacket dtp = new DatagramPacket(bytes_to_send,bytes_to_send.length,inet,50000+m_CDHT.node.get_id_of_fir_suc());
					socket.send(dtp);
					System.out.println("My second successor quit suddenly, Now my first successor is Peer" + m_CDHT.node.get_id_of_fir_suc());
					System.out.println("asking for my second suc");
				}
				
				
				String string_to_fir = m_CDHT.node.id + " "
						+ m_CDHT.PING_RES_COMM + " " + m_CDHT.FIR_SUC;
				String string_to_sec = m_CDHT.node.id + " "
						+ m_CDHT.PING_RES_COMM + " " + m_CDHT.SEC_SUC;
				byte[] byte_to_fir = string_to_fir.getBytes();
				byte[] byte_to_sec = string_to_sec.getBytes();
				int port_of_first_suc = 50000 + m_CDHT.node.get_id_of_fir_suc();
				int port_of_sec_suc = 50000 + m_CDHT.node.get_id_of_sec_suc();
				DatagramPacket packet_to_fir = new DatagramPacket(byte_to_fir,
						byte_to_fir.length, inet, port_of_first_suc);
				DatagramPacket packet_to_sec = new DatagramPacket(byte_to_sec,
						byte_to_sec.length, inet, port_of_sec_suc);
				socket.send(packet_to_fir);
				m_CDHT.force_quit_condition1 += 1;
				socket.send(packet_to_sec);
				m_CDHT.force_quit_condition2 += 1;
			}
			else if(flag == m_CDHT.RESPONSE_FIRST){
				String send = m_CDHT.node.id + " "+ m_CDHT.RESPONSE_FIRST+ " "+ m_CDHT.node.get_id_of_fir_suc();
				byte[] data_to_send = send.getBytes();
				DatagramPacket dtp = new DatagramPacket(data_to_send,data_to_send.length,inet,50000+m_CDHT.node.get_id_of_sec_pre());
				socket.send(dtp);
			}
			else if(flag == m_CDHT.RESPONSE_SECOND){
				String send = m_CDHT.node.id + " "+ m_CDHT.RESPONSE_SECOND+ " "+ m_CDHT.node.get_id_of_sec_suc();
				byte[] data_to_send = send.getBytes();
				DatagramPacket dtp = new DatagramPacket(data_to_send,data_to_send.length,inet,50000+m_CDHT.node.get_id_of_fir_pre());
				socket.send(dtp);
			}
			else{
				String current_port = m_CDHT.node.id +" "+ m_CDHT.CONFIRM+ " "+ m_CDHT.FLAG;
				byte[] byte_to_send = current_port.getBytes();
				int port_to_response = 50000 + flag;
				DatagramPacket packet_to_response = new DatagramPacket(byte_to_send,byte_to_send.length,inet,port_to_response);
				socket.send(packet_to_response);
			}
		}
		
		
	}
}
