public class SimpleJavaCode {
    public static void main(String[] args) {
        String sql = "SELECT * FROM users";

        String php = "<? echo \"Hello, world!\" ?>";

        String go = """
            package main
            
            impor<caret>t "fmt"
            
            func main() {
                fmt.Println("Hello, world)
            }
            """;
    }
}