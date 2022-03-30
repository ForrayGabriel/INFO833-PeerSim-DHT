package helloWorld;

import peersim.edsim.*;

public class Message {

    public final static int HELLOWORLD = 0;
    public final static int NEXT = 1;
    public final static int PREVIOUS = 2;
    public static final int ACTIVATE = 3;
    public static final int JOIN = 4;
    public static final int PLACE = 5;
    public static final int NEW_SUIV = 6;
    public static final int NEW_PREC = 7;
    public static final int SHOW = 8;


    private int type;
    private String content;

    Message(int type, String content) {
	this.type = type;
	this.content = content;
    }

    public String getContent() {
	return this.content;
    }

    public int getType() {
	return this.type;
    }
    
}