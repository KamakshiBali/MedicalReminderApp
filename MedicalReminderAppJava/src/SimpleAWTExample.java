import java.awt.*;
import java.awt.event.*;

public class SimpleAWTExample {
    public static void main(String[] args) {
        // Create a frame (window)
        Frame frame = new Frame("AWT Example");

        // Create a button
        Button button = new Button("Click Me");

        // Set the layout manager of the frame
        frame.setLayout(new FlowLayout());

        // Add the button to the frame
        frame.add(button);

        // Define the button click behavior using ActionListener
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked!");
            }
        });

        // Set the frame size and make it visible
        frame.setSize(300, 200);
        frame.setVisible(true);

        // Close the application when the window is closed
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
}
