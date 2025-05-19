package ThreadHandler;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import javax.swing.*;

public class MainFrame extends JFrame {
	// If wanted to send this GUI through network. It's help to convert the data to byte.
	private static final long serialVersionUID = 1L;
	
	
    private final JTextArea outputArea = new JTextArea();

    private final static Map<String, JLabel> queueLabels = new HashMap<>();
    private final Map<String, JLabel> accQueueLabels = new HashMap<>();

    private final JLabel accQueueStatusLabel = new JLabel("Shared acc_queue size: 0");
    private final JLabel accQueueContentLabel = new JLabel("Contents: ");

    private final BlockingQueue<Integer> acc_queue;

    public MainFrame(BlockingQueue<Integer> queue, SharedCounter sharedCounter) {
        this.acc_queue = queue;

        setTitle("Threads handler GUI - With shared AtomicInteger");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // Output log area
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Events"));
        add(scrollPane, BorderLayout.CENTER);

        // Display shared acc_queue size and contents
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(accQueueStatusLabel);
        topPanel.add(accQueueContentLabel);
        add(topPanel, BorderLayout.NORTH);

        // Panel for thread controls
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        add(bottomPanel, BorderLayout.SOUTH);

        for (int i = 1; i <= 3; i++) {
            String threadName = "Thread-" + i;

            JPanel rowPanel = new JPanel(new BorderLayout());

            JLabel queueLabel = new JLabel(threadName + " queue: ");
            queueLabels.put(threadName, queueLabel);

            JButton startBtn = new JButton("Start");
            JButton stopBtn = new JButton("Stop");

            stopBtn.setBackground(Color.RED);
            stopBtn.setForeground(Color.WHITE);
            stopBtn.setFocusable(false);

            startBtn.setBackground(null);
            startBtn.setForeground(Color.BLACK);
            startBtn.setFocusable(false);

            ProducerTask task = new ProducerTask(queue, threadName, sharedCounter, this);
            Thread thread = new Thread(task);
            thread.start();

            startBtn.addActionListener(_ -> {
                task.start();
                queueLabel.setText(threadName + " queue: ");
                startBtn.setBackground(Color.GREEN);
                startBtn.setForeground(Color.WHITE);
                stopBtn.setBackground(null);
                stopBtn.setForeground(Color.BLACK);
            });

            stopBtn.addActionListener(_ -> {
                task.stop();
                stopBtn.setBackground(Color.RED);
                stopBtn.setForeground(Color.WHITE);
                startBtn.setBackground(null);
                startBtn.setForeground(Color.BLACK);
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(startBtn);
            buttonPanel.add(stopBtn);

            rowPanel.add(queueLabel, BorderLayout.CENTER);
            rowPanel.add(buttonPanel, BorderLayout.EAST);

            bottomPanel.add(rowPanel);
        }

        // Timer to update acc_queue size and contents every 500ms
        Timer timer = new Timer(500, _ -> updateAccQueueSize());
        timer.start();

        setVisible(true);
    }

    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    // Updates the label for shared acc_queue size and its contents
    private void updateAccQueueSize() {
        SwingUtilities.invokeLater(() -> {
            accQueueStatusLabel.setText("Shared acc_queue size: " + acc_queue.size());
            accQueueContentLabel.setText("Contents: " + acc_queue.toString());
        });
    }

    /**
     * Updates the GUI label showing the current state of the account queue for a specific thread.
     * 
     * @param threadName the name of the thread (used as a key to find the correct label)
     * @param text the text to be displayed on the label
     */
    public void setAccQueueDisplay(String threadName, String text) {
        // Ensures that GUI updates are done on the Event Dispatch Thread (EDT), which is required by Swing
        SwingUtilities.invokeLater(() -> {
            JLabel label = accQueueLabels.get(threadName); // Get the label associated with the thread name
            if (label != null) {
                label.setText(text); // Set the label text
            }
        });
    }

    /**
     * Static method to update the GUI label showing the last value taken from the queue for a specific thread.
     * 
     * @param threadName the name of the thread (used as a key to find the correct label)
     * @param text the text to be displayed on the label
     */
    public static void setThreadQueueDisplay(String threadName, String text) {
        // Updates must be performed on the Swing Event Dispatch Thread to avoid concurrency issues
        SwingUtilities.invokeLater(() -> {
            JLabel label = queueLabels.get(threadName); // Get the label for the thread
            if (label != null) {
                label.setText(threadName + " queue: " + text); // Display formatted text with thread name
            }
        });
    }

}
