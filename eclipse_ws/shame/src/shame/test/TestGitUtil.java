package shame.test;

import java.io.File;
import java.util.List;

import org.junit.Test;

import shame.git.GitUtil;
import shame.model.TrackedFile;
import shame.util.DiffUtil;
import shame.util.TrackedFileUtil;
import shame.util.collections.BiGraph;

public class TestGitUtil {

	// TODO: uhhhhhhhhhh how do we deal with 3 parents to a node?
	// SOLUTION: left side can have a tab for each parent to diff with. For
	// bonus points, color code the lines of the (right-hand) merge result with
	// the tabs of the left hand parent tab

	@Test
	public void testGitUtil() {
		File file = new File(
				"/Users/jferland/Experiments/git-shame-testing/RenamedClass.java");
		// TODO: testing a file with merged history. Can't seem to find a file with a blame history that actually has multiple tracks.
		file = new File(
				"/Users/jferland/Experiments/git-multi-parent/branchfile");
		List<TrackedFile> trackedFiles = GitUtil.blame(file);

		BiGraph<TrackedFile> tree = TrackedFileUtil.buildGraph(trackedFiles);
		System.out.println(tree);

		System.out.println("sorted nodes");
		for (BiGraph<TrackedFile> node : TrackedFileUtil.sort(tree)) {
			System.out.println(node.getData().getCommit().getRevision());
		}

		System.out.println();
		System.out.println("tracked nodes");
		List<List<BiGraph<TrackedFile>>> tracks = TrackedFileUtil
				.buildTracks(tree);
		for (List<BiGraph<TrackedFile>> track : tracks) {
			System.out.println("new track");
			for (BiGraph<TrackedFile> node : track) {
				System.out.println(node.getData().getCommit().getRevision());
			}
		}

		String[] s1 = { "a", "s", "d", "f" };
		String[] s2 = { "d", "f" };

		System.out.println(DiffUtil.findLongestCommonSubsequence(s1, s2));
	}

	@Test
	public void testSoemthing() {
		System.out
				.println(GitUtil
						.rev(new File(
								"/Users/jferland/Experiments/git-shame-testing/RenamedClass.java")));
	}
}
