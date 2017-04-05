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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceBandwidth {
	private HashMap<String, Long> map;
	private List<String> wrongFormat;
	
	public ResourceBandwidth(){
		map = new HashMap<>();
		wrongFormat = new ArrayList<>();
	}
	
	//Constructor which directly loads data file.
	public ResourceBandwidth(InputStreamReader inFile) throws IOException{
		map = new HashMap<>();
		wrongFormat = new ArrayList<>();
		
		//Assumed data line format: host \s [timeStamp] \s request(quotes included) \s code \s bytes 
		Pattern format = Pattern.compile("([\\S]*)\\s(\\[[^\\[\\]]*\\])\\s(\".*\"||.*)\\s([\\d]+)\\s([\\d|-]+)");
		
		//Load input data file and construct resource bandwidth map. 
		BufferedReader reader = new BufferedReader(inFile);
		int idx = 0;
		String dataLine;
		
		while((dataLine = reader.readLine()) != null){
			idx++;
			String row = dataLine.replace(" - - ", " ");
			Matcher cols = format.matcher(row);
			
			if(cols.find()){
				//Group(3) is the request; Group(5) is the number of bytes.
				try{
					int bytes;
					if(cols.group(5).indexOf("-") > -1) bytes = 0;
					else bytes = Integer.parseInt(cols.group(5));
					
					String source;
					String[] notes = cols.group(3).split(" +");
					if(notes.length < 2) source = notes[0];
					else source = notes[1];
					addSource(source, bytes);
				}catch(IndexOutOfBoundsException e){
					wrongFormat.add("Line " + idx + ": " + dataLine);
				}
			}
			else{
				wrongFormat.add("Line " + idx + ": " + dataLine);
			}
		}
		
		reader.close();
	}
	
	public void addSource(String source, Integer bytes){
		Long bandWidth = map.getOrDefault(source, (long) 0);
		map.put(source, bandWidth + bytes);
	}
	
	public List<String> getWrongFormat(){
		return wrongFormat;
	}
	
	public List<String> getCurrTop(int n){
		if(n < 1) throw new IllegalArgumentException("The input should be a positive integer");
		
		PriorityQueue<resourceCount> queue = new PriorityQueue<>(n+1, new Comparator<resourceCount>(){
			public int compare(resourceCount rc1, resourceCount rc2){
				if(rc1.count - rc2.count != 0) return rc1.count.compareTo(rc2.count);
				else return rc1.resource.compareTo(rc2.resource);
			}
		});
		
		for(String resource : map.keySet()){
			resourceCount rc = new resourceCount(resource, map.get(resource));
			
			if(queue.size()<n) queue.add(rc);
			else{
				queue.add(rc);
				queue.poll();
				
			}
		}
		
		List<String> tops = new ArrayList<>();
		while(!queue.isEmpty()){
			resourceCount rc = queue.poll();
			tops.add(rc.resource);
		}
		return tops;
	}
	
	
	class resourceCount{
		String resource;
		Long count;
		
		public resourceCount(String resource, long count){
			this.resource = resource;
			this.count = count;
		}
	} 
}
