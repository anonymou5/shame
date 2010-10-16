package shame.model;

import java.util.regex.Pattern;

public class Revision {
	public static final Pattern HEX_STRING_PATTERN = Pattern
			.compile("^[0-9a-fA-F]{40}$");
	private String hexString;

	/**
	 * Create a new <code>Revision</code>.
	 * 
	 * @param hexString
	 * @throws NullPointerException
	 *             if any argument is null
	 * @throws IllegalArgumentException
	 *             if <code>hexString</code> does not match the expected SHA-1
	 *             hexadecimal string format
	 */
	public Revision(String hexString) {
		if (hexString == null) {
			throw new NullPointerException("Hex-string cannot be null.");
		} else if (!isValidHexString(hexString)) {
			throw new IllegalArgumentException(
					"Hex-string does not match expected SHA-1 hexadecimal string format.");
		}

		this.hexString = hexString;
	}

	public String getHexString() {
		return hexString;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof Revision))
			return false;

		Revision hash = (Revision) o;

		return hash.hexString.equals(hexString);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hexString.hashCode();
		return result;
	}

	public String toString() {
		return hexString;
	}

	public static boolean isValidHexString(String candidate) {
		return HEX_STRING_PATTERN.matcher(candidate).matches();
	}
}
