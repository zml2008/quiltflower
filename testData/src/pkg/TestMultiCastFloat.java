package pkg;

public class TestMultiCastFloat {
    public void test(double arg1, double arg2) {
        useNumbers((float)arg1, (float)arg2);
    }

    private static void useNumbers(double arg1, double arg2) {

    }

    public void test2() {
        useNumbers(provide(), provide());
    }

    public void test3() {
        useNumbers(provide() + 1, provide() + 1);
    }

    public void test4() {
        useNumbers(provide() + 0.5, provide() + 0.5);
    }

    public void test5() {
        useNumbers(provide() + 0.5f, provide() + 0.5f);
    }

    private static float provide() {
        return 1;
    }
}
