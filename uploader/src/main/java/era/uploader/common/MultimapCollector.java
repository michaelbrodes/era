package era.uploader.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * There is no Collector definition for {@link Multimap} in
 * {@link java.util.stream.Collectors} so this is our own. It takes in a stream
 * of MultiMaps and joins them together into a giant multimap
 */
public class MultimapCollector<T,V>
        implements Collector<Multimap<T, V>, ArrayListMultimap<T, V>, Multimap<T, V>> {
    @Override
    public Supplier<ArrayListMultimap<T, V>> supplier() {
        return ArrayListMultimap::create;
    }

    @Override
    public BiConsumer<ArrayListMultimap<T, V>, Multimap<T, V>> accumulator() {
        return ArrayListMultimap::putAll;
    }

    @Override
    public BinaryOperator<ArrayListMultimap<T, V>> combiner() {
        return (l, r) -> {
            l.putAll(r);
            return l;
        };
    }

    @Override
    public Function<ArrayListMultimap<T, V>, Multimap<T, V>> finisher() {
        return (f) -> (Multimap<T, V>) f;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.immutableEnumSet (
                Characteristics.UNORDERED,
                Characteristics.CONCURRENT
        );
    }

    public static <T, V> MultimapCollector<T, V> toMultimap() {
        return new MultimapCollector<>();
    }
}
