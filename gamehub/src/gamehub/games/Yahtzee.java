package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Simplified Yahtzee (no extra-Yahtzee bonus / joker). Moves: {0,rerollMask} or {1,category}. */
public class Yahtzee extends GameState {
    public static final String[] CATS = {"Ones","Twos","Threes","Fours","Fives","Sixes","Three of a kind","Four of a kind","Full house","Small straight","Large straight","Yahtzee","Chance"};
    public int[][] card = new int[2][13];
    public int[] dice = new int[5];
    public int rolls = 2;
    private Random rng = new Random();

    public Yahtzee() { for (int[] r : card) Arrays.fill(r, -1); rollAll(); }
    private Yahtzee(boolean x) {}

    private void rollAll() { for (int i = 0; i < 5; i++) dice[i] = rng.nextInt(6) + 1; rolls = 2; }

    public static int score(int cat, int[] d) {
        int[] n = new int[7]; int sum = 0;
        for (int x : d) { n[x]++; sum += x; }
        int max = 0; boolean two = false, three = false;
        for (int i = 1; i <= 6; i++) { max = Math.max(max, n[i]); if (n[i] == 2) two = true; if (n[i] == 3) three = true; }
        switch (cat) {
            case 6: return max >= 3 ? sum : 0;
            case 7: return max >= 4 ? sum : 0;
            case 8: return two && three ? 25 : 0;
            case 9: for (int s = 1; s <= 3; s++) if (n[s] > 0 && n[s+1] > 0 && n[s+2] > 0 && n[s+3] > 0) return 30; return 0;
            case 10: return (n[1]*n[2]*n[3]*n[4]*n[5] > 0 || n[2]*n[3]*n[4]*n[5]*n[6] > 0) ? 40 : 0;
            case 11: return max == 5 ? 50 : 0;
            case 12: return sum;
            default: return n[cat + 1] * (cat + 1);
        }
    }

    public int upper(int p) { int s = 0; for (int i = 0; i < 6; i++) if (card[p][i] > 0) s += card[p][i]; return s; }
    public int total(int p) { int s = upper(p) >= 63 ? 35 : 0; for (int v : card[p]) if (v > 0) s += v; return s; }
    private boolean done(int p) { for (int v : card[p]) if (v == -1) return false; return true; }

    public GameState copy() {
        Yahtzee c = base(new Yahtzee(true));
        c.card = new int[][]{card[0].clone(), card[1].clone()}; c.dice = dice.clone(); c.rolls = rolls; return c;
    }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        for (int c = 0; c < 13; c++) if (card[p][c] == -1) l.add(mv(1, c));
        if (rolls > 0) for (int m = 1; m < 32; m++) l.add(mv(0, m));
        return l;
    }

    public void apply(int p, int[] m) {
        if (m[0] == 0) { for (int i = 0; i < 5; i++) if ((m[1] >> i & 1) != 0) dice[i] = rng.nextInt(6) + 1; rolls--; return; }
        card[p][m[1]] = score(m[1], dice);
        if (done(0) && done(1)) { winner = total(0) > total(1) ? 0 : total(1) > total(0) ? 1 : 2; return; }
        turn = 1 - p; rollAll();
    }

    protected void fill(Map<String, Object> m, int v) {
        int[] pot = new int[13]; for (int c = 0; c < 13; c++) pot[c] = score(c, dice);
        m.put("dice", dice); m.put("rolls", rolls); m.put("card", card); m.put("pot", pot);
        m.put("upper", new int[]{upper(0), upper(1)}); m.put("total", new int[]{total(0), total(1)});
        m.put("cats", CATS);
    }
}
