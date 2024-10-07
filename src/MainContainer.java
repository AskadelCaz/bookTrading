import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainContainer {
    public static void main(String[] args) {
        // Crear la instancia de JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true");

        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // Crear el agente vendedor
            AgentController sellerAgent = mainContainer.createNewAgent("seller", "BookSellerAgent", null);
            sellerAgent.start();

            // Crear el agente comprador y pasarle el título del libro como argumento
            Object[] buyerArgs = new Object[]{"Artificial Intelligence"};
            AgentController buyerAgent = mainContainer.createNewAgent("buyer", "BookBuyerAgent", buyerArgs);
            buyerAgent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
