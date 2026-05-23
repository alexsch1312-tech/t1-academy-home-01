package testrun;

import exception.TestAssertionError;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SampleTests {


    @BeforeEach
    void BeforeEach1() {
        System.out.println("BeforeEach1");
    }
    @BeforeEach
    void beforeEach2() {
        System.out.println("BeforeEach2");
    }
    @AfterEach
    void afterEach1() {
        System.out.println("AfterEach1");
    }
    @BeforeSuite
    static void beforeSuite1() {
        System.out.println("beforeSuite1");
    }
    @AfterSuite
    static void afterSuite1() {
        System.out.println("afterSuite1");
    }
    @AfterSuite
    static void afterSuite2() {
        System.out.println("afterSuite2");
    }

    @LetTest(priority = 10, name = "Priority 10")
    void testOneEqualsOne() {
        // Этот тест пройдет успешно
    }

    @LetTest
    void testDivisionByZero() {
        // Этот тест упадет, так как будет ArithmeticException
        int result = 1 / 0;
    }

    @LetTest(priority = 1, name = "Priority 1")
    void testTestAssertionError() {
        throw new TestAssertionError("Error!!!");
    }

    @LetTest
    void testException() {
        throw new RuntimeException("Error!!!");
    }

    @LetTest
    @Disabled
    void testDisabled() {

    }
    /*
    @LetTest
    static void teststatic() {
        // статический метод для проверки ошибки
    }
    */

/*
    @LetTest(priority = 100)
    void testlimit() {
        // статический метод для проверки ошибки
    }
*/

    void ordinaryMethod() {
        // Этот метод не запустится
    }
}
