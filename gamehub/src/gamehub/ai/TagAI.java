package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Tag;

public class TagAI extends Minimax {
    protected int hardDepth() { return 4; }
    protected int unbeatableDepth() { return 14; }
    protected long unbeatableMs() { return 2000; }
    protected double easyRandom() { return 0.6; }

    protected double eval(GameState st, int me) {
        Tag t = (Tag) st;
        int d = t.dist();
        double s = (t.itCount[1 - me] - t.itCount[me]) * 6;
        s += t.it == me ? -d * 1.5 : d * 1.5;
        return s;
    }
}
