<caret>public class LanguageUnInjectionTestCase {
    public static void testData() {
        String sql = "SELECT name, email FROM users WHERE id > 1 OR id < 10;";
    }
}