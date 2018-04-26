package era.uploader.common;

import era.uploader.common.tuple.FiveTuple;
import era.uploader.common.tuple.FourTuple;
import era.uploader.common.tuple.ThreeTuple;
import era.uploader.common.tuple.TwoTuple;

public class Tuples {
    private Tuples() {
        // not instantiatable
    }

    public static <FIRST, SECOND> TwoTuple<FIRST, SECOND> createTuple(FIRST first, SECOND second) {
        return new TwoTuple<>(first, second);
    }

    public static <FIRST, SECOND, THIRD> ThreeTuple<FIRST, SECOND, THIRD> createTuple(FIRST first, SECOND second, THIRD third) {
        return new ThreeTuple<>(first, second, third);
    }

    public static <FIRST, SECOND, THIRD, FOURTH> FourTuple<FIRST, SECOND, THIRD, FOURTH> createTuple(FIRST first, SECOND second, THIRD third, FOURTH fourth) {
        return new FourTuple<>(first, second, third, fourth);
    }

    public static <FIRST, SECOND, THIRD, FOURTH, FIFTH> FiveTuple<FIRST, SECOND, THIRD, FOURTH, FIFTH> createTuple(FIRST first, SECOND second, THIRD third, FOURTH fourth, FIFTH fifth) {
        return new FiveTuple<>(first, second, third, fourth, fifth);
    }
}
