package era.uploader.common.tuple;

public class FourTuple<FIRST, SECOND, THIRD, FOURTH> {
    private final FIRST first;
    private final SECOND second;
    private final THIRD third;
    private final FOURTH fourth;

    public FourTuple(FIRST first, SECOND second, THIRD third, FOURTH fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
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

    public FOURTH getFourth() {
        return fourth;
    }

    @Override
    public String toString() {
        return "("
                + first.toString()
                + ", "
                + second.toString()
                + ", "
                + third.toString()
                + ", "
                + fourth.toString()
                + ")";
    }
}
