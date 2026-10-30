package fraud.util;

/**
 * Generic interface for CRUD operations on entities.
 * Demonstrates: Generic interfaces with multiple type parameters.
 *
 * @param <T> the entity type
 * @param <K> the key type
 */
public interface Repository<T, K> extends Searchable<T, K> {
    void save(T entity);
    void update(T entity);
    void delete(K id);
    int count();
}
