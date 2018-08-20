package com.jpmc.trade.engine.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public interface IReportCalculator {
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
	/**
	 * An utility method to calculate the amount in USD as per requirement
	 * @param pricePerUnit
	 * @param units
	 * @param agreedFx
	 * @return BigDecimal
	 */
	public static BigDecimal calulateTradeTotal (BigDecimal pricePerUnit, int units, BigDecimal agreedFx) {
		return agreedFx.multiply(pricePerUnit).multiply(new BigDecimal(units));
	}
	
	/**
	 * An utility method to validate the working day and return a date string accordingly
	 * @param settlementDateString
	 * @param currency
	 * @return String
	 */
	public static String getNextWorkingDay (String settlementDateString, String currency) {
		LocalDate requestedSettlementDate = getStringAsDate(settlementDateString);
		LocalDate actualDate = requestedSettlementDate;
		String dayName = requestedSettlementDate.getDayOfWeek().name();
		String nextWD = null;
		if (currency.equals("AED") || currency.equals("SAR")) {
			if(dayName.equalsIgnoreCase("FRIDAY")) {
				actualDate = requestedSettlementDate.plusDays(2);
			} 
			if (dayName.equalsIgnoreCase("SATURDAY")) {
				actualDate = requestedSettlementDate.plusDays(1);
			}
			nextWD = actualDate.format(formatter);
		} else {
			if(dayName.equalsIgnoreCase("SATURDAY")) {
				actualDate = requestedSettlementDate.plusDays(2);
			} 
			if (dayName.equalsIgnoreCase("SUNDAY")) {
				actualDate = requestedSettlementDate.plusDays(1);
			}
			nextWD = actualDate.format(formatter);
		}
		return nextWD;
	}
	
	/**
	 * A method to get a parsed LocalDate from a String 
	 * @param dateString
	 * @return LocalDate
	 */
	public static LocalDate getStringAsDate(String dateString) {
		LocalDate requestedSettlementDate = LocalDate.parse(dateString, formatter);
		return requestedSettlementDate;
	} 
	
	Comparator<String> settlementDate = Comparator.comparing(String::new, (d1,d2) -> {
		return getStringAsDate(d1).compareTo(getStringAsDate(d2));
	});
}
