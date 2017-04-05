/**
 * Author: Ko-Shin Chen
 * Date: 4/5/2017
 */

package processLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunAllAtOnce {

	public static void main(String[] args) throws IOException {
		InputStreamReader inFile;
		File[] outFiles = new File[4];
		
		if(args == null || args.length < 5){
			throw new IllegalArgumentException("No specified input/output files");
		}
		else{
			inFile = new InputStreamReader(new FileInputStream(args[0]));
			for(int i = 1; i <= 4; i++){
				File outFile = new File(args[i]);
				if(!outFile.exists()) outFile.createNewFile();
				outFiles[i-1] = outFile;
			}
		}
		
		IPFrequency ipf = new IPFrequency();
		ResourceBandwidth rb = new ResourceBandwidth();
		BusyPeriod bp = new BusyPeriod();
		UserCodeRecord uc = new UserCodeRecord();
		
		loadData(inFile, ipf, rb, bp, uc);
		List<String> topHost = ipf.getCurrTop(10);
		List<String> topResource = rb.getCurrTop(10);
		List<String> busiestHours = bp.getBusiest(10);
		List<String> blocked = uc.getBlocked();
		Collections.reverse(blocked);
		
		writeInToFile(topHost, outFiles[0]);
		writeInToFile(topResource, outFiles[1]);
		writeInToFile(busiestHours, outFiles[2]);
		writeInToFile(blocked, outFiles[3]);
		
		//debug used
		if(!ipf.getWrongFormat().isEmpty()) printWarning(ipf.getWrongFormat(), 1);
		if(!rb.getWrongFormat().isEmpty()) printWarning(rb.getWrongFormat(), 2);
		if(!bp.getWrongFormat().isEmpty()) printWarning(bp.getWrongFormat(), 3);
		if(!uc.getWrongFormat().isEmpty()) printWarning(uc.getWrongFormat(), 4);
	}
	
	public static void loadData(InputStreamReader inFile, IPFrequency ipf, ResourceBandwidth rb, BusyPeriod bp, UserCodeRecord uc) throws IOException{
		//Assumed data line format: host \s [timeStamp] \s request(quotes included) \s code \s bytes 
		Pattern format = Pattern.compile("([\\S]*)\\s(\\[[^\\[\\]]*\\])\\s(\".*\"||.*)\\s([\\d]+)\\s([\\d|-]+)");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
		
		//Load input data file and construct objects. 
		BufferedReader reader = new BufferedReader(inFile);
		int idx = 0;
		String dataLine;
		
		while((dataLine = reader.readLine()) != null){
			idx++;
			String row = dataLine.replace(" - - ", " ");
			Matcher cols = format.matcher(row);
			
			String ip;
			String timeStamp;
			String resource;
			String codeString;
			String bytesString;
			
			if(cols.find()){
				try{
					ip = cols.group(1);
					timeStamp = cols.group(2);
					resource = cols.group(3);
					codeString = cols.group(4);
					bytesString = cols.group(5);
				}catch(IndexOutOfBoundsException e){
					System.out.println("WrongFormat Line " + idx + ": " + dataLine);
					continue;
				}
			}else{
				System.out.println("WrongFormat Line " + idx + ": " + dataLine);
				continue;
			}
			
			//Update user frequency map
			if(!ip.isEmpty()) ipf.addCount(ip);
			else ipf.getWrongFormat().add("Line " + idx + ": " + dataLine);
			
			//Get bytes and code as integers
			int bytes;
			if(bytesString.indexOf('-')>-1) bytes = 0;
			else bytes = Integer.parseInt(bytesString);
			int code = Integer.parseInt(codeString);
			
			//Get resource
			String source;
			String[] notes = resource.split(" +");
			if(notes.length < 2) source = notes[0];
			else source = notes[1];
			//Update resource bandwidth map
			rb.addSource(source, bytes);
			
			//Get timeStamp as a Date object
			Date date;
			try{
				date = dateFormat.parse(timeStamp.substring(1, timeStamp.length()-1));
				//Update busy period map
				bp.addTimeCount(date.getTime());
				uc.updateStatus(dataLine, ip, code, date);
				
			}catch(ParseException e){
				bp.getWrongFormat().add("Line " + idx + ": timestamp catched is" + timeStamp);
			}
		}
		
		reader.close();
	}
	
	public static void writeInToFile(List<String> result, File outFile) throws FileNotFoundException, UnsupportedEncodingException{
		int len = result.size();
		if(len == 0) return;
		
		PrintWriter writer = new PrintWriter(outFile);
		for(int i = len - 1; i >= 0; i--){
			writer.println(result.get(i));
		}
		writer.close();
	}
	
	public static void printWarning(List<String> wrongFormat, int feature){
		System.out.println("Feature " + feature + ": Some data lines have wrong format.");
		for(int i = 0; i < wrongFormat.size(); i++){
			System.out.println(wrongFormat.get(i));
		}
	}

}
