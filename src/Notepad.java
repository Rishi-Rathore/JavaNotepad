import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Notepad extends JFrame {
    JTextArea textArea;
    JLabel statusBar;

    public Notepad() {
        setTitle("Advanced Notepad");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // ---------------- MENU BAR ----------------
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitFile = new JMenuItem("Exit");

        // Keyboard Shortcuts
        newFile.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        openFile.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exitFile);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutText = new JMenuItem("Cut");
        JMenuItem copyText = new JMenuItem("Copy");
        JMenuItem pasteText = new JMenuItem("Paste");
        JMenuItem findReplace = new JMenuItem("Find & Replace");

        // Keyboard Shortcuts
        cutText.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        copyText.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        pasteText.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        editMenu.add(cutText);
        editMenu.add(copyText);
        editMenu.add(pasteText);
        editMenu.addSeparator();
        editMenu.add(findReplace);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem darkMode = new JMenuItem("Toggle Dark Mode");
        JMenuItem fontSettings = new JMenuItem("Font Settings");
        toolsMenu.add(darkMode);
        toolsMenu.add(fontSettings);

        // Add Menus to MenuBar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        setJMenuBar(menuBar);

        // ---------------- STATUS BAR ----------------
        statusBar = new JLabel(" Words: 0 | Characters: 0");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);

        // ---------------- ACTIONS ----------------

        // File Handling
        newFile.addActionListener(e -> textArea.setText(""));

        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    textArea.read(br, null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    textArea.write(bw);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exitFile.addActionListener(e -> System.exit(0));

        // Edit Menu
        cutText.addActionListener(e -> textArea.cut());
        copyText.addActionListener(e -> textArea.copy());
        pasteText.addActionListener(e -> textArea.paste());

        findReplace.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField findField = new JTextField();
            JTextField replaceField = new JTextField();
            panel.add(new JLabel("Find:"));
            panel.add(findField);
            panel.add(new JLabel("Replace with:"));
            panel.add(replaceField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Find & Replace",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String find = findField.getText();
                String replace = replaceField.getText();
                String text = textArea.getText();
                textArea.setText(text.replace(find, replace));
            }
        });

        // Dark Mode
        darkMode.addActionListener(e -> {
            Color bg = textArea.getBackground();
            if (bg.equals(Color.WHITE)) {
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.WHITE);
                statusBar.setForeground(Color.WHITE);
                statusBar.setBackground(Color.DARK_GRAY);
            } else {
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                statusBar.setForeground(Color.BLACK);
                statusBar.setBackground(null);
            }
        });

        // Font Settings
        fontSettings.addActionListener(e -> {
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            String font = (String) JOptionPane.showInputDialog(
                    this, "Choose Font:", "Font Settings",
                    JOptionPane.PLAIN_MESSAGE, null, fonts, textArea.getFont().getFamily());

            if (font != null) {
                String sizeStr = JOptionPane.showInputDialog("Enter Font Size:", textArea.getFont().getSize());
                try {
                    int size = Integer.parseInt(sizeStr);
                    textArea.setFont(new Font(font, Font.PLAIN, size));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid size!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Word Count Update
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            private void updateCount() {
                String text = textArea.getText();
                int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
                int chars = text.length();
                statusBar.setText(" Words: " + words + " | Characters: " + chars);
            }

            public void insertUpdate(DocumentEvent e) {
                updateCount();
            }

            public void removeUpdate(DocumentEvent e) {
                updateCount();
            }

            public void changedUpdate(DocumentEvent e) {
                updateCount();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Notepad());
    }
}
