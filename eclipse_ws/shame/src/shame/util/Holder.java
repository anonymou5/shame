package shame.util;

/**
 * Poor man's pointer.
 * 
 * @author jferland
 *
 * @param <T> type of <code>Object</code> to hold.
 */
public class Holder<T> {
	private T value;

	public Holder(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
