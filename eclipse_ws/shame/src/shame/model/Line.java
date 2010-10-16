package shame.model;

public class Line {
	// keep track of where each line came from
	private final FileRevision fileRev;
	// actual line
	private final String data;

	/**
	 * Create a new <code>Line</code>.
	 * 
	 * @param fileRev
	 * @param data
	 * @throws NullPointerException
	 *             if any arguments are null
	 */
	public Line(FileRevision fileRev, String data) {
		if (fileRev == null) {
			throw new NullPointerException("FileRevision cannot be null.");
		} else if (data == null) {
			throw new NullPointerException(
					"String data can be empty, but not null.");
		}

		this.fileRev = fileRev;
		this.data = data;
	}

	public FileRevision getFileRevision() {
		return fileRev;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		return "{fileHash=" + fileRev + ", data=" + data + "}";
	}
}
