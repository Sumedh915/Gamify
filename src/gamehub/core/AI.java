package gamehub.core;

import java.util.Random;

public interface AI {
    /** level: "easy", "hard" or "unbeatable". Returns a legal move for player `me`. */
    int[] choose(GameState s, int me, String level, Random rng);
}
