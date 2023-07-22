public class CaretOnTheRightInsideTheString {
    public static void testData() {
        String sql = """SELECT name, email FROM users WHERE id > 5 OR id < 10;<caret>""";
    }
}