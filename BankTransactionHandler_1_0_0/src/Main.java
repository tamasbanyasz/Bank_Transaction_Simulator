import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.*;

import ThreadHandler.MainFrame;
import ThreadHandler.SharedCounter;

public class Main {

    // Reference to the GUI window so it can be reopened from the system tray
    private static MainFrame gui;

    public static void main(String[] args) {
        BankRunner runner = new BankRunner(); // Initializes and prepares the bank simulation logic
        BlockingQueue<Integer> queue = runner.getSharedQueue(); // Retrieves the shared queue used by the simulation
        runner.start(); // Starts the simulation in a separate thread

        // Launch the GUI on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SharedCounter sharedCounter = new SharedCounter(); // Shared total sum counter for all threads
            gui = new MainFrame(queue, sharedCounter); // GUI window that displays the queue and thread info
            gui.setVisible(true);
            
            // Don't fully exit the application when GUI is closed — just hide it. Little '^' icon (Hided icons) at right bottom
            gui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 

            // Setup tray icon with menu options
            setupSystemTray(sharedCounter, queue);
        });
    }

    /**
     * Sets up the system tray icon with menu items for re-opening the GUI and exiting the application.
     * 
     * @param sharedCounter Shared total value from all processing threads
     * @param queue The shared queue used by the simulation
     */
    private static void setupSystemTray(SharedCounter sharedCounter, BlockingQueue<Integer> queue) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported.");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // Use a valid image file here

        // Create a pop-up menu for the tray icon
        PopupMenu popup = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(image, "Bank Threads App", popup);

        // Menu item to reopen the GUI
        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(_ -> {
            SwingUtilities.invokeLater(() -> {
                if (gui == null || !gui.isDisplayable()) {
                    // If the window has been disposed, recreate it
                    gui = new MainFrame(queue, sharedCounter);
                } else {
                    gui.setVisible(true); // Make the GUI visible
                    gui.setExtendedState(JFrame.NORMAL); // Restore from minimized
                }
            });
        });

        // Menu item to exit the application completely
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(_ -> {
            tray.remove(trayIcon); // Remove icon from tray
            System.exit(0); // Terminate the application
        });

        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setImageAutoSize(true); // Automatically resize icon to fit tray
        trayIcon.setToolTip("Bank Threads App");

        try {
            tray.add(trayIcon); // Add the icon to the system tray
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

        // Handle double-click on the tray icon to open the GUI
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate clicking "Open"
                openItem.dispatchEvent(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }
}
