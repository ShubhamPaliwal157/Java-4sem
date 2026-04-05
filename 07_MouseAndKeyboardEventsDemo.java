// WAP in Java to show Implementation of Mouse Events and Keyboard Events
//
// Demonstrates:
//   Mouse Events   : mouseClicked, mousePressed, mouseReleased,
//                    mouseEntered, mouseExited, mouseMoved, mouseDragged
//   Keyboard Events: keyPressed, keyReleased, keyTyped
//   Combines both in an interactive Swing drawing canvas

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MouseAndKeyboardEventsDemo {

    // ── Data class for a drawn dot/line ──────────────────────────────────────
    static class DrawPoint {
        int x, y;
        Color color;
        int   size;

        DrawPoint(int x, int y, Color color, int size) {
            this.x     = x;
            this.y     = y;
            this.color = color;
            this.size  = size;
        }
    }

    // ── The interactive canvas ────────────────────────────────────────────────
    static class EventCanvas extends JPanel
            implements MouseListener, MouseMotionListener, KeyListener {

        // State
        private final List<DrawPoint> points    = new ArrayList<>();
        private String  mouseStatus  = "Move mouse over canvas...";
        private String  keyStatus    = "Press any key (canvas must be focused)...";
        private Color   currentColor = Color.BLUE;
        private int     brushSize    = 8;
        private boolean dragging     = false;

        // Log of last 6 events
        private final List<String> eventLog = new ArrayList<>();

        EventCanvas() {
            setBackground(Color.WHITE);
            setFocusable(true);   // needed for key events
            setPreferredSize(new Dimension(700, 500));
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            // Register listeners
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
        }

        // ── Logging helper ─────────────────────────────────────────────────
        private void log(String msg) {
            eventLog.add(0, msg);
            if (eventLog.size() > 6) eventLog.remove(eventLog.size() - 1);
        }

        // ══════════════════════════════════════════════════════════════════════
        // MouseListener methods
        // ══════════════════════════════════════════════════════════════════════

        @Override
        public void mouseClicked(MouseEvent e) {
            String btn = e.getButton() == MouseEvent.BUTTON1 ? "Left" :
                         e.getButton() == MouseEvent.BUTTON3 ? "Right" : "Middle";
            log("mouseClicked [" + btn + "] at (" + e.getX() + "," + e.getY()
                + ") count=" + e.getClickCount());
            mouseStatus = "Clicked [" + btn + "] at (" + e.getX() + ", " + e.getY() + ")";

            // Right-click changes color
            if (e.getButton() == MouseEvent.BUTTON3) {
                currentColor = new Color(
                        (int)(Math.random() * 200),
                        (int)(Math.random() * 200),
                        (int)(Math.random() * 200));
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            log("mousePressed at (" + e.getX() + "," + e.getY() + ")");
            points.add(new DrawPoint(e.getX(), e.getY(), currentColor, brushSize));
            dragging = false;
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            log("mouseReleased at (" + e.getX() + "," + e.getY() + ")");
            dragging = false;
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            log("mouseEntered canvas");
            mouseStatus = "Mouse entered the canvas";
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            log("mouseExited canvas");
            mouseStatus = "Mouse left the canvas";
            repaint();
        }

        // ══════════════════════════════════════════════════════════════════════
        // MouseMotionListener methods
        // ══════════════════════════════════════════════════════════════════════

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseStatus = "Mouse at (" + e.getX() + ", " + e.getY() + ")";
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            dragging = true;
            log("mouseDragged at (" + e.getX() + "," + e.getY() + ")");
            points.add(new DrawPoint(e.getX(), e.getY(), currentColor, brushSize));
            mouseStatus = "Dragging at (" + e.getX() + ", " + e.getY() + ")";
            repaint();
        }

        // ══════════════════════════════════════════════════════════════════════
        // KeyListener methods
        // ══════════════════════════════════════════════════════════════════════

        @Override
        public void keyPressed(KeyEvent e) {
            String key = KeyEvent.getKeyText(e.getKeyCode());
            log("keyPressed  : " + key + "  (code=" + e.getKeyCode() + ")");
            keyStatus = "Key Pressed : " + key;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_C     -> { points.clear(); log("Canvas cleared"); }
                case KeyEvent.VK_UP    -> { brushSize = Math.min(brushSize + 2, 40);
                                            log("Brush size → " + brushSize); }
                case KeyEvent.VK_DOWN  -> { brushSize = Math.max(brushSize - 2, 2);
                                            log("Brush size → " + brushSize); }
                case KeyEvent.VK_R     -> currentColor = Color.RED;
                case KeyEvent.VK_G     -> currentColor = new Color(0, 160, 0);
                case KeyEvent.VK_B     -> currentColor = Color.BLUE;
                case KeyEvent.VK_SPACE -> currentColor = Color.BLACK;
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            log("keyReleased : " + KeyEvent.getKeyText(e.getKeyCode()));
            keyStatus = "Key Released: " + KeyEvent.getKeyText(e.getKeyCode());
            repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            log("keyTyped    : '" + e.getKeyChar() + "'");
            repaint();
        }

        // ══════════════════════════════════════════════════════════════════════
        // Painting
        // ══════════════════════════════════════════════════════════════════════

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // ── Draw all points ──
            for (DrawPoint dp : points) {
                g2.setColor(dp.color);
                g2.fillOval(dp.x - dp.size / 2, dp.y - dp.size / 2, dp.size, dp.size);
            }

            // ── Status bar (bottom strip) ──
            int bh = 120;
            g2.setColor(new Color(30, 30, 30, 210));
            g2.fillRect(0, getHeight() - bh, getWidth(), bh);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(new Color(180, 255, 180));
            g2.drawString("🖱  " + mouseStatus, 10, getHeight() - bh + 18);
            g2.setColor(new Color(255, 220, 150));
            g2.drawString("⌨  " + keyStatus,   10, getHeight() - bh + 36);

            // ── Color swatch & brush size ──
            g2.setColor(Color.WHITE);
            g2.drawString("Color:", 10, getHeight() - bh + 56);
            g2.setColor(currentColor);
            g2.fillRect(58, getHeight() - bh + 44, 24, 16);
            g2.setColor(Color.WHITE);
            g2.drawRect(58, getHeight() - bh + 44, 24, 16);
            g2.drawString("  Brush: " + brushSize + "px", 90, getHeight() - bh + 56);

            // ── Event log ──
            g2.setColor(new Color(160, 200, 255));
            g2.drawString("Event Log:", 10, getHeight() - bh + 74);
            g2.setColor(new Color(200, 200, 200));
            for (int i = 0; i < eventLog.size(); i++) {
                g2.drawString("  " + eventLog.get(i), 10, getHeight() - bh + 88 + i * 13);
            }

            // ── Hints (top-right) ──
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.setColor(new Color(100, 100, 100));
            String[] hints = {
                "Left-click/drag : draw",
                "Right-click     : random color",
                "R/G/B           : set color",
                "↑/↓             : brush size",
                "C               : clear canvas",
                "SPACE           : black color"
            };
            for (int i = 0; i < hints.length; i++)
                g2.drawString(hints[i], getWidth() - 200, 16 + i * 14);
        }
    }

    // ─── Main ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Mouse & Keyboard Events Demo   ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Opening interactive canvas...\n");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mouse & Keyboard Events Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            EventCanvas canvas = new EventCanvas();
            frame.add(canvas, BorderLayout.CENTER);

            // Title label
            JLabel title = new JLabel(
                "  Draw with mouse | Keyboard shortcuts shown (canvas must be focused)",
                SwingConstants.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 13));
            title.setOpaque(true);
            title.setBackground(new Color(50, 50, 80));
            title.setForeground(Color.WHITE);
            title.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            frame.add(title, BorderLayout.NORTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            canvas.requestFocusInWindow(); // grab keyboard focus
        });
    }
}