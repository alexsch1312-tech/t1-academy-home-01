package testrun;

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

