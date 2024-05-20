import javax.swing.*;
import java.awt.*;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cave");
            frame.setPreferredSize(new Dimension(400, 400));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Cave cave = new Cave();
            frame.getContentPane().add(cave);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
             // Set smaller size
        });
    }
}

