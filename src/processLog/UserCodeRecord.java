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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCodeRecord {
	HashMap<String, LogInRecord> map;
	List<String> blocked;
	List<String> wrongFormat;
	
	public UserCodeRecord(){
		map = new HashMap<>();
		blocked = new ArrayList<>();
		wrongFormat = new ArrayList<>();
	}
	
	//Constructor which directly loads data file.
	public UserCodeRecord (InputStreamReader inFile) throws IOException{
		map = new HashMap<>();
		blocked = new ArrayList<>();
		wrongFormat = new ArrayList<>();
		
		Pattern format = Pattern.compile("(\\[[^\\[\\]]*\\])\\s.*\\s([\\d]+)\\s([\\d|-]+)");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
		
		//Load input data file, update status map and construct blocked list. 
		//Assumption: input log records are ordered by time. 
		BufferedReader reader = new BufferedReader(inFile);
		int idx = 0;
		String dataLine;
		
		while((dataLine = reader.readLine()) != null){
			idx++;
			String[] cols = dataLine.split(" - - ");
			if(cols.length < 2){
				wrongFormat.add("Line " + idx + ": " + dataLine);
				continue;
			}
			
			String ip = cols[0];
			int code;
			Date time;
			Matcher timeAndCode = format.matcher(cols[1]);
			if(timeAndCode.find()){
				try{
					time = dateFormat.parse(timeAndCode.group(1).substring(1, timeAndCode.group(1).length()-1));
					code = Integer.parseInt(timeAndCode.group(2));
				}catch(IndexOutOfBoundsException e){
					wrongFormat.add("IndexOutOfBound Line " + idx + ": " + dataLine);
					continue;
				}catch(ParseException e){
					wrongFormat.add("ParseException Line " + idx + ": " + dataLine);
					continue;
				}
				
				//update status map and blocked list 
				updateStatus(dataLine, ip, code, time);
			}	
		}
		
		reader.close();
	}
	
	public void updateStatus(String dataLine, String ip, int code, Date time){
		if(map.containsKey(ip)){
			Date firstTime = map.get(ip).firstFailed;
			Date latestTime = map.get(ip).latestFailed;
			
			//within 5 minutes blocked period.
			if(map.get(ip).frozen && (latestTime.getTime() - time.getTime())/1000 <= 300){
				blocked.add(dataLine);
				return;
			}
			//has failed login attempts in 20 seconds  
			if(code == 401 && (firstTime.getTime() - time.getTime())/1000 <= 20){
				map.get(ip).failed++;
				map.get(ip).latestFailed = time;
				
				if(map.get(ip).failed == 3) map.get(ip).frozen = true;
				return;
			}
			
			//either successfully login in 20 seconds or make request after 20 seconds before frozen.   
			if(code != 401 || (firstTime.getTime() - time.getTime())/1000 > 20) map.remove(ip);
		}
		if(code == 401) map.put(ip, new LogInRecord(time));	
	}
	
	public List<String> getBlocked(){
		return blocked;
	}
	
	public List<String> getWrongFormat(){
		return wrongFormat;
	}
	
	class LogInRecord{
		int failed;
		Date firstFailed;
		Date latestFailed;
		boolean frozen;
		
		public LogInRecord(Date currFailed){
			this.failed = 1;
			this.firstFailed = currFailed;
			this.latestFailed = currFailed;
			this.frozen = false;
		}
	}

}
