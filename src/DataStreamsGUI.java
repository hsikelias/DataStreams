import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsGUI extends JFrame{
    private JTextArea originalArea;
    private JTextArea filteredArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private JLabel statusLabel;
    private Path selectedFile;





    public DataStreamsGUI(){
        setTitle("DataStreams: File Search");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 0, 10)
        );

        searchField = new JTextField();
        searchButton = new JButton("Search file");
        searchButton.setEnabled(false);

        searchPanel.add(new JLabel("Search string:"), BorderLayout.WEST);


        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        originalArea = new JTextArea();
        filteredArea = new JTextArea();

        originalArea.setEditable(false);
        filteredArea.setEditable(false);

        originalArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        filteredArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane originalScroll = new JScrollPane(originalArea);
        JScrollPane filteredScroll = new JScrollPane(filteredArea);
        JPanel originalPanel = new JPanel(new BorderLayout());
        JPanel filteredPanel = new JPanel(new BorderLayout());



        originalPanel.add(new JLabel("Original file"), BorderLayout.NORTH);
        originalPanel.add(originalScroll, BorderLayout.CENTER);

        filteredPanel.add(new JLabel("Filtered results"), BorderLayout.NORTH);
        filteredPanel.add(filteredScroll, BorderLayout.CENTER);


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                originalPanel,
                filteredPanel
        );

        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());


        loadButton = new JButton("Load file");
        quitButton = new JButton("Quit");

        buttonPanel.add(loadButton);
        buttonPanel.add(quitButton);

        statusLabel = new JLabel("No file loaded");

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void loadFile(){
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);


        if (result == JFileChooser.APPROVE_OPTION) {
            Path newPath = chooser.getSelectedFile().toPath();
            try {
                String content = Files.readString(newPath);

                selectedFile = newPath;
                originalArea.setText(content);
                filteredArea.setText("");
                originalArea.setCaretPosition(0);
                searchButton.setEnabled(true);

                statusLabel.setText(
                        "Loaded: " + selectedFile.getFileName()
                );

            } catch (IOException e){
                JOptionPane.showMessageDialog(
                        this,
                        "Error loading the file: " + e.getMessage(),
                        "File error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }



    private void searchFile() {
        if (selectedFile == null) {
            return;
        }

        String searchString = searchField.getText();

        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter search string."
            );
            return;
        }



        try (Stream<String> lines = Files.lines(selectedFile)){

            List<String> matches = lines
                    .filter(line -> line.contains(searchString))
                    .collect(Collectors.toList());

            filteredArea.setText(
                    String.join(System.lineSeparator(), matches)
            );
            filteredArea.setCaretPosition(0);
            statusLabel.setText(
                    "Found " + matches.size() + " matching lines."
            );

        } catch (IOException e){
            JOptionPane.showMessageDialog(
                    this,
                    "Error searching file: " + e.getMessage(),
                    "Search error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new DataStreamsGUI();});
    }
}
