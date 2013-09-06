package org.jrenner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static org.jrenner.Master.*;

public class MapTests {
	public static int loopCount = 1000;
	private static boolean runningTests = false;
	public static Thread testRunnerThread = null;
	private static ObjectMap<String, Float> putRecords = new ObjectMap<>();
	private static ObjectMap<String, Float> getRecords = new ObjectMap<>();
	private static ObjectMap<ObjectMap, String> recordNames = new ObjectMap<>();
	private static ObjectMap<String, Float> workingMap; // used for comparison
	static {
		recordNames.put(putRecords, "put");
		recordNames.put(getRecords, "get");
	}

	public static void hashMapTest() {
		System.gc();
		Map<DummyObject, Integer> map = new HashMap<>();
		String testName = map.getClass().getSimpleName();
		int i = 0;
		PerformanceCounter perf = new PerformanceCounter(testName);
		while (i < loopCount) {
			perf.start();
			i++;
			String name = "dummy " + i;
			DummyObject dummy = new DummyObject(rand.nextFloat() * 10000, rand.nextFloat() * 10000, name);
			Integer someValue = rand.nextInt(100);
			map.put(dummy, someValue);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "put test");
		printHeap();;

		perf.reset();
		int x;
		for (DummyObject dummy : map.keySet()) {
			perf.start();
			x = map.get(dummy);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "get test");
		System.gc();
	}

	public static void arrayMapTest() {
		System.gc();
		ArrayMap<DummyObject, Integer> map = new ArrayMap<>();
		String testName = map.getClass().getSimpleName();
		int i = 0;
		PerformanceCounter perf = new PerformanceCounter(testName);
		while (i < loopCount) {
			perf.start();
			i++;
			String name = "dummy " + i;
			DummyObject dummy = new DummyObject(rand.nextFloat() * 10000, rand.nextFloat() * 10000, name);
			Integer someValue = rand.nextInt(100);
			map.put(dummy, someValue);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "put test");
		printHeap();;

		perf.reset();
		int x;
		for (DummyObject dummy : map.keys()) {
			perf.start();
			x = map.get(dummy);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "get test");
		System.gc();
	}


	public static void objectMapTest() {
		System.gc();
		ObjectMap<DummyObject, Integer> map = new ObjectMap<>();
		String testName = map.getClass().getSimpleName();
		int i = 0;
		PerformanceCounter perf = new PerformanceCounter(testName);
		while (i < loopCount) {
			perf.start();
			i++;
			String name = "dummy " + i;
			DummyObject dummy = new DummyObject(rand.nextFloat() * 10000, rand.nextFloat() * 10000, name);
			Integer someValue = rand.nextInt(100);
			map.put(dummy, someValue);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "put test");
		printHeap();;

		perf.reset();
		int x;
		for (DummyObject dummy : map.keys()) {
			perf.start();
			x = map.get(dummy);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "get test");
		System.gc();
	}

	public static void identityMapTest() {
		System.gc();
		IdentityMap<DummyObject, Integer> map = new IdentityMap<>();
		String testName = map.getClass().getSimpleName();
		int i = 0;
		PerformanceCounter perf = new PerformanceCounter(testName);
		while (i < loopCount) {
			perf.start();
			i++;
			String name = "dummy " + i;
			DummyObject dummy = new DummyObject(rand.nextFloat() * 10000, rand.nextFloat() * 10000, name);
			Integer someValue = rand.nextInt(100);
			map.put(dummy, someValue);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "put test");
		printHeap();

		perf.reset();
		int x;
		for (DummyObject dummy : map.keys()) {
			perf.start();
			x = map.get(dummy);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "get test");
		System.gc();
	}

	public static void longMapTest() {
		System.gc();
		LongMap<DummyObject> map = new LongMap<>();
		String testName = map.getClass().getSimpleName();
		int i = 0;
		PerformanceCounter perf = new PerformanceCounter(testName);
		while (i < loopCount) {
			perf.start();
			i++;
			String name = "dummy " + i;
			DummyObject dummy = new DummyObject(rand.nextFloat() * 10000, rand.nextFloat() * 10000, name);
			Integer someValue = rand.nextInt(100);
			map.put(dummy.longCode, dummy);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "put test");
		printHeap();

		perf.reset();
		DummyObject dum;
		for (long j = 0; j < loopCount; j++) {
			perf.start();
			dum = map.get(j);
			perf.stop();
			perf.tick();
		}
		processResults(perf, "get test");
		System.gc();
	}

	public static void printHeap() {
		boolean disabled = true;
		if (disabled) return;
		double heapSize = (double) Gdx.app.getJavaHeap() / 1000000.0;
		print(String.format("Java heap: %.2fM", heapSize));

		double nativeHeapSize = (double) Gdx.app.getNativeHeap() / 1000000.0;
		if (Math.abs(heapSize - nativeHeapSize) > 10) {
			print(String.format("Native heap: %.2fM", nativeHeapSize));
		}
	}

	public static void processResults(PerformanceCounter perf, String testName) {
		String name = String.format("Finished %22s", perf.name + " " + testName);
		String avg = String.format("average time: %.5fms", perf.time.average * 1000);
		String report = name + " - " + avg;
		updateStatus(report);
		print(report);

		if (testName.contains("put")) {
			putRecords.put(perf.name, perf.time.average * 1000);
		} else if (testName.contains("get")) {
			getRecords.put(perf.name, perf.time.average * 1000);
		} else {
			throw new RuntimeException("unhandled test name in processResults: " + testName);
		}
	}

	public static void runAllTests() {

		if (runningTests) {
			return;
		}
		testRunnerThread = new Thread(testRunner);
		testRunnerThread.start();
	}

	private static Runnable testRunner = new Runnable() {
		@Override
		public void run() {
			updateStatus("running " + loopCount + " iterations for each test");
			runningTests = true;
			long start = System.nanoTime();
			hashMapTest();
			arrayMapTest();
			objectMapTest();
			identityMapTest();
			longMapTest();
			long duration = System.nanoTime() - start;
			float timeTaken = (float) duration / 1000000f;

			updateStatus(String.format("Total time taken for all tests: %.1fms", timeTaken));
			reportSortedResults(putRecords);
			updateStatus("--------------------");
			reportSortedResults(getRecords);
			getRecords.clear();
			putRecords.clear();
			runningTests = false;
		}
	};

	private static void updateStatus(String status) {
		Master.addTextLine(status);
	}

	private static Comparator<String> resultsTimeComp = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			Float t1 = workingMap.get(o1);
			Float t2 = workingMap.get(o2);
			print(String.format("t1: %f, t2: %f", t1, t2));
			long t1long = (long) (t1 * 10000);
			long t2long = (long) (t2 * 10000);
			print(String.format("t1long: %d, t2long: %d", t1long, t2long));
			print("final return: " + (int) (t1long - t2long));
			return (int) (t1long - t2long);
		}
	};

	private static Array<String> getSortedTestResults(ObjectMap<String, Float> map) {
		workingMap = map;
		print("workingMap: " + recordNames.get(workingMap));
		Array<String> tests = new Array<>();
		for (String test : map.keys()) {
			tests.add(test);
		}
		tests.sort(resultsTimeComp);
		return tests;
	}

	private static void reportSortedResults(ObjectMap<String, Float> map) {
		Array<String> sortedResults = getSortedTestResults(map);
		String mapName = recordNames.get(map);
		updateStatus(String.format("Scores for %s, fastest first", mapName));
		int i = 1;
		for (String test : sortedResults) {
			String place = String.format("[%d]", i++);
			String name = String.format(".%22s", test + " " + recordNames.get(map));
			String total = String.format("average: %.5fms", map.get(test));
			updateStatus(place + " " + name + " " + total);
		}
	}
}
