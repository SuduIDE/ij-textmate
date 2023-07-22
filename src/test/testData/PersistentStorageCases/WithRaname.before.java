public class WithRaname {
    public void test() {
        if (true) {
            while (true) {
                String newSql = "SELECT * FROM<caret> tables";
            }
        }
    }
}