
import testrun.SampleTests;
import testrun.Test;
import testrun.TestResult;
import testrun.TestRunner;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Передаем класс SampleTests в наш метод
        Map<TestResult, List<Test>> results = TestRunner.runTests(SampleTests.class);
        System.out.println("");
        // Выводим результаты в консоль
        System.out.println("SUCCESS: " + results.get(TestResult.SUCCESS));
        System.out.println("FAILED: " + results.get(TestResult.FAILED));
        System.out.println("ERROR: " + results.get(TestResult.ERROR));
        System.out.println("SKIPPED: " + results.get(TestResult.SKIPPED));

        System.out.println(results);
    }
}