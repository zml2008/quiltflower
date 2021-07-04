package pkg;

public class TestMultiCast {
    public void test(double arg1, double arg2) {
        useNumbers((int)arg1, (int)arg2);
    }

    private static void useNumbers(double arg1, double arg2) {

    }

    public void test2() {
        useNumbers(provide(), provide());
    }

    private static int provide() {
        return 1;
    }
}
