package era.uploader.common.tuple;

public class FiveTuple<FIRST, SECOND, THIRD, FOURTH, FIFTH> {
    private final FIRST first;
    private final SECOND second;
    private final THIRD third;
    private final FOURTH fourth;
    private final FIFTH fifth;

    public FiveTuple(FIRST first, SECOND second, THIRD third, FOURTH fourth, FIFTH fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
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

    public FIFTH getFifth() {
        return fifth;
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
                + ", "
                + fifth.toString()
                + ")";
    }
}
