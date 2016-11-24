

public class Node {
	public int id;
	public Node fir_suc;
	public Node sec_suc;
	public Node fir_pre;
	public Node sec_pre;
	public Node(int identifier){
		id = identifier;
	}
	public void set_fir_suc(Node node){
		fir_suc = node;
	}
	public void set_sec_suc(Node node){
		sec_suc = node;
	}
	public void set_fir_pre(Node node){
		fir_pre = node;
	}
	public void set_sec_pre(Node node){
		sec_pre = node;
	}
	public int get_id_of_fir_suc(){
		return fir_suc.id;
	}
	public int get_id_of_sec_suc(){
		return sec_suc.id;
	}
	public int get_id_of_sec_pre(){
		return sec_pre.id;
	}
	public int get_id_of_fir_pre(){
		return fir_pre.id;
	}
	

}
