/**
 * Author: Ko-Shin Chen
 * Date: 4/5/2017
 */

package processLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class BusyPeriod {
	private HashMap<Long, Integer> map;
	private List<Long> orderedDates;
	private List<String> wrongFormat;
	
	public BusyPeriod(){
		map = new HashMap<>();
		orderedDates = new ArrayList<>();
		wrongFormat = new ArrayList<>();
	}
	
	//Constructor which directly loads data file. 
	public BusyPeriod(InputStreamReader inFile) throws IOException{
		map = new HashMap<>();
		orderedDates = new ArrayList<>();
		wrongFormat = new ArrayList<>();
		
		//Load input data file and construct timeStamp frequency.
		BufferedReader reader = new BufferedReader(inFile);
		int idx = 0;
		String dataLine;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
		
		while((dataLine = reader.readLine()) != null){
			idx++;
			int startIdx = dataLine.indexOf('[');
			int endIdx = dataLine.indexOf(']');
			if(startIdx < 0 || endIdx < 0 || endIdx - startIdx < 27){
				wrongFormat.add("Line " + idx + ": " + dataLine);
				continue;
			}
			
			String timeStamp = dataLine.substring(startIdx+1, endIdx);
			Date date;
			try{
				date = dateFormat.parse(timeStamp);
			}catch(ParseException e){
				wrongFormat.add("ParseException Line " + idx + ": timestamp catched is" + timeStamp);
				continue;
			}
			
			addTimeCount(date.getTime());
		}
		
		reader.close();
	}
	
	public void addTimeCount(Long date){
		if(!map.keySet().contains(date)){ 
			orderedDates.add(date);
		}
		int count = map.getOrDefault(date, 0);
		count++;
		map.put(date, count);
	}
	
	public List<String> getWrongFormat(){
		return wrongFormat;
	}
	
	public List<String> getBusiest(int n){
		if(n < 1) throw new IllegalArgumentException("The input should be a positive integer");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
		PriorityQueue<hourCount> queue = new PriorityQueue<>(n+1, new Comparator<hourCount>(){
			public int compare(hourCount hc1, hourCount hc2){
				if(hc1.count - hc2.count != 0) return hc1.count.compareTo(hc2.count);
				else return hc2.start.compareTo(hc1.start);
			}
		});
		
		long head = orderedDates.get(0);
		long tail = head;
		long endTime = orderedDates.get(orderedDates.size() - 1);
		Long sum = (long)0;
		
		while(tail < head + 1000*60*60){
			sum += map.getOrDefault(tail, 0);
			tail+= 1000;
		}
		
		hourCount hc0 = new hourCount(head, sum);
		queue.add(hc0);
		
		while(tail < endTime){
			tail += 1000;
			sum += map.getOrDefault(tail, 0);
			sum -= map.getOrDefault(head, 0);
			head += 1000;
			
			hourCount hci = new hourCount(head,sum);
			queue.add(hci);
			if(queue.size() > n) queue.poll();	
		}
		
		while(queue.size() < n){
			sum -= map.getOrDefault(head, 0);
			head += 1000;
			hourCount hci = new hourCount(head,sum);
			queue.add(hci);
		}
		
		
		/* Second version which uses event times as hour-start-times. 
		int j = 0;
		Long sum = (long)0;
		
		for(int i = 0; i < orderedDates.size(); i++){
			if(i > 0) sum -= map.get(orderedDates.get(i-1));
			if(j < i) j = i;
			while(j < orderedDates.size() && (orderedDates.get(i) - orderedDates.get(j)) < 1000*60*60){
				sum += map.get(orderedDates.get(j));
				j++;
			}
			hourCount hc = new hourCount(orderedDates.get(i), sum);
			
			if(queue.size() < n) queue.add(hc);
			else{
				queue.add(hc);
				queue.poll();
			}
		}
		*/
		
		List<String> busy = new ArrayList<>();
		while(!queue.isEmpty()){
			hourCount hc = queue.poll();
			busy.add(dateFormat.format(hc.start) + "," + hc.count);
		}
		
		return busy;
	}
	
	
	class hourCount{
		Date start;
		Long count;
		public hourCount(Long start, Long count){
			this.start = new Date(start);
			this.count = count;
		}
	}

}
