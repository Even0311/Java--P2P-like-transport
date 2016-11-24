

import java.util.ArrayList;
import java.util.Queue;

public class Timer extends Thread{
	private cdht_ex m_CDHT;
	public Timer(cdht_ex t) {
		m_CDHT = t;
	}
	@Override
	public void run() {
		super.run();
		for(;;){
			try{
				Thread.sleep(5000);
				m_CDHT.write(m_CDHT.PING_COMM);
				m_CDHT.m_UDPs.resume();
			}catch(Exception e){}
		}
	}
}
