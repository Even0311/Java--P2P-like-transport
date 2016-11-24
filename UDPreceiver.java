
import java.io.*; 
import java.net.*;
public class UDPreceiver extends Thread {
	private cdht_ex m_cdht;
	public  UDPreceiver(cdht_ex t){
		m_cdht = t;
	}
	@Override
	public void run() {
		
		super.run();
		try {
			receive_function();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	public void receive_function() throws IOException{
		@SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket(50000+m_cdht.node.id);
		while(true){
			DatagramPacket request = new DatagramPacket(new byte[1024],1024);
			socket.receive(request);
			String receive_data = new String(request.getData(),0,request.getLength());
			String[] receive_array = receive_data.split(" ");
			int source_port = Integer.parseInt(receive_array[0]);
			int flag = Integer.parseInt(receive_array[1]);
			int pre_suc = Integer.parseInt(receive_array[2]);
			if(flag == m_cdht.PING_RES_COMM){
				System.out.println("A ping request message was received from Peer" + source_port + ".");
				if(pre_suc == m_cdht.FIR_SUC){
					m_cdht.node.set_fir_pre(new Node(source_port));
					
				}
				if(pre_suc == m_cdht.SEC_SUC){
					m_cdht.node.set_sec_pre(new Node(source_port));
				}
				m_cdht.write(source_port);
				m_cdht.m_UDPs.resume();
			
			}
			else if(flag == m_cdht.CONFIRM){
				System.out.println("A ping response message was received from Peer "+ source_port +".");
				if(source_port == m_cdht.node.get_id_of_fir_suc()){
					m_cdht.force_quit_condition1 = 0;
				}
				else if(source_port == m_cdht.node.get_id_of_sec_suc()){
					m_cdht.force_quit_condition2 = 0;
				}
			}
			else if(flag == m_cdht.LOSE_FIRST){
				System.out.println(source_port + "losed the first sucessor ");
				System.out.println("Now I am going to give it my first sucessor as his second suc");
				m_cdht.write(m_cdht.RESPONSE_FIRST);
				
			}
			else if(flag == m_cdht.RESPONSE_FIRST){
				m_cdht.node.set_fir_suc(new Node(source_port));
				m_cdht.node.set_sec_suc(new Node(pre_suc));
				System.out.println("set successful");
				
			}
			else if(flag == m_cdht.LOSE_SECOND){
				System.out.println(source_port + "losed the second sucessor ");
				System.out.println("Now I am going to give it my second sucessor as his sec suc");
				m_cdht.write(m_cdht.RESPONSE_SECOND);
			}
			else if(flag == m_cdht.RESPONSE_SECOND){
				m_cdht.node.set_sec_suc(new Node(pre_suc));
				System.out.println("set successful");
			}
		}
	}
}
