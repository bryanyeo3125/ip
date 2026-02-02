import java.util.Scanner;

public class ChadGPT {

    private static final String LINE = "----------------------------------------";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printGreeting();

        while (true) {
            String input = scanner.nextLine();

            if (isBye(input)) {
                printGoodbye();
                break;
            }

            addList(input);
        }

        scanner.close();
    }

    // ---------- Helper methods ----------

    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println("Hello! I am ChadGPT.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    private static boolean isBye(String input) {
        return input.equals("bye");
    }

    private static void addList(String input) {
        System.out.println(LINE);
        System.out.println("added: ");
        System.out.println(input);
        System.out.println(LINE);
    }

    private static void printGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
