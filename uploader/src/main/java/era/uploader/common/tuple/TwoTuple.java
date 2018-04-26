package era.uploader.common.tuple;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class TwoTuple<FIRST, SECOND> {
    private final FIRST first;
    private final SECOND second;

    public TwoTuple(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public Map<FIRST, SECOND> asMap() {
        return ImmutableMap.of(first, second);
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }
}
