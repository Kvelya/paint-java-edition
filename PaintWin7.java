import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

public class PaintWin7 extends JFrame {
    private DrawPanel drawPanel = new DrawPanel();
    private Color selectedColor = Color.BLACK;

    public PaintWin7() {
        try {
            setIconImage(ImageIO.read(new File("img/paint.png")));
        } catch (IOException e) {
        }
        setTitle("Paint - Java Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        add(createToolPanel(), BorderLayout.WEST);
        add(drawPanel, BorderLayout.CENTER);
        add(createColorPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem saveItem = new JMenuItem("Save as PNG");

        newItem.addActionListener(_ -> drawPanel.clearCanvas());
        saveItem.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                drawPanel.saveImage(file);
            }
        });

        fileMenu.add(newItem);
        fileMenu.add(saveItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(_ -> drawPanel.clearCanvas());
        editMenu.add(clearItem);

        JMenu imageMenu = new JMenu("Image");
        JMenuItem insertImageItem = new JMenuItem("Insert Image");
        insertImageItem.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                drawPanel.insertImage(file);
            }
        });
        imageMenu.add(insertImageItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(imageMenu);

        return menuBar;
    }

    private JPanel createToolPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(120, 0));

        String[] tools = {"Brush", "Eraser", "Line", "Rectangle", "Ellipse", "Fill"};
        String[] icons = {"img/brush.png", "img/eraser.png", "img/line.png", "img/rect.png", "img/ellipse.png", "img/fill.png"};

        JPanel grid = new JPanel(new GridLayout((int)Math.ceil(tools.length/2.0), 2, 5, 5));
        for (int i = 0; i < tools.length; i++) {
            final int idx = i;
            JButton btn;
            File iconFile = new File(icons[i]);
            int iconSize = (i == 0 || i == 1) ? 32 : 40;
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(icons[i]);
                Image img = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                btn = new JButton(new ImageIcon(img));
            } else {
                btn = new JButton(tools[i]);
            }
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setMaximumSize(new Dimension(40, 40));
            btn.setMinimumSize(new Dimension(40, 40));
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.addActionListener(_ -> drawPanel.setTool(tools[idx]));
            grid.add(btn);
        }

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider brushSizeSlider = new JSlider(JSlider.VERTICAL, 1, 100, 3);
        brushSizeSlider.setMajorTickSpacing(25);
        brushSizeSlider.setMinorTickSpacing(5);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);
        brushSizeSlider.setPreferredSize(new Dimension(60, 260));
        brushSizeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        java.util.Hashtable<Integer, JLabel> brushLabels = new java.util.Hashtable<>();
        brushLabels.put(1, new JLabel("1"));
        brushLabels.put(25, new JLabel("25"));
        brushLabels.put(50, new JLabel("50"));
        brushLabels.put(75, new JLabel("75"));
        brushLabels.put(100, new JLabel("100"));
        brushSizeSlider.setLabelTable(brushLabels);

        brushSizeSlider.addChangeListener(_ -> drawPanel.setBrushSize(brushSizeSlider.getValue()));

        JLabel brushSizeLabel = new JLabel("Brush size");
        brushSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider alphaSlider = new JSlider(JSlider.VERTICAL, 1, 100, 100);
        alphaSlider.setMajorTickSpacing(25);
        alphaSlider.setMinorTickSpacing(5);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);
        alphaSlider.setPreferredSize(new Dimension(60, 260));
        alphaSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        java.util.Hashtable<Integer, JLabel> alphaLabels = new java.util.Hashtable<>();
        alphaLabels.put(1, new JLabel("1"));
        alphaLabels.put(25, new JLabel("25"));
        alphaLabels.put(50, new JLabel("50"));
        alphaLabels.put(75, new JLabel("75"));
        alphaLabels.put(100, new JLabel("100"));
        alphaSlider.setLabelTable(alphaLabels);

        alphaSlider.addChangeListener(_ -> drawPanel.setAlpha(alphaSlider.getValue()));

        JLabel alphaLabel = new JLabel("Transparency");
        alphaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sliderPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sliderPanel.add(brushSizeSlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(brushSizeLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sliderPanel.add(alphaSlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(alphaLabel);

        panel.add(grid, BorderLayout.NORTH);
        panel.add(sliderPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createColorPanel() {
        JPanel colorPanel = new JPanel();

        Color[] baseColors = {
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN,
            Color.BLUE, Color.MAGENTA, Color.PINK
        };

        for (Color color : baseColors) {
            JButton btn = new JButton();
            btn.setBackground(color);
            btn.setPreferredSize(new Dimension(25, 25));
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.addActionListener(_ -> {
                selectedColor = color;
                drawPanel.setColor(color);
            });
            colorPanel.add(btn);
        }

        JButton customColorBtn = new JButton("...");
        customColorBtn.setPreferredSize(new Dimension(30, 30));
        customColorBtn.setBackground(Color.LIGHT_GRAY);
        customColorBtn.setToolTipText("Wybierz własny kolor");
        customColorBtn.addActionListener(_ -> {
            Color chosen = JColorChooser.showDialog(this, "Wybierz kolor", selectedColor);
            if (chosen != null) {
                selectedColor = chosen;
                drawPanel.setColor(chosen);
                customColorBtn.setBackground(chosen);
            }
        });
        colorPanel.add(customColorBtn);

        return colorPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PaintWin7::new);
    }

    // ------------------------- DRAW PANEL -------------------------
    class DrawPanel extends JPanel {
        private BufferedImage canvasImage = null;
        private Point start, current;
        private Point lineStart = null;
        private String tool = "Brush";
        private int brushSize = 3;
        private boolean dragging = false;
        private Color color = selectedColor;
        private boolean shiftDown = false;
        private int alpha = 255;

        // --- Dla skalowania obrazka ---
        private BufferedImage tempImage = null;
        private Point tempImageStart = null;
        private Point tempImageEnd = null;
        private boolean insertingImage = false;

        public void setAlpha(int a) {
            this.alpha = (int)Math.round((a - 1) * 255.0 / 99.0);
        }

        public DrawPanel() {
            setBackground(Color.WHITE);

            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    shiftDown = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
                    requestFocusInWindow();
                    start = e.getPoint();
                    current = start;
                    if (insertingImage && tempImage != null) {
                        tempImageStart = e.getPoint();
                        tempImageEnd = tempImageStart;
                        dragging = true;
                    } else {
                        dragging = true;
                        if (tool.equals("Brush") || tool.equals("Eraser")) {
                            drawLine(start, start);
                        } else if (tool.equals("Fill")) {
                            floodFill(e.getX(), e.getY(), color);
                        } else if (tool.equals("Line")) {
                            lineStart = e.getPoint();
                            current = e.getPoint();
                        }
                    }
                    repaint();
                }

                public void mouseDragged(MouseEvent e) {
                    shiftDown = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
                    if (insertingImage && tempImage != null && dragging) {
                        tempImageEnd = e.getPoint();
                        repaint();
                        return;
                    }
                    if (!dragging) return;
                    current = e.getPoint();
                    if (tool.equals("Brush") || tool.equals("Eraser")) {
                        drawLine(start, current);
                        start = current;
                    }
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    shiftDown = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
                    if (insertingImage && tempImage != null && tempImageStart != null && tempImageEnd != null) {
                        int x = Math.min(tempImageStart.x, tempImageEnd.x);
                        int y = Math.min(tempImageStart.y, tempImageEnd.y);
                        int w = Math.abs(tempImageStart.x - tempImageEnd.x);
                        int h = Math.abs(tempImageStart.y - tempImageEnd.y);
                        if (w > 0 && h > 0) {
                            Graphics2D g2 = getCanvasImage().createGraphics();
                            g2.drawImage(tempImage, x, y, w, h, null);
                            g2.dispose();
                        }
                        tempImage = null;
                        tempImageStart = null;
                        tempImageEnd = null;
                        insertingImage = false;
                        setTool("Brush");
                        dragging = false;
                        repaint();
                        return;
                    }
                    if (tool.equals("Rectangle") || tool.equals("Ellipse")) {
                        drawShape(start, e.getPoint());
                    } else if (tool.equals("Line") && lineStart != null) {
                        drawStraightLine(lineStart, e.getPoint());
                        lineStart = null;
                    }
                    dragging = false;
                    repaint();
                }
            };

            addMouseListener(adapter);
            addMouseMotionListener(adapter);
            setFocusable(true);
        }

        public void setTool(String tool) {
            this.tool = tool;
        }

        public void setColor(Color c) {
            this.color = c;
        }

        public void setBrushSize(int size) {
            this.brushSize = size;
        }

        public void clearCanvas() {
            if (canvasImage != null) {
                Graphics2D g2 = canvasImage.createGraphics();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                repaint();
            }
        }

        public void saveImage(File file) {
            try {
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(canvasImage, "PNG", file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage());
            }
        }

        public void insertImage(File file) {
            try {
                tempImage = ImageIO.read(file);
                insertingImage = true;
                tempImageStart = null;
                tempImageEnd = null;
                setTool("InsertImage");
                repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        }

        private void drawLine(Point p1, Point p2) {
            Graphics2D g2 = getCanvasImage().createGraphics();
            Color drawColor = tool.equals("Eraser") ? new Color(255,255,255,255) : new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(drawColor);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            g2.dispose();
        }

        private void drawStraightLine(Point p1, Point p2) {
            Graphics2D g2 = getCanvasImage().createGraphics();
            Color drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(drawColor);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            g2.dispose();
        }

        private void drawShape(Point p1, Point p2) {
            Graphics2D g2 = getCanvasImage().createGraphics();
            Color drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setColor(drawColor);
            g2.setStroke(new BasicStroke(brushSize));
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int w = Math.abs(p1.x - p2.x);
            int h = Math.abs(p1.y - p2.y);
            if (tool.equals("Rectangle")) {
                if (shiftDown) {
                    int size = Math.min(w, h);
                    g2.drawRect(x, y, size, size);
                } else {
                    g2.drawRect(x, y, w, h);
                }
            } else if (tool.equals("Ellipse")) {
                if (shiftDown) {
                    int size = Math.min(w, h);
                    g2.drawOval(x, y, size, size);
                } else {
                    g2.drawOval(x, y, w, h);
                }
            }
            g2.dispose();
        }

        private void floodFill(int x, int y, Color fillColor) {
            BufferedImage image = getCanvasImage();
            int targetColor = image.getRGB(x, y);
            int replacementColor = fillColor.getRGB();
            if (targetColor == replacementColor) return;

            Queue<Point> queue = new LinkedList<>();
            queue.add(new Point(x, y));

            while (!queue.isEmpty()) {
                Point p = queue.poll();
                int px = p.x, py = p.y;
                if (px < 0 || py < 0 || px >= image.getWidth() || py >= image.getHeight()) continue;
                if (image.getRGB(px, py) != targetColor) continue;
                image.setRGB(px, py, replacementColor);
                queue.add(new Point(px + 1, py));
                queue.add(new Point(px - 1, py));
                queue.add(new Point(px, py + 1));
                queue.add(new Point(px, py - 1));
            }
        }

        private BufferedImage getCanvasImage() {
            if (canvasImage == null || canvasImage.getWidth() != getWidth() || canvasImage.getHeight() != getHeight()) {
                canvasImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = canvasImage.createGraphics();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
            return canvasImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (canvasImage != null) {
                g.drawImage(canvasImage, 0, 0, null);
            }
            // Podgląd linii podczas rysowania
            if (tool.equals("Line") && dragging && lineStart != null && current != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                g2.drawLine(lineStart.x, lineStart.y, current.x, current.y);
                g2.dispose();
            }
            // Podgląd prostokąta podczas rysowania
            if (tool.equals("Rectangle") && dragging && start != null && current != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(brushSize));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                int x = Math.min(start.x, current.x);
                int y = Math.min(start.y, current.y);
                int w = Math.abs(start.x - current.x);
                int h = Math.abs(start.y - current.y);
                if (shiftDown) {
                    int size = Math.min(w, h);
                    g2.drawRect(x, y, size, size);
                } else {
                    g2.drawRect(x, y, w, h);
                }
                g2.dispose();
            }
            // Podgląd elipsy podczas rysowania
            if (tool.equals("Ellipse") && dragging && start != null && current != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(brushSize));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                int x = Math.min(start.x, current.x);
                int y = Math.min(start.y, current.y);
                int w = Math.abs(start.x - current.x);
                int h = Math.abs(start.y - current.y);
                if (shiftDown) {
                    int size = Math.min(w, h);
                    g2.drawOval(x, y, size, size);
                } else {
                    g2.drawOval(x, y, w, h);
                }
                g2.dispose();
            }
            // Podgląd skalowanego obrazka
            if (insertingImage && tempImage != null && tempImageStart != null && tempImageEnd != null) {
                int x = Math.min(tempImageStart.x, tempImageEnd.x);
                int y = Math.min(tempImageStart.y, tempImageEnd.y);
                int w = Math.abs(tempImageStart.x - tempImageEnd.x);
                int h = Math.abs(tempImageStart.y - tempImageEnd.y);
                if (w > 0 && h > 0) {
                    g.drawImage(tempImage, x, y, w, h, null);
                    g.setColor(Color.GRAY);
                    ((Graphics2D)g).setStroke(new BasicStroke(2));
                    g.drawRect(x, y, w, h);
                }
            }
        }
    }
}