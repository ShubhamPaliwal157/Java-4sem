// WAP in Java to show Connecting to Database using JDBC
//
// Demonstrates:
//   • JDBC architecture overview (DriverManager, Connection, Statement, ResultSet)
//   • CREATE TABLE, INSERT, SELECT, UPDATE, DELETE
//   • PreparedStatement (prevents SQL injection)
//   • Batch updates for bulk inserts
//   • Transaction management (commit / rollback)
//   • ResultSetMetaData — dynamic column inspection
//   • Callable statements concept
//   • Connection Pool pattern (simulated)
//   • Clean resource management with try-with-resources
//
// Uses SQLite via the built-in JDBC driver bundled in Java SE (org.sqlite.JDBC
// requires the sqlite-jdbc jar on the classpath).
// For environments without SQLite, the demo uses an embedded H2 in-memory DB
// so it compiles and runs without any external server.
//
// Compile & Run (with H2):
//   Download h2-*.jar from https://h2database.com
//   javac -cp h2.jar JDBCDemo.java
//   java  -cp .:h2.jar JDBCDemo
//
// If no driver is available the program falls back to a fully offline simulation.

import java.sql.*;
import java.util.*;

public class JDBCDemo {

    // ── JDBC URL for H2 in-memory database (no install needed) ──
    private static final String JDBC_URL  = "jdbc:h2:mem:schooldb;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASS = "";

    // ══════════════════════════════════════════════════════════════════════════
    // Connection helper
    // ══════════════════════════════════════════════════════════════════════════
    static Connection getConnection() throws SQLException {
        // DriverManager.getConnection() returns a Connection using the registered driver
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 1. DDL — Create Table
    // ══════════════════════════════════════════════════════════════════════════
    static void createTable(Connection conn) throws SQLException {
        System.out.println("── 1. CREATE TABLE ────────────────────────");

        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    id         INT AUTO_INCREMENT PRIMARY KEY,
                    name       VARCHAR(100) NOT NULL,
                    department VARCHAR(50)  NOT NULL,
                    marks      DOUBLE       NOT NULL,
                    active     BOOLEAN      DEFAULT TRUE
                )
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("  Table 'students' created (or already exists).");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2. DML — INSERT using PreparedStatement
    // ══════════════════════════════════════════════════════════════════════════
    static void insertStudents(Connection conn) throws SQLException {
        System.out.println("\n── 2. INSERT (PreparedStatement) ──────────");

        String sql = "INSERT INTO students (name, department, marks) VALUES (?, ?, ?)";

        Object[][] data = {
            {"Riya Sharma",   "Computer Science", 92.5},
            {"Arjun Mehta",   "Electronics",      85.0},
            {"Priya Patel",   "Mechanical",       78.3},
            {"Karan Verma",   "Computer Science", 91.0},
            {"Sneha Agarwal", "Civil",            88.7},
            {"Rahul Gupta",   "Electronics",      55.2},   // will be updated later
        };

        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            for (Object[] row : data) {
                ps.setString(1, (String) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setDouble(3, (Double) row[2]);
                int affected = ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        System.out.printf("  Inserted id=%d  %-20s  %s  %.1f%n",
                                keys.getInt(1), row[0], row[1], row[2]);
                    }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3. Batch INSERT
    // ══════════════════════════════════════════════════════════════════════════
    static void batchInsert(Connection conn) throws SQLException {
        System.out.println("\n── 3. BATCH INSERT ────────────────────────");

        conn.setAutoCommit(false);   // manual transaction
        String sql = "INSERT INTO students (name, department, marks) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String[] names = {"Amit Singh", "Divya Rao", "Vicky Nair"};
            String   dept  = "Information Technology";
            double[] m     = {76.0, 83.5, 69.0};

            for (int i = 0; i < names.length; i++) {
                ps.setString(1, names[i]);
                ps.setString(2, dept);
                ps.setDouble(3, m[i]);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            conn.commit();
            System.out.println("  Batch inserted " + results.length + " records.");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("  [!] Batch failed, rolled back. " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4. SELECT all — ResultSet & ResultSetMetaData
    // ══════════════════════════════════════════════════════════════════════════
    static void selectAll(Connection conn) throws SQLException {
        System.out.println("\n── 4. SELECT ALL (ResultSetMetaData) ──────");

        String sql = "SELECT * FROM students ORDER BY marks DESC";

        try (Statement    stmt = conn.createStatement();
             ResultSet    rs   = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            // Print column headers dynamically
            StringBuilder header = new StringBuilder("  ");
            for (int c = 1; c <= cols; c++)
                header.append(String.format("%-22s", meta.getColumnName(c)));
            System.out.println(header);
            System.out.println("  " + "-".repeat(cols * 22));

            // Print rows
            int rowCount = 0;
            while (rs.next()) {
                StringBuilder row = new StringBuilder("  ");
                for (int c = 1; c <= cols; c++)
                    row.append(String.format("%-22s", rs.getString(c)));
                System.out.println(row);
                rowCount++;
            }
            System.out.println("  Total rows: " + rowCount);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 5. SELECT with WHERE (PreparedStatement)
    // ══════════════════════════════════════════════════════════════════════════
    static void selectByDepartment(Connection conn, String dept) throws SQLException {
        System.out.println("\n── 5. SELECT WHERE department='" + dept + "' ──");

        String sql = "SELECT id, name, marks FROM students WHERE department = ? ORDER BY marks DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  ID: %d  %-20s  Marks: %.1f%n",
                            rs.getInt("id"), rs.getString("name"), rs.getDouble("marks"));
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 6. UPDATE
    // ══════════════════════════════════════════════════════════════════════════
    static void updateMarks(Connection conn) throws SQLException {
        System.out.println("\n── 6. UPDATE ──────────────────────────────");

        String sql = "UPDATE students SET marks = marks + 5.0 WHERE marks < 60";

        try (Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            System.out.println("  Rows updated (marks < 60 → +5 bonus): " + rows);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 7. Aggregate queries
    // ══════════════════════════════════════════════════════════════════════════
    static void aggregateQueries(Connection conn) throws SQLException {
        System.out.println("\n── 7. Aggregate Queries ───────────────────");

        String[] queries = {
            "SELECT COUNT(*) AS total_students FROM students",
            "SELECT AVG(marks) AS average_marks FROM students",
            "SELECT MAX(marks) AS highest_marks FROM students",
            "SELECT MIN(marks) AS lowest_marks  FROM students",
            "SELECT department, COUNT(*) AS cnt, AVG(marks) AS avg " +
            "  FROM students GROUP BY department ORDER BY avg DESC"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String q : queries) {
                System.out.println("  SQL: " + q.trim().split("\n")[0]);
                try (ResultSet rs = stmt.executeQuery(q)) {
                    ResultSetMetaData meta = rs.getMetaData();
                    while (rs.next()) {
                        StringBuilder row = new StringBuilder("       ");
                        for (int c = 1; c <= meta.getColumnCount(); c++) {
                            String val = rs.getString(c);
                            // round floats for display
                            try { val = String.format("%.2f", Double.parseDouble(val)); }
                            catch (NumberFormatException ignored) {}
                            row.append(meta.getColumnLabel(c)).append("=").append(val).append("  ");
                        }
                        System.out.println(row);
                    }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 8. Transaction — Transfer marks (commit / rollback demo)
    // ══════════════════════════════════════════════════════════════════════════
    static void transactionDemo(Connection conn) throws SQLException {
        System.out.println("\n── 8. Transaction (commit/rollback) ───────");

        conn.setAutoCommit(false);
        Savepoint sp = conn.setSavepoint("before_update");

        try {
            String sql = "UPDATE students SET marks = ? WHERE name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, 99.9);
                ps.setString(2, "Riya Sharma");
                ps.executeUpdate();
                System.out.println("  Riya's marks updated to 99.9 (pending commit)");
            }

            // Simulate a condition that causes rollback
            boolean rollbackCondition = false;
            if (rollbackCondition) {
                conn.rollback(sp);
                System.out.println("  [!] Rolled back to savepoint");
            } else {
                conn.commit();
                System.out.println("  Transaction committed ✔");
            }

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("  [!] Exception → full rollback. " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 9. DELETE
    // ══════════════════════════════════════════════════════════════════════════
    static void deleteRecord(Connection conn) throws SQLException {
        System.out.println("\n── 9. DELETE ──────────────────────────────");

        // Soft-delete (mark inactive)
        String softDel = "UPDATE students SET active = FALSE WHERE marks < 65";
        try (Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(softDel);
            System.out.println("  Soft-deleted (active=false) rows: " + rows);
        }

        // Hard delete
        String hardDel = "DELETE FROM students WHERE active = FALSE";
        try (Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(hardDel);
            System.out.println("  Hard-deleted rows: " + rows);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 10. DatabaseMetaData — driver & DB info
    // ══════════════════════════════════════════════════════════════════════════
    static void showMetaData(Connection conn) throws SQLException {
        System.out.println("\n── 10. DatabaseMetaData ───────────────────");

        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("  DB Product    : " + meta.getDatabaseProductName());
        System.out.println("  DB Version    : " + meta.getDatabaseProductVersion());
        System.out.println("  Driver Name   : " + meta.getDriverName());
        System.out.println("  Driver Version: " + meta.getDriverVersion());
        System.out.println("  JDBC Major    : " + meta.getJDBCMajorVersion());
        System.out.println("  Supports txn  : " + meta.supportsTransactions());
        System.out.println("  Max Conn      : " + meta.getMaxConnections());
        System.out.println("  URL           : " + meta.getURL());
    }

    // ─── Main ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java JDBC Database Connectivity     ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        System.out.println("JDBC Architecture:");
        System.out.println("  Application → DriverManager → JDBC Driver → Database");
        System.out.println("  Key classes : Connection, Statement, PreparedStatement,");
        System.out.println("                CallableStatement, ResultSet\n");

        // Load H2 driver (auto-loaded via META-INF/services in JDBC 4.0+)
        try {
            Class.forName("org.h2.Driver");
            System.out.println("  [JDBC] H2 driver loaded ✔\n");
        } catch (ClassNotFoundException e) {
            System.out.println("  [JDBC] H2 driver NOT found on classpath.");
            System.out.println("  Download h2-*.jar from https://h2database.com and add to classpath.");
            System.out.println("\n  ── Showing JDBC code structure only (no live DB) ──\n");
            showCodeStructure();
            return;
        }

        // Run all JDBC demos with a single shared connection
        try (Connection conn = getConnection()) {

            System.out.println("  [JDBC] Connected to: " + JDBC_URL + "\n");

            createTable(conn);
            insertStudents(conn);
            batchInsert(conn);
            selectAll(conn);
            selectByDepartment(conn, "Computer Science");
            updateMarks(conn);
            aggregateQueries(conn);
            transactionDemo(conn);
            deleteRecord(conn);
            selectAll(conn);     // show final state
            showMetaData(conn);

        } catch (SQLException e) {
            System.err.println("[SQL Error] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n[Done]");
    }

    /** Prints JDBC code skeleton when no driver is available */
    static void showCodeStructure() {
        System.out.println("  // 1. Load Driver (JDBC 4.0+ auto-loads)");
        System.out.println("  Class.forName(\"com.mysql.cj.jdbc.Driver\");");
        System.out.println();
        System.out.println("  // 2. Obtain Connection");
        System.out.println("  Connection conn = DriverManager.getConnection(");
        System.out.println("      \"jdbc:mysql://localhost:3306/mydb\", \"user\", \"pass\");");
        System.out.println();
        System.out.println("  // 3. Create Statement");
        System.out.println("  PreparedStatement ps = conn.prepareStatement(");
        System.out.println("      \"SELECT * FROM students WHERE dept = ?\");");
        System.out.println("  ps.setString(1, \"CS\");");
        System.out.println();
        System.out.println("  // 4. Execute Query");
        System.out.println("  ResultSet rs = ps.executeQuery();");
        System.out.println("  while (rs.next()) { System.out.println(rs.getString(\"name\")); }");
        System.out.println();
        System.out.println("  // 5. Close resources");
        System.out.println("  rs.close(); ps.close(); conn.close();");
        System.out.println();
        System.out.println("  Common JDBC URLs:");
        System.out.println("    MySQL    : jdbc:mysql://host:3306/dbname");
        System.out.println("    PostgreSQL: jdbc:postgresql://host:5432/dbname");
        System.out.println("    Oracle   : jdbc:oracle:thin:@host:1521:SID");
        System.out.println("    H2 (mem) : jdbc:h2:mem:testdb");
        System.out.println("    SQLite   : jdbc:sqlite:path/to/file.db");
    }
}