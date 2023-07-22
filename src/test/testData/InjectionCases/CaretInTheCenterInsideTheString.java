public class CaretInTheCenterInsideTheString {
    public static void testData() {
        String sql = "SELECT name, email FROM<caret> users WHERE id > 1 OR id < 10;";
    }
}