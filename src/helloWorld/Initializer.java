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

	//recuperation de la taille du reseau
	nodeNb = Network.size();

	if (nodeNb < 1) {
	    System.err.println("Network size is not positive");
	    System.exit(1);
	}

	//recuperation de la couche applicative de l'emetteur (le noeud 0)
	first = (Noeud)Network.get(0).getProtocol(this.helloWorldPid);
	first.setTransportLayer(0, 0);

	//pour chaque noeud, on fait le lien entre la couche applicative et la couche transport
	//puis on fait envoyer au noeud 0 un message "Hello"
	for (int i = 1; i < 10; i++) {
	    dest = Network.get(i);
	    current = (Noeud)dest.getProtocol(this.helloWorldPid);
	    current.setTransportLayer(i, rand.nextInt(10000));
		System.out.println(current.getNodeId() + " : " + current.getNodeUid());
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

	System.out.println(first);
	System.out.println(second);
	System.out.println(third);

	EDSimulator.add(100, new Message(Message.ACTIVATE,"Hello"), Network.get(3),0);

	System.out.println("Initialization completed");
	return false;
    }
}