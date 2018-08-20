package com.jpmc.trade.engine.business;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jpmc.trade.engine.models.TradeData;

public class ReportEngine {
	
	/**
	 * Method to generate the reports based on the file supplied
	 * @param ar
	 * @throws TradeReportGenerationException
	 * @throws IOException 
	 */
	public static void main(String ar[]) throws IOException {
		// STEP - 1: Read the instructions sent by various clients
		// Assumptions: a) The data will be fed through a comma delimited csv file.
		//				b) The full fileName with path will be passed through command line.
		//				c) The file will have just a business day's data and report to be generated based on settlement date
		Optional<List<TradeData>> instructionsReceived = ReportEngineService.setFetchTradeData(ar[0]);
		
		//STEP - 2: Group the trade data for each instruction type and settlement date in descending order of amount
		Map<String, Map<String,List<TradeData>>> instructionFromEntities = ReportEngineService.getAllTradeDataForEntities(instructionsReceived.get());	
		
		//STEP - 3: Iterate over map of TradeData and generate the reports 
		ReportEngineService.generateReports(instructionFromEntities);	
	}
}
