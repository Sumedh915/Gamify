package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** 4x4 boxes (5x5 dots). Edges: horizontal first (r*N+c), then vertical (H + r*(N+1)+c). */
public class DotsAndBoxes extends GameState {
    public static final int N = 4, H = N * (N + 1), E = 2 * H;
    public int[] eo = new int[E];       // -1 undrawn, else player who drew it
    public int[] own = new int[N * N];  // box owner
    public int[] sc = new int[2];

    public DotsAndBoxes() { Arrays.fill(eo, -1); Arrays.fill(own, -1); }

    public GameState copy() {
        DotsAndBoxes d = base(new DotsAndBoxes());
        d.eo = eo.clone(); d.own = own.clone(); d.sc = sc.clone(); return d;
    }

    public int[] boxesOf(int e) {
        if (e < H) { int r = e / N, c = e % N; return r == 0 ? new int[]{c} : r == N ? new int[]{(N - 1) * N + c} : new int[]{(r - 1) * N + c, r * N + c}; }
        int j = e - H, r = j / (N + 1), c = j % (N + 1);
        return c == 0 ? new int[]{r * N} : c == N ? new int[]{r * N + N - 1} : new int[]{r * N + c - 1, r * N + c};
    }

    public int sides(int b) {
        int r = b / N, c = b % N, n = 0;
        if (eo[r * N + c] != -1) n++;
        if (eo[(r + 1) * N + c] != -1) n++;
        if (eo[H + r * (N + 1) + c] != -1) n++;
        if (eo[H + r * (N + 1) + c + 1] != -1) n++;
        return n;
    }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (over()) return l;
        for (int e = 0; e < E; e++) if (eo[e] == -1) l.add(mv(e));
        return l;
    }

    public void apply(int p, int[] m) {
        int e = m[0]; eo[e] = p; boolean got = false;
        for (int b : boxesOf(e)) if (own[b] == -1 && sides(b) == 4) { own[b] = p; sc[p]++; got = true; }
        if (sc[0] + sc[1] == N * N) { winner = sc[0] > sc[1] ? 0 : sc[1] > sc[0] ? 1 : 2; return; }
        turn = got ? p : 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) { m.put("N", N); m.put("eo", eo); m.put("own", own); m.put("sc", sc); }
}
