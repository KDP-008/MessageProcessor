package com.jpmc.trade.engine.business;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jpmc.trade.engine.interfaces.IReportCalculator;
import com.jpmc.trade.engine.models.TradeData;

public class ReportEngineService {

	/**
	 * Method to read the file with trade data for the day and add them in a
	 * list
	 * 
	 * @param filePath
	 * @return
	 * @throws TradeReportGenerationException 
	 * @throws IOException 
	 */
	public static Optional<List<TradeData>> setFetchTradeData(String filePath) throws IOException {
		List<TradeData> tradeList = new ArrayList<>();
		// check if path is valid
		Path path = Paths.get(filePath);
		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
		String line = null;
		if (pathExists) {
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				while ((line = br.readLine()) != null) {
					parseTradeData(line, tradeList);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new IOException("Invalid Path");
		}
		return Optional.ofNullable(tradeList);
	}

	private static void parseTradeData(String line, List<TradeData> tradeList) {
		TradeData td = null;
		String[] tradeData = line.split(",");
		td = new TradeData();
		td.setEntityName(tradeData[0]);
		td.setInstruction(tradeData[1]);
		td.setAgreedFx(new BigDecimal(tradeData[2]));
		td.setCurrency(tradeData[3]);
		td.setInstructionDate(tradeData[4]);
		td.setRequestedSettlementDate(tradeData[5]);
		td.setUnits(Integer.parseInt(tradeData[6]));
		td.setPricePerUnit(new BigDecimal(tradeData[7]));	
		//Calculate the amount and set in TradeData 
		td.setAmount(IReportCalculator.calulateTradeTotal(td.getPricePerUnit(),td.getUnits(),td.getAgreedFx()));
		//Check the requested settlement date and set the actual date of settlement as per requirement 
		td.setActualSettlementDate(IReportCalculator.getNextWorkingDay(td.getRequestedSettlementDate(),td.getCurrency()));
		tradeList.add(td);
	}

	/**
	 * Method to group the instruction type and entity
	 * @param instructionsReceived
	 * @return Map<String,List<TradeData>>
	 */
	public static Map<String, Map<String,List<TradeData>>> getAllTradeDataForEntities(List<TradeData> instructionsReceived) {
		Comparator<TradeData> entityAmountComp = Comparator.comparing(TradeData::getAmount, Comparator.reverseOrder());
		Map<String, Map<String,List<TradeData>>> inOutInstrMap =  instructionsReceived.stream()
				.sorted(entityAmountComp)
				.collect(Collectors.groupingBy(TradeData::getInstruction, Collectors.groupingBy(TradeData::getActualSettlementDate)));
		return inOutInstrMap;
	}

	/**
	 * Method to generate the reports as per requirement
	 * @param instructionFromEntities
	 */
	public static void generateReports(Map<String, Map<String, List<TradeData>>> instructionFromEntities) {
		Map<String, List<TradeData>> incomingInstr = instructionFromEntities.get("S");
		//Print the Settlement Data incoming everyday in order of higher Amount to lower
		printData("InComing",incomingInstr);
		
		//Print the Settlement Data outgoing everyday in order of higher Amount to lower
		Map<String, List<TradeData>> outGoingInstr = instructionFromEntities.get("B");
		printData("OutGoing",outGoingInstr);
	}

	private static void printData(String reportType, Map<String, List<TradeData>> incomingInstr) {
		System.out.println("**********Report Type: "+reportType+" Instructions**********");
		incomingInstr.keySet().stream().sorted(IReportCalculator.settlementDate).forEach( k -> {
			System.out.println("Settlement Date: "+k);
			System.out.println("Entity\t|\tTotal Amount(in dollars)");
			System.out.println("-----------------------------------------");
			incomingInstr.get(k).forEach( t -> {
				System.out.println(t.getEntityName()+"\t|\t"+t.getAmount());
			});
			System.out.println("-----------------------------------------");
		});
	}
}
