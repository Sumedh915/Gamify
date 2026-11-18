package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/**
 * Players alternate single rolls, each on their own box of tiles 1-9 (tile i = bit i-1).
 * A move is the set of tiles to shut (a bit mask) that adds up to the roll.
 * If no tiles can make the roll, the only legal move is {0} ("no available move"): that player is finished with their open total.
 * A player who is finished (stuck or box shut) is skipped, so the other player keeps rolling alone. Lowest final total wins.
 */
public class ShutTheBox extends GameState {
    public int[] open = {511, 511};
    public int[] score = {-1, -1};          // -1 = still rolling, otherwise the final total
    public int d1, d2;                      // the current roll, for the player whose turn it is
    public int[] ev = {-1, 0, 0, -1};       // last event: player, roll, tiles shut (0 = no move), that player's final total (-1 if not finished)
    private Random rng = new Random();

    public ShutTheBox() { turn = rng.nextInt(2); rollDice(); }
    private ShutTheBox(boolean noRoll) {}

    public static int tileSum(int mask) { int s = 0; for (int i = 0; i < 9; i++) if ((mask >> i & 1) != 0) s += i + 1; return s; }
    public static boolean oneDie(int open) { return (open & 0x1C0) == 0; }
    public int roll() { return d1 + d2; }

    public GameState copy() {
        ShutTheBox c = base(new ShutTheBox(true));
        c.open = open.clone(); c.score = score.clone(); c.d1 = d1; c.d2 = d2; c.ev = ev.clone(); return c;
    }

    private void rollDice() {
        d1 = rng.nextInt(6) + 1; d2 = oneDie(open[turn]) ? 0 : rng.nextInt(6) + 1;
    }

    private boolean hasMove(int p) {
        for (int m = 511; m >= 1; m--) if ((m & ~open[p]) == 0 && tileSum(m) == roll()) return true;
        return false;
    }

    public boolean stuck() { return !over() && !hasMove(turn); }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        for (int m = 511; m >= 1; m--) if ((m & ~open[p]) == 0 && tileSum(m) == roll()) l.add(mv(m));
        if (l.isEmpty()) l.add(mv(0));      // no available move: the player confirms it
        return l;
    }

    public void apply(int p, int[] m) {
        int mask = m[0], r = roll();
        open[p] &= ~mask;
        if (mask == 0 || open[p] == 0) score[p] = tileSum(open[p]);   // stuck, or box shut (total 0)
        ev = new int[]{p, r, mask, score[p]};
        if (score[0] >= 0 && score[1] >= 0) { winner = score[0] < score[1] ? 0 : score[1] < score[0] ? 1 : 2; return; }
        int next = 1 - p;
        if (score[next] >= 0) next = p;     // the other player is finished, so this player rolls again
        turn = next;
        rollDice();
    }

    protected void fill(Map<String, Object> m, int v) {
        m.put("open", open); m.put("score", score); m.put("d1", d1); m.put("d2", d2); m.put("ev", ev); m.put("stuck", stuck());
    }
}
