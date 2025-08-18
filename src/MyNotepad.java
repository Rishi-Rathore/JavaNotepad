import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.*;

public class MyNotepad extends JFrame {
    JTabbedPane tabbedPane;
    JLabel statusBar;

    public MyNotepad() {
        setTitle("Advanced Notepad");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // TabbedPane add kiya (multi-tab ke liye)
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Default ek blank tab
        addNewTab("Untitled");

        // MenuBar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitFile = new JMenuItem("Exit");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exitFile);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutText = new JMenuItem("Cut");
        JMenuItem copyText = new JMenuItem("Copy");
        JMenuItem pasteText = new JMenuItem("Paste");

        editMenu.add(cutText);
        editMenu.add(copyText);
        editMenu.add(pasteText);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        // Status Bar
        statusBar = new JLabel("Line: 1, Col: 1 | Words: 0 | Chars: 0");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);

        // File Actions
        newFile.addActionListener(e -> addNewTab("Untitled"));

        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    addNewTab(file.getName());
                    JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
                    JTextArea area = (JTextArea) scrollPane.getViewport().getView();
                    area.read(br, null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveFile.addActionListener(e -> {
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
            JTextArea area = (JTextArea) scrollPane.getViewport().getView();

            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    area.write(bw);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exitFile.addActionListener(e -> System.exit(0));

        // Edit actions
        cutText.addActionListener(e -> getCurrentTextArea().cut());
        copyText.addActionListener(e -> getCurrentTextArea().copy());
        pasteText.addActionListener(e -> getCurrentTextArea().paste());

        setVisible(true);
    }

    // ======= New Tab banane ka method =======
    private void addNewTab(String title) {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(area);

        tabbedPane.addTab(title, scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);

        // Caret Listener for line/col
        area.addCaretListener(e -> updateStatusBar(area));

        // Document Listener for word/char count
        area.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStatusBar(area); }
            public void removeUpdate(DocumentEvent e) { updateStatusBar(area); }
            public void changedUpdate(DocumentEvent e) { updateStatusBar(area); }
        });
    }

    // ======= Status bar update =======
    private void updateStatusBar(JTextArea area) {
        try {
            int caretPos = area.getCaretPosition();
            int line = area.getLineOfOffset(caretPos);
            int col = caretPos - area.getLineStartOffset(line);

            String text = area.getText();
            int words = (text.trim().isEmpty()) ? 0 : text.trim().split("\\s+").length;
            int chars = text.length();

            statusBar.setText("Line: " + (line + 1) + ", Col: " + (col + 1) +
                    " | Words: " + words + " | Chars: " + chars);
        } catch (Exception ex) {
            statusBar.setText("Error calculating position!");
        }
    }

    // ======= Current TextArea (jo tab active hai) =======
    private JTextArea getCurrentTextArea() {
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        return (JTextArea) scrollPane.getViewport().getView();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Notepad::new);
    }
}
