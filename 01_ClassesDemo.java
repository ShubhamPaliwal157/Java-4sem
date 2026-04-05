// WAP in Java to show implementation of Classes
// Demonstrates: class definition, constructors, methods, encapsulation, objects

public class ClassesDemo {

    // ─── Class: BankAccount ───────────────────────────────────────────────────
    static class BankAccount {
        // Private fields (encapsulation)
        private String accountHolder;
        private String accountNumber;
        private double balance;

        // Default constructor
        public BankAccount() {
            this.accountHolder = "Unknown";
            this.accountNumber = "000000";
            this.balance = 0.0;
        }

        // Parameterized constructor
        public BankAccount(String accountHolder, String accountNumber, double initialBalance) {
            this.accountHolder  = accountHolder;
            this.accountNumber  = accountNumber;
            this.balance        = initialBalance;
        }

        // ── Getters (accessors) ──
        public String getAccountHolder() { return accountHolder; }
        public String getAccountNumber() { return accountNumber; }
        public double getBalance()        { return balance; }

        // ── Setters (mutators) ──
        public void setAccountHolder(String name) { this.accountHolder = name; }

        // ── Business methods ──
        public void deposit(double amount) {
            if (amount <= 0) {
                System.out.println("  [!] Deposit amount must be positive.");
                return;
            }
            balance += amount;
            System.out.printf("  Deposited ₹%.2f | New Balance: ₹%.2f%n", amount, balance);
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                System.out.println("  [!] Withdrawal amount must be positive.");
                return;
            }
            if (amount > balance) {
                System.out.println("  [!] Insufficient funds.");
                return;
            }
            balance -= amount;
            System.out.printf("  Withdrew  ₹%.2f | New Balance: ₹%.2f%n", amount, balance);
        }

        // ── toString override ──
        @Override
        public String toString() {
            return String.format("Account[holder='%s', no='%s', balance=₹%.2f]",
                    accountHolder, accountNumber, balance);
        }
    }

    // ─── Class: Student ───────────────────────────────────────────────────────
    static class Student {
        private String name;
        private int    rollNo;
        private int[]  marks;   // marks for 5 subjects

        public Student(String name, int rollNo, int[] marks) {
            this.name   = name;
            this.rollNo = rollNo;
            this.marks  = marks;
        }

        public double calculateAverage() {
            int sum = 0;
            for (int m : marks) sum += m;
            return (double) sum / marks.length;
        }

        public String getGrade() {
            double avg = calculateAverage();
            if (avg >= 90) return "A+";
            if (avg >= 75) return "A";
            if (avg >= 60) return "B";
            if (avg >= 45) return "C";
            return "F";
        }

        public void displayResult() {
            System.out.printf("  Roll No : %d%n", rollNo);
            System.out.printf("  Name    : %s%n", name);
            System.out.printf("  Average : %.2f%n", calculateAverage());
            System.out.printf("  Grade   : %s%n", getGrade());
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Classes Demo                   ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── BankAccount object creation ──
        System.out.println("── BankAccount ──────────────────────────");
        BankAccount acc1 = new BankAccount("Riya Sharma", "SBI1001", 5000.00);
        System.out.println("  " + acc1);
        acc1.deposit(2000);
        acc1.withdraw(1500);
        acc1.withdraw(10000);   // insufficient
        System.out.println("  Final: " + acc1);

        System.out.println();

        // Default constructor
        BankAccount acc2 = new BankAccount();
        acc2.setAccountHolder("Arjun Mehta");
        acc2.deposit(500);
        System.out.println("  " + acc2);

        System.out.println();

        // ── Student objects ──
        System.out.println("── Student ──────────────────────────────");
        Student s1 = new Student("Priya Verma", 101, new int[]{92, 88, 95, 90, 87});
        Student s2 = new Student("Karan Patel", 102, new int[]{55, 60, 48, 52, 50});

        System.out.println("\n  [Student 1]");
        s1.displayResult();
        System.out.println("\n  [Student 2]");
        s2.displayResult();

        System.out.println("\n[Done]");
    }
}