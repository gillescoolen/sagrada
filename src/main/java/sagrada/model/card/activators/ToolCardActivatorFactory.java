package sagrada.model.card.activators;

import sagrada.controller.GameController;
import sagrada.model.ToolCard;

import java.text.Normalizer;

public class ToolCardActivatorFactory {
    public static ToolCardActivator getToolCardActivator(GameController gameController, ToolCard toolCard) {

        ToolCardActivator toolCardActivator = null;

        String name = toolCard.getName();

        name = Normalizer
                .normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        switch (name) {
            case "Fluxborstel":
                toolCardActivator = new FluxBrushActivator(gameController, toolCard);
                break;
            case "Loodhamer":
                toolCardActivator = new GlazingHammerActivator(gameController, toolCard);
                break;
            case "Schuurblok":
                toolCardActivator = new GrindingStoneActivator(gameController, toolCard);
                break;
            case "Rondsnijder":
                toolCardActivator = new LensCutterActivator(gameController, toolCard);
                break;
            case "Folie-aandrukker":
                toolCardActivator = new CopperFoilBurnisherActivator(gameController, toolCard);
                break;
            case "Snijliniaal":
                toolCardActivator = new CorkBackedStraightedgeActivator(gameController, toolCard);
                break;
        }

        return toolCardActivator;
    }
}
