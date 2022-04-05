package helloWorld;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

import java.util.Random;

/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    
    private int helloWorldPid;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
	this.helloWorldPid = Configuration.getPid(prefix + ".helloWorldProtocolPid");
    }

    public boolean execute() {
	int nodeNb;
	Noeud first;
	Noeud current;
	Node dest;

	Random rand = new Random();
	rand.setSeed(8465655);

	//recuperation de la taille du reseau
	nodeNb = Network.size();

	if (nodeNb < 1) {
	    System.err.println("Network size is not positive");
	    System.exit(1);
	}

	//recuperation de la couche applicative de l'emetteur (le noeud 0)
	first = (Noeud)Network.get(0).getProtocol(this.helloWorldPid);
	first.setTransportLayer(0, 0);

	System.out.println(ANSI_GREEN + "\nWe have the following nodes with their UID :"+ANSI_BLUE);
	//pour chaque noeud, on fait le lien entre la couche applicative et la couche transport
	//puis on fait envoyer au noeud 0 un message "Hello"
	for (int i = 1; i < nodeNb; i++) {
	    dest = Network.get(i);
	    current = (Noeud)dest.getProtocol(this.helloWorldPid);
	    current.setTransportLayer(i, rand.nextInt(10000));
		System.out.println("	" + current.getNodeId() + " : " + current.getNodeUid());
	}

	Noeud second = (Noeud) Network.get(1).getProtocol(this.helloWorldPid);
	Noeud third = (Noeud) Network.get(2).getProtocol(this.helloWorldPid);


	if (second.getNodeUid() < third.getNodeUid()){
		first.setSuiv(second.getNodeId(), second.getNodeUid());
		first.setPrec(third.getNodeId(), third.getNodeUid());
		second.setSuiv(third.getNodeId(), third.getNodeUid());
		second.setPrec(first.getNodeId(), first.getNodeUid());
		third.setSuiv(first.getNodeId(), first.getNodeUid());
		third.setPrec(second.getNodeId(), second.getNodeUid());
	} else {
		first.setPrec(second.getNodeId(), second.getNodeUid());
		first.setSuiv(third.getNodeId(), third.getNodeUid());
		second.setPrec(third.getNodeId(), third.getNodeUid());
		second.setSuiv(first.getNodeId(), first.getNodeUid());
		third.setPrec(first.getNodeId(), first.getNodeUid());
		third.setSuiv(second.getNodeId(), second.getNodeUid());
	}

	System.out.println(ANSI_GREEN+"\nThe 3 first active nodes are :"+ANSI_BLUE);
	System.out.println("	" + first);
	System.out.println("	" + second);
	System.out.println("	" + third + ANSI_RESET);

	EDSimulator.add(100, new Message(Message.ACTIVATE,"Time to wake up"), Network.get(3),0);
	EDSimulator.add(1500, new Message(Message.ACTIVATE,"Time to wake up"), Network.get(4),0);
	EDSimulator.add(3000, new Message(Message.SEND,"3304,This is a message for number 3304"), Network.get(0),0);
	EDSimulator.add(4500, new Message(Message.LEAVE,"Leave"), Network.get(1),0);

	System.out.println("\n "+ANSI_WHITE_BACKGROUND + ANSI_BLACK + "Initialization completed" + ANSI_RESET);
	return false;
    }
}