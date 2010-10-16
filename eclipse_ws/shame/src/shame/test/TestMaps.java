package shame.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TestMaps {
	@Test
	public void testHashMap() {
		Map<Mutable, Mutable> map = new HashMap<Mutable, Mutable>();
		Mutable key = new Mutable();
		key.a = 1;
		map.put(key, key);
		System.out.println(map.containsKey(key));
		key.a = 2;
		Mutable key2 = new Mutable();
		key2.a = 2;
		System.out.println(map.containsKey(key2));
		System.out.println(map.containsKey(key));
	}
	
	@Test
	public void testHashSet() {
		Set<Mutable> set = new HashSet<Mutable>();
		Mutable v = new Mutable();
		v.a = 1;
		set.add(v);
		System.out.println(set.contains(v));
		v.a = 2;
		System.out.println(set.contains(v));
		v.a = 1;
		System.out.println(set.contains(v));
	}
	
	public static class Mutable {
		public int a;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + a;
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
			Mutable other = (Mutable) obj;
			if (a != other.a)
				return false;
			return true;
		}
	}
}
