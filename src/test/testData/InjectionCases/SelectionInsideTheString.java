public class SelectionInsideTheString {
    public static void testData() {
        String sql = """<selection>SELECT name, email FROM users WHERE id > 1 OR id < 10;</selection>""";
    }
}