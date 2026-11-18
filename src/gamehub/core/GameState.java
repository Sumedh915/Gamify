package gamehub.core;

import java.util.*;

/** Base class for every game. To add a game: extend this, write an AI, register in GameRegistry. */
public abstract class GameState {
    public int turn = 0;       // player to move (0 or 1)
    public int winner = -1;    // -1 playing, 0/1 winner, 2 draw

    public final boolean over() { return winner != -1; }
    public boolean canMove(int p) { return !over() && p == turn; }
    public abstract GameState copy();
    /** Legal moves for player p (each move is an int[]). */
    public abstract List<int[]> moves(int p);
    public final List<int[]> legalMoves() { return moves(turn); }
    public abstract void apply(int player, int[] move);
    /** Add game-specific data for the given viewer (hide secrets here). */
    protected abstract void fill(Map<String, Object> m, int viewer);

    public final Map<String, Object> view(int viewer) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("turn", turn);
        m.put("winner", winner);
        fill(m, viewer);
        return m;
    }

    protected <T extends GameState> T base(T c) { c.turn = turn; c.winner = winner; return c; }
    public static int[] mv(int... a) { return a; }
}
