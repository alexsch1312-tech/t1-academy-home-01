package testrun;

import exception.BadTestClassError;
import exception.TestAssertionError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {

    public static Map<TestResult, List<Test>> runTests(Class<?> c) {
        System.out.println("---Starting tests---");
        List<Test> methodTest = new ArrayList<>();
        List<Test> methodBeforeEach = new ArrayList<>();
        List<Test> methodAfterEach = new ArrayList<>();
        List<Test> methodBeforeSuite = new ArrayList<>();
        List<Test> methodAfterSuite = new ArrayList<>();

        Map<TestResult, List<Test>> report = new HashMap<>();
        report.put(TestResult.SUCCESS, new ArrayList<>());
        report.put(TestResult.FAILED, new ArrayList<>());
        report.put(TestResult.ERROR, new ArrayList<>());
        report.put(TestResult.SKIPPED, new ArrayList<>());

        try {
            Object testObject = c.getDeclaredConstructor().newInstance();

            System.out.println("---Create Lists Test---");
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(LetTest.class)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new BadTestClassError("Annotation @LetTest dont use with static method");
                    }
                    LetTest letTest = method.getAnnotation(LetTest.class);
                    if ((letTest.priority()<0) || (letTest.priority() > 10)) {
                        throw new BadTestClassError("Annotation @LetTest priority goes beyond the limits ");
                    }
                    //throw new BadTestClassError("Annotation @LetTest dont use with static method");
                    Test testInfo = new Test(
                            Optional.ofNullable(letTest.name())
                                    .filter(s -> !s.isBlank())
                                    .orElse(method.getName()),
                            letTest.priority(),
                            method,
                            method.isAnnotationPresent(Disabled.class));
                    methodTest.add(testInfo);
                }

                if (method.isAnnotationPresent(BeforeEach.class)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new BadTestClassError("Annotation @LetTest dont use with static method");
                    }
                    Test testInfo = new Test(
                            method.getName(),
                            5,
                            method,
                            false);
                    methodBeforeEach.add(testInfo);
                }

                if (method.isAnnotationPresent(AfterEach.class)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new BadTestClassError("Annotation @LetTest dont use with static method");
                    }
                    Test testInfo = new Test(
                            method.getName(),
                            5,
                            method,
                            false);
                    methodAfterEach.add(testInfo);
                }

                if (method.isAnnotationPresent(BeforeSuite.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new BadTestClassError("Annotation @LetTest use only static method");
                    }
                    Test testInfo = new Test(
                            method.getName(),
                            5,
                            method,
                            false);
                    methodBeforeSuite.add(testInfo);
                }

                if (method.isAnnotationPresent(AfterSuite.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new BadTestClassError("Annotation @LetTest use only static method");
                    }
                    Test testInfo = new Test(
                            method.getName(),
                            5,
                            method,
                            false);
                    methodAfterSuite.add(testInfo);
                }
            }

            methodTest.sort(Comparator.comparingInt(Test::getOrder).reversed()
                    .thenComparing(Test::getName));

            System.out.println("---Run tests---");
            System.out.println("---Run BeforeSuite---");
            methodBeforeSuite.forEach(testInfo -> runMethodSimple(testObject, testInfo));

            for (Test testInfoMain : methodTest) {
                System.out.println("---Run BeforeEach---");
                methodBeforeEach.forEach(testInfo -> runMethodSimple(testObject, testInfo));

                if (!testInfoMain.isDisabled()) {
                    try {
                        System.out.println("Run test " + testInfoMain.getName());
                        testInfoMain.getMethod().setAccessible(true);
                        testInfoMain.getMethod().invoke(testObject);
                        testInfoMain.setTestResult(TestResult.SUCCESS);
                        testInfoMain.setMessageException("");
                        report.get(TestResult.SUCCESS).add(testInfoMain);
                        System.out.println("SUCCESS");

                    } catch (Exception e) {
                        if (e.getCause() instanceof TestAssertionError) {
                            testInfoMain.setTestResult(TestResult.FAILED);
                            testInfoMain.setMessageException(e.getCause().toString());
                            report.get(TestResult.FAILED).add(testInfoMain);
                            System.out.println("FAILED");
                        } else {
                            testInfoMain.setTestResult(TestResult.ERROR);
                            testInfoMain.setMessageException(e.toString());
                            report.get(TestResult.ERROR).add(testInfoMain);
                            System.out.println("ERROR");
                        }

                    }
                } else {
                    testInfoMain.setTestResult(TestResult.SKIPPED);
                    testInfoMain.setMessageException("");
                    report.get(TestResult.SKIPPED).add(testInfoMain);
                    System.out.println("SKIPPED");
                }
                System.out.println("---Run AfterEach---");
                methodAfterEach.forEach(testInfo -> runMethodSimple(testObject, testInfo));
            }

            System.out.println("---Run AfterSuite---");
            methodAfterSuite.forEach(testInfo -> runMethodSimple(testObject, testInfo));

        } catch (Exception e) {
            throw new BadTestClassError("Dont initialization test class: " + e.getMessage());
        }

        return report;
    }

    private static void runMethodSimple(Object object, Test testInfo) {
        try {
            System.out.println("call "+testInfo.getName());
            testInfo.getMethod().setAccessible(true);
            testInfo.getMethod().invoke(object);
        } catch (Exception e) {
            System.out.println("Method" + testInfo.getName() + " execute error: " + e.getMessage());
        }
    }
}
