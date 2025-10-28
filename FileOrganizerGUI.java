import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FileOrganizerGUI extends JFrame {

    private final JTextField directoryField;
    private final JTextArea logArea;
    private final JButton organizeButton;
    private Path selectedDirectory;

    public FileOrganizerGUI() {
        super("Smart File Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        directoryField = new JTextField(40);
        directoryField.setEditable(false);
        directoryField.setText("Select a directory to organize...");

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(this::browseDirectory);
        
        topPanel.add(new JLabel("Target Directory: "), BorderLayout.WEST);
        topPanel.add(directoryField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        add(scrollPane, BorderLayout.CENTER);

        organizeButton = new JButton("Organize Files");
        organizeButton.setEnabled(false);
        organizeButton.addActionListener(this::startOrganization);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(organizeButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        setLocationRelativeTo(null);
    }

    private void browseDirectory(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Directory to Organize");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = fileChooser.getSelectedFile().toPath();
            selectedDirectory = path;
            directoryField.setText(path.toString());
            organizeButton.setEnabled(true);
            logArea.setText("Directory selected. Press 'Organize Files' to start.");
        }
    }

    private void startOrganization(ActionEvent event) {
        if (selectedDirectory == null || !Files.isDirectory(selectedDirectory)) {
            JOptionPane.showMessageDialog(this, "Please select a valid directory first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        organizeButton.setEnabled(false);
        logArea.setText("");
        
        FileOrganizerWorker worker = new FileOrganizerWorker(selectedDirectory);
        worker.execute();
    }

    private void appendToLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private class FileOrganizerWorker extends SwingWorker<Map<String, Integer>, String> {
        
        private final Path dir;

        public FileOrganizerWorker(Path dir) {
            this.dir = dir;
        }

        @Override
        protected Map<String, Integer> doInBackground() throws Exception {
            return OrganizerLogic.organize(dir, this::publish);
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String message : chunks) {
                appendToLog(message);
            }
        }

        @Override
        protected void done() {
            organizeButton.setEnabled(true);
            
            try {
                Map<String, Integer> summary = get();
                
                appendToLog("\n--- ORGANIZATION COMPLETE! ---");
                summary.forEach((folder, count) -> 
                    appendToLog(String.format("Successfully moved %d files into the '%s' folder.", count, folder)));
                appendToLog("-----------------------------");

            } catch (InterruptedException | ExecutionException e) {
                appendToLog("\nFATAL ERROR during organization: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileOrganizerGUI().setVisible(true);
        });
    }
}