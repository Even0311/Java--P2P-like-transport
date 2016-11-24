import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class cdht_ex {
	public static final int PING_COMM = -1;
	public static final int PING_RES_COMM = 0;
	public static final int FIR_SUC = -10;
	public static final int SEC_SUC = -20;
	public static final int COMM_FROM_TYPE = -1;
	public static final int COMM_FROM_QUIT = -2;
	public static final int COMM_FROM_REQU = -3;
	public static final int CONFIRM = -4;
	public static final int QUIT_CONFIRM2 = -6;
	public static final int QUIT_CONFIRM1 = -7;
	public static final int ASK = -5;
	public static final int FLAG = -5555;
	public static final int LOSE_FIRST = -8;
	public static final int LOSE_SECOND = -9;
	public static final int RESPONSE_FIRST = -11;
	public static final int RESPONSE_SECOND = -12;
	public int quit_condition1;
	public int quit_condition2;
	public UDPsender m_UDPs;
	public UDPreceiver m_UDPr;
	public int id_of_fir;
	public int id_of_sec;
	public Timer m_Timer;
	public Node node;
	public int force_quit_condition1;
	public int force_quit_condition2;

	public TCPreceiver tcp_rc;
	public TCPsender tcp_se;
	ArrayList<Integer> m_CommQueue;
	ArrayList<Integer> Queue_of_tcp;
	public static Integer m_lock = new Integer(1);
	public static Integer tcp_lock = new Integer(2);

	public void write(int n) {
		synchronized (m_lock) {
			m_CommQueue.add(n);
		}
	}

	public int read() {
		synchronized (m_lock) {
			int i = m_CommQueue.get(0);
			m_CommQueue.remove(0);
			return i;
		}
	}

	public void tcp_write(int n) {
		synchronized (tcp_lock) {
			Queue_of_tcp.add(n);
		}
	}

	public int tcp_read() {
		synchronized (tcp_lock) {
			int i = Queue_of_tcp.get(0);
			Queue_of_tcp.remove(0);
			return i;
		}
	}

	public static void main(String[] args) throws IOException {
		cdht_ex t = new cdht_ex();
		int i1 = Integer.parseInt(args[0]);
		int i2 = Integer.parseInt(args[1]);
		int i3 = Integer.parseInt(args[2]);
		t.node = new Node(i1);
		t.node.set_fir_suc(new Node(i2));
		t.node.set_sec_suc(new Node(i3));
		t.force_quit_condition1 = 0;
		t.force_quit_condition2 = 0;
		t.m_CommQueue = new ArrayList<Integer>();
		t.Queue_of_tcp = new ArrayList<Integer>();
		t.m_Timer = new Timer(t);
		t.m_UDPs = new UDPsender(t);
		t.m_UDPr = new UDPreceiver(t);
		t.tcp_se = new TCPsender(t);
		t.tcp_rc = new TCPreceiver(t);
		t.m_Timer.start();
		t.m_UDPr.start();
		t.m_UDPs.start();
		t.tcp_se.start();
		t.tcp_rc.start();
		while (true) {
			Scanner s = new Scanner(System.in);
			while (s.hasNext()) {
				String user_input = s.nextLine();
				if("quit".equals(user_input)) {
					t.quit_condition1 = 0;
					t.quit_condition2 = 0;
					t.tcp_write(COMM_FROM_QUIT);
					t.tcp_write(FLAG);
					t.tcp_write(FLAG);
					t.tcp_se.resume();
					
				
					
					
				}
				String[] user_input_ar = user_input.split(" ");
				if("request".equals(user_input_ar[0])) {
					int request_file = Integer.parseInt(user_input_ar[1]);
					t.tcp_write(COMM_FROM_TYPE);
					t.tcp_write(request_file);
					t.tcp_write(t.node.id);
					t.tcp_se.resume();

				}
			}
		}
	}

}
