// WAP in Java Using Exception Handling Mechanisms
// Demonstrates:
//   • try-catch-finally
//   • Multiple catch blocks
//   • Nested try-catch
//   • throw and throws
//   • Custom (user-defined) exceptions
//   • try-with-resources (AutoCloseable)
//   • Exception chaining (cause)
//   • Checked vs Unchecked exceptions

public class ExceptionHandlingDemo {

    // ══════════════════════════════════════════════════════════════════════════
    // Custom Checked Exception
    // ══════════════════════════════════════════════════════════════════════════
    static class InsufficientFundsException extends Exception {
        private final double amount;

        public InsufficientFundsException(double amount) {
            super(String.format("Insufficient funds! Short by ₹%.2f", amount));
            this.amount = amount;
        }

        public double getShortfall() { return amount; }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Custom Unchecked Exception
    // ══════════════════════════════════════════════════════════════════════════
    static class InvalidAgeException extends RuntimeException {
        public InvalidAgeException(int age) {
            super("Age must be between 0 and 150. Provided: " + age);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BankAccount using custom exceptions
    // ══════════════════════════════════════════════════════════════════════════
    static class BankAccount {
        private double balance;

        BankAccount(double balance) { this.balance = balance; }

        /** throws checked exception */
        public void withdraw(double amount) throws InsufficientFundsException {
            if (amount > balance) {
                throw new InsufficientFundsException(amount - balance);
            }
            balance -= amount;
            System.out.printf("  Withdrew ₹%.2f | Balance: ₹%.2f%n", amount, balance);
        }

        /** throws unchecked exception */
        public static void validateAge(int age) {
            if (age < 0 || age > 150) throw new InvalidAgeException(age);
            System.out.println("  Age validated: " + age);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Simulated resource implementing AutoCloseable (for try-with-resources)
    // ══════════════════════════════════════════════════════════════════════════
    static class DatabaseConnection implements AutoCloseable {
        private final String url;

        DatabaseConnection(String url) {
            this.url = url;
            System.out.println("  [DB] Connection opened → " + url);
        }

        public void query(String sql) throws Exception {
            if (sql == null || sql.isBlank())
                throw new Exception("SQL query cannot be empty!");
            System.out.println("  [DB] Executing: " + sql);
        }

        @Override
        public void close() {
            System.out.println("  [DB] Connection closed ← " + url);
        }
    }

    // ─── Helper methods ───────────────────────────────────────────────────────

    /** Demonstrates arithmetic and array exceptions */
    static void demonstrateBuiltinExceptions() {
        System.out.println("── 1. Built-in Exception Types ────────────");

        // ArithmeticException
        try {
            int result = 10 / 0;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("  ArithmeticException : " + e.getMessage());
        }

        // ArrayIndexOutOfBoundsException
        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[10]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  ArrayIndexOutOfBounds: " + e.getMessage());
        }

        // NumberFormatException
        try {
            int n = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            System.out.println("  NumberFormatException: " + e.getMessage());
        }

        // NullPointerException
        try {
            String s = null;
            s.length();
        } catch (NullPointerException e) {
            System.out.println("  NullPointerException : caught null reference");
        }

        // ClassCastException
        try {
            Object obj = "hello";
            Integer i  = (Integer) obj;
        } catch (ClassCastException e) {
            System.out.println("  ClassCastException   : " + e.getMessage());
        }

        // StackOverflowError
        try {
            recurse(0);
        } catch (StackOverflowError e) {
            System.out.println("  StackOverflowError   : caught!");
        }
    }

    static void recurse(int n) { recurse(n + 1); }

    /** Demonstrates multiple catch, finally, nested try */
    static void demonstrateControlFlow() {
        System.out.println("\n── 2. try-catch-finally & Nested try ──────");

        // Multiple catch + finally
        try {
            System.out.println("  Entering outer try");
            int[] data = new int[5];

            try {
                System.out.println("  Entering inner try");
                data[10] = 42;          // will throw
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("  Inner catch: " + e.getClass().getSimpleName());
                throw new RuntimeException("Wrapped exception", e);  // exception chaining
            } finally {
                System.out.println("  Inner finally always runs");
            }

        } catch (RuntimeException e) {
            System.out.println("  Outer catch: " + e.getMessage());
            System.out.println("  Cause      : " + e.getCause().getClass().getSimpleName());
        } finally {
            System.out.println("  Outer finally always runs");
        }
    }

    /** Demonstrates custom exceptions */
    static void demonstrateCustomExceptions() {
        System.out.println("\n── 3. Custom Exceptions ───────────────────");
        BankAccount acc = new BankAccount(1000);

        // Successful withdrawal
        try {
            acc.withdraw(500);
        } catch (InsufficientFundsException e) {
            System.out.println("  [!] " + e.getMessage());
        }

        // Failed withdrawal
        try {
            acc.withdraw(2000);
        } catch (InsufficientFundsException e) {
            System.out.println("  [!] " + e.getMessage());
            System.out.printf("  Shortfall: ₹%.2f%n", e.getShortfall());
        }

        // Unchecked exception
        try {
            BankAccount.validateAge(25);
            BankAccount.validateAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    /** Demonstrates try-with-resources */
    static void demonstrateTryWithResources() {
        System.out.println("\n── 4. try-with-resources (AutoCloseable) ──");

        // Normal flow — connection auto-closed
        try (DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost/mydb")) {
            db.query("SELECT * FROM users");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();

        // Exception inside block — connection still closed
        try (DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost/mydb")) {
            db.query("");   // throws
        } catch (Exception e) {
            System.out.println("  Caught: " + e.getMessage());
        }
    }

    /** Demonstrates multi-catch (Java 7+) */
    static void demonstrateMultiCatch() {
        System.out.println("\n── 5. Multi-catch (Java 7+) ───────────────");
        Object[] inputs = {"hello", null, 42};

        for (Object obj : inputs) {
            try {
                String s = (String) obj;        // may throw ClassCastException
                int len  = s.length();          // may throw NullPointerException
                System.out.println("  Length of '" + s + "' = " + len);
            } catch (ClassCastException | NullPointerException e) {
                System.out.println("  Multi-catch: " + e.getClass().getSimpleName());
            }
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Exception Handling Demo        ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        demonstrateBuiltinExceptions();
        demonstrateControlFlow();
        demonstrateCustomExceptions();
        demonstrateTryWithResources();
        demonstrateMultiCatch();

        System.out.println("\n[Done]");
    }
}