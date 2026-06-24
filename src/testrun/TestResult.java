package testrun;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)

// Аннотация для маркировки тестовых методов
@Retention(RetentionPolicy.RUNTIME)
@interface LetTest {
    int priority() default 5;
    String name() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@interface Disabled {}

@Retention(RetentionPolicy.RUNTIME)
@interface BeforeEach {}

@Retention(RetentionPolicy.RUNTIME)
@interface AfterEach {}

@Retention(RetentionPolicy.RUNTIME)
@interface BeforeSuite {}

@Retention(RetentionPolicy.RUNTIME)
@interface AfterSuite {}

/*
@Retention(RetentionPolicy.RUNTIME)
@interface Order {
    int value() default 5;
}
*/

// Возможные результаты теста
public enum TestResult {
    SUCCESS, FAILED, ERROR, SKIPPED
}
