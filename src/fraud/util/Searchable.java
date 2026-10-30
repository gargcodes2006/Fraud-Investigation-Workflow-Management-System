package fraud.util;

/**
 * Generic interface for searchable repositories.
 * Demonstrates: Generic interfaces, type parameters.
 *
 * @param <T> the entity type
 * @param <K> the key type
 */
public interface Searchable<T, K> {
    T findById(K id);
    java.util.List<T> findAll();
    java.util.List<T> search(String keyword);
}
