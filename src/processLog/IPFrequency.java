/**
 * Author: Ko-Shin Chen
 * Date: 4/5/2017
 */

package processLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class IPFrequency {
	private HashMap<String, Integer> map;
	private List<String> wrongFormat;
	
	public IPFrequency(){
		map = new HashMap<>();
		wrongFormat = new ArrayList<>();
	}
	
	//Constructor which directly loads data file.
	public IPFrequency(InputStreamReader inFile) throws IOException{
		map = new HashMap<>();
		wrongFormat = new ArrayList<>();
		
		//Load input data file and construct the frequency map.
		BufferedReader reader = new BufferedReader(inFile);
		int idx = 0;
		String dataLine;
		
		while((dataLine = reader.readLine()) != null){
			idx++;
			String ip = dataLine.split(" - - ")[0];
			
			if(!ip.isEmpty()){
				addCount(ip);
			}
			else{
				wrongFormat.add("Line " + idx + ": " + dataLine);
			}
		}
		reader.close();	
	}
	
	public void addCount(String ip){
		int count = map.getOrDefault(ip, 0);
		count++;
		map.put(ip, count);
	}
	
	public List<String> getWrongFormat(){
		return wrongFormat;
	}
	
	public List<String> getCurrTop(int n){
		if(n < 1) throw new IllegalArgumentException("The input should be a positive integer");
		
		PriorityQueue<ipCount> queue = new PriorityQueue<>(n+1, new Comparator<ipCount>(){
			public int compare(ipCount ic1, ipCount ic2){
				if(ic1.count - ic2.count != 0) return ic1.count - ic2.count;
				else return ic1.ip.compareTo(ic2.ip);
			}
		});
		
		for(String ip : map.keySet()){
			ipCount ic = new ipCount(ip, map.get(ip));
			
			if(queue.size()<n) queue.add(ic);
			else{
				queue.add(ic);
				queue.poll();
			}
		}
		
		List<String> tops = new ArrayList<>();
		while(!queue.isEmpty()){
			ipCount ic = queue.poll();
			tops.add(ic.ip + ","+ic.count);
		}
		return tops;
	}
	
	
	class ipCount{
		String ip;
		int count;
		
		public ipCount(String ip, int count){
			this.ip = ip;
			this.count = count;
		}
	}
}
