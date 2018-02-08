package era.uploader.data.model;

public enum Term {
    FALL,
    SPRING,
    SUMMER,
    WINTER;

    public final String humanReadable() {
        return this.name().substring(0, 1)
                + this.name().substring(1).toLowerCase();
    }

    public static Term humanValueOf(String humanReadable) {
        return Term.valueOf(humanReadable.toUpperCase());
    }
}
