package era.server.common;

public final class Tuples {

    public static <LEFT, RIGHT> TwoTuple<LEFT, RIGHT> of(LEFT left, RIGHT right) {
        return new TwoTuple<>(left, right);
    }

    public static <LEFT, MIDDLE, RIGHT> ThreeTuple<LEFT, MIDDLE, RIGHT> of(LEFT left, MIDDLE middle, RIGHT right) {
        return new ThreeTuple<>(left, middle, right);
    }

    public static <ONE, TWO, THREE, FOUR> FourTuple<ONE, TWO, THREE, FOUR> of(ONE first, TWO second, THREE third, FOUR fourth) {
        return new FourTuple<>(first, second, third, fourth);
    }

    public static <ONE, TWO, THREE, FOUR, FIVE> FiveTuple<ONE, TWO, THREE, FOUR, FIVE> of(ONE first, TWO second, THREE third, FOUR fourth, FIVE fifth) {
        return new FiveTuple<>(first, second, third, fourth, fifth);
    }

    public static final class TwoTuple<LEFT, RIGHT> {
        private final LEFT left;
        private final RIGHT right;

        private TwoTuple(LEFT left, RIGHT right) {
            this.left = left;
            this.right = right;
        }

        public LEFT getLeft() {
            return left;
        }

        public RIGHT getRight() {
            return right;
        }
    }

    public static final class ThreeTuple<LEFT, MIDDLE, RIGHT> {
        private final LEFT left;
        private final MIDDLE middle;
        private final RIGHT right;

        private ThreeTuple(LEFT left, MIDDLE middle, RIGHT right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public LEFT getLeft() {
            return left;
        }

        public MIDDLE getMiddle() {
            return middle;
        }

        public RIGHT getRight() {
            return right;
        }
    }

    public static final class FourTuple<ONE, TWO, THREE, FOUR> {
        private final ONE first;
        private final TWO second;
        private final THREE third;
        private final FOUR fourth;

        private FourTuple(ONE first, TWO second, THREE third, FOUR fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        public ONE getFirst() {
            return first;
        }

        public TWO getSecond() {
            return second;
        }

        public THREE getThird() {
            return third;
        }

        public FOUR getFourth() {
            return fourth;
        }
    }

    public static final class FiveTuple<ONE, TWO, THREE, FOUR, FIVE> {
        private final ONE first;
        private final TWO second;
        private final THREE third;
        private final FOUR fourth;
        private final FIVE fifth;

        private FiveTuple(ONE first, TWO second, THREE third, FOUR fourth, FIVE fifth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
        }

        public ONE getFirst() {
            return first;
        }

        public TWO getSecond() {
            return second;
        }

        public THREE getThird() {
            return third;
        }

        public FOUR getFourth() {
            return fourth;
        }

        public FIVE getFifth() {
            return fifth;
        }
    }
}

