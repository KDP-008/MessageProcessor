/**
 * 
 */
package com.jpmc.trade.engine.test;

import org.junit.Test;

import com.jpmc.sales.processing.business.MessageTypes;
import com.jpmc.sales.processing.business.ProcessMessages;

import static org.junit.Assert.*;

/**
 * @author kdp
 *
 */
public class SaleProcessorTest {
	
	@Test
	public void testCheckMessageForPattern1() {
		String[] msgArr = ProcessMessages.checkMessageForPattern("apple at 10p", MessageTypes.MessageType2);
		String [] expectedArr = {String.valueOf(0)};
		assertArrayEquals(expectedArr, msgArr);
	}
	
	@Test
	public void testCheckMessageForPattern2() {
		String[] msgArr = ProcessMessages.checkMessageForPattern("apple at 10p", MessageTypes.MessageType1);
		String [] expectedArr = {String.valueOf(1),"apple","at","10p"};
		assertArrayEquals(expectedArr, msgArr);
	}
	
	@Test
	public void testCheckMessageForPattern3() {
		String[] msgArr = ProcessMessages.checkMessageForPattern("20 sales of apples at 10p each", MessageTypes.MessageType1);
		String [] expectedArr = {String.valueOf(0)};
		assertArrayEquals(expectedArr, msgArr);
	}

}
