package shame.util;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.Writer;

/**
 * Works with <code>ProcessUtil</code> to provide methods that are called when
 * data from a <code>Process</code> can be read or written.
 * 
 * @see shame.util.ProcessUtil
 * @author jferland
 * 
 */
public interface ProcessIO {
	/**
	 * Hook to read <code>Process</code> output.
	 * 
	 * @param reader
	 *            where data can be read from
	 * @return should return <code>true</code> if you would like to continue to
	 *         read, otherwise return <code>false</code> when you are forever
	 *         done reading from the <code>Process</code>
	 * @throws IOException
	 */
	public boolean read(BufferedReader reader) throws IOException;

	/**
	 * Hook to write <code>Process</code> input.
	 * 
	 * @param writer
	 *            where data can be written to
	 * @return should return <code>true</code> if you would like to continue to
	 *         write, otherwise return <code>false</code> when you are forever
	 *         done writing to the <code>Process</code>
	 * @throws IOException
	 */
	public boolean write(Writer writer) throws IOException;
}
