// WAP in Java to show Implementation of Applets
//
// NOTE: The java.applet API was deprecated in Java 9 and removed in Java 17.
//       This file shows the complete, classic Applet pattern (compiles with Java ≤ 11)
//       alongside a modern Swing equivalent that runs on any JDK.
//
// Demonstrates:
//   • Applet lifecycle  : init(), start(), stop(), destroy()
//   • Graphics drawing  : drawString, drawRect, drawOval, drawLine, setColor, setFont
//   • Mouse interaction via applet
//   • Equivalent Swing  JFrame / JPanel program (runs on Java 17+)

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// ══════════════════════════════════════════════════════════════════════════════
// PART A – Classic Applet skeleton (requires java.applet on classpath)
//           Kept as comments so the file compiles with modern JDKs too.
// ══════════════════════════════════════════════════════════════════════════════
/*
import java.applet.Applet;

public class MyApplet extends Applet {

    private String status = "Applet Running";
    private int    clickX = 100, clickY = 100;

    // ── Lifecycle ──────────────────────────────────────────────────────────
    @Override
    public void init() {
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);
        System.out.println("init()  : Applet initialized");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickX  = e.getX();
                clickY  = e.getY();
                status  = "Clicked at (" + clickX + ", " + clickY + ")";
                repaint();
            }
        });
    }

    @Override public void start()   { System.out.println("start() : Applet started"); }
    @Override public void stop()    { System.out.println("stop()  : Applet stopped"); }
    @Override public void destroy() { System.out.println("destroy(): Applet destroyed"); }

    // ── Painting ────────────────────────────────────────────────────────────
    @Override
    public void paint(Graphics g) {
        // Title
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.BLUE);
        g.drawString("Java Applet Demo", 20, 40);

        // Rectangle
        g.setColor(Color.RED);
        g.drawRect(20, 60, 150, 80);
        g.setColor(new Color(255, 200, 200));
        g.fillRect(21, 61, 149, 79);

        // Oval
        g.setColor(Color.GREEN.darker());
        g.drawOval(200, 60, 120, 80);
        g.setColor(new Color(200, 255, 200));
        g.fillOval(201, 61, 119, 79);

        // Line
        g.setColor(Color.MAGENTA);
        g.drawLine(20, 160, 340, 160);

        // Status
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.setColor(Color.BLACK);
        g.drawString(status, 20, 190);

        // Click indicator
        g.setColor(Color.ORANGE);
        g.fillOval(clickX - 6, clickY - 6, 12, 12);
    }
}
*/

// ══════════════════════════════════════════════════════════════════════════════
// PART B – Modern Swing equivalent (runs on Java 8 – 21+)
//           Reproduces the same applet lifecycle visually.
// ══════════════════════════════════════════════════════════════════════════════

/** Simulates the Applet drawing panel */
class AppletPanel extends JPanel {

    private String status  = "Applet Running — click anywhere!";
    private int    clickX  = 175;
    private int    clickY  = 175;
    private int    animTick = 0;
    private Timer  timer;

    AppletPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(420, 320));

        // Mouse click listener (mirrors Applet mouseListener)
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                clickX  = e.getX();
                clickY  = e.getY();
                status  = "Clicked at (" + clickX + ", " + clickY + ")";
                repaint();
            }
        });

        // Simple animation timer (simulates start/stop lifecycle)
        timer = new Timer(50, e -> { animTick++; repaint(); });
    }

    /** Applet lifecycle: init */
    public void appletInit()    { System.out.println("init()   : Panel initialized"); }
    /** Applet lifecycle: start */
    public void appletStart()   { System.out.println("start()  : Panel started"); timer.start(); }
    /** Applet lifecycle: stop */
    public void appletStop()    { System.out.println("stop()   : Panel stopped");  timer.stop();  }
    /** Applet lifecycle: destroy */
    public void appletDestroy() { System.out.println("destroy(): Panel destroyed"); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ── Title ──
        g2.setFont(new Font("Georgia", Font.BOLD, 20));
        g2.setColor(new Color(30, 80, 200));
        g2.drawString("Java Applet Demo (Swing)", 20, 38);

        // ── Animated rectangle ──
        int offset = (int)(10 * Math.sin(animTick * 0.08));
        g2.setColor(new Color(220, 60, 60));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(20 + offset, 60, 150, 80);
        g2.setColor(new Color(255, 210, 210, 160));
        g2.fillRect(21 + offset, 61, 149, 79);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("drawRect()", 55 + offset, 105);

        // ── Animated oval ──
        int oy = (int)(8 * Math.cos(animTick * 0.08));
        g2.setColor(new Color(40, 160, 40));
        g2.drawOval(200, 60 + oy, 120, 80);
        g2.setColor(new Color(190, 255, 190, 160));
        g2.fillOval(201, 61 + oy, 119, 79);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("drawOval()", 232, 105 + oy);

        // ── Horizontal line ──
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(180, 40, 180));
        g2.drawLine(20, 165, 380, 165);
        g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
        g2.drawString("drawLine()", 155, 162);

        // ── Polygons ──
        int[] xp = {310, 340, 370, 370, 340, 310};
        int[] yp = { 70,  60,  70, 140, 150, 140};
        g2.setColor(new Color(255, 165, 0, 180));
        g2.fillPolygon(xp, yp, 6);
        g2.setColor(Color.ORANGE.darker());
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(xp, yp, 6);

        // ── Status bar ──
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
        g2.drawString(status, 20, 195);

        // ── Click dot ──
        g2.setColor(Color.ORANGE.darker());
        g2.fillOval(clickX - 7, clickY - 7, 14, 14);
        g2.setColor(Color.RED.darker());
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(clickX - 7, clickY - 7, 14, 14);

        // ── Legend ──
        int ly = 230;
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(Color.GRAY);
        g2.drawString("Applet Lifecycle Methods:", 20, ly);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        String[] methods = {"init() → initialises applet",
                "start() → called when applet becomes visible",
                "stop()  → called when applet hidden/paused",
                "destroy() → cleanup before applet is removed"};
        for (int i = 0; i < methods.length; i++) {
            g2.setColor(i % 2 == 0 ? new Color(50, 100, 200) : new Color(180, 50, 50));
            g2.drawString("  " + methods[i], 20, ly + 16 + i * 16);
        }
    }
}

public class AppletsDemo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Applet Demo (Swing equivalent) ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Applet Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AppletPanel panel = new AppletPanel();

            // Simulate applet lifecycle
            panel.appletInit();

            // Button bar
            JPanel buttons = new JPanel();
            JButton startBtn   = new JButton("start()");
            JButton stopBtn    = new JButton("stop()");
            JButton destroyBtn = new JButton("destroy()");

            startBtn.addActionListener(e -> panel.appletStart());
            stopBtn.addActionListener(e  -> panel.appletStop());
            destroyBtn.addActionListener(e -> {
                panel.appletStop();
                panel.appletDestroy();
            });

            buttons.add(startBtn);
            buttons.add(stopBtn);
            buttons.add(destroyBtn);

            frame.add(panel, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Auto-start
            panel.appletStart();
        });
    }
}