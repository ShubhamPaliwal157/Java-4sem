// WAP in Java to show implementation of Inheritance
// Demonstrates: single, multilevel, hierarchical inheritance, method overriding, super keyword

public class InheritanceDemo {

    // ─── Base class ───────────────────────────────────────────────────────────
    static class Animal {
        protected String name;
        protected String color;

        public Animal(String name, String color) {
            this.name  = name;
            this.color = color;
        }

        public void eat() {
            System.out.println("  " + name + " is eating.");
        }

        public void sleep() {
            System.out.println("  " + name + " is sleeping.");
        }

        // Will be overridden
        public void makeSound() {
            System.out.println("  " + name + " makes a sound.");
        }

        @Override
        public String toString() {
            return "Animal[name=" + name + ", color=" + color + "]";
        }
    }

    // ─── Single Inheritance: Animal → Dog ─────────────────────────────────────
    static class Dog extends Animal {
        private String breed;

        public Dog(String name, String color, String breed) {
            super(name, color);   // call parent constructor
            this.breed = breed;
        }

        @Override
        public void makeSound() {
            System.out.println("  " + name + " says: Woof! Woof!");
        }

        public void fetch() {
            System.out.println("  " + name + " fetches the ball!");
        }

        @Override
        public String toString() {
            return "Dog[name=" + name + ", breed=" + breed + ", color=" + color + "]";
        }
    }

    // ─── Single Inheritance: Animal → Cat ────────────────────────────────────
    static class Cat extends Animal {

        public Cat(String name, String color) {
            super(name, color);
        }

        @Override
        public void makeSound() {
            System.out.println("  " + name + " says: Meow~");
        }

        public void purr() {
            System.out.println("  " + name + " is purring...");
        }
    }

    // ─── Multilevel Inheritance: Animal → Dog → GuideDog ─────────────────────
    static class GuideDog extends Dog {
        private String owner;

        public GuideDog(String name, String color, String breed, String owner) {
            super(name, color, breed);
            this.owner = owner;
        }

        public void guide() {
            System.out.println("  " + name + " is guiding " + owner + " safely.");
        }

        @Override
        public void makeSound() {
            // call grandparent (Animal) sound via super chain
            super.makeSound();   // Dog's version → "Woof!"
            System.out.println("  (Guide dog barks softly for " + owner + ")");
        }
    }

    // ─── Shape hierarchy (hierarchical inheritance) ───────────────────────────
    static abstract class Shape {
        protected String shapeColor;

        public Shape(String shapeColor) {
            this.shapeColor = shapeColor;
        }

        public abstract double area();
        public abstract double perimeter();

        public void display() {
            System.out.printf("  %-12s | Area: %8.2f | Perimeter: %8.2f%n",
                    getClass().getSimpleName(), area(), perimeter());
        }
    }

    static class Circle extends Shape {
        private double radius;
        public Circle(double radius, String color) {
            super(color);
            this.radius = radius;
        }
        @Override public double area()      { return Math.PI * radius * radius; }
        @Override public double perimeter() { return 2 * Math.PI * radius; }
    }

    static class Rectangle extends Shape {
        private double length, width;
        public Rectangle(double length, double width, String color) {
            super(color);
            this.length = length;
            this.width  = width;
        }
        @Override public double area()      { return length * width; }
        @Override public double perimeter() { return 2 * (length + width); }
    }

    static class Triangle extends Shape {
        private double a, b, c;
        public Triangle(double a, double b, double c, String color) {
            super(color);
            this.a = a; this.b = b; this.c = c;
        }
        @Override public double perimeter() { return a + b + c; }
        @Override public double area() {
            double s = perimeter() / 2;
            return Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Inheritance Demo               ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── Single Inheritance ──
        System.out.println("── Single Inheritance (Animal → Dog / Cat) ──");
        Dog dog = new Dog("Bruno", "Brown", "Labrador");
        System.out.println("  " + dog);
        dog.eat();
        dog.makeSound();
        dog.fetch();

        System.out.println();
        Cat cat = new Cat("Whiskers", "White");
        cat.makeSound();
        cat.purr();
        cat.sleep();

        // ── Multilevel Inheritance ──
        System.out.println("\n── Multilevel Inheritance (Animal → Dog → GuideDog) ──");
        GuideDog gd = new GuideDog("Rex", "Golden", "Golden Retriever", "Mr. Sharma");
        gd.eat();           // inherited from Animal
        gd.fetch();         // inherited from Dog
        gd.makeSound();     // overridden in GuideDog
        gd.guide();         // own method

        // ── Hierarchical Inheritance via abstract Shape ──
        System.out.println("\n── Hierarchical Inheritance (Shape → Circle / Rectangle / Triangle) ──");
        Shape[] shapes = {
            new Circle(7, "Red"),
            new Rectangle(10, 5, "Blue"),
            new Triangle(3, 4, 5, "Green")
        };
        for (Shape s : shapes) s.display();

        // ── instanceof check ──
        System.out.println("\n── instanceof checks ──");
        System.out.println("  gd instanceof GuideDog : " + (gd instanceof GuideDog));
        System.out.println("  gd instanceof Dog      : " + (gd instanceof Dog));
        System.out.println("  gd instanceof Animal   : " + (gd instanceof Animal));

        System.out.println("\n[Done]");
    }
}