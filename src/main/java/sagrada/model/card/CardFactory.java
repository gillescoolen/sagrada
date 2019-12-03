package sagrada.model.card;

import sagrada.model.*;
import sagrada.model.card.objective.*;
import sagrada.model.card.tool.*;

public final class CardFactory {
    public static PublicObjectiveCard getPublicObjectiveCard(String name, int id, String description, int points) {

        PublicObjectiveCard objectiveCard = null;

        switch (name) {
            case "Kleurdiagonalen":
                objectiveCard = new ColorDiagonals(id, name, description, points);
                break;
            case "Kleurvariëteit":
                objectiveCard = new ColorVariety(id, name, description, points);
                break;
            case "Kleurvariëteit per Kolom":
                objectiveCard = new ColumnColorVariety(id, name, description, points);
                break;
            case "Tintvariëteit":
                objectiveCard = new ShadeVariety(id, name, description, points);
                break;
            case "Donkere Tinten":
                objectiveCard = new DeepShades(id, name, description, points);
                break;
            case "Lichte Tinten":
                objectiveCard = new LightShades(id, name, description, points);
                break;
            case "Halfdonkere Tinten":
                objectiveCard = new MediumShades(id, name, description, points);
                break;
            case "Kleurvariëteit per Rij":
                objectiveCard = new RowColorVariety(id, name, description, points);
                break;
            case "Tintvariëteit per Rij":
                objectiveCard = new RowShadeVariety(id, name, description, points);
                break;
            case "Tintvariëteit per Kolom":
                objectiveCard = new ColumnShadeVariety(id, name, description, points);
                break;
            default:
                System.out.print("Unsupported objective card: " + name);
                break;
        }

        return objectiveCard;
    }

    public static PrivateObjectiveCard getPrivateObjectiveCard(Color color) {
        return new PrivateObjectiveCard(color);
    }

    public static ToolCard getToolCard(int id, String name, String description) {
        ToolCard toolCard = null;

        switch (name) {
            case "Folie-aandrukker":
                toolCard = new CopperFoilBurnisher(id, name, description);
                break;
            case "Snijliniaal":
                toolCard = new CorkBackedStraightedge(id, name, description);
                break;
            case "Églomisé Borstel":
                toolCard = new EglomiseBrush(id, name, description);
                break;
            case "Fluxborstel":
                toolCard = new FluxBrush(id, name, description);
                break;
            case "Fluxverwijderaar":
                toolCard = new FluxRemover(id, name, description);
                break;
            case "Loodhamer":
                toolCard = new GlazingHammer(id, name, description);
                break;
            case "Schuurblok":
                toolCard = new GrindingStone(id, name, description);
                break;
            case "Driepuntstang":
                toolCard = new GrozingPliers(id, name, description);
                break;
            case "Loodopenhaler":
                toolCard = new Lathekin(id, name, description);
                break;
            case "Rondsnijder":
                toolCard = new LensCutter(id, name, description);
                break;
            case "Glasbreektang":
                toolCard = new RunningPliers(id, name, description);
                break;
            case "Olieglassnijder":
                toolCard = new TapWheel(id, name, description);
                break;
            default:
                System.out.print("Unsupported tool card: " + name);
                break;
        }

        return toolCard;
    }

}
