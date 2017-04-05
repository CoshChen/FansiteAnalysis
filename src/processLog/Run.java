/**
 * Author: Ko-Shin Chen
 * Date: 4/5/2017
 */

package processLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class Run {

	public static void main(String[] args) throws IOException {
		InputStreamReader inFile;
		File outFile;
		int feature;
		
		if(args == null || args.length < 3){
			throw new IllegalArgumentException("Arguments: inFilePath, outFilePath, featureLabel");
		}
		else if(Integer.valueOf(args[2]) < 1 || Integer.valueOf(args[2]) > 4){
			throw new IllegalArgumentException("No such feature");
		}
		else{
			inFile =  new InputStreamReader(new FileInputStream(args[0]));
			outFile = new File(args[1]);
			if(!outFile.exists()) outFile.createNewFile();
			feature = Integer.valueOf(args[2]);
		}
		
		if(feature == 1){
			IPFrequency table = new IPFrequency(inFile);
			List<String> tops = table.getCurrTop(10);
			writeInToFile(tops, outFile);
			
			//debug used
			if(!table.getWrongFormat().isEmpty()) printWarning(table.getWrongFormat());
		}
		else if(feature == 2){
			ResourceBandwidth table = new ResourceBandwidth(inFile);
			List<String> tops = table.getCurrTop(10);
			writeInToFile(tops, outFile);
			
			//debug used
			if(!table.getWrongFormat().isEmpty()) printWarning(table.getWrongFormat());
		}
		else if(feature == 3){
			BusyPeriod table = new BusyPeriod(inFile);
			List<String> busy = table.getBusiest(10);
			writeInToFile(busy, outFile);
			
			//debug used
			if(!table.getWrongFormat().isEmpty()) printWarning(table.getWrongFormat());
		}
		else if(feature == 4){
			UserCodeRecord record = new UserCodeRecord(inFile);
			List<String> blocked = record.getBlocked();
			Collections.reverse(blocked);
			writeInToFile(blocked, outFile);
			
			//debug used
			if(!record.getWrongFormat().isEmpty()) printWarning(record.getWrongFormat());
		}
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
	
	public static void printWarning(List<String> wrongFormat){
		System.out.println("Some data lines have wrong format.");
		for(int i = 0; i < wrongFormat.size(); i++){
			System.out.println(wrongFormat.get(i));
		}
	}

}
