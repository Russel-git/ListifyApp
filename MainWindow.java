import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;


public class MainWindow {
    public MainWindow() {
        // Create the main frame
        JFrame frame = new JFrame("Main Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        ImageIcon image = new ImageIcon("C:\\Users\\RUSSEL\\Downloads\\logooo.jpg");
        frame.setIconImage(image.getImage());

        // Add a label
        JLabel label = new JLabel("Welcome to Listify", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(label, BorderLayout.NORTH);

        // Add a start button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 33));
        frame.add(startButton, BorderLayout.CENTER);

        // Button action listener
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close main window
                new ListifyApp2(); // Open Listify application
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}

