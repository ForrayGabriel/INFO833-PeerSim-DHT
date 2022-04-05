# INFO833 - DHT  

## Presentation  
The goal of this project is to use peersim, a peer to peer communication simulator, to create a DHT composed of different nodes.

## How install  
```
git clone https://github.com/ForrayGabriel/INFO833-PeerSim-DHT 
``` 

Make sure you have the following packages :  
``` 
djep-1.0.0.jar
jep-2.3.0.jar
```   
Then run the peersim.Simulator.java by adding : 
```
config_file.cfg
```  

## Explaination of the project  

We decided to create a new class for the object Noeud. This class has the following caracteristics :  
* An id for the sim to acces it
* An uid for the simulation
* The id and uid of the next node in the DHT
* The id and iud of the previous node in the DHT
* A method *receive* that is executed when the node receive a message

When the simulator start, it uses the class Initializer to initialize the simulation.  
First it creates some nodes. The first one always has the **id 0** and the **uid 0**. The other ones have an id that is incremented, and an **uid that is a random number between 1 and 9 999.**  
Then, we set the previous and next nodes attribut of the 3 firsts nodes as follow :  

![Nodes DHT](https://user-images.githubusercontent.com/72502592/161786587-9649f04b-369d-425e-8cbd-250582c71989.png)


Then, we add some message to send to some nodes at some time points.  

We created different message types to change the reaction of a node to a message.  
* For a **NEXT** or **PREVIOUS** message :  
These functions were made for testing purpose in the begining of the project.  
* For an **ACTIVATE** message :  
When a sleeping node receive this kind of message, it try to join the DHT by sending a **JOIN** message to the Node 0 with its own id and uid.  
* For a **JOIN** message : 
The node parse the message and get the id and uid of the node trying to join the DHT. If the new node id is bigger than the next node's, it passes the message to its next node. Else, it send a **PLACE** message to the entering node with its id and the one of the next node.
* For a **PLACE** message :  
The node finally have its place. It update its next and previous nodes ids and uids Then it send a **NEW_SUIV** and a **NEW_PREC** message to its new neighbors.  
* For a **NEW_SUIV** or **NEW_PREC** message :  
The node update the info about its new neighbour.
* For a **SHOW** message :  
The node passes the message to the next one, unless it's 0. This is for showing the whole DHT in order.  
* For a **LEAVE** message :  
The node send **NEW_SUIV** and a **NEW_PREC** message to its new neighbors with their ids and uids to get itself out of the loop.  
* For a **SEND** message :  
Id the node is node the targeted node, it forwards the message to the next one.





