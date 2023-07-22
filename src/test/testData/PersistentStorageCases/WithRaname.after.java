public class WithRaname {
    public void test() {
        if (true) {
            while (true) {
                String sql = "SELECT * FROM<caret> tables";
            }
        }
    }
}