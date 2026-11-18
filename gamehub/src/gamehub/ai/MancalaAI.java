package gamehub.ai;

import gamehub.core.*;
import gamehub.games.*;

public class MancalaAI extends Minimax {
    protected int hardDepth() { return 4; }

    protected double eval(GameState st, int me) {
        Mancala m = (Mancala) st;
        int a = 0, b = 0;
        for (int i = 0; i < 6; i++) { a += m.pit[me * 7 + i]; b += m.pit[(1 - me) * 7 + i]; }
        return (m.pit[me * 7 + 6] - m.pit[(1 - me) * 7 + 6]) * 3 + (a - b) * 0.3;
    }
}
