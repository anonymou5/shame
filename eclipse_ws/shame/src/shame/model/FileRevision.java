package shame.model;

import java.io.File;

public class FileRevision {
	private final File file;
	private final Revision rev;

	/**
	 * Create a new <code>FileRevision</code>.
	 * 
	 * @param file
	 * @param rev
	 * @throws NullPointerException
	 *             if any of the arguments are null
	 */
	public FileRevision(File file, Revision rev) {
		if (file == null) {
			throw new NullPointerException("File cannot be null.");
		} else if (rev == null) {
			throw new NullPointerException("Revision cannot be null.");
		}

		this.file = file.getAbsoluteFile();
		this.rev = rev;
	}

	public File getFile() {
		return file.getAbsoluteFile();
	}

	public Revision getRevision() {
		return rev;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof FileRevision))
			return false;

		FileRevision fh = (FileRevision) o;

		if (!file.equals(fh.file))
			return false;

		if (rev == null) {
			if (fh.rev != null)
				return false;
		} else if (!rev.equals(fh.rev))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + file.hashCode();
		result = prime * result + rev.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "{file=" + file + ", rev=" + rev + "}";
	}
}
