package sagrada.model.card;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;
import sagrada.model.card.objective.*;
import sagrada.model.card.tool.*;

import java.text.Normalizer;

public final class CardFactory {
    public static PublicObjectiveCard getPublicObjectiveCard(String name, int id, String description, int points) {

        PublicObjectiveCard objectiveCard = null;

        name = Normalizer
                .normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        switch (name) {
            case "Kleurdiagonalen":
                objectiveCard = new ColorDiagonals(id, name, description, points);
                break;
            case "Kleurvarieteit":
                objectiveCard = new ColorVariety(id, name, description, points);
                break;
            case "Kleurvarieteit per Kolom":
                objectiveCard = new ColumnColorVariety(id, name, description, points);
                break;
            case "Tintvarieteit":
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
            case "Kleurvarieteit per Rij":
                objectiveCard = new RowColorVariety(id, name, description, points);
                break;
            case "Tintvarieteit per Rij":
                objectiveCard = new RowShadeVariety(id, name, description, points);
                break;
            case "Tintvarieteit per Kolom":
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

    public static ToolCard getToolCard(int id, String name, String description, DatabaseConnection connection) {
        ToolCard toolCard = null;

        name = Normalizer
                .normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        switch (name) {
            case "Folie-aandrukker":
                toolCard = new CopperFoilBurnisher(id, name, description, connection);
                break;
            case "Snijliniaal":
                toolCard = new CorkBackedStraightedge(id, name, description, connection);
                break;
            case "Eglomise Borstel":
                toolCard = new EglomiseBrush(id, name, description, connection);
                break;
            case "Fluxborstel":
                toolCard = new FluxBrush(id, name, description, connection);
                break;
            case "Fluxverwijderaar":
                toolCard = new FluxRemover(id, name, description, connection);
                break;
            case "Loodhamer":
                toolCard = new GlazingHammer(id, name, description, connection);
                break;
            case "Schuurblok":
                toolCard = new GrindingStone(id, name, description, connection);
                break;
            case "Driepuntstang":
                toolCard = new GrozingPliers(id, name, description, connection);
                break;
            case "Loodopenhaler":
                toolCard = new Lathekin(id, name, description, connection);
                break;
            case "Rondsnijder":
                toolCard = new LensCutter(id, name, description, connection);
                break;
            case "Glasbreektang":
                toolCard = new RunningPliers(id, name, description, connection);
                break;
            case "Olieglassnijder":
                toolCard = new TapWheel(id, name, description, connection);
                break;
            default:
                System.out.print("Unsupported tool card: " + name);
                break;
        }

        return toolCard;
    }

}
