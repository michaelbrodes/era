package era.uploader.common.tuple;

public class ThreeTuple<FIRST, SECOND, THIRD> {
    private final FIRST first;
    private final SECOND second;
    private final THIRD third;

    public ThreeTuple(FIRST first, SECOND second, THIRD third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public THIRD getThird() {
        return third;
    }
    @Override
    public String toString() {
        return "("
                + first.toString()
                + ", "
                + second.toString()
                + ", "
                + third.toString()
                + ")";
    }
}
