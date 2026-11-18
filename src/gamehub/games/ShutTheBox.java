package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Each player plays a full turn on their own box; lowest remaining total wins. Tile i = bit i-1. */
public class ShutTheBox extends GameState {
    public int[] open = {511, 511};
    public int[] score = {-1, -1};
    public int d1, d2;
    public String note = "";
    private Random rng = new Random();

    public ShutTheBox() { rollAndResolve(); }

    public static int tileSum(int mask) { int s = 0; for (int i = 0; i < 9; i++) if ((mask >> i & 1) != 0) s += i + 1; return s; }
    public static boolean oneDie(int open) { return (open & 0x1C0) == 0; }
    public int roll() { return d1 + d2; }

    public GameState copy() {
        ShutTheBox c = base(new ShutTheBox(true));
        c.open = open.clone(); c.score = score.clone(); c.d1 = d1; c.d2 = d2; c.note = note; return c;
    }
    private ShutTheBox(boolean noRoll) {}

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        for (int m = 511; m >= 1; m--) if ((m & ~open[p]) == 0 && tileSum(m) == roll()) l.add(mv(m));
        return l;
    }

    private void rollAndResolve() {
        d1 = rng.nextInt(6) + 1; d2 = oneDie(open[turn]) ? 0 : rng.nextInt(6) + 1;
        if (!moves(turn).isEmpty()) return;
        score[turn] = tileSum(open[turn]);
        note = "Player " + (turn + 1) + " rolled " + roll() + " with no legal move and scores " + score[turn] + ".";
        endTurn();
    }

    private void endTurn() {
        if (turn == 0) { turn = 1; rollAndResolve(); }
        else winner = score[0] < score[1] ? 0 : score[1] < score[0] ? 1 : 2;
    }

    public void apply(int p, int[] m) {
        open[p] &= ~m[0]; note = "";
        if (open[p] == 0) { score[p] = 0; note = "Player " + (p + 1) + " shut the box!"; endTurn(); }
        else rollAndResolve();
    }

    protected void fill(Map<String, Object> m, int v) {
        m.put("open", open); m.put("score", score); m.put("d1", d1); m.put("d2", d2); m.put("note", note);
    }
}
