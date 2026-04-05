// WAP in Java to show Implementation of Packages and Interfaces
// Note: All types are in the default package for single-file compilation.
//       In a real project each type would reside in its own package directory.
//
// Demonstrates:
//   • Interface definition (constants + abstract methods + default methods)
//   • Functional interface
//   • Multiple interface implementation
//   • Interface as type (polymorphism)
//   • Package-level organisation (documented via comments)

public class PackagesAndInterfacesDemo {

    // ══════════════════════════════════════════════════════════════════════════
    // Simulated package: com.shapes
    // ══════════════════════════════════════════════════════════════════════════

    /** Core geometry contract */
    interface Drawable {
        // Constant (implicitly public static final)
        String UNIT = "cm";

        // Abstract methods every shape must implement
        double area();
        double perimeter();

        // Default method — shared implementation
        default void describe() {
            System.out.printf("  [%s] area=%.2f %s²  perimeter=%.2f %s%n",
                    getClass().getSimpleName(), area(), UNIT, perimeter(), UNIT);
        }

        // Static factory helper
        static void printSeparator() {
            System.out.println("  ──────────────────────────────────");
        }
    }

    /** Optional capability: resizing */
    interface Resizable {
        void resize(double factor);
    }

    /** Marker-style interface with a default */
    interface Colorable {
        String getColor();
        default void paint() {
            System.out.println("  Painting " + getClass().getSimpleName()
                    + " with color: " + getColor());
        }
    }

    // ── Implementing multiple interfaces ──────────────────────────────────────
    static class Circle implements Drawable, Resizable, Colorable {
        private double radius;
        private final String color;

        Circle(double radius, String color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override public double area()      { return Math.PI * radius * radius; }
        @Override public double perimeter() { return 2 * Math.PI * radius; }
        @Override public void   resize(double factor) { radius *= factor; }
        @Override public String getColor()  { return color; }
    }

    static class Rectangle implements Drawable, Resizable, Colorable {
        private double length, width;
        private final String color;

        Rectangle(double length, double width, String color) {
            this.length = length;
            this.width  = width;
            this.color  = color;
        }

        @Override public double area()      { return length * width; }
        @Override public double perimeter() { return 2 * (length + width); }
        @Override public void   resize(double factor) { length *= factor; width *= factor; }
        @Override public String getColor()  { return color; }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Simulated package: com.payment
    // ══════════════════════════════════════════════════════════════════════════

    /** Payment gateway contract */
    interface PaymentGateway {
        boolean processPayment(double amount);
        void generateReceipt(double amount);

        default void pay(double amount) {
            System.out.printf("  Initiating payment of ₹%.2f via %s...%n",
                    amount, getClass().getSimpleName());
            if (processPayment(amount)) {
                generateReceipt(amount);
            } else {
                System.out.println("  [!] Payment failed.");
            }
        }
    }

    static class UPIPayment implements PaymentGateway {
        private final String upiId;
        UPIPayment(String upiId) { this.upiId = upiId; }

        @Override
        public boolean processPayment(double amount) {
            System.out.println("  Deducting ₹" + amount + " from UPI: " + upiId);
            return true;
        }

        @Override
        public void generateReceipt(double amount) {
            System.out.printf("  ✔ UPI Receipt | ₹%.2f debited from %s%n", amount, upiId);
        }
    }

    static class CreditCardPayment implements PaymentGateway {
        private final String lastFour;
        CreditCardPayment(String lastFour) { this.lastFour = lastFour; }

        @Override
        public boolean processPayment(double amount) {
            System.out.println("  Charging card ending " + lastFour);
            return amount <= 50000; // simulate limit
        }

        @Override
        public void generateReceipt(double amount) {
            System.out.printf("  ✔ Card Receipt | ₹%.2f charged to card **%s%n",
                    amount, lastFour);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Functional Interface (single abstract method) — usable with lambda
    // ══════════════════════════════════════════════════════════════════════════
    @FunctionalInterface
    interface Validator {
        boolean validate(String input);
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Packages & Interfaces Demo     ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── Drawable / Resizable / Colorable ──
        System.out.println("── Shape Interface Demo ──────────────────");
        Drawable[] shapes = { new Circle(5, "Red"), new Rectangle(8, 4, "Blue") };

        for (Drawable d : shapes) {
            d.describe();
            d.paint();                  // Colorable default
            Drawable.printSeparator();  // static interface method
        }

        // Resize via Resizable reference
        System.out.println("  Resizing Circle by factor 2x:");
        Resizable r = (Resizable) shapes[0];
        r.resize(2.0);
        shapes[0].describe();

        // ── Payment Interface Demo ──
        System.out.println("\n── Payment Gateway Interface Demo ────────");
        PaymentGateway pg1 = new UPIPayment("riya@oksbi");
        PaymentGateway pg2 = new CreditCardPayment("4321");

        pg1.pay(1500);
        System.out.println();
        pg2.pay(30000);
        System.out.println();
        pg2.pay(75000);    // over limit → fail

        // ── Functional Interface with Lambda ──
        System.out.println("\n── Functional Interface (Lambda) ─────────");
        Validator emailValidator = input -> input != null && input.contains("@") && input.contains(".");
        Validator phoneValidator = input -> input != null && input.matches("\\d{10}");

        String[] emails = {"priya@gmail.com", "bad-email", "user@domain.org"};
        String[] phones = {"9876543210", "12345", "8001234567"};

        System.out.println("  Email Validation:");
        for (String e : emails)
            System.out.printf("    %-25s → %s%n", e, emailValidator.validate(e) ? "✔ Valid" : "✘ Invalid");

        System.out.println("  Phone Validation:");
        for (String p : phones)
            System.out.printf("    %-15s → %s%n", p, phoneValidator.validate(p) ? "✔ Valid" : "✘ Invalid");

        System.out.println("\n[Done]");
    }
}