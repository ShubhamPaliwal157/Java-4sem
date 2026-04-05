# Java-4sem

| # | File | Key Concepts |
| -- | --- | ------------ |
| 01 | ClassesDemo.javaClass | definition, constructors, encapsulation, getters/setters, toString() |
| 02 | InheritanceDemo.java | Single, multilevel & hierarchical inheritance, super, method overriding, abstract, instanceof |
| 03 | PackagesAndInterfacesDemo.java | Interface definitions, constants, default/static methods, multiple interface implementation, @FunctionalInterface, lambda |
| 04 | ThreadsDemo.java | Thread subclass, Runnable, lambda threads, sleep/join/priority, synchronized, wait/notify, Producer–Consumer |
| 05 | ExceptionHandlingDemo.java | try-catch-finally, multiple/nested catch, throw/throws, custom checked & unchecked exceptions, try-with-resources, multi-catch, exception chaining |
| 06 | AppletsDemo.java | Classic Applet lifecycle (init/start/stop/destroy) + modern Swing equivalent with animated Graphics2D |
| 07 | MouseAndKeyboardEventsDemo.java | MouseListener, MouseMotionListener, KeyListener — interactive paint canvas with live event log |
| 08 | FileReadWriteDemo.java | FileWriter/Reader, BufferedReader, PrintWriter, Scanner, DataOutputStream/InputStream, append mode, CSV, java.nio.file.Files |
| 09 | NetworkingDemo.java | InetAddress, URL/HttpURLConnection, TCP ServerSocket/Socket, UDP DatagramSocket |
| 10 | JDBCDemo.java | JDBC connection, Statement/PreparedStatement, CRUD, batch inserts, transactions, ResultSetMetaData, DatabaseMetaData (runs with H2 in-memory DB) |

To compile and run any file (e.g., file 1): ``javac 01_ClassesDemo.java && java ClassesDemo``. For file 10, add the H2 jar: ``javac -cp h2.jar 10_JDBCDemo.java && java -cp .:h2.jar JDBCDemo``.
