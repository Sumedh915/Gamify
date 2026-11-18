package gamehub;

import gamehub.ai.*;
import gamehub.core.GameDef;
import gamehub.games.*;
import java.util.*;

/** ADD NEW GAMES HERE: one line per game (id = URL path and name of web/games/<id>.js). */
public final class GameRegistry {
    private static final Map<String, GameDef> DEFS = new LinkedHashMap<>();

    private static void reg(GameDef d) { DEFS.put(d.id(), d); }

    static {
        reg(new GameDef("tictactoe", "Tic Tac Toe", "Three in a row. The classic.", "✕○", TicTacToe::new, new TicTacToeAI()));
        reg(new GameDef("ultimate", "Ultimate Tic Tac Toe", "Win small boards to win the big one.", "▦", UltimateTicTacToe::new, new UltimateTicTacToeAI()));
        reg(new GameDef("dots", "Dots and Boxes", "Close boxes, take extra turns.", "⠿", DotsAndBoxes::new, new DotsAndBoxesAI()));
        reg(new GameDef("mancala", "Mancala", "Sow stones, capture, go again.", "◍", Mancala::new, new MancalaAI()));
        reg(new GameDef("shutbox", "Shut the Box", "Roll, flip tiles, lowest total wins.", "⚀", ShutTheBox::new, new ShutTheBoxAI()));
        reg(new GameDef("yahtzee", "Yahtzee", "Thirteen rounds of five dice.", "⚄", Yahtzee::new, new YahtzeeAI()));
        reg(new GameDef("quoridor", "Quoridor", "Race to the far side, build walls.", "♟", Quoridor::new, new QuoridorAI()));
        reg(new GameDef("tag", "Tag", "Don't be it when the clock runs out.", "☄", Tag::new, new TagAI()));
        reg(new GameDef("checkers", "Checkers", "Jump, crown, corner.", "⛀", Checkers::new, new CheckersAI()));
        reg(new GameDef("connect4", "Connect 4", "Drop discs, line up four.", "●", Connect4::new, new Connect4AI()));
        reg(new GameDef("battleship", "Battleship", "Hide your fleet, sink theirs.", "⚓", Battleship::new, new BattleshipAI()));
    }

    public static GameDef get(String id) { return DEFS.get(id); }
    public static Collection<GameDef> all() { return DEFS.values(); }
}
