package gamehub.ai;

import gamehub.core.*;

/** Unbeatable = full-depth minimax (solves the game: it can never lose). */
public class TicTacToeAI extends Minimax {
    protected double eval(GameState s, int me) { return 0; }
    protected int hardDepth() { return 3; }
    protected int unbeatableDepth() { return 9; }
    protected long unbeatableMs() { return 10000; }
}
