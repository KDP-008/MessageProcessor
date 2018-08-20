package com.jpmc.sales.processing.business;

public enum MessageTypes {
	//apple at 10p ->1
	//20 sales of apples at 10p each ->2
	//Add 20p apples ->3
	MessageType1("(^[a-zA-Z].*) (at) (\\d+p)"),
	MessageType2("(^[0-9]+).* (of) ([\\w]+{1}).* ([0-9]+p)"),
	MessageType3("([a-zA-Z].*) (\\d+p) ([\\w]+{1})");
	
	private String messagePattern;
	MessageTypes(String messagePattern){
		this.messagePattern = messagePattern;
	}
	
	public String getMessagePattern() {
        return this.messagePattern;
    }
}