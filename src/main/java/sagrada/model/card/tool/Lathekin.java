package sagrada.model.card.tool;

import sagrada.model.*;

import java.util.List;

public final class Lathekin extends ToolCard {
    public Lathekin(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        @SuppressWarnings("unchecked") // LOLOLOLOLOL
        List<Square[]> messageList = (List<Square[]>) message;

        Square[] squares = messageList.get(0);
        Square[] newSquares = messageList.get(1);

        for (int i = 0; i < squares.length; i++) {
            newSquares[i].setDie(squares[i].getDie());
            squares[i].setDie(null);
        }

        this.incrementCost();
    }
}
