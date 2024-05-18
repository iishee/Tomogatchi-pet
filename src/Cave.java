import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cave extends JPanel {
    private static final int DELAY = 100; // Delay between frame updates in milliseconds
    private static final int FRAME_COUNT = 11; // Number of frames in the animation
    private int currentFrameIndex = 0; // Current frame index
    private final Image[] originalFrames; // Array to store the original frames
    private Timer animationTimer; // Timer for animation loop
    private Timer transitionTimer; // Timer for transitions
    private int xPos = 0; // X position for transition
    private boolean mirrored = false; // Flag to indicate if the image should be mirrored
    private ImageIcon imageIcon; // ImageIcon for the additional image
    private ImageIcon caveBackground; // ImageIcon for the cave background
    private int iconX = 50; // X position for the ImageIcon
    private int iconY = 600; // Y position for the ImageIcon
    private int iconWidth = 100; // Width of the ImageIcon
    private int iconHeight = 100; // Height of the ImageIcon
    private List<DraggableImage> draggableImages; // List to store draggable images
    private DraggableImage draggedImage = null; // Reference to the currently dragged image
    private int count = 100; // Initial value for the hunger bar
    private JProgressBar hunger; // Progress bar to represent hunger

    public Cave() {
        hunger = new JProgressBar();
        hunger.setValue(count);
        setLayout(null); // Set layout to null for absolute positioning
        hunger.setBounds(10, 10, 200, 30); // Position and size of the hunger bar
        add(hunger); // Add the hunger bar to the panel

        originalFrames = new Image[FRAME_COUNT];
        loadFrames();

        imageIcon = new ImageIcon("C:\\Users\\User\\IdeaProjects\\TOMO\\src\\PixelApple.png");
        caveBackground = new ImageIcon("C:\\Users\\User\\IdeaProjects\\TOMO\\src\\cave.png");
        draggableImages = new ArrayList<>();

        initializeAnimationTimer();
        initializeMouseListeners();
    }

    // Method to initialize and start the animation timer
    private void initializeAnimationTimer() {
        animationTimer = new Timer(DELAY, e -> {
            currentFrameIndex = (currentFrameIndex + 1) % FRAME_COUNT; // Increment frame index
            repaint(); // Redraw the panel
        });
        animationTimer.start();
    }

    // Method to initialize mouse listeners for dragging and transitions
    private void initializeMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedImage = null; // Stop dragging when mouse is released
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick(e);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    // Method to handle mouse pressed events
    private void handleMousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (isInsideIcon(mouseX, mouseY)) {
            DraggableImage draggableImage = new DraggableImage(imageIcon.getImage(), mouseX - iconX, mouseY - iconY);
            draggableImages.add(draggableImage);
            draggedImage = draggableImage; // Start dragging the new image
        } else {
            for (DraggableImage image : draggableImages) {
                if (image.contains(mouseX, mouseY)) {
                    draggedImage = image;
                    draggedImage.setOffset(mouseX - image.getX(), mouseY - image.getY());
                    break;
                }
            }
        }
    }

    // Method to handle double click events
    private void handleDoubleClick(MouseEvent e) {
        int mouseX = e.getX();
        int targetX = mouseX - (iconWidth / 2);
        startTransition(targetX);
    }

    // Method to handle mouse dragged events
    private void handleMouseDragged(MouseEvent e) {
        if (draggedImage != null) {
            draggedImage.setPosition(e.getX() - draggedImage.getOffsetX(), e.getY() - draggedImage.getOffsetY());
            repaint();
        }
    }

    // Method to check if a point is inside the icon area
    private boolean isInsideIcon(int x, int y) {
        return x >= iconX && x <= (iconX + iconWidth) && y >= iconY && y <= (iconY + iconHeight);
    }

    // Method to start a transition towards a target X position
    private void startTransition(int targetX) {
        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }

        transitionTimer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xPos < targetX) {
                    xPos += 4;
                    if (xPos >= targetX) {
                        xPos = targetX;
                        ((Timer) e.getSource()).stop();
                    }
                } else if (xPos > targetX) {
                    xPos -= 4;
                    if (xPos <= targetX) {
                        xPos = targetX;
                        ((Timer) e.getSource()).stop();
                    }
                }
                mirrored = targetX < xPos;
                repaint();
            }
        });
        transitionTimer.start();
    }

    // Method to load frames for the animation
    private void loadFrames() {
        try {
            for (int i = 0; i < FRAME_COUNT; i++) {
                String imagePath = "C:\\Users\\User\\IdeaProjects\\TOMO\\src\\BEar" + (i + 1) + ".png";
                originalFrames[i] = ImageIO.read(new File(imagePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCaveBackground(g);
        drawAnimationFrame(g);
        drawImageIcon(g);
        drawDraggableImages(g);
    }

    // Method to draw the cave background
    private void drawCaveBackground(Graphics g) {
        if (caveBackground != null) {
            Image img = caveBackground.getImage();
            g.drawImage(img, 0, 0, 1920, 1080, this);
        }
    }

    // Method to draw the current animation frame
    private void drawAnimationFrame(Graphics g) {
        if (originalFrames[currentFrameIndex] != null) {
            Image originalImage = originalFrames[currentFrameIndex];
            int newWidth = originalImage.getWidth(null) * 10; // Scaling width
            int newHeight = originalImage.getHeight(null) * 10; // Scaling height
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform originalTransform = g2d.getTransform(); // Save the original transform

            if (mirrored) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-newWidth, 0);
                g2d.setTransform(tx);
            }

            g2d.drawImage(originalImage, mirrored ? -xPos : xPos, getHeight() - newHeight, newWidth, newHeight, this);
            g2d.setTransform(originalTransform); // Restore the original transform
        }
    }

    // Method to draw the ImageIcon
    private void drawImageIcon(Graphics g) {
        if (imageIcon != null) {
            Image img = imageIcon.getImage();
            g.drawImage(img, iconX, iconY, iconWidth, iconHeight, this);
        }
    }

    // Method to draw all draggable images
    private void drawDraggableImages(Graphics g) {
        for (DraggableImage draggableImage : draggableImages) {
            draggableImage.draw(g);
        }
    }

    // Method to decrement the hunger bar
    public void fill() {
        Timer fillTimer = new Timer(50, e -> {
            if (count > 0) {
                count--;
                hunger.setValue(count);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        fillTimer.start();
    }

    // Class to represent a draggable image
    private static class DraggableImage {
        private final Image image;
        private int x;
        private int y;
        private int offsetX;
        private int offsetY;

        public DraggableImage(Image image, int offsetX, int offsetY) {
            this.image = image;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setOffset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public boolean contains(int px, int py) {
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            return px >= x && px <= (x + width) && py >= y && py <= (y + height);
        }

        public void draw(Graphics g) {
            int width = 100;
            int height = 100;
            g.drawImage(image, x, y, width, height, null);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cave Animation");
        Cave cavePanel = new Cave();
        frame.add(cavePanel);
        frame.setSize(1920, 1080); // Set frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
