package shame.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrackedFile {
	private final File file;
	private final Commit commit;
	private final List<Line> lines = new ArrayList<Line>();

	/**
	 * Create a new <code>TrackedFile</code>.
	 * 
	 * @param file
	 * @param commit
	 * @param lines
	 * @throws NullPointerException
	 *             if any argument is null
	 */
	public TrackedFile(File file, Commit commit, List<Line> lines) {
		if (file == null) {
			throw new NullPointerException("File cannot be null.");
		} else if (commit == null) {
			throw new NullPointerException("Commit cannot be null.");
		} else if (lines == null) {
			throw new NullPointerException("Line-s can be empty, but not null.");
		}

		this.file = file;
		this.commit = commit;
		this.lines.addAll(lines);
	}

	public File getFile() {
		return file;
	}

	public Commit getCommit() {
		return commit;
	}

	public List<Line> getLines() {
		return new ArrayList<Line>(lines);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + commit.hashCode();
		result = prime * result + file.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		TrackedFile other = (TrackedFile) obj;

		if (!commit.equals(other.commit))
			return false;
		if (!file.equals(other.file))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "{file=" + file + ", commit=" + commit + ", lines=" + lines
				+ "}";
	}
}