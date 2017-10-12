package era.uploader.processing;

import java.io.IOException;

/**
 * Converts an file of type {@link T} to a file of type {@link F}
 * @param <T> input that needs to be converted to F
 * @param <F> output that needs to be converted from T
 */
public interface Converter<T, F> {
    F convert(T file) throws IOException;
}
