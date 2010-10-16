package shame.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class Commit {
	private volatile Integer hashCode;
	private final Set<Revision> parents = new LinkedHashSet<Revision>();
	private final Revision rev;
	private final String summary;
	private final User author;
	private final Date authorTime;
	private final User committer;
	private final Date committerTime;

	public static class Builder {
		private Set<Revision> parents = new LinkedHashSet<Revision>();
		private Revision rev;
		private String summary;
		// who wrote the code
		private User.Builder author = new User.Builder();
		private Date authorTime;
		// who committed the code (e.g. could be someone who committed the
		// changes on behalf of the author)
		private User.Builder committer = new User.Builder();
		private Date committerTime;

		public Set<Revision> getParents() {
			return parents;
		}

		public void setParents(Set<Revision> parents) {
			this.parents = parents;
		}

		public Revision getRevision() {
			return rev;
		}

		public void setRevision(Revision rev) {
			this.rev = rev;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public User.Builder getAuthor() {
			return author;
		}

		public void setAuthor(User.Builder author) {
			this.author = author;
		}

		public Date getAuthorTime() {
			return authorTime;
		}

		public void setAuthorTime(Date authorTime) {
			this.authorTime = authorTime;
		}

		public User.Builder getCommitter() {
			return committer;
		}

		public void setCommitter(User.Builder committer) {
			this.committer = committer;
		}

		public Date getCommitterTime() {
			return committerTime;
		}

		public void setCommitterTime(Date committerTime) {
			this.committerTime = committerTime;
		}

		public Commit build() {
			return new Commit(parents, rev, summary, author.build(),
					authorTime, committer.build(), committerTime);
		}

		@Override
		public String toString() {
			return "{parents=" + parents + ", rev=" + rev + ", summary="
					+ summary + ", author=" + author + ", authorTime="
					+ authorTime + ", committer=" + committer
					+ ", committerTime=" + committerTime + "}";
		}
	}

	/**
	 * Create a new <code>Commit</code>.
	 * 
	 * @param parents
	 * @param rev
	 * @param summary
	 * @param author
	 * @param authorTime
	 * @param committer
	 * @param committerTime
	 * @throws NullPointerException
	 *             if any of the arguments are null
	 */
	public Commit(Set<Revision> parents, Revision rev, String summary,
			User author, Date authorTime, User committer, Date committerTime) {
		if (parents == null) {
			throw new NullPointerException("Parents can be empty but not null.");
		} else if (rev == null) {
			throw new NullPointerException("Revision cannot be null.");
		} else if (summary == null) {
			throw new NullPointerException("Summary cannot be null.");
		} else if (author == null) {
			throw new NullPointerException("Author cannot be null.");
		} else if (authorTime == null) {
			throw new NullPointerException("Author time cannot be null.");
		} else if (committer == null) {
			throw new NullPointerException("Committer cannot be null.");
		} else if (committerTime == null) {
			throw new NullPointerException("Committer time cannot be null.");
		}

		this.parents.addAll(parents);
		this.rev = rev;
		this.summary = summary;
		this.author = author;
		this.authorTime = authorTime;
		this.committer = committer;
		this.committerTime = committerTime;
	}

	public Set<Revision> getParents() {
		return new LinkedHashSet<Revision>(parents);
	}

	public Revision getRevision() {
		return rev;
	}

	public String getSummary() {
		return summary;
	}

	public User getAuthor() {
		return author;
	}

	public Date getAuthorTime() {
		return authorTime;
	}

	public User getCommitter() {
		return committer;
	}

	public Date getCommitterTime() {
		return committerTime;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof Commit))
			return false;

		Commit commit = (Commit) o;

		return commit.rev.equals(rev);
	}

	@Override
	public int hashCode() {
		if (hashCode == null) {
			int result = 23;
			result = 41 * result + rev.hashCode();

			hashCode = new Integer(result);
		}

		return hashCode.intValue();
	}

	@Override
	public String toString() {
		return "{parents=" + parents + ", rev=" + rev + ", summary=" + summary
				+ ", author=" + author + ", authorTime=" + authorTime
				+ ", committer=" + committer + ", committerTime="
				+ committerTime + "}";
	}
}
