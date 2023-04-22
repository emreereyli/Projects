package com.kickstart;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {

    private JMenuItem theme;
    private JMenu file;
    private JMenuItem New;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem exit;
    private JMenu settings;
    private JMenu help;
    private JMenuItem Default;
    private JMenuItem font;
    private static JTextArea text;
    private JMenuBar bar;
    private static Highlighter.HighlightPainter myHighlightPainter;
    private static JFileChooser jfc;
    private static TextLineNumber tln;
    private static Main gui;
    private static UndoManager undo;

    public static String nameOfTheme = "";
    public static Integer fontSize = 16;

    private String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();


    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setUIFont (new javax.swing.plaf.FontUIResource("Sans Serif", Font.BOLD,13));

        ImageIcon image = new ImageIcon("C:\\Users\\user\\Downloads\\icon4.png");
        setIconImage(image.getImage());

        myHighlightPainter = new MyHighlightPainter(Color.decode("#c2c2c2"));

        UIManager.put("FileChooser.saveButtonText", "OK");
        UIManager.put("FileChooser.saveButtonToolTipText", "Do action with selected file");
        UIManager.put("Menu.selectionBackground", Color.WHITE);
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("Menu.background", Color.WHITE);
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("Menu.opaque", false);
        UIManager.put("MenuItem.selectionBackground", Color.WHITE);
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("MenuItem.foreground", Color.BLACK);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("Menu.border", null);
        UIManager.put("MenuItem.border", null);
        UIManager.put("TextArea.selectionBackground", new Color(0,0,0,0));
        UIManager.put("TextArea.selectionForeground", Color.decode("#999999"));

        getContentPane().setBackground(Color.WHITE);

        bar = new JMenuBar();
        bar.setBorder(BorderFactory.createRaisedBevelBorder());
        bar.setBackground(Color.WHITE);
        bar.setForeground(Color.WHITE);
        setJMenuBar(bar);

        file = new JMenu("File") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        file.setPreferredSize(new Dimension(40, 30));
        file.setFont(file.getFont().deriveFont(16.0f));
        file.setToolTipText("   File menu   ");
        file.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bar.add(file);

        New = new JMenuItem("New") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        New.setFont(New.getFont().deriveFont(15.0f));
        New.setToolTipText("   New file   ");
        New.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        New.addActionListener(e -> {
            JCTextField create = new JCTextField();
            create.setPlaceholder("Example: file.txt");

            Object[] menu = {
                    "Enter a new file name ( with extension ): ", create
            };

            String[] options = new String[2];
            options[0] = "Create";
            options[1] = "Cancel";

            int value = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "New file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
            if(value == 0) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("New file");
                File file = new File(create.getText());
                jfc.setSelectedFile(file);
                jfc.setDialogType(0);
                int returnValue = jfc.showSaveDialog(null);
                setTitle(create.getText() + " - Text editor");
                try {
                    if(returnValue == JFileChooser.APPROVE_OPTION) {
                        File new_file = jfc.getSelectedFile();
                        if(!new_file.createNewFile()) {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.out.println("Canceled");
                    }
                } catch(Exception efg) {
                    efg.printStackTrace();
                    JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't create a new file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("Canceled");
            }
        });
        file.add(New);

        open = new JMenuItem("Open") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        open.setFont(open.getFont().deriveFont(15.0f));
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.setToolTipText("   Open file   ");
        open.addActionListener(e -> {
            jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Open file");
            int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                try {
                    Scanner sc = new Scanner(selectedFile);
                    String value = "";
                    while(sc.hasNextLine()) {
                        String a = sc.nextLine();
                        value = value.concat(a + '\n');
                    }
                    text.setText(value);
                    String name = jfc.getSelectedFile().getName();
                    setTitle(name + " - Text editor");
                } catch(FileNotFoundException ef) {
                    ef.printStackTrace();
                }
            }
        });
        file.add(open);

        save = new JMenuItem("Save") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        save.setFont(save.getFont().deriveFont(15.0f));
        save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        save.setToolTipText("   Save file   ");
        save.addActionListener(e -> {
            try {
                String path = jfc.getSelectedFile().getPath();
                try {
                    File file = new File(path);
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    out.write(text.getText());
                    out.close();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ef) {
                ef.printStackTrace();
                JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        file.add(save);

        exit = new JMenuItem("Exit") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        exit.setFont(exit.getFont().deriveFont(15.0f));
        exit.setToolTipText("   Exit editor   ");
        exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exit.addActionListener(e -> {
            System.exit(0); // Quit program
        });
        file.add(exit);

        settings = new JMenu("Settings") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        settings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settings.setFont(settings.getFont().deriveFont(16.0f));
        settings.setToolTipText("   Change styles   ");
        bar.add(settings);

        theme = new JMenuItem("Change theme") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        theme.setFont(theme.getFont().deriveFont(15.0f));
        theme.setToolTipText("   Change the theme   ");
        theme.addActionListener(e -> {
            final JPanel panel = new JPanel();
            final JRadioButton light_theme = new JRadioButton("Light theme\n");
            final JRadioButton dark_theme = new JRadioButton("Dark theme\n");
            final JRadioButton hacker_theme = new JRadioButton("Hacker theme\n");
            final JRadioButton monokai_theme = new JRadioButton("Kimbie Monokai theme\n");
            final JRadioButton night_blue_theme = new JRadioButton("Night blue theme\n");
            final ButtonGroup group = new ButtonGroup();

            light_theme.setSelected(true);

            panel.add(light_theme);
            panel.add(new JLabel("\n"));
            panel.add(dark_theme);
            panel.add(hacker_theme);
            panel.add(monokai_theme);
            panel.add(night_blue_theme);

            String[] themes = {"Light theme", "Dark theme", "Hacker theme", "Kimbie Monokai theme", "Night blue theme"};
            JComboBox list = new JComboBox(themes);

            group.add(light_theme);
            group.add(dark_theme);
            group.add(hacker_theme);
            group.add(monokai_theme);
            group.add(night_blue_theme);

            int result = JOptionPane.showOptionDialog(null, list, "Change theme", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            String themeName = (String)list.getSelectedItem();
            if (result == JOptionPane.OK_OPTION && themeName != null) {
                switch (themeName) {
                    case "Light theme":
                        nameOfTheme = "Light";
                        text.setSelectedTextColor(Color.decode("#999999"));
                        removeHighlights(text);
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#c2c2c2"));
                        panel.setBackground(Color.WHITE);
                        getContentPane().setBackground(Color.WHITE);
                        getContentPane().setForeground(Color.BLACK);
                        text.setBackground(Color.WHITE);
                        text.setForeground(Color.BLACK);
                        bar.setBackground(Color.WHITE);
                        bar.setForeground(Color.WHITE);
                        file.setForeground(Color.BLACK);
                        settings.setForeground(Color.BLACK);
                        help.setForeground(Color.BLACK);
                        New.setForeground(Color.BLACK);
                        New.setBackground(Color.WHITE);
                        open.setForeground(Color.BLACK);
                        open.setBackground(Color.WHITE);
                        save.setForeground(Color.BLACK);
                        save.setBackground(Color.WHITE);
                        exit.setForeground(Color.BLACK);
                        exit.setBackground(Color.WHITE);
                        Default.setForeground(Color.BLACK);
                        Default.setBackground(Color.WHITE);
                        font.setForeground(Color.BLACK);
                        font.setBackground(Color.WHITE);
                        theme.setForeground(Color.BLACK);
                        theme.setBackground(Color.WHITE);
                        break;
                    case "Dark theme":
                        nameOfTheme = "Dark";
                        text.setSelectedTextColor(Color.decode("#8c8b8b"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Dark theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#6e6e6e"));
                        panel.setBackground(Color.decode("#1E1E1E"));
                        getContentPane().setBackground(Color.decode("#1E1E1E"));
                        getContentPane().setForeground(Color.decode("#ededed"));
                        text.setBackground(Color.decode("#1E1E1E"));
                        text.setForeground(Color.decode("#ededed"));
                        bar.setBackground(Color.decode("#323232"));
                        bar.setForeground(Color.decode("#ededed"));
                        file.setForeground(Color.decode("#ededed"));
                        settings.setForeground(Color.decode("#ededed"));
                        help.setForeground(Color.decode("#ededed"));
                        New.setForeground(Color.decode("#ededed"));
                        New.setBackground(Color.decode("#323232"));
                        open.setForeground(Color.decode("#ededed"));
                        open.setBackground(Color.decode("#323232"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#ededed"));
                        save.setBackground(Color.decode("#323232"));
                        exit.setForeground(Color.decode("#ededed"));
                        exit.setBackground(Color.decode("#323232"));
                        Default.setForeground(Color.decode("#ededed"));
                        Default.setBackground(Color.decode("#323232"));
                        font.setForeground(Color.decode("#ededed"));
                        font.setBackground(Color.decode("#323232"));
                        theme.setBackground(Color.decode("#323232"));
                        theme.setForeground(Color.decode("#ededed"));
                        break;
                    case "Hacker theme":
                        nameOfTheme = "Hacker";
                        text.setSelectedTextColor(Color.decode("#3cba2f"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Hacker theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#0e2e0b"));
                        panel.setBackground(Color.decode("#000000"));
                        getContentPane().setBackground(Color.decode("#000000"));
                        getContentPane().setForeground(Color.decode("#1D5F16"));
                        text.setBackground(Color.decode("#000000"));
                        text.setForeground(Color.decode("#1D5F16"));
                        bar.setBackground(Color.decode("#0f0f0f"));
                        bar.setForeground(Color.decode("#1D5F16"));
                        file.setForeground(Color.decode("#1D5F16"));
                        settings.setForeground(Color.decode("#1D5F16"));
                        help.setForeground(Color.decode("#1D5F16"));
                        New.setForeground(Color.decode("#1D5F16"));
                        New.setBackground(Color.decode("#1E1E1E"));
                        open.setForeground(Color.decode("#1D5F16"));
                        open.setBackground(Color.decode("#1E1E1E"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#1D5F16"));
                        save.setBackground(Color.decode("#1E1E1E"));
                        exit.setForeground(Color.decode("#1D5F16"));
                        exit.setBackground(Color.decode("#1E1E1E"));
                        Default.setForeground(Color.decode("#1D5F16"));
                        Default.setBackground(Color.decode("#1E1E1E"));
                        font.setForeground(Color.decode("#1D5F16"));
                        font.setBackground(Color.decode("#1E1E1E"));
                        theme.setBackground(Color.decode("#1E1E1E"));
                        theme.setForeground(Color.decode("#1D5F16"));
                        break;
                    case "Kimbie Monokai theme":
                        nameOfTheme = "Kimbie";
                        text.setSelectedTextColor(Color.decode("#fcb068"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Kimbie Monokai theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#4f3c23"));
                        panel.setBackground(Color.decode("#221A0F"));
                        getContentPane().setBackground(Color.decode("#221A0F"));
                        text.setBackground(Color.decode("#221A0F"));
                        text.setForeground(Color.decode("#b37439"));
                        bar.setBackground(Color.decode("#221A0F"));
                        bar.setForeground(Color.decode("#b37439"));
                        file.setForeground(Color.decode("#b37439"));
                        settings.setForeground(Color.decode("#b37439"));
                        help.setForeground(Color.decode("#b37439"));
                        New.setForeground(Color.decode("#b37439"));
                        New.setBackground(Color.decode("#221A0F"));
                        open.setForeground(Color.decode("#b37439"));
                        open.setBackground(Color.decode("#221A0F"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#b37439"));
                        save.setBackground(Color.decode("#221A0F"));
                        exit.setForeground(Color.decode("#b37439"));
                        exit.setBackground(Color.decode("#221A0F"));
                        Default.setForeground(Color.decode("#b37439"));
                        Default.setBackground(Color.decode("#221A0F"));
                        font.setForeground(Color.decode("#b37439"));
                        font.setBackground(Color.decode("#221A0F"));
                        theme.setBackground(Color.decode("#221A0F"));
                        theme.setForeground(Color.decode("#b37439"));
                        break;
                    case "Night blue theme":
                        nameOfTheme = "Night";
                        text.setSelectedTextColor(Color.decode("#0298cf"));
                        removeHighlights(text);
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#6e6e6e"));
                        list.getModel().setSelectedItem("Night blue theme");
                        panel.setBackground(Color.decode("#000C18"));
                        getContentPane().setBackground(Color.decode("#000C18"));
                        text.setBackground(Color.decode("#000C18"));
                        text.setForeground(Color.decode("#ededed"));
                        bar.setBackground(Color.decode("#10182B"));
                        bar.setForeground(Color.decode("#ededed"));
                        file.setForeground(Color.decode("#ededed"));
                        settings.setForeground(Color.decode("#ededed"));
                        help.setForeground(Color.decode("#ededed"));
                        New.setForeground(Color.decode("#ededed"));
                        New.setBackground(Color.decode("#10182B"));
                        open.setForeground(Color.decode("#ededed"));
                        open.setBackground(Color.decode("#10182B"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#ededed"));
                        save.setBackground(Color.decode("#10182B"));
                        exit.setForeground(Color.decode("#ededed"));
                        exit.setBackground(Color.decode("#10182B"));
                        Default.setForeground(Color.decode("#ededed"));
                        Default.setBackground(Color.decode("#10182B"));
                        font.setForeground(Color.decode("#ededed"));
                        font.setBackground(Color.decode("#10182B"));
                        theme.setBackground(Color.decode("#10182B"));
                        theme.setForeground(Color.decode("#ededed"));
                        break;
                }
            } else {
                System.out.print("The user canceled the action.");
            }
        });
        settings.add(theme);

        font = new JMenuItem("Change font") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        font.setFont(font.getFont().deriveFont(15.0f));
        font.setToolTipText("   Change the font of the text   ");
        font.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        font.addActionListener(e -> {
            JComboBox list = new JComboBox(fonts);
            int result = JOptionPane.showOptionDialog(null, list , "Change font", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (result == JOptionPane.OK_OPTION) {
                String fontName = (String)list.getSelectedItem();
                if(fontName != null) {
                    if (fontName.equals(fonts[0])) {
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[0], Font.BOLD, 16));
                        setJavaFont(fonts[0]);
                    } else if (fontName.equals(fonts[1])) {
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[1], Font.BOLD, 16));
                        setJavaFont(fonts[1]);
                    } else if (fontName.equals(fonts[2])) {
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[2], Font.BOLD, 16));
                        setJavaFont(fonts[2]);
                    } else if (fontName.equals(fonts[3])) {
                        setJavaFont(fonts[3]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[3], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[4])) {
                        setJavaFont(fonts[4]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[4], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[5])) {
                        setJavaFont(fonts[5]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[5], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[6])) {
                        setJavaFont(fonts[6]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[6], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[7])) {
                        setJavaFont(fonts[7]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[7], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[8])) {
                        setJavaFont(fonts[8]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[8], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[9])) {
                        setJavaFont(fonts[9]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[9], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[10])) {
                        setJavaFont(fonts[9]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[10], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[11])) {
                        setJavaFont(fonts[10]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[11], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[12])) {
                        setJavaFont(fonts[11]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[12], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[13])) {
                        setJavaFont(fonts[12]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[13], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[14])) {
                        setJavaFont(fonts[13]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[14], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[15])) {
                        setJavaFont(fonts[15]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[15], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[16])) {
                        setJavaFont(fonts[16]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[16], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[17])) {
                        setJavaFont(fonts[17]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[17], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[18])) {
                        setJavaFont(fonts[18]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[18], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[19])) {
                        setJavaFont(fonts[19]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[19], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[20])) {
                        setJavaFont(fonts[20]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[20], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[21])) {
                        setJavaFont(fonts[21]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[21], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[22])) {
                        setJavaFont(fonts[22]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[22], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[23])) {
                        setJavaFont(fonts[23]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[23], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[24])) {
                        setJavaFont(fonts[24]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[24], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[25])) {
                        setJavaFont(fonts[25]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[25], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[26])) {
                        setJavaFont(fonts[26]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[26], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[27])) {
                        setJavaFont(fonts[27]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[27], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[28])) {
                        setJavaFont(fonts[28]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[28], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[29])) {
                        setJavaFont(fonts[29]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[29], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[30])) {
                        setJavaFont(fonts[30]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[30], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[31])) {
                        setJavaFont(fonts[31]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[31], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[32])) {
                        setJavaFont(fonts[32]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[32], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[33])) {
                        setJavaFont(fonts[33]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[33], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[34])) {
                        setJavaFont(fonts[34]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[34], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[35])) {
                        setJavaFont(fonts[35]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[35], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[36])) {
                        setJavaFont(fonts[36]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[36], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[37])) {
                        setJavaFont(fonts[37]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[37], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[38])) {
                        setJavaFont(fonts[38]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[38], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[39])) {
                        setJavaFont(fonts[39]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[39], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[40])) {
                        setJavaFont(fonts[40]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[40], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[41])) {
                        setJavaFont(fonts[41]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[41], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[42])) {
                        setJavaFont(fonts[42]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[42], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[43])) {
                        setJavaFont(fonts[43]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[43], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[44])) {
                        setJavaFont(fonts[44]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[44], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[45])) {
                        setJavaFont(fonts[45]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[45], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[46])) {
                        setJavaFont(fonts[46]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[46], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[47])) {
                        setJavaFont(fonts[47]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[47], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[48])) {
                        setJavaFont(fonts[48]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[48], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[49])) {
                        setJavaFont(fonts[49]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[49], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[50])) {
                        setJavaFont(fonts[50]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[50], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[51])) {
                        setJavaFont(fonts[51]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[51], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[52])) {
                        setJavaFont(fonts[52]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[52], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[53])) {
                        setJavaFont(fonts[53]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[53], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[54])) {
                        setJavaFont(fonts[54]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[54], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[55])) {
                        setJavaFont(fonts[55]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[55], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[56])) {
                        setJavaFont(fonts[56]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[56], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[57])) {
                        setJavaFont(fonts[57]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[57], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[58])) {
                        setJavaFont(fonts[58]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[58], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[59])) {
                        setJavaFont(fonts[59]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[59], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[60])) {
                        setJavaFont(fonts[60]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[60], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[61])) {
                        setJavaFont(fonts[61]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[61], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[62])) {
                        setJavaFont(fonts[62]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[62], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[63])) {
                        setJavaFont(fonts[63]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[63], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[64])) {
                        setJavaFont(fonts[64]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[64], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[65])) {
                        setJavaFont(fonts[65]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[65], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[66])) {
                        setJavaFont(fonts[66]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[66], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[67])) {
                        setJavaFont(fonts[67]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[67], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[68])) {
                        setJavaFont(fonts[68]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[68], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[69])) {
                        setJavaFont(fonts[69]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[69], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[70])) {
                        setJavaFont(fonts[70]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[70], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[71])) {
                        setJavaFont(fonts[71]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[71], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[72])) {
                        setJavaFont(fonts[72]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[72], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[73])) {
                        setJavaFont(fonts[73]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[73], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[74])) {
                        setJavaFont(fonts[74]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[74], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[75])) {
                        setJavaFont(fonts[75]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[75], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[76])) {
                        setJavaFont(fonts[76]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[76], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[77])) {
                        setJavaFont(fonts[77]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[77], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[78])) {
                        setJavaFont(fonts[78]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[78], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[79])) {
                        setJavaFont(fonts[79]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[79], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[80])) {
                        setJavaFont(fonts[80]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[80], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[81])) {
                        setJavaFont(fonts[81]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[81], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[82])) {
                        setJavaFont(fonts[82]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[82], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[83])) {
                        setJavaFont(fonts[83]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[83], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[84])) {
                        setJavaFont(fonts[84]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[84], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[85])) {
                        setJavaFont(fonts[85]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[85], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[86])) {
                        setJavaFont(fonts[86]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[86], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[87])) {
                        setJavaFont(fonts[87]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[87], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[88])) {
                        setJavaFont(fonts[88]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[88], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[89])) {
                        setJavaFont(fonts[89]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[89], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[90])) {
                        setJavaFont(fonts[90]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[90], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[91])) {
                        setJavaFont(fonts[91]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[91], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[92])) {
                        setJavaFont(fonts[92]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[92], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[93])) {
                        setJavaFont(fonts[93]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[93], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[94])) {
                        setJavaFont(fonts[94]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[94], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[95])) {
                        setJavaFont(fonts[95]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[95], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[96])) {
                        setJavaFont(fonts[96]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[96], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[97])) {
                        setJavaFont(fonts[97]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[97], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[98])) {
                        setJavaFont(fonts[98]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[98], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[99])) {
                        setJavaFont(fonts[99]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[99], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[100])) {
                        setJavaFont(fonts[100]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[100], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[101])) {
                        setJavaFont(fonts[101]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[101], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[102])) {
                        setJavaFont(fonts[102]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[102], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[103])) {
                        setJavaFont(fonts[103]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[103], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[104])) {
                        setJavaFont(fonts[104]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[104], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[105])) {
                        setJavaFont(fonts[105]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[105], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[106])) {
                        setJavaFont(fonts[106]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[106], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[107])) {
                        setJavaFont(fonts[107]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[107], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[108])) {
                        setJavaFont(fonts[108]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[108], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[109])) {
                        setJavaFont(fonts[109]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[109], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[110])) {
                        setJavaFont(fonts[110]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[110], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[111])) {
                        setJavaFont(fonts[111]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[111], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[112])) {
                        setJavaFont(fonts[112]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[112], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[113])) {
                        setJavaFont(fonts[113]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[113], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[114])) {
                        setJavaFont(fonts[114]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[114], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[115])) {
                        setJavaFont(fonts[115]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[115], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[116])) {
                        setJavaFont(fonts[116]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[116], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[117])) {
                        setJavaFont(fonts[117]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[117], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[118])) {
                        setJavaFont(fonts[118]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[118], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[119])) {
                        setJavaFont(fonts[119]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[119], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[120])) {
                        setJavaFont(fonts[120]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[120], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[121])) {
                        setJavaFont(fonts[121]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[121], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[122])) {
                        setJavaFont(fonts[122]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[122], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[123])) {
                        setJavaFont(fonts[123]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[123], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[124])) {
                        setJavaFont(fonts[124]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[124], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[125])) {
                        setJavaFont(fonts[125]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[125], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[126])) {
                        setJavaFont(fonts[126]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[126], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[127])) {
                        setJavaFont(fonts[127]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[127], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[128])) {
                        setJavaFont(fonts[128]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[128], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[129])) {
                        setJavaFont(fonts[129]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[129], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[130])) {
                        setJavaFont(fonts[130]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[130], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[131])) {
                        setJavaFont(fonts[131]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[131], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[132])) {
                        setJavaFont(fonts[132]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[132], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[133])) {
                        setJavaFont(fonts[133]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[133], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[134])) {
                        setJavaFont(fonts[134]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[134], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[135])) {
                        setJavaFont(fonts[135]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[135], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[136])) {
                        setJavaFont(fonts[136]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[136], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[137])) {
                        setJavaFont(fonts[137]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[137], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[138])) {
                        setJavaFont(fonts[138]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[138], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[139])) {
                        setJavaFont(fonts[139]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[139], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[140])) {
                        setJavaFont(fonts[140]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[140], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[141])) {
                        setJavaFont(fonts[141]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[141], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[142])) {
                        setJavaFont(fonts[142]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[142], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[143])) {
                        setJavaFont(fonts[143]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[143], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[144])) {
                        setJavaFont(fonts[144]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[144], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[145])) {
                        setJavaFont(fonts[145]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[145], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[146])) {
                        setJavaFont(fonts[146]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[146], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[147])) {
                        setJavaFont(fonts[147]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[147], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[148])) {
                        setJavaFont(fonts[148]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[148], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[149])) {
                        setJavaFont(fonts[149]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[159], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[150])) {
                        setJavaFont(fonts[150]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[150], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[151])) {
                        setJavaFont(fonts[151]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[151], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[152])) {
                        setJavaFont(fonts[152]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[152], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[153])) {
                        setJavaFont(fonts[153]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[153], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[154])) {
                        setJavaFont(fonts[154]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[154], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[155])) {
                        setJavaFont(fonts[155]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[155], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[156])) {
                        setJavaFont(fonts[156]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[156], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[157])) {
                        setJavaFont(fonts[157]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[157], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[158])) {
                        setJavaFont(fonts[158]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[158], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[159])) {
                        setJavaFont(fonts[159]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[159], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[160])) {
                        setJavaFont(fonts[160]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[160], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[161])) {
                        setJavaFont(fonts[161]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[161], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[162])) {
                        setJavaFont(fonts[162]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[162], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[163])) {
                        setJavaFont(fonts[163]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[163], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[164])) {
                        setJavaFont(fonts[164]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[164], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[165])) {
                        setJavaFont(fonts[165]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[165], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[166])) {
                        setJavaFont(fonts[166]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[166], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[167])) {
                        setJavaFont(fonts[167]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[167], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[168])) {
                        setJavaFont(fonts[168]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[168], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[169])) {
                        setJavaFont(fonts[169]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[169], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[170])) {
                        setJavaFont(fonts[170]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[170], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[171])) {
                        setJavaFont(fonts[171]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[171], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[172])) {
                        setJavaFont(fonts[172]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[172], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[173])) {
                        setJavaFont(fonts[173]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[173], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[174])) {
                        setJavaFont(fonts[174]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[174], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[175])) {
                        setJavaFont(fonts[175]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[175], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[176])) {
                        setJavaFont(fonts[176]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[176], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[177])) {
                        setJavaFont(fonts[177]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[177], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[178])) {
                        setJavaFont(fonts[178]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[178], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[179])) {
                        setJavaFont(fonts[179]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[179], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[180])) {
                        setJavaFont(fonts[180]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[180], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[181])) {
                        setJavaFont(fonts[181]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[181], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[182])) {
                        setJavaFont(fonts[182]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[182], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[183])) {
                        setJavaFont(fonts[183]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[183], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[184])) {
                        setJavaFont(fonts[184]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[184], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[185])) {
                        setJavaFont(fonts[185]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[185], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[186])) {
                        setJavaFont(fonts[186]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[186], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[187])) {
                        setJavaFont(fonts[187]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[187], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[188])) {
                        setJavaFont(fonts[188]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[188], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[189])) {
                        setJavaFont(fonts[189]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[189], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[190])) {
                        setJavaFont(fonts[190]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[190], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[191])) {
                        setJavaFont(fonts[191]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[191], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[192])) {
                        setJavaFont(fonts[192]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[192], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[193])) {
                        setJavaFont(fonts[193]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[193], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[194])) {
                        setJavaFont(fonts[194]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[194], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[195])) {
                        setJavaFont(fonts[195]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[195], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[196])) {
                        setJavaFont(fonts[196]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[196], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[197])) {
                        setJavaFont(fonts[197]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[197], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[198])) {
                        setJavaFont(fonts[198]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[198], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[199])) {
                        setJavaFont(fonts[199]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[199], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[200])) {
                        setJavaFont(fonts[200]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[200], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[201])) {
                        setJavaFont(fonts[201]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[201], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[202])) {
                        setJavaFont(fonts[202]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[202], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[203])) {
                        setJavaFont(fonts[203]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[203], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[204])) {
                        setJavaFont(fonts[204]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[204], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[205])) {
                        setJavaFont(fonts[205]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[205], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[206])) {
                        setJavaFont(fonts[206]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[206], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[207])) {
                        setJavaFont(fonts[207]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[207], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[208])) {
                        setJavaFont(fonts[208]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[208], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[209])) {
                        setJavaFont(fonts[209]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[209], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[210])) {
                        setJavaFont(fonts[210]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[210], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[211])) {
                        setJavaFont(fonts[211]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[211], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[212])) {
                        setJavaFont(fonts[212]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[212], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[213])) {
                        setJavaFont(fonts[213]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[213], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[214])) {
                        setJavaFont(fonts[214]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[214], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[215])) {
                        setJavaFont(fonts[215]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[215], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[216])) {
                        setJavaFont(fonts[216]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[216], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[217])) {
                        setJavaFont(fonts[217]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[217], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[218])) {
                        setJavaFont(fonts[218]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[218], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[219])) {
                        setJavaFont(fonts[219]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[219], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[220])) {
                        setJavaFont(fonts[220]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[220], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[221])) {
                        setJavaFont(fonts[221]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[221], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[222])) {
                        setJavaFont(fonts[222]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[222], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[223])) {
                        setJavaFont(fonts[223]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[223], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[224])) {
                        setJavaFont(fonts[224]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[224], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[225])) {
                        setJavaFont(fonts[225]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[225], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[226])) {
                        setJavaFont(fonts[226]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[226], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[227])) {
                        setJavaFont(fonts[227]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[227], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[228])) {
                        setJavaFont(fonts[228]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[228], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[229])) {
                        setJavaFont(fonts[229]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[229], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[230])) {
                        setJavaFont(fonts[230]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[230], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[231])) {
                        setJavaFont(fonts[231]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[231], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[232])) {
                        setJavaFont(fonts[232]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[232], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[233])) {
                        setJavaFont(fonts[233]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[233], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[234])) {
                        setJavaFont(fonts[234]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[234], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[235])) {
                        setJavaFont(fonts[235]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[235], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[236])) {
                        setJavaFont(fonts[236]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[236], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[237])) {
                        setJavaFont(fonts[237]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[237], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[238])) {
                        setJavaFont(fonts[238]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[238], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[239])) {
                        setJavaFont(fonts[239]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[239], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[240])) {
                        setJavaFont(fonts[240]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[240], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[241])) {
                        setJavaFont(fonts[241]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[241], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[242])) {
                        setJavaFont(fonts[242]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[242], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[243])) {
                        setJavaFont(fonts[243]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[243], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[244])) {
                        setJavaFont(fonts[244]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[244], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[245])) {
                        setJavaFont(fonts[245]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[245], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[246])) {
                        setJavaFont(fonts[246]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[246], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[247])) {
                        setJavaFont(fonts[247]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[247], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[248])) {
                        setJavaFont(fonts[248]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[248], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[249])) {
                        setJavaFont(fonts[249]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[249], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[250])) {
                        setJavaFont(fonts[250]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[250], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[251])) {
                        setJavaFont(fonts[251]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[251], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[252])) {
                        setJavaFont(fonts[252]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[252], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[253])) {
                        setJavaFont(fonts[253]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[253], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[254])) {
                        setJavaFont(fonts[254]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[254], Font.BOLD, 16));
                    } else if (fontName.equals(fonts[255])) {
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[255], Font.BOLD, 16));
                        setJavaFont(fonts[255]);
                    }
                }
            } else {
                System.out.println("The action was canceled.");
            }
        });
        settings.add(font);

        Default = new JMenuItem("Default") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        Default.setFont(Default.getFont().deriveFont(15.0f));
        Default.setToolTipText("   Reset the font and the theme   ");
        Default.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Default.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(null, "Change settings.\nAre you sure?", "Change theme", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            if(result == JOptionPane.YES_OPTION) {
                nameOfTheme = "Light";
                text.setSelectedTextColor(Color.decode("#999999"));
                getContentPane().setBackground(Color.WHITE);
                getContentPane().setForeground(Color.BLACK);
                text.setBackground(Color.WHITE);
                text.setForeground(Color.BLACK);
                bar.setBackground(Color.WHITE);
                bar.setForeground(Color.WHITE);
                file.setForeground(Color.BLACK);
                settings.setForeground(Color.BLACK);
                help.setForeground(Color.BLACK);
                New.setForeground(Color.BLACK);
                New.setBackground(Color.WHITE);
                open.setForeground(Color.BLACK);
                open.setBackground(Color.WHITE);
                save.setForeground(Color.BLACK);
                save.setBackground(Color.WHITE);
                exit.setForeground(Color.BLACK);
                exit.setBackground(Color.WHITE);
                Default.setForeground(Color.BLACK);
                Default.setBackground(Color.WHITE);
                font.setForeground(Color.BLACK);
                font.setBackground(Color.WHITE);
                theme.setForeground(Color.BLACK);
                theme.setBackground(Color.WHITE);
                text.setFont(new Font("Sans Serif",Font.BOLD,15));
                tln.setFont(new Font("Sans Serif",Font.BOLD,16));
                help.setFont(new Font("Sans Serif",Font.BOLD,16));
                Default.setFont(new Font("Sans Serif",Font.BOLD,15));
                font.setFont(new Font("Sans Serif",Font.BOLD,15));
                theme.setFont(new Font("Sans Serif",Font.BOLD,15));
                settings.setFont(new Font("Sans Serif",Font.BOLD,16));
                exit.setFont(new Font("Sans Serif",Font.BOLD,15));
                save.setFont(new Font("Sans Serif",Font.BOLD,15));
                open.setFont(new Font("Sans Serif",Font.BOLD,15));
                New.setFont(new Font("Sans Serif",Font.BOLD,15));
                file.setFont(new Font("Sans Serif",Font.BOLD,16));
                setUIFont (new javax.swing.plaf.FontUIResource("Sans Serif", Font.BOLD,13));
            } else {
                System.out.print("The action was canceled.");
            }
        });
        settings.add(Default);

        help = new JMenu("Help") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        help.setFont(help.getFont().deriveFont(16.0f));
        help.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        help.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JOptionPane.showOptionDialog(null, "What is this program? \nThis text editor is a basic text-editing program and it's most commonly used to view or edit text files.\nA text file is a file type typically identified by the .txt file name extension.\n\nHow do I change the font and theme?\n1. Go to 'Settings' menu\n2. Click on 'Change font' or 'Change settings' category and choose.\n(!) If u want to reset the settings, press the default button.\n\nWhat are the shortcuts for paste, delete, etc.?\n* To copy, do CTRL + C.\n* To paste, do CTRL + V.\n* To undo, do CTRL + Z.\n* To redo, do CTRL + R.\n* To replace, do CTRL + H.\n* To delete, do CTRL + X.\n* To select all text, do CTRL + A.\n\nHow can I work with the editor?\n* If you want to create a file, go to 'File' menu and select 'New' or, do CTRL + N.\n* If you want to open a file, go to 'File' menu and select 'Open' or, do CTRL + O.\n* If you want to save a file, go to 'File' menu and select 'Save' or, do CTRL + S.", "Help", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            }
            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }

        });
        help.setToolTipText("   View info   ");
        bar.add(help);


        undo = new UndoManager();

        text = new JTextArea(46, 850);
        text.getDocument().addUndoableEditListener(undo);
        text.setBorder(null);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setTabSize(2);
        text.setFont(new Font("Sans Serif", Font.BOLD, fontSize));
        text.addKeyListener(new MyKeyListener());
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
        scrollPane.setComponentZOrder(scrollPane.getViewport(), 0);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.addMouseWheelListener(new MyMouseListener());
        scrollPane.setBorder(null);
        tln = new TextLineNumber(text);
        tln.setFont(text.getFont());
        tln.setBackground(text.getBackground());
        scrollPane.setRowHeaderView(tln);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            private final Dimension d = new Dimension();
            @Override protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color color;
                JScrollBar sb = (JScrollBar)c;
                if(!sb.isEnabled() || r.width > r.height) {
                    return;
                } else if(isDragging) {
                    color = new Color(0,0,0,0);
                } else if(isThumbRollover()) {
                    color = new Color(0,0,0,0);
                } else {
                    color = new Color(0,0,0,0);
                }
                g2.setPaint(color);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g2.dispose();
            }
        });
        add(scrollPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        gui = new Main();
        gui.setVisible(true);
        gui.setSize(1200, 800);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setResizable(true);
        gui.setTitle("Text editor");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2 - 10);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void setUIFont (javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    private void setJavaFont(String fontName) {
        text.setFont(new Font(fontName, Font.BOLD,15));
    }

    static class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                try {
                    String path = jfc.getSelectedFile().getPath();
                    try {
                        File file = new File(path);
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(text.getText());
                        out.close();
                    } catch(Exception efg) {
                        efg.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch(Exception ef) {
                    ef.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if(e.getKeyCode() == KeyEvent.VK_O && e.isControlDown()) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Open file");
                int returnValue = jfc.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    try {
                        Scanner sc = new Scanner(selectedFile);
                        String value = "";
                        while(sc.hasNextLine()) {
                            String a = sc.nextLine();
                            value = value.concat(a + '\n');
                        }
                        text.setText(value);
                        String name = jfc.getSelectedFile().getName();
                        gui.setTitle(name + " - Text editor");
                    } catch(FileNotFoundException ef) {
                        ef.printStackTrace();
                    }
                }
            } else if(e.getKeyCode() == KeyEvent.VK_H && e.isControlDown()) {
                JTextField replace1 = new JTextField();
                JTextField replace2 = new JTextField();
                Object[] menu = {
                        "Search for: ", replace1,
                        "Replace with: ", replace2
                };
                String[] options = new String[2];
                options[0] = "Replace";
                options[1] = "Cancel";

                int option = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "Replace", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                if(option == 0) {
                    try {
                        if(replace1.getText().length() > 0 && replace2.getText().length() > 0) {
                            String value = text.getText().replaceAll(replace1.getText(), replace2.getText());
                            text.setText(value + value.charAt(value.length() - 1));
                        } else {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "The fields must be completed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch(Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't replace!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                try {
                    undo.undo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    System.out.println("Error");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                try {
                    undo.redo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    System.out.println("Error");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                JTextField find = new JTextField();

                Object[] menu = {
                        "Search for: ", find
                };

                String[] option = new String[1];
                option[0] = "Find";
                int option1 = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "Find text", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, option, null);
                if(option1 == 0) {
                    try {
                        if(find.getText().length() > 0) {
                            highlight(text, find.getText());
                        } else {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "The field must be completed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't find text!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_N && e.isControlDown()) {
                JCTextField create = new JCTextField();
                create.setPlaceholder("Example: file.txt");
                Object[] menu = {
                        "Enter a new file name: ", create
                };

                String[] options = new String[2];
                options[0] = "Create";
                options[1] = "Cancel";

                int value = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "New file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                if(value == 0) {
                    jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    jfc.setDialogTitle("New file");
                    File file = new File(create.getText());
                    jfc.setSelectedFile(file);
                    jfc.setDialogType(1);
                    int returnValue = jfc.showSaveDialog(null);
                    gui.setTitle(create.getText() + " - Text editor");
                    try {
                        if(returnValue == JFileChooser.APPROVE_OPTION) {
                            File new_file = jfc.getSelectedFile();
                            if(!new_file.createNewFile()) {
                                JOptionPane.showMessageDialog(gui.getContentPane(), "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } catch(Exception efg) {
                        efg.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't create a new file!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {}
    }

    static class MyMouseListener extends JPanel implements MouseWheelListener {
        MyMouseListener() {
            addMouseWheelListener(this);
        }
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.isControlDown()) {
                if (e.getWheelRotation() < 0) { // wheel up
                    fontSize++;
                } else { // wheel down
                    fontSize--;
                }
                text.setFont(text.getFont().deriveFont(fontSize * 1f));
                tln.setFont(text.getFont());
            }
        }

    }

    private static class CustomJToolTip extends JToolTip {
        public CustomJToolTip(JComponent component) {
            super();
            setComponent(component);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            switch(nameOfTheme) {
                case "Dark":
                    setBackground(Color.decode("#1E1E1E"));
                    setForeground(Color.decode("#ededed"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#ededed")));
                    break;
                case "Hacker":
                    setBackground(Color.decode("#000000"));
                    setForeground(Color.decode("#1D5F16"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#1D5F16")));
                    break;
                case "Kimbie":
                    setBackground(Color.decode("#221A0F"));
                    setForeground(Color.decode("#b37439"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#b37439")));
                    break;
                case "Night":
                    setBackground(Color.decode("#000C18"));
                    setForeground(Color.decode("#ededed"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#ededed")));
                    break;
                default:
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                    setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    break;
            }
            //setBackground(Color.decode("#2d2e2e"));
            //setBorder(BorderFactory.createLineBorder(Color.decode("#1b1c1c")));
            //setForeground(Color.WHITE);
        }
    }

    private static void highlight(JTextComponent textComp, String pattern) {
        removeHighlights(textComp);

        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());

            int pos = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                pos += pattern.length();
            }

        } catch (BadLocationException e) {
            System.out.println("Error");
        }
    }

    private static void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (Highlighter.Highlight highlight : hilites) {
            if (highlight.getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(highlight);
            }
        }
    }

    private static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        private MyHighlightPainter(Color color) {
            super(color);
        }
    }

    public class TextLineNumber extends JPanel
            implements CaretListener, DocumentListener, PropertyChangeListener {

        public void main(String[] args) {
            getContentPane().setBackground(text.getBackground());
        }

        private final static float RIGHT = 0.2f;

        private final  Border OUTER = new MatteBorder(0, 0, 0, 0, text.getBackground());

        private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

        private JTextArea component;

        private boolean updateFont;
        private float digitAlignment;
        private int minimumDisplayDigits;

        private int lastDigits;
        private int lastHeight;
        private int lastLine;

        private HashMap<String, FontMetrics> fonts;
        private TextLineNumber(JTextArea component)
        {
            this(component, 3);
        }

        private TextLineNumber(JTextArea component, int minimumDisplayDigits)
        {
            this.component = component;

            setFont( component.getFont() );

            setBorderGap();
            setDigitAlignment();
            setMinimumDisplayDigits( minimumDisplayDigits );

            component.getDocument().addDocumentListener(this);
            component.addCaretListener( this );
            component.addPropertyChangeListener("font", this);
        }

        private void setBorderGap()
        {
            Border inner = new EmptyBorder(0, 4, 0, 4);
            setBorder( new CompoundBorder(OUTER, inner) );
            lastDigits = 0;
            setPreferredWidth();
        }

        private void setDigitAlignment()
        {
            this.digitAlignment = TextLineNumber.RIGHT;
        }

        private void setMinimumDisplayDigits(int minimumDisplayDigits)
        {
            this.minimumDisplayDigits = minimumDisplayDigits;
            setPreferredWidth();
        }

        private void setPreferredWidth()
        {
            Element root = component.getDocument().getDefaultRootElement();
            int lines = root.getElementCount();
            int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

            if (lastDigits != digits)
            {
                lastDigits = digits;
                FontMetrics fontMetrics = getFontMetrics( getFont() );
                int width = fontMetrics.charWidth( '0' ) * digits;
                Insets insets = getInsets();
                int preferredWidth = insets.left + insets.right + width;

                Dimension d = getPreferredSize();
                d.setSize(preferredWidth, HEIGHT);
                setPreferredSize( d );
                setSize( d );
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            FontMetrics fontMetrics = component.getFontMetrics( component.getFont() );
            Insets insets = getInsets();
            int availableWidth = getSize().width - insets.left - insets.right;

            Rectangle clip = g.getClipBounds();
            int rowStartOffset = component.viewToModel2D(new Point(0, clip.y));
            int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));

            while (rowStartOffset <= endOffset)
            {
                try
                {
                    setBackground(text.getBackground());
                    g.setColor(text.getForeground());

                    String lineNumber = getTextLineNumber(rowStartOffset) + ". ";
                    int stringWidth = fontMetrics.stringWidth( lineNumber );
                    int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                    int y = getOffsetY(rowStartOffset, fontMetrics);
                    g.drawString(lineNumber, x, y);

                    rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
                }
                catch(Exception e) {break;}
            }
        }

        private String getTextLineNumber(int rowStartOffset)
        {
            Element root = component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex( rowStartOffset );
            Element line = root.getElement( index );

            if (line.getStartOffset() == rowStartOffset)
                return String.valueOf(index + 1);
            else
                return " ";
        }

        private int getOffsetX(int availableWidth, int stringWidth)
        {
            return (int)((availableWidth - stringWidth) * digitAlignment);
        }

        private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
                throws BadLocationException
        {

            Rectangle r = component.modelToView( rowStartOffset );
            int lineHeight = fontMetrics.getHeight();
            int y = r.y + r.height;
            int descent = 0;

            if (r.height == lineHeight)
            {
                descent = fontMetrics.getDescent();
            }
            else
            {
                if (fonts == null)
                    fonts = new HashMap<>();

                Element root = component.getDocument().getDefaultRootElement();
                int index = root.getElementIndex( rowStartOffset );
                Element line = root.getElement( index );

                for (int i = 0; i < line.getElementCount(); i++)
                {
                    Element child = line.getElement(i);
                    AttributeSet as = child.getAttributes();
                    String fontFamily = (String)as.getAttribute(StyleConstants.FontFamily);
                    Integer fontSize = (Integer)as.getAttribute(StyleConstants.FontSize);
                    String key = fontFamily + fontSize;

                    FontMetrics fm = fonts.get( key );

                    if (fm == null)
                    {
                        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                        fm = component.getFontMetrics( font );
                        fonts.put(key, fm);
                    }

                    descent = Math.max(descent, fm.getDescent());
                }
            }

            return y - descent;
        }

        @Override
        public void caretUpdate(CaretEvent e)
        {

            int caretPosition = component.getCaretPosition();
            Element root = component.getDocument().getDefaultRootElement();
            int currentLine = root.getElementIndex( caretPosition );

            if (lastLine != currentLine)
            {
                repaint();
                lastLine = currentLine;
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        private void documentChanged()
        {

            SwingUtilities.invokeLater(() -> {
                try
                {
                    int endPos = component.getDocument().getLength();
                    Rectangle rect = component.modelToView(endPos);

                    if (rect != null && rect.y != lastHeight)
                    {
                        setPreferredWidth();
                        repaint();
                        lastHeight = rect.y;
                    }
                }
                catch (BadLocationException ex) { /* nothing to do */ }
            });
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (evt.getNewValue() instanceof Font)
            {
                if (updateFont)
                {
                    Font newFont = (Font) evt.getNewValue();
                    setFont(newFont);
                    lastDigits = 0;
                    setPreferredWidth();
                }
                else
                {
                    repaint();
                }
            }
        }
    }
    public static class JCTextField extends JTextField {

        private String placeholder = "";
        private Color phColor= new Color(0,0,0);
        private boolean band = true;

        public JCTextField()  {
            super();
            Dimension d = new Dimension(200, 32);
            setSize(d);
            setPreferredSize(d);
            setVisible(true);
            setMargin( new Insets(3,6,3,6));
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    band = getText().length() <= 0;
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    band = false;
                }

                @Override
                public void changedUpdate(DocumentEvent de) {}

            });
        }

        public void setPlaceholder(String placeholder)
        {
            this.placeholder=placeholder;
        }

        /*public String getPlaceholder()
        {
            return placeholder;
        }

        public Color getPhColor() {
            return phColor;
        }

        public void setPhColor(Color phColor) {
            this.phColor = phColor;
        }
        */

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor( new Color(phColor.getRed(), phColor.getGreen(), phColor.getBlue(),90));
            g.drawString( (band) ? placeholder:"", getMargin().left, (getSize().height) / 2 + getFont().getSize() / 2 );
        }

    }

}