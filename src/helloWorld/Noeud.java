package helloWorld;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class Noeud implements EDProtocol {
    
    //identifiant de la couche transport
    private int transportPid;

    //objet couche transport
    private HWTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;

    //Id aléatoire
    private int nodeUid;

    private int prec_id;
    private int prec_uid;
    private int suiv_id;
    private int suiv_uid;

    private int number = 3;

    public Noeud(String prefix) {
	this.prefix = prefix;
	//initialisation des identifiants a partir du fichier de configuration
	this.transportPid = Configuration.getPid(prefix + ".transport");
	this.mypid = Configuration.getPid(prefix + ".myself");
	this.transport = null;
    }

    //methode appelee lorsqu'un message est recu par le protocole HelloWorld du noeud
    public void processEvent( Node node, int pid, Object event ) {
	    this.receive((Message)event);
    }
    
    //methode necessaire pour la creation du reseau (qui se fait par clonage d'un prototype)
    public Object clone() {

        Noeud dolly = new Noeud(this.prefix);

	    return dolly;
    }

    //liaison entre un objet de la couche applicative et un 
    //objet de la couche transport situes sur le meme noeud
    public void setTransportLayer(int nodeId, int nodeUid) {
        this.nodeId = nodeId;
        this.nodeUid = nodeUid;
        this.transport = (HWTransport) Network.get(this.nodeId).getProtocol(this.transportPid);
    }

    //envoi d'un message (l'envoi se fait via la couche transport)
    public void send(Message msg, Node dest) {
	    this.transport.send(getMyNode(), dest, msg, this.mypid);
    }

    //affichage a la reception et renvoie de message
    private void receive(Message msg) {
        System.out.println(CommonState.getTime() + " : NODE " + this.nodeId + " Received : " + msg.getContent());
        if (msg.getType() == Message.NEXT) {
            Node dest = Network.get(this.suiv_id);
            this.send(new Message(msg.getType(),"Hello from " + this.nodeId), dest);
        }
        if (msg.getType() == Message.PREVIOUS) {
            Node dest = Network.get(this.prec_id);
            this.send(new Message(msg.getType(),"Hello from " + this.nodeId), dest);
        }
        if (msg.getType() == Message.ACTIVATE) {
            System.out.println("Je me réveille " + this.nodeId);
            Node dest = Network.get(0);
            this.send(new Message(Message.JOIN,"I'd like to enter,"+ this.nodeId+","+this.nodeUid), dest);
        }
        if (msg.getType() == Message.JOIN) {
            String[] content = msg.getContent().split(",");
            if (Integer.parseInt(content[2]) <this.suiv_uid) {
                this.send(new Message(Message.PLACE,"Here is your place," + this.nodeId+","+this.suiv_id), Network.get(Integer.parseInt(content[1])));
            }
            else if (this.suiv_id == 0) {
                this.send(new Message(Message.PLACE,"Here is your place," + this.nodeId+","+this.suiv_id), Network.get(Integer.parseInt(content[1])));
            }
            else {
                this.send(new Message(Message.JOIN,"I'd like to enter,"+ content[1] +","+content[2]), Network.get(this.suiv_id));
            }
        }
        if (msg.getType() == Message.PLACE) {
            String[] content = msg.getContent().split(",");
            Noeud prec = (Noeud) Network.get(Integer.parseInt(content[1])).getProtocol(0);
            Noeud suiv = (Noeud) Network.get(Integer.parseInt(content[2])).getProtocol(0);
            this.setPrec(prec.getNodeId(), prec.getNodeUid());
            this.setSuiv(suiv.getNodeId(), suiv.getNodeUid());
            this.send(new Message(Message.NEW_SUIV,"I'm your new suiv,"+ this.nodeId +","+this.nodeUid), Network.get(Integer.parseInt(content[1])));
            this.send(new Message(Message.NEW_PREC,"I'm your new prec,"+ this.nodeId +","+this.nodeUid), Network.get(Integer.parseInt(content[2])));
        }
        if (msg.getType() == Message.NEW_PREC) {
            String[] content = msg.getContent().split(",");
            this.setPrec(Integer.parseInt(content[1]), Integer.parseInt(content[2]));
        }
        if (msg.getType() == Message.NEW_SUIV) {
            String[] content = msg.getContent().split(",");
            this.setSuiv(Integer.parseInt(content[1]), Integer.parseInt(content[2]));
            this.send(new Message(Message.SHOW,"Show us the whole DHT"), Network.get(0));
        }
        if (msg.getType() == Message.SHOW) {
            if (this.suiv_id!=0) {
                Node dest = Network.get(this.suiv_id);
                this.send(new Message(msg.getType(),"Hello from " + this.nodeId), dest);
            } else {
                this.number++;
                this.send(new Message(Message.ACTIVATE,"Hello"), Network.get(this.number));
            }
        }
        if (msg.getType() == Message.LEAVE){            
            this.send(new Message(Message.NEW_SUIV,"I'm your new suiv,"+ this.suiv_id+","+this.suiv_uid), Network.get(this.prec_id));
            this.send(new Message(Message.NEW_PREC,"I'm your new prec,"+ this.prec_id +","+this.prec_uid), Network.get(this.suiv_id));
           
        }
    }

    //retourne le noeud courant
    private Node getMyNode() {
	return Network.get(this.nodeId);
    }

    public String toString() {
	return "Node "+ this.nodeId + " Suiv : " + this.suiv_id + "," + this.suiv_uid + " Prec : " + this.prec_id + "," + this.prec_uid;
    }

    public int getNodeId() {
        return this.nodeId;
    }

    public int getNodeUid() {
        return this.nodeUid;
    }

    public void setPrec(int id, int uid) {
        this.prec_id = id;
        this.prec_uid = uid;
    }

    public void setSuiv(int id, int uid) {
        this.suiv_id = id;
        this.suiv_uid = uid;
    }
}