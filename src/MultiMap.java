

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MultiMap {
	HashMap<String,ArrayList<String>> multiMap = new HashMap<String,ArrayList<String>>();

	public void put(String key, String value){
		ArrayList<String> arr = multiMap.get(key);
		if(arr != null){
			arr.add(value);
			HashSet<String> listToSet = new HashSet<String>(arr);
			ArrayList<String> listWithoutDuplicates = new ArrayList<String>(listToSet);
			multiMap.put(key, listWithoutDuplicates);
		}else{
			ArrayList<String> array = new ArrayList<String>();			
			array.add(value);
			multiMap.put(key, array);			
		}
	}
	
	public void put(String key, ArrayList<String> arr){
		if(arr != null){
			ArrayList<String> arrayList = multiMap.get(key);
			
			//To avoid ConcurrentModificationException below array is used instead of arraylist "arr"
			String array[] = new String[arr.size()];              
			for(int j =0;j<arr.size();j++){
			  array[j] = arr.get(j);
			}
			
			if(arrayList != null){		
				for(int i = 0;i<array.length;i++){
					arrayList.add(array[i]);
				}
				HashSet<String> listToSet = new HashSet<String>(arrayList);
				ArrayList<String> listWithoutDuplicates = new ArrayList<String>(listToSet);
				multiMap.put(key, listWithoutDuplicates);
			}else{
				HashSet<String> listToSet = new HashSet<String>(arr);
				ArrayList<String> listWithoutDuplicates = new ArrayList<String>(listToSet);
				multiMap.put(key, listWithoutDuplicates);
			}
		}
	}
	
	public ArrayList<String> get(String key){
		return multiMap.get(key);
	}

	public boolean exists(String key, String string) {
		ArrayList<String> arr = multiMap.get(key);
		if(arr == null){
			return false;
		}else{
			Iterator<String> i = arr.iterator();
			while(i.hasNext()){
				if(string.matches((String) i.next())){
					return true;
				}
			}
		}
		return false;
	}

	public void removeNullProds(String key) {
		ArrayList<String> arrayList = multiMap.get(key);
		ArrayList<String> arr = new ArrayList<String>();
		for(int j =0;j<arrayList.size();j++){
			if(!arrayList.get(j).equals("e"))
				arr.add(arrayList.get(j));
		}		
		multiMap.put(key, arr);
	}
}
