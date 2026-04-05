// WAP in Java to show Implementing basic File Reading and Writing methods
//
// Demonstrates:
//   • Writing text files   : FileWriter, BufferedWriter, PrintWriter
//   • Reading text files   : FileReader, BufferedReader, Scanner
//   • Writing binary files : FileOutputStream, DataOutputStream
//   • Reading binary files : FileInputStream, DataInputStream
//   • File operations      : File class — exists, length, mkdir, list, delete
//   • Appending to files
//   • Reading/writing with java.nio.file (Files utility, Java 7+)

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileReadWriteDemo {

    private static final String DEMO_DIR  = "file_demo";
    private static final String TEXT_FILE = DEMO_DIR + File.separator + "students.txt";
    private static final String CSV_FILE  = DEMO_DIR + File.separator + "marks.csv";
    private static final String BIN_FILE  = DEMO_DIR + File.separator + "data.bin";
    private static final String NIO_FILE  = DEMO_DIR + File.separator + "nio_output.txt";

    // ══════════════════════════════════════════════════════════════════════════
    // 1. Setup directory
    // ══════════════════════════════════════════════════════════════════════════
    static void setupDirectory() {
        File dir = new File(DEMO_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            System.out.println("  Directory created : " + dir.getAbsolutePath() + " → " + created);
        } else {
            System.out.println("  Directory exists  : " + dir.getAbsolutePath());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2. Write text file using BufferedWriter + PrintWriter
    // ══════════════════════════════════════════════════════════════════════════
    static void writeTextFile() throws IOException {
        System.out.println("\n── Writing Text File (BufferedWriter) ─────");

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(TEXT_FILE)))) {
            pw.println("=== Student Records ===");
            pw.printf("%-5s %-20s %-8s %-6s%n", "ID", "Name", "Subject", "Marks");
            pw.println("-".repeat(45));

            String[][] students = {
                {"1", "Riya Sharma",   "Maths",   "92"},
                {"2", "Arjun Mehta",   "Science", "85"},
                {"3", "Priya Patel",   "English", "78"},
                {"4", "Karan Verma",   "Maths",   "91"},
                {"5", "Sneha Agarwal", "Science", "88"},
            };

            for (String[] s : students)
                pw.printf("%-5s %-20s %-8s %-6s%n", s[0], s[1], s[2], s[3]);

            pw.println("-".repeat(45));
            pw.println("Total students: " + students.length);
        }
        System.out.println("  Written → " + TEXT_FILE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3. Read text file using BufferedReader
    // ══════════════════════════════════════════════════════════════════════════
    static void readTextFile() throws IOException {
        System.out.println("\n── Reading Text File (BufferedReader) ─────");

        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
                lineCount++;
            }
        }
        System.out.println("  Total lines read: " + lineCount);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4. Read text file using Scanner (token-based)
    // ══════════════════════════════════════════════════════════════════════════
    static void readWithScanner() throws IOException {
        System.out.println("\n── Reading with Scanner ───────────────────");

        try (Scanner sc = new Scanner(new File(TEXT_FILE))) {
            int count = 0;
            while (sc.hasNextLine() && count < 4) {
                System.out.println("  [Scanner] " + sc.nextLine());
                count++;
            }
            System.out.println("  ... (truncated)");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 5. Append to existing file
    // ══════════════════════════════════════════════════════════════════════════
    static void appendToFile() throws IOException {
        System.out.println("\n── Appending to File ──────────────────────");

        // FileWriter(path, true) → append mode
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TEXT_FILE, true))) {
            bw.newLine();
            bw.write("Appended: " + new Date());
            bw.newLine();
        }
        System.out.println("  Appended timestamp to " + TEXT_FILE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 6. Write binary file using DataOutputStream
    // ══════════════════════════════════════════════════════════════════════════
    static void writeBinaryFile() throws IOException {
        System.out.println("\n── Writing Binary File (DataOutputStream) ─");

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(BIN_FILE)))) {
            dos.writeInt(42);
            dos.writeDouble(3.14159);
            dos.writeBoolean(true);
            dos.writeUTF("Binary Data Record");
            dos.writeLong(System.currentTimeMillis());
        }
        System.out.println("  Written binary → " + BIN_FILE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 7. Read binary file using DataInputStream
    // ══════════════════════════════════════════════════════════════════════════
    static void readBinaryFile() throws IOException {
        System.out.println("\n── Reading Binary File (DataInputStream) ──");

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(BIN_FILE)))) {
            System.out.println("  int     : " + dis.readInt());
            System.out.println("  double  : " + dis.readDouble());
            System.out.println("  boolean : " + dis.readBoolean());
            System.out.println("  UTF     : " + dis.readUTF());
            System.out.println("  long    : " + dis.readLong());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 8. Write CSV using FileWriter
    // ══════════════════════════════════════════════════════════════════════════
    static void writeCsvFile() throws IOException {
        System.out.println("\n── Writing CSV File ───────────────────────");

        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("roll_no,name,math,science,english,average");
            int[][] marks = {{92,88,95},{85,90,82},{78,75,80},{91,94,89},{88,86,90}};
            String[] names = {"Riya Sharma","Arjun Mehta","Priya Patel","Karan Verma","Sneha Agarwal"};
            for (int i = 0; i < names.length; i++) {
                double avg = (marks[i][0] + marks[i][1] + marks[i][2]) / 3.0;
                pw.printf("%d,%s,%d,%d,%d,%.2f%n",
                        i + 1, names[i], marks[i][0], marks[i][1], marks[i][2], avg);
            }
        }
        System.out.println("  Written CSV → " + CSV_FILE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 9. java.nio.file.Files (Java 7+)
    // ══════════════════════════════════════════════════════════════════════════
    static void nioFilesDemo() throws IOException {
        System.out.println("\n── java.nio.file.Files (NIO) ──────────────");

        Path path = Paths.get(NIO_FILE);

        // Write all lines at once
        List<String> lines = Arrays.asList(
                "NIO Files API demo",
                "Written with Files.write()",
                "Charset: UTF-8",
                "Date: " + new Date()
        );
        Files.write(path, lines, StandardCharsets.UTF_8);
        System.out.println("  Written NIO → " + path);

        // Read all lines at once
        List<String> read = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("  Lines read:");
        read.forEach(l -> System.out.println("    " + l));

        // File metadata
        System.out.println("  Size (bytes) : " + Files.size(path));
        System.out.println("  Exists       : " + Files.exists(path));
        System.out.println("  Readable     : " + Files.isReadable(path));
        System.out.println("  Writable     : " + Files.isWritable(path));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 10. File class utilities
    // ══════════════════════════════════════════════════════════════════════════
    static void fileClassDemo() {
        System.out.println("\n── File Class Utilities ───────────────────");

        File dir  = new File(DEMO_DIR);
        File[] files = dir.listFiles();

        System.out.println("  Files in '" + DEMO_DIR + "':");
        if (files != null) {
            for (File f : files) {
                System.out.printf("    %-25s  size: %5d bytes  readable: %b%n",
                        f.getName(), f.length(), f.canRead());
            }
        }

        File textF = new File(TEXT_FILE);
        System.out.println("\n  File path     : " + textF.getAbsolutePath());
        System.out.println("  Parent dir    : " + textF.getParent());
        System.out.println("  Is file       : " + textF.isFile());
        System.out.println("  Is directory  : " + textF.isDirectory());
        System.out.println("  Last modified : " + new Date(textF.lastModified()));
    }

    // ─── Main ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java File Reading & Writing Demo    ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        try {
            System.out.println("── Setup ──────────────────────────────────");
            setupDirectory();

            writeTextFile();
            readTextFile();
            readWithScanner();
            appendToFile();

            writeBinaryFile();
            readBinaryFile();

            writeCsvFile();
            nioFilesDemo();
            fileClassDemo();

        } catch (IOException e) {
            System.err.println("[IO Error] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n[Done]");
    }
}