import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class BookSellerAgent extends Agent {
    private Map<String, Integer> catalogue;

    @Override
    protected void setup() {
        catalogue = new HashMap<>();
        catalogue.put("Java Programming", 50);
        catalogue.put("Artificial Intelligence", 100);
        catalogue.put("Distributed Systems", 75);

        System.out.println(getAID().getName() + " is ready to sell books.");

        // Comportamiento para responder a solicitudes de precios
        addBehaviour(new OfferRequestsServer());
        // Comportamiento para recibir respuestas de aceptación de propuesta
        addBehaviour(new PurchaseOrdersServer());
    }

    // Comportamiento para manejar solicitudes de precio
    private class OfferRequestsServer extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP); // Match de mensajes CFP (Call for Proposal)
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer price = catalogue.get(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price));
                    System.out.println("Proposed price for " + title + " is " + price);
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    // Comportamiento para manejar aceptación de la oferta y confirmar venta
    private class PurchaseOrdersServer extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL); // Match de mensajes de aceptación
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                // Si el libro está disponible y se acepta la oferta, confirmar la venta
                Integer price = catalogue.remove(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("sold");
                    System.out.println(title + " sold to agent " + msg.getSender().getLocalName());
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
