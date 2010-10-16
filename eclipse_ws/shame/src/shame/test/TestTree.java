package shame.test;

import org.junit.Test;

import shame.util.collections.BiGraph;

public class TestTree {
	@Test
	public void test() {
		BiGraph<Integer> tree = new BiGraph<Integer>(1);
		
		tree.addChild(2);
		tree.addChild(3).addChild(4);
		
		tree.addChild(2);
		tree.addChild(3).addChild(5);
		
		tree.addChild(6).addChild(7).addChild(8).addChild(9).addChild(10);
		
		tree.addChild(5).addChild(11);
		
		System.out.println(tree);
		
		
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
