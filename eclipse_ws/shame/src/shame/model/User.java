package shame.model;

public class User {
	private String name;
	private String mail;

	public static class Builder {
		private String name;
		private String mail;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public User build() {
			return new User(name, mail);
		}
	}

	public User(String name, String mail) {
		if (name == null) {
			throw new NullPointerException("Name can be empty, but not null.");
		} else if (mail == null) {
			throw new NullPointerException("Mail can be empty, but not null.");
		}

		this.name = name;
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public String getMail() {
		return mail;
	}

	public String toString() {
		return name + " <" + mail + ">";
	}
}
