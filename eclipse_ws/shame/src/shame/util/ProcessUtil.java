package shame.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtil {
	public static final int EXIT_CODE_OK = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(ProcessUtil.class);
	private static final int EXIT_CODE_ERR = -1;

	/**
	 * Straight pass-through to <code>exec(Process, ProcessIO)</code>.
	 * 
	 * @param command
	 *            a specified system command.
	 * @param dir
	 *            the working directory of the subprocess, or null if the
	 *            subprocess should inherit the working directory of the current
	 *            process.
	 * @param pio
	 *            <code>ProcessIO</code> to handle reading and writing. Use
	 *            <code>null</code> if you do not need to do any reading or
	 *            writing.
	 * @return
	 */
	public static int exec(String command, File dir, ProcessIO pio) {
		int exitCode = EXIT_CODE_ERR;

		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command.toString(), null, dir);
			exitCode = ProcessUtil.exec(p, pio);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return exitCode;
	}

	/**
	 * Execute the given <code>Process</code>. Calls <code>read()</code> on the
	 * given <code>ProcessIO</code> implementation when there is a chance to
	 * read. Similary, calls <code>write()</code> on the <code>ProcessIO</code>
	 * when there is an opportunity to write.
	 * 
	 * Will wait for the <code>Process</code> to finish.
	 * 
	 * @see shame.util.ProcessIO
	 * @param p
	 *            <code>Process</code> to execute
	 * @param pio
	 *            <code>ProcessIO</code> to handle reading and writing. Use
	 *            <code>null</code> if you do not need to do any reading or
	 *            writing.
	 * @return exit code of the <code>Process</code> execution
	 */
	public static int exec(Process p, ProcessIO pio) {
		int exitCode = EXIT_CODE_ERR;

		try {
			InputStream in = null;
			Reader reader = null;
			BufferedReader bufferedReader = null;
			Writer writer = null;
			OutputStream out = null;

			if (pio != null) {
				in = new BufferedInputStream(p.getInputStream());
				reader = new InputStreamReader(in);
				bufferedReader = new BufferedReader(reader);

				out = p.getOutputStream();
				writer = new OutputStreamWriter(out);

				while (pio.read(bufferedReader) || pio.write(writer)) {
				}
			}

			try {
				exitCode = p.waitFor();
			} catch (InterruptedException e) {
				logger.error("Unable to wait for Process termination: "
						+ e.getMessage());
			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (in != null) {
					in.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (out != null) {
					out.close();
				}
			}
		} catch (IOException e) {
			logger.error("Process execution error: " + e.getMessage());
		}

		return exitCode;
	}
}
