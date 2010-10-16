package shame.test;

import org.junit.Test;

public class TestFinally {
	@Test
	public void testFinally() {
		try {
			System.out.println("hrm?");
			return;
		} catch (Exception e) {
			
		}
		finally {
			System.out.println("asdf");
		}
	}
}
