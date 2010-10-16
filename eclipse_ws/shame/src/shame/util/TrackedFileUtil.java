package shame.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shame.model.TrackedFile;

import shame.util.collections.BiGraph;

public class TrackedFileUtil {
	private static final Comparator<List<BiGraph<TrackedFile>>> ORDER_TRACKS_BY_FIRST_COMMITTER_TIME = new Comparator<List<BiGraph<TrackedFile>>>() {
		@Override
		public int compare(List<BiGraph<TrackedFile>> o1,
				List<BiGraph<TrackedFile>> o2) {
			return minCommitterTime(o1).compareTo(minCommitterTime(o2));
		}
	};
	private static final Comparator<BiGraph<TrackedFile>> ORDER_NODES_BY_COMMITTER_TIME = new Comparator<BiGraph<TrackedFile>>() {
		@Override
		public int compare(BiGraph<TrackedFile> o1, BiGraph<TrackedFile> o2) {
			Date o1ct = o1.getData().getCommit().getCommitterTime();
			Date o2ct = o2.getData().getCommit().getCommitterTime();
			return o1ct.compareTo(o2ct);
		}
	};
	private static final Logger logger = LoggerFactory
			.getLogger(TrackedFileUtil.class);

	/**
	 * Return a <code>List</code> of graph nodes where parents always preceed
	 * children and ordered by non-ascending committer date (as much as is
	 * possible according to the first constraint).
	 * 
	 * @param root
	 *            start ordering from this node of the graph
	 * @return
	 */
	// TODO: test?
	// TODO: do we even need this? d'oh
	public static List<BiGraph<TrackedFile>> sort(BiGraph<TrackedFile> root) {
		List<BiGraph<TrackedFile>> result = new LinkedList<BiGraph<TrackedFile>>();

		// add the latest node to the result, then add its children to the list
		// of nodes to consider next
		List<BiGraph<TrackedFile>> unplaced = new LinkedList<BiGraph<TrackedFile>>();
		unplaced.add(root);
		while (unplaced.size() > 0) {
			BiGraph<TrackedFile> node = getOldestNode(unplaced);
			result.add(node);

			unplaced.remove(node);
			unplaced.addAll(node.getChildren());
		}

		return result;
	}

	private static BiGraph<TrackedFile> getOldestNode(
			Collection<BiGraph<TrackedFile>> nodes) {
		BiGraph<TrackedFile> latest = null;

		for (BiGraph<TrackedFile> node : nodes) {
			if (latest == null) {
				latest = node;
			} else if (latest.getData().getCommit().getCommitterTime()
					.before(node.getData().getCommit().getCommitterTime())) {
				latest = node;
			}
		}

		return latest;
	}

	private static Date minCommitterTime(Collection<BiGraph<TrackedFile>> nodes) {
		Date min = null;

		for (BiGraph<TrackedFile> node : nodes) {
			if (min == null
					|| min.after(node.getData().getCommit().getCommitterTime())) {
				min = node.getData().getCommit().getCommitterTime();
			}
		}

		return min;
	}

	// TODO: test this!
	public static List<List<BiGraph<TrackedFile>>> buildTracks(
			final BiGraph<TrackedFile> root) {
		List<List<BiGraph<TrackedFile>>> tracks = new ArrayList<List<BiGraph<TrackedFile>>>();

		List<BiGraph<TrackedFile>> track = new LinkedList<BiGraph<TrackedFile>>();
		track.add(root);
		tracks.add(track);

		buildTracks(root, track, tracks);

		// sort tracks in order of non-descending first committer time
		Collections.sort(tracks, ORDER_TRACKS_BY_FIRST_COMMITTER_TIME);

		return tracks;
	}

	// TODO: add comment: do depth-first search so we build entire tracks at a
	// time, then link to the first one.
	private static void buildTracks(final BiGraph<TrackedFile> node,
			final List<BiGraph<TrackedFile>> track,
			final List<List<BiGraph<TrackedFile>>> tracks) {

		List<BiGraph<TrackedFile>> children = new ArrayList<BiGraph<TrackedFile>>(
				node.getChildren());
		Collections.sort(children, ORDER_NODES_BY_COMMITTER_TIME);

		for (int i = 0, n = children.size(); i < n; i++) {
			BiGraph<TrackedFile> child = children.get(i);
			if (!isInTrack(child, tracks)) {
				if (i == 0) {
					track.add(child);

					buildTracks(child, track, tracks);
				} else {
					List<BiGraph<TrackedFile>> newTrack = new LinkedList<BiGraph<TrackedFile>>();
					newTrack.add(child);
					tracks.add(newTrack);

					buildTracks(child, newTrack, tracks);
				}
			}
		}
	}

	private static boolean isInTrack(BiGraph<TrackedFile> needle,
			List<List<BiGraph<TrackedFile>>> haystack) {
		for (List<BiGraph<TrackedFile>> track : haystack) {
			for (BiGraph<TrackedFile> node : track) {
				if (needle == node) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Build a graph from <code>TrackedFile</code>-s. There must be no cycles or
	 * orphans in the graph. There must also be a single root. The graph is
	 * built based on the hierarchy information found in the <code>Commit</code>
	 * -s.
	 * 
	 * @param files
	 * @return
	 * @throws IllegalArgumentException
	 *             if any cycle, orphan, or multiple-roots are detected
	 */
	public static BiGraph<TrackedFile> buildGraph(List<TrackedFile> files) {
		BiGraph<TrackedFile> tree = null;

		if (files.size() > 0) {
			tree = new BiGraph<TrackedFile>(files.get(0));

			Queue<BiGraph<TrackedFile>> queue = new LinkedList<BiGraph<TrackedFile>>();
			Set<BiGraph<TrackedFile>> visited = new HashSet<BiGraph<TrackedFile>>();

			queue.add(tree);
			while (queue.size() > 0) {
				BiGraph<TrackedFile> node = queue.remove();

				for (TrackedFile file : files) {
					if (node.getData().getCommit().getParents()
							.contains(file.getCommit().getRevision())) {
						queue.add(node.addParent(file));
					}
				}

				// never revisit a node
				visited.add(node);
				queue.removeAll(visited);
			}

			if (tree.getAllNodes().size() != files.size()) {
				logger.error("TrackedFile list forms disconnected graph.");
				throw new IllegalArgumentException(
						"TrackedFile list forms disconnected graph.");
			}

			List<BiGraph<TrackedFile>> roots = tree.getRoots();

			if (roots.size() == 1) {
				tree = roots.get(0);
			} else if (roots.size() == 0) {
				logger.error("TrackedFile list has no root.");
				throw new IllegalArgumentException(
						"TrackedFile list has no root.");
			} else if (roots.size() > 1) {
				logger.error("TrackedFile list has more than one root.");
				throw new IllegalArgumentException(
						"TrackedFile list has more than one root.");
			}

			// TODO: detect if any cycles?
		}

		return tree;
	}
}
