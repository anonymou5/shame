package shame.util.collections;

/**
 * Define a test that can be evaluated on T, the result of which is true or
 * false. The test can then be applied to a collection to filter out results for
 * which evaluate() returns false
 * 
 * @author jferland
 * 
 * @param <T>
 */
public interface Predicate<T> {
	boolean evaluate(T type);
}