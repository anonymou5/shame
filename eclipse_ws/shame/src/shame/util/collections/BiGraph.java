package shame.util.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import shame.util.Holder;

// TODO: although this seems correct, confirm with tests?
// TODO: make this a builder in an immutable parent class?
public class BiGraph<T> {
	private static final int FIRST_ID = 1;
	private static final int ID_INCREMENT = 1;

	private final Comparator<BiGraph<T>> ORDER_BY_ID = new Comparator<BiGraph<T>>() {
		@Override
		public int compare(BiGraph<T> o1, BiGraph<T> o2) {
			return o1.id - o2.id;
		}
	};

	// share a directory of data to Tree<T> and a "pointer" to the next id
	private final Map<T, BiGraph<T>> directory;
	private final Holder<Integer> nextId;

	private final T data;
	private final Collection<BiGraph<T>> children = new LinkedList<BiGraph<T>>();
	private final Collection<BiGraph<T>> parents = new LinkedList<BiGraph<T>>();
	private final int id; // for identifying with toString()

	public BiGraph(T data) {
		this(data, new HashMap<T, BiGraph<T>>(), new Holder<Integer>(FIRST_ID));
	}

	/**
	 * Should use this internal constructor to maintain shared data between
	 * <code>Tree</code>-s added within this <code>Tree</code>.
	 * 
	 * @param data
	 *            node data
	 * @param directory
	 *            <code>Map</code> of data to its <code>Tree&lt;T&gt;</code>
	 * @param nextId
	 *            "pointer" to the next identifier
	 */
	private BiGraph(T data, Map<T, BiGraph<T>> directory, Holder<Integer> nextId) {
		if (data == null) {
			throw new NullPointerException();
		}

		this.data = data;

		this.directory = directory;
		directory.put(data, this);

		id = nextId.getValue();

		this.nextId = nextId;
		nextId.setValue(id + ID_INCREMENT);
	}

	/**
	 * Fetch the existing &lt;T&gt; data from the <code>Tree&lt;T&gt;</code> or
	 * create a new one, add it to the <code>Tree&lt;T&gt;</code> and return it.
	 * 
	 * @param data
	 * @return
	 */
	private BiGraph<T> addData(T data) {
		BiGraph<T> tree = directory.get(data);
		if (tree == null) {
			tree = new BiGraph<T>(data, directory, nextId);
		}
		return tree;
	}

	public T getData() {
		return data;
	}

	public BiGraph<T> addChild(T child) {
		BiGraph<T> tree = addData(child);
		children.add(tree);
		tree.parents.add(this);
		return tree;
	}

	public Collection<BiGraph<T>> getChildren() {
		return new LinkedList<BiGraph<T>>(children);
	}

	public BiGraph<T> addParent(T parent) {
		BiGraph<T> tree = addData(parent);
		parents.add(tree);
		tree.children.add(this);
		return tree;
	}

	public Collection<BiGraph<T>> getParents() {
		return new LinkedList<BiGraph<T>>(parents);
	}

	public Collection<BiGraph<T>> getAllNodes() {
		return new LinkedList<BiGraph<T>>(directory.values());
	}

	/**
	 * Get a <code>List</code> of all the <code>Tree&lt;T&gt;</code>-s connected
	 * to this <code>Tree&lt;T&gt;</code> that have no parents.
	 * 
	 * @return
	 */
	public List<BiGraph<T>> getRoots() {
		List<BiGraph<T>> roots = new LinkedList<BiGraph<T>>();
		for (BiGraph<T> node : directory.values()) {
			if (node.parents.size() == 0) {
				roots.add(node);
			}
		}
		return roots;
	}

	@Override
	public String toString() {
		int numNodes = directory.size();
		int idLength = nextId.getValue().toString().length();
		int capacity = (int) Math.pow(((numNodes + idLength) * 2), 2);
		StringBuilder sb = new StringBuilder(capacity);

		String rowIdFormatString = "%" + idLength + "d";

		List<BiGraph<T>> nodes = new LinkedList<BiGraph<T>>(directory.values());
		Collections.sort(nodes, ORDER_BY_ID);

		// legend
		for (BiGraph<T> node : nodes) {
			sb.append(node.id);
			sb.append(": ");
			sb.append(node.data);
			sb.append("\r\n");
		}

		sb.append("\r\n");

		// column ids
		for (int i = 0; i < idLength; i++) {
			for (int j = 0; j <= idLength; j++) {
				sb.append(" ");
			}
			for (BiGraph<T> node : nodes) {
				sb.append(String.format(rowIdFormatString, node.id).substring(
						i, i + 1));
				sb.append(" ");
			}
			sb.append("\r\n");
		}

		// table and row ids
		for (BiGraph<T> row : nodes) {
			sb.append(String.format(rowIdFormatString, row.id));
			sb.append(" ");
			for (BiGraph<T> col : nodes) {
				if (row.children.contains(col) || col.children.contains(row)) {
					sb.append("x");
				} else {
					sb.append(".");
				}
				sb.append(" ");
			}
			sb.append("\r\n");
		}

		return sb.toString();
	}
}