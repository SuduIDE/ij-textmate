public class WithMovingExpressionHard {
    public void test() {
        if (true) {
            String sql = "SELECT * FROM<caret> tables";
        }
    }
}