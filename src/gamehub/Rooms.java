package gamehub;

import gamehub.core.GameDef;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public final class Rooms {
    private static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    private static final String ALPHA = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom R = new SecureRandom();

    static {
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor(r -> { Thread t = new Thread(r); t.setDaemon(true); return t; });
        s.scheduleAtFixedRate(() -> ROOMS.values().removeIf(r -> System.currentTimeMillis() - r.touched > 3 * 3600_000L), 10, 10, TimeUnit.MINUTES);
    }

    public static Room create(GameDef def, String mode, String cid, boolean tournament) {
        String code;
        do { StringBuilder sb = new StringBuilder(); for (int i = 0; i < 5; i++) sb.append(ALPHA.charAt(R.nextInt(ALPHA.length()))); code = sb.toString(); }
        while (ROOMS.containsKey(code));
        Room r = new Room(code, def, mode, cid, tournament);
        ROOMS.put(code, r); return r;
    }

    public static Room get(String code) {
        Room r = code == null ? null : ROOMS.get(code.toUpperCase());
        if (r == null) throw new ApiError("Room not found. Check the code.");
        return r;
    }
}
