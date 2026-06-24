package testrun;

import exception.BadTestClassError;
import exception.TestAssertionError;

import java.lang.annotation.Annotation;
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

        Object testObject;

        try {
            testObject = c.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new BadTestClassError("Dont initialization test class: " + e.getMessage());
        }

        System.out.println("---Create Lists Test---");
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(LetTest.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new BadTestClassError("Annotation @LetTest dont use with static method");
                }
                LetTest letTest = method.getAnnotation(LetTest.class);
                if ((letTest.priority() < 0) || (letTest.priority() > 10)) {
                    throw new BadTestClassError("Annotation @LetTest priority goes beyond the limits ");
                }

                Test testInfo = new Test(
                        Optional.ofNullable(letTest.name())
                                .filter(s -> !s.isBlank())
                                .orElse(method.getName()),
                        letTest.priority(),
                        method,
                        method.isAnnotationPresent(Disabled.class));
                methodTest.add(testInfo);
            }
            checkAndAddAnnotation(method, BeforeEach.class, true, methodBeforeEach);
            checkAndAddAnnotation(method, AfterEach.class, true, methodAfterEach);
            checkAndAddAnnotation(method, BeforeSuite.class, false, methodBeforeSuite);
            checkAndAddAnnotation(method, AfterSuite.class, false, methodAfterSuite);
        }

        methodTest.sort(Comparator.comparingInt(Test::getOrder).reversed()
                .thenComparing(Test::getName));

        System.out.println("---Run tests---");
        System.out.println("---Run BeforeSuite---");
        methodBeforeSuite.forEach(testInfo -> runMethodSimple(testObject, testInfo));

        for (Test testInfoMain : methodTest) {
            System.out.println("---Run BeforeEach---");
            methodBeforeEach.forEach(testInfo -> runMethodSimple(testObject, testInfo));

            if (testInfoMain.isDisabled()) {
                registerResult(testInfoMain, TestResult.SKIPPED, "", report);
                continue;
            }

            try {
                System.out.println("Run test " + testInfoMain.getName());
                testInfoMain.getMethod().setAccessible(true);
                testInfoMain.getMethod().invoke(testObject);
                registerResult(testInfoMain, TestResult.SUCCESS, "", report);
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof TestAssertionError) {
                    registerResult(testInfoMain, TestResult.FAILED, cause.toString(), report);
                } else {
                    registerResult(testInfoMain, TestResult.ERROR, e.toString(), report);
                }
            }

            System.out.println("---Run AfterEach---");
            methodAfterEach.forEach(testInfo -> runMethodSimple(testObject, testInfo));
        }

        System.out.println("---Run AfterSuite---");
        methodAfterSuite.forEach(testInfo -> runMethodSimple(testObject, testInfo));

        return report;
    }

    private static void runMethodSimple(Object object, Test testInfo) {
        try {
            System.out.println("call " + testInfo.getName());
            testInfo.getMethod().setAccessible(true);
            testInfo.getMethod().invoke(object);
        } catch (Exception e) {
            System.out.println("Method" + testInfo.getName() + " execute error: " + e.getMessage());
            throw new BadTestClassError("Method" + testInfo.getName() + " execute error");
        }
    }

    private static void registerResult(Test testInfo, TestResult result, String message, Map<TestResult, List<Test>> report) {
        testInfo.setTestResult(result);
        testInfo.setMessageException(message);
        report.get(result).add(testInfo);
        System.out.println(result.name());
    }

    private static void checkAndAddAnnotation(
            Method method,
            Class<? extends Annotation> annotationClass,
            boolean shouldBeNonStatic,
            List<Test> targetList
    ) {
        if (!method.isAnnotationPresent(annotationClass)) {
            return;
        }

        String errorMessage = "Annotation @" + annotationClass.getSimpleName() + (shouldBeNonStatic ? " dont" : "") + " use with static method";
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if ((shouldBeNonStatic && isStatic) || (!shouldBeNonStatic && !isStatic)) {
            throw new BadTestClassError(errorMessage);
        }

        Test testInfo = new Test(method.getName(), 5, method, false);
        targetList.add(testInfo);
    }
}
