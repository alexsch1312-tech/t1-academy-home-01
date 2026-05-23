package testrun;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Test {
    private final String name;
    private final int order;
    private final Method method;
    private final boolean disabled;

    private TestResult testResult;
    private String messageException;

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public String getMessageException() {
        return messageException;
    }

    public void setMessageException(String messageException) {
        this.messageException = messageException;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isDisabled() {
        return disabled;
    }

    /*
            private final Boolean isDisabled;
            private final Boolean isBeforeEach;
            private final Boolean isAfterEach;

            private final Boolean isBeforeSuite;
            private final Boolean isAfterSuite;

            private final Boolean isStatic;

         */
    public Test(String name, int order, Method method, boolean disabled) {
        this.name = name;
        this.order = order;
        this.method = method;
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return name + " " + testResult + " " + messageException;
    }
}
/*
@Test: вешается на метод. Указанный метод является тестом, который надо запускать. Аннотация позволяет указать строку, которая задает название метода. Если название не задано, то имя метода считается именем теста. Также для теста можно указать приоритет в диапазоне от 0 до 10. Тесты исполняются в порядке приоритета, где 10 – самый высокий приоритет. Тесты с одинаковым приоритетом сортируются по имени. Если приоритет не задан, то его приоритет 5.
@Disabled: вешается на метод, где уже есть @Test. Указанный тест высветится в списке тестов, но не будет запущен.
@BeforeEach и @AfterEach: вешаются на методы. Указанные методы выполняются до и после каждого исполняемого теста.
@BeforeSuite и @AfterSuite: вешаются только на статические методы. Указанные методы выполняются один раз, до всех тестов, и после соответственно.
@Order: вешаются на тестовые методы. Имеет параметр – число в диапазоне от 1 до 10. По умолчанию (если ничего не указано) стоит значение 5. Тестовые методы с числом меньшим должны выполнятся раньше тестовых методов у которых Order больше.
*/
