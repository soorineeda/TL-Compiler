

import java.util.ArrayList;

public class MainTreeNode {
	String name;
	ArrayList<MainTreeNode> list;
	String value;
	String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<MainTreeNode> getList() {
		return list;
	}
	public void setList(ArrayList<MainTreeNode> list) {
		this.list = list;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
