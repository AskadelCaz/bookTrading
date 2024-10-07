import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookBuyerAgent extends Agent {
    private String targetBookTitle;
    private int ticker_timer = 10000; // Intervalo de búsqueda (ms)

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            System.out.println("Target book is: " + targetBookTitle);

            // Comportamiento para buscar el libro periódicamente
            addBehaviour(new TickerBehaviour(this, ticker_timer) {
                @Override
                protected void onTick() {
                    System.out.println("Trying to buy " + targetBookTitle);
                    ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                    msg.addReceiver(getAID("seller")); // Nombre del agente vendedor
                    msg.setContent(targetBookTitle);
                    myAgent.send(msg);
                }
            });

            // Comportamiento para recibir respuestas del vendedor
            addBehaviour(new HandleSellerResponse());
        } else {
            System.out.println("No target book title specified");
            doDelete();
        }
    }

    // Comportamiento para recibir respuestas del agente vendedor
    private class HandleSellerResponse extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String price = msg.getContent();
                System.out.println("Received proposal: " + targetBookTitle + " for $" + price);

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(targetBookTitle);
                myAgent.send(reply);
            } else {
                block();
            }

            // Manejo de mensajes de confirmación de compra
            MessageTemplate confirmTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage informMsg = myAgent.receive(confirmTemplate);
            if (informMsg != null) {
                if ("sold".equals(informMsg.getContent())) {
                    System.out.println(targetBookTitle + " successfully purchased!");
                    myAgent.doDelete(); // Eliminar el agente después de la compra
                }
            }
        }
    }
}

