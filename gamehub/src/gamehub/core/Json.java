package gamehub.core;

import java.util.*;

/** Tiny JSON writer (no dependencies). */
public final class Json {
    public static String write(Object o) { StringBuilder sb = new StringBuilder(); w(sb, o); return sb.toString(); }

    private static void w(StringBuilder sb, Object o) {
        if (o == null) sb.append("null");
        else if (o instanceof String s) str(sb, s);
        else if (o instanceof Number || o instanceof Boolean) sb.append(o);
        else if (o instanceof Map<?, ?> m) {
            sb.append('{'); boolean f = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!f) sb.append(','); f = false;
                str(sb, String.valueOf(e.getKey())); sb.append(':'); w(sb, e.getValue());
            }
            sb.append('}');
        }
        else if (o instanceof int[] a) { sb.append('['); for (int i = 0; i < a.length; i++) { if (i > 0) sb.append(','); sb.append(a[i]); } sb.append(']'); }
        else if (o instanceof boolean[] a) { sb.append('['); for (int i = 0; i < a.length; i++) { if (i > 0) sb.append(','); sb.append(a[i]); } sb.append(']'); }
        else if (o instanceof Object[] a) { sb.append('['); for (int i = 0; i < a.length; i++) { if (i > 0) sb.append(','); w(sb, a[i]); } sb.append(']'); }
        else if (o instanceof Iterable<?> it) { sb.append('['); boolean f = true; for (Object x : it) { if (!f) sb.append(','); f = false; w(sb, x); } sb.append(']'); }
        else str(sb, o.toString());
    }

    private static void str(StringBuilder sb, String s) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        sb.append('"');
    }
}
