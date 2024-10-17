package telis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DiscountOptimizationTest {
	
	static final double[] DISCOUNTS = new double[] {0, 0.05, 0.1, 0.2, 0.25};
	
	static final DiscountOptimizer DISCOUNTOPTIMIZER = 
			new DiscountOptimizer(8.00, DISCOUNTS, 5);
	
	static final String SET_RESULT_STRING = "Set %s passes: %b %n";
	static final String VALUE_RESULT_STRING = "Value %s passes: %b %n";
	static final String VALUE_TEST_FAILS = """
			The expected output was %.2f, but the actual is %.2f""";
	
	static final Set<Integer> RESULT_SET_1 = new HashSet<>(Arrays.asList(1));
	
	static final Set<Integer> RESULT_SET_123 
		= new HashSet<>(Arrays.asList(1,2,3));
	
	static final Set<Integer> RESULT_SET_12 = new HashSet<>(Arrays.asList(1,2));
	
	static final Set<Integer> RESULT_SET_12345 
		= new HashSet<>(Arrays.asList(1,2,3,4,5));
	
	static final Set<Integer> RESULT_SET_1234 
		= new HashSet<>(Arrays.asList(1,2,3,4));
	
	static final Set<Integer> RESULT_SET_1235 
		= new HashSet<>(Arrays.asList(1,2,3,5));

	static final Set<Integer> RESULT_SET_1345 
		= new HashSet<>(Arrays.asList(1,3,4,5));
	
	static final Set<Integer> RESULT_SET_2345 
		= new HashSet<>(Arrays.asList(2,3,4,5));
	
	static final Set<Integer> RESULT_SET_5 = new HashSet<>(Arrays.asList(5));
	
	static final Set<Integer> RESULT_SET_45 = new HashSet<>(Arrays.asList(4,5));
	
	public static void main(String[] args) {
		
		test10000();
		test30000();
		test11100();
		test22211();
		test11111();
		test33333();
		test33332();
		test33321();
		test11222();
		test54321();
		test12345();
		testNull();
		testEmpty();
	}
	
	private static void test10000() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 1);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("Test10000", resultList.size() == 1 
				&& resultList.get(0).equals(RESULT_SET_1));
		
		printValueResult("Test10000", new BigDecimal("8.00"), 
			DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test11100() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 1);
		availabilityMap.put(2, 1);
		availabilityMap.put(3, 1);
		availabilityMap.put(4, 0);
		availabilityMap.put(5, 0);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("test33300", resultList.size() == 1 
				&& resultList.get(0).equals(RESULT_SET_123));
		
		printValueResult("Test33300", new BigDecimal("21.60"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test33332() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 3);
		availabilityMap.put(2, 3);
		availabilityMap.put(3, 3);
		availabilityMap.put(4, 3);
		availabilityMap.put(5, 2);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("test33332", 
			resultList.size() == 3 && resultList.get(0).equals(RESULT_SET_1234)
			&& resultList.get(1).equals(resultList.get(2)) 
			&& resultList.get(1).equals(RESULT_SET_12345));
		
		printValueResult("Test33332", new BigDecimal("85.60"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test33321() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 3);
		availabilityMap.put(2, 3);
		availabilityMap.put(3, 3);
		availabilityMap.put(4, 2);
		availabilityMap.put(5, 1);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("test33321", resultList.size() == 3 
				&& resultList.get(0).equals(RESULT_SET_1234) 
				&& resultList.get(0).equals(resultList.get(2)) 
				&& resultList.get(1).equals(RESULT_SET_1235));
		
		printValueResult("Test33321", new BigDecimal("76.80"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test30000() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 3);
		availabilityMap.put(2, 0);
		availabilityMap.put(3, 0);
		availabilityMap.put(4, 0);
		availabilityMap.put(5, 0);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		boolean resultBool = 
				resultList.stream().anyMatch(entry -> !entry.equals(RESULT_SET_1));
		
		printSetResult("Test30000", resultList.size() == 3 && !resultBool);
		
		printValueResult("Test30000", new BigDecimal("24.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test22211() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 2);
		availabilityMap.put(2, 2);
		availabilityMap.put(3, 2);
		availabilityMap.put(4, 1);
		availabilityMap.put(5, 1);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("Test22211", resultList.contains(RESULT_SET_1234) 
			&& resultList.contains(RESULT_SET_1235));
		
		printValueResult("Test22211", new BigDecimal("51.20"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test11222() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 1);
		availabilityMap.put(2, 1);
		availabilityMap.put(3, 2);
		availabilityMap.put(4, 2);
		availabilityMap.put(5, 2);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);

		printSetResult("Test11222", resultList.size() == 2 
				&& resultList.contains(RESULT_SET_1345) 
				&& resultList.contains(RESULT_SET_2345));
		
		printValueResult("Test11222", new BigDecimal("51.20"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test11111() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 1);
		availabilityMap.put(2, 1);
		availabilityMap.put(3, 1);
		availabilityMap.put(4, 1);
		availabilityMap.put(5, 1);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("Test11111", resultList.size() == 1 
				&& resultList.get(0).equals(RESULT_SET_12345));
		
		printValueResult("Test30000", new BigDecimal("30.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test33333() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 3);
		availabilityMap.put(2, 3);
		availabilityMap.put(3, 3);
		availabilityMap.put(4, 3);
		availabilityMap.put(5, 3);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		boolean resultBool = 
			resultList.stream().anyMatch(entry -> !entry.equals(RESULT_SET_12345));
		
		printSetResult("Test33333", resultList.size() == 3 && !resultBool);
		
		printValueResult("Test30000", new BigDecimal("90.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test54321() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 5);
		availabilityMap.put(2, 4);
		availabilityMap.put(3, 3);
		availabilityMap.put(4, 2);
		availabilityMap.put(5, 1);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		long count = 
			resultList.stream().filter(entry -> entry.equals(RESULT_SET_1234)).count();
		
		printSetResult("Test54321", resultList.size() == 5 
			&& resultList.contains(RESULT_SET_1235) 
			&& count == 2 && resultList.contains(RESULT_SET_1)
			&& resultList.contains(RESULT_SET_12));
		
		printValueResult("Test54321", new BigDecimal("100.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void test12345() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		availabilityMap.put(1, 1);
		availabilityMap.put(2, 2);
		availabilityMap.put(3, 3);
		availabilityMap.put(4, 4);
		availabilityMap.put(5, 5);
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		long count = 
			resultList.stream().filter(entry -> entry.equals(RESULT_SET_2345)).count();
		
		printSetResult("Test12345", resultList.size() == 5 
			&& resultList.contains(RESULT_SET_1345) 
			&& count == 2 && resultList.contains(RESULT_SET_5)
			&& resultList.contains(RESULT_SET_45));
		
		printValueResult("Test12345", new BigDecimal("100.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void testNull() {
		
		List<Set<Integer>> resultList = DiscountOptimizer.purchaseAllUnits(null);
		
		printSetResult("TestNull", resultList.isEmpty());
		
		printValueResult("TestNull", new BigDecimal("0.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void testEmpty() {
		
		Map<Integer,Integer> availabilityMap = new HashMap<>();
		
		List<Set<Integer>> resultList = 
				DiscountOptimizer.purchaseAllUnits(availabilityMap);
		
		printSetResult("TestEmpty", resultList.isEmpty());
		
		printValueResult("TestEmpty", new BigDecimal("0.00"), 
				DiscountOptimizer.calculateTotalSum(resultList));
	}
	
	private static void printSetResult(String testName, boolean result) {
		
		System.out.printf(SET_RESULT_STRING, testName, result);
	}
	
	private static void printValueResult(String testName, BigDecimal expected, 
			BigDecimal actual) {
		
		System.out.printf(VALUE_RESULT_STRING, testName, expected.equals(actual));
		
		if(!expected.equals(actual)) {
			
			System.out.printf(VALUE_TEST_FAILS, expected.doubleValue(), 
				actual.doubleValue());
		}
	}
}
