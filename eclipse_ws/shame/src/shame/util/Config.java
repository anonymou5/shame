package shame.util;

import java.util.Properties;

// TODO: add methods to write to and read from disk
public enum Config { // easy way to guarantee singleton
	INSTANCE;

	private static final String KEY_GIT_EXEC = "git.exec";
	private static final String DEFAULT_GIT_EXEC = "/usr/local/git/bin/git";

	private Properties props = new Properties();

	public String getGitExec() {
		return props.getProperty(KEY_GIT_EXEC, DEFAULT_GIT_EXEC);
	}

	public static Config getInstance() {
		return Config.INSTANCE;
	}
}
