package com.jpmc.sales.processing.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpmc.sales.processing.models.Product;
import com.jpmc.sales.processing.models.Sales;

public class ProcessMessages {
	
	private static Map<String, List<Sales>> sales = new HashMap<>();
	private static int processedMessageCounter = 0;
	
	
	public static void main(String ar[]) {
		//"apple at 10p","20 sales of apples at 10p each","Add 20p apples","orange at 20p","banana at 12p","Add 2p oranges","10 sales of banana at 14p each"
			ProcessMessages.processMessage();
	}
	/**
	 * Method to process the messages
	 */
	public static void processMessage() {
		boolean processed= false;
		String message = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));) {
			while (processedMessageCounter<51) {
				System.out.println("Waiting for message...");
				message = br.readLine();
				if (message!=null) {
					//get the message and check its type and return the data
					processed = detectMessageTypeAndProcess(message);
					if (processed) {
						processedMessageCounter++;
						if (processedMessageCounter%10==0) {
							logSalesReport();
						}
						if (processedMessageCounter%50==0) {
							System.out.println("----System is pausing----");
							logSalesReport();
							System.out.println("----Can't accept more messages. Stopping...");
						}	
						System.out.println("Message processed successfully. Total Message Processed:"+processedMessageCounter);
					} else {
						System.err.println("Enter valid message. Total Message Processed:"+processedMessageCounter);
					}
				} else {
					System.err.println("Enter valid message. Total Message Processed:"+processedMessageCounter);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void logSalesReport() {
		System.out.println("-----------Sale Report----------");
		System.out.println("Product | Total Sale | Sale Value");
		sales.forEach((k,v) -> {
			v.stream().forEach((e) -> {
				System.out.println(e.getProduct().getProductName()+" | "+e.getSaleCount()+" | "+e.getSaleTotal());
			});
		});
	}

	private static boolean detectMessageTypeAndProcess(String message) {
		System.out.println("Message to be processed:"+message);
		int match = 0;
		String [] messageChunks = null;
		String messageType = null;
		for (MessageTypes msgType : MessageTypes.values()) {
			messageChunks = checkMessageForPattern(message,msgType);
			match = Integer.parseInt(messageChunks[0]);
			if(match!=0) {
				messageType = msgType.name();
				break;
			}
		}
		return processMessageArray(messageChunks,messageType);
	}

	private static boolean processMessageArray(String[] messageChunks, String messageType) {
		boolean processed = false;
		if (messageType!=null) {
			Product p = new Product();
			Sales s = new Sales();
			String op = null;
			if (messageType.equals("MessageType1")) {
				p.setProductName(messageChunks[1]);
				p.setProductRate(messageChunks[3]);
				s.setSaleCount(1);
			} else if (messageType.equals("MessageType2")) {
				if (messageChunks[3].endsWith("s")) {
					messageChunks[3] = messageChunks[3].substring(0, messageChunks[3].length()-1);
				}
				p.setProductName(messageChunks[3]);
				p.setProductRate(messageChunks[4]);
				s.setSaleCount(Integer.parseInt(messageChunks[1]));
			} else if (messageType.equals("MessageType3")) {
				if (messageChunks[3].endsWith("s")) {
					messageChunks[3] = messageChunks[3].substring(0, messageChunks[3].length()-1);
				}
				p.setProductName(messageChunks[3]);
				p.setProductRate(messageChunks[2]);
				s.setSaleCount(0);
				op = messageChunks[1];
			}
			s.setProduct(p);
			addUpdateSalesData(s, op);
			processed = true;
		}
		return processed;
	}

	private static void addUpdateSalesData(Sales s, String operation) {
		List<Sales> saleList = new ArrayList<>();
		String [] saleRate = s.getProduct().getProductRate().split("p");
		List<Sales> getProd = null;
		BigDecimal saleTotal = BigDecimal.ZERO;
		if (operation == null) {
			//calculate the current sale total
			saleTotal = new BigDecimal(saleRate[0]).multiply(BigDecimal.valueOf(s.getSaleCount()));
			s.setSaleTotal(saleTotal);
			//Check if current product exists in the map
		} else {
			s.setSaleTotal(saleTotal);
		}
		getProd = sales.get(s.getProduct().getProductName());
		//If exists, iterate list and update the current product sale count and total value
		if (getProd!=null) {
			updateExisitingSales(s,getProd,operation);
		}
		saleList.add(s);
		sales.put(s.getProduct().getProductName(), saleList);
	}

	private static void updateExisitingSales(Sales s, List<Sales> getProd, String op) {
		String prodName = null;
		Iterator<Sales> it = getProd.iterator();
		while(it.hasNext()) {
			Sales itS = it.next(); 
			prodName = itS.getProduct().getProductName();
			if (prodName.equals(s.getProduct().getProductName())) {
				if (null==op) {
					s.setSaleCount(s.getSaleCount()+itS.getSaleCount());
					s.setSaleTotal(s.getSaleTotal().add(itS.getSaleTotal()));	
				} else {
					String [] saleRate = s.getProduct().getProductRate().split("p");
					BigDecimal saleTotal = s.getSaleTotal();
					s.setSaleCount(itS.getSaleCount());
					saleTotal = new BigDecimal(saleRate[0]).multiply(BigDecimal.valueOf(itS.getSaleCount()));
					if ("add".equalsIgnoreCase(op)) {
						s.setSaleTotal(itS.getSaleTotal().add(saleTotal));
					} else if ("subtract".equalsIgnoreCase(op)) {
						s.setSaleTotal(itS.getSaleTotal().subtract(saleTotal));
					} else if ("multiply".equalsIgnoreCase(op)) {
						s.setSaleTotal(saleTotal.add(itS.getSaleTotal()));
					}
				}
				break;
			}
		}
	}

	/**
	 * Method to check the message pattern (Type1, Type2 or Type3)
	 * @param message
	 * @param msgType
	 * @return String[]
	 */
	public static String[] checkMessageForPattern(String message, MessageTypes msgType) {
		String [] messageChunks = new String[1];
		Pattern p = Pattern.compile(msgType.getMessagePattern());
		Matcher m = p.matcher(message);
		int c=0;
	    while(m.find()) {
	    	c++;
	    	messageChunks = new String[m.groupCount()+1];
	    	for (int i=1; i<=m.groupCount();i++) {
	    		messageChunks[i] = (m.group(i));
	    	}
	      }
	    messageChunks[0] = String.valueOf(c);
		return messageChunks;
	}
}