package era.server.data.model;

import java.util.EnumSet;

public enum Term {
    FALL,
    SPRING,
    SUMMER,
    WINTER;

    public static Term humanValueOf(String humanReadable) {
        return Term.valueOf(humanReadable.toUpperCase());
    }

    public static boolean contains(final String value) {
        return EnumSet.allOf(Term.class)
                .stream()
                .anyMatch((term) -> term.name().equals(value));
    }

    public final String humanReadable() {
        return this.name().substring(0, 1)
                + this.name().substring(1).toLowerCase();
    }
}
