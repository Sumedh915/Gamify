package gamehub.core;

import java.util.function.Supplier;

public record GameDef(String id, String name, String desc, String icon, Supplier<GameState> factory, AI ai) {}
