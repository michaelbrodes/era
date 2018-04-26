package era.uploader.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A decorator to make a map not care about the case of the keys. This is slower
 * than a normal implementation of a Map, so should only be used with a small
 * amount of entries.
 *
 * @param <VALUE> the type of values being stored in the map.
 */
public class CaseInsensitiveMap<VALUE> implements Map<String, VALUE> {
    private final Map<String, String> keyMap;
    private final Map<String, VALUE> delegate;

    private CaseInsensitiveMap(Map<String, VALUE> delegate) {
        this.delegate = delegate;
        this.keyMap = Maps.uniqueIndex(delegate.keySet(), this::normalizeKey);
    }

    public static <VALUE> Map<String, VALUE> decorate(@Nonnull Map<String, VALUE> map) {
        Preconditions.checkNotNull(map);
        return new CaseInsensitiveMap<>(map);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyMap.containsKey(((String) key).trim().toUpperCase());
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public VALUE get(Object key) {
        String delegateKey = keyMap.get(normalizeKey(key));
        return delegateKey != null ? delegate.get(delegateKey) : null;
    }

    @Override
    public VALUE put(String key, VALUE value) {
        String normalKey = normalizeKey(key);
        if (keyMap.containsKey(normalKey)) {
            return delegate.put(keyMap.get(normalKey), value);
        } else {
            keyMap.put(normalKey, key);
            return delegate.put(key, value);
        }
    }

    @Override
    public VALUE remove(Object key) {
        String normalKey = normalizeKey(key);
        if (keyMap.containsKey(normalKey)) {
            VALUE ret = delegate.remove(keyMap.get(normalKey));
            keyMap.remove(normalKey);
            return ret;
        } else {
            return null;
        }

    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ? extends VALUE> m) {
        for (Entry<? extends String, ? extends VALUE> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        keyMap.clear();
        delegate.clear();
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Nonnull
    @Override
    public Collection<VALUE> values() {
        return delegate.values();
    }

    @Nonnull
    @Override
    public Set<Entry<String, VALUE>> entrySet() {
        return delegate.entrySet();
    }

    private String normalizeKey(@Nullable Object key) {
        if (key == null) {
            return null;
        } else {
            return ((String) key).trim().toUpperCase();
        }
    }
}
