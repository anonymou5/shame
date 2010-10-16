package shame.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shame.model.Commit;
import shame.model.FileRevision;
import shame.model.Line;
import shame.model.TrackedFile;
import shame.model.Revision;
import shame.util.Config;
import shame.util.DateUtil;
import shame.util.Holder;
import shame.util.ProcessIO;
import shame.util.ProcessUtil;

public class GitUtil {
	private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s");
	private static final Logger logger = LoggerFactory.getLogger(GitUtil.class);

	public static List<TrackedFile> blame(File file) {
		Revision rev = rev(file); // new Revision("");
		FileRevision fileRev = new FileRevision(file, rev);
		return blame(fileRev);
	}

	public static List<TrackedFile> blame(FileRevision topFileRev) {
		List<TrackedFile> trackedFiles = new ArrayList<TrackedFile>();

		Set<FileRevision> filesRead = new HashSet<FileRevision>();
		Queue<FileRevision> queue = new LinkedList<FileRevision>();
		queue.add(topFileRev);
		while (!queue.isEmpty()) {
			FileRevision fileRev = queue.remove();

			TrackedFile trackedFile = blameOnce(fileRev, queue);

			if (trackedFile != null) {
				trackedFiles.add(trackedFile);
			} else {
				logger.error("Unable to blame file: " + topFileRev);
			}

			filesRead.add(fileRev);

			// don't lookup files we've already read
			queue.removeAll(filesRead);
		}

		return trackedFiles;
	}

	private static TrackedFile blameOnce(final FileRevision fileRev,
			final Queue<FileRevision> queue) {

		File file = fileRev.getFile();

		final Holder<TrackedFile> trackedFile = new Holder<TrackedFile>(null);

		// `git blame -p <rev> -- <file>`
		StringBuilder command = new StringBuilder();
		command.append(Config.getInstance().getGitExec());
		command.append(" blame -p ");
		command.append(fileRev.getRevision());
		command.append(" -- ");
		command.append(file.getPath());

		ProcessUtil.exec(command.toString(), file.getParentFile(),
				new ProcessIO() {
					public boolean read(BufferedReader r) throws IOException {
						TrackedFile tmp = parseTrackedFile(fileRev, r, queue);
						trackedFile.setValue(tmp);
						return false;
					}

					public boolean write(Writer writer) throws IOException {
						return false;
					}
				});

		return trackedFile.getValue();
	}

	private static TrackedFile parseTrackedFile(final FileRevision fileRev,
			BufferedReader bufferedreader, Queue<FileRevision> queue) {
		// a context for filenames coming from blame
		File parentFile = fileRev.getFile().getParentFile();

		TrackedFile trackedFile = null;

		try {
			List<Line> lines = new ArrayList<Line>();

			Revision currentRev = null;
			FileRevision currentFileRev = null;

			Map<Revision, FileRevision> fileRevMap = new HashMap<Revision, FileRevision>();

			String line;
			while ((line = bufferedreader.readLine()) != null) {
				if (line.length() > 0 && line.substring(0, 1).equals("\t")) {
					lines.add(new Line(currentFileRev, line.substring(1)));
				} else {
					Scanner scanner = new Scanner(line);
					if (scanner.hasNext()) {
						String label = scanner.next();

						if ("previous".equals(label)) {
							String hexString = scanner.next().trim();
							Revision prevRev = new Revision(hexString);

							String prevFilename = scanner.next().trim();
							FileRevision prevFileRev = new FileRevision(
									new File(parentFile, prevFilename), prevRev);

							// another potential revision to look for
							queue.add(prevFileRev);
						} else if ("filename".equals(label)) {
							String filename = scanner.nextLine().trim();
							File file = new File(parentFile, filename);

							if (!fileRevMap.containsKey(currentRev)) {
								currentFileRev = new FileRevision(file,
										currentRev);
								fileRevMap.put(currentRev, currentFileRev);
							}
						} else if (Revision.isValidHexString(label)) {
							currentRev = new Revision(label);
							currentFileRev = fileRevMap.get(currentRev);
						}
					}
				}
			}

			// look for any revision we came across
			queue.addAll(fileRevMap.values());

			Commit commit = log(parentFile, fileRev.getRevision());

			trackedFile = new TrackedFile(fileRev.getFile(), commit, lines);
		} catch (IOException e) {
			logger.error("Unable to parse TrackedFile: " + e.getMessage());
		}

		return trackedFile;
	}

	public static Revision rev(File file) {
		// `git log -1 --format=%H <file>`
		StringBuilder command = new StringBuilder();
		command.append(Config.getInstance().getGitExec());
		command.append(" log -1 --format=%H -- ");
		command.append(file.getPath());

		final Holder<Revision> rev = new Holder<Revision>(null);

		ProcessUtil.exec(command.toString(), file.getParentFile(),
				new ProcessIO() {
					public boolean read(BufferedReader r) throws IOException {
						String line = r.readLine();
						Revision tmp = new Revision(line);
						rev.setValue(tmp);
						return false;
					}

					public boolean write(Writer writer) throws IOException {
						return false;
					}
				});

		return rev.getValue();
	}

	public static Commit log(File dir, Revision rev) {
		// `git log -1 --format=<format> <rev>`
		StringBuilder command = new StringBuilder();
		command.append(Config.getInstance().getGitExec());
		command.append(" log -1 --format=");
		command.append("%H%n"); // full hash
		command.append("%P%n"); // parent hashes
		command.append("%an%n"); // author name
		command.append("%ae%n"); // author email
		command.append("%ai%n"); // author time (ISO 8601 format)
		command.append("%cn%n"); // committer name
		command.append("%ce%n"); // committer email
		command.append("%ci%n"); // committer time
		command.append("%B"); // raw body (unwrapped subject and body)
		// TODO: read commit notes?
		// command.append("%x00"); // %x00: print a byte from a hex code
		// command.append("%N"); // commit notes
		command.append(" ");
		command.append(rev.getHexString());

		final Commit.Builder builder = new Commit.Builder();

		ProcessUtil.exec(command.toString(), dir, new ProcessIO() {
			public boolean read(BufferedReader r) throws IOException {

				String line;

				// hash
				line = r.readLine();
				Revision rev = new Revision(line);
				builder.setRevision(rev);

				// parent hashes
				for (String s : WHITE_SPACE_PATTERN.split(r.readLine())) {
					rev = new Revision(s);
					builder.getParents().add(rev);
				}

				// author
				builder.getAuthor().setName(r.readLine());
				builder.getAuthor().setMail(r.readLine());

				line = r.readLine();
				try {
					builder.setAuthorTime(DateUtil.ISO8601_FORMAT.parse(line));
				} catch (ParseException e) {
					logger.error("Cannot parse author date '" + line + "': "
							+ e.getMessage());
				}

				// committer
				builder.getCommitter().setName(r.readLine());
				builder.getCommitter().setMail(r.readLine());

				line = r.readLine();
				try {
					builder.setCommitterTime(DateUtil.ISO8601_FORMAT
							.parse(line));
				} catch (ParseException e) {
					logger.error("Cannot parse committer date '" + line + "': "
							+ e.getMessage());
				}

				int character;
				StringBuilder summary = new StringBuilder();

				// raw body
				while ((character = r.read()) != -1) {
					summary.append((char) character);
				}

				builder.setSummary(summary.toString());

				return false;
			}

			public boolean write(Writer writer) throws IOException {
				return false;
			}
		});

		return builder.build();
	}
}
