package ionshield.expertsystem.core;

import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class MainWindow {
    private JPanel rootPanel;
    private JTextArea log;
    private JButton calculateButton;
    private JTable inputTable;
    private JTable outputTable;
    private JButton addInputButton;
    private JButton deleteInputButton;
    private JButton loadInputButton;
    private JTable inputSettingsTable;
    private JTable rulesSettingsTable;
    private JTable outputSettingsTable;
    private JTextField iterationsTextField;
    private JButton applyButton;
    private JButton addRuleButton;
    private JButton deleteRuleButton;
    private JButton loadRuleButton;
    private JButton addOutputButton;
    private JButton deleteOutputButton;
    private JButton loadOutputButton;

    private DefaultTableModel inputTableModel;
    private DefaultTableModel outputTableModel;
    private DefaultTableModel inputSettingsTableModel;
    private DefaultTableModel outputSettingsTableModel;
    private DefaultTableModel rulesSettingsTableModel;

    private Map<String, Symbol> inputSettingsVariables = new HashMap<>();
    private Map<String, Symbol> outputSettingsVariables = new HashMap<>();
    private Map<String, Rule> rulesSettings = new HashMap<>();
    private Map<String, Symbol> inputVariables = new HashMap<>();

    private int iterations;

    public static final String TITLE = "Expert-System";
    
    private MainWindow() {
        initComponents();
    }
    
    private void initComponents() {
        calculateButton.addActionListener(e -> calculate());
        applyButton.addActionListener(e -> applySettings());
        inputTableModel = new TableModel(Config.InputTable.COLUMNS, 0, new boolean[]{false, false, true});
        outputTableModel = new TableModel(Config.OutputTable.COLUMNS, 0, null);
        inputSettingsTableModel = new DefaultTableModel(Config.InputSettingsTable.COLUMNS, 0);
        outputSettingsTableModel = new DefaultTableModel(Config.OutputSettingsTable.COLUMNS, 0);
        rulesSettingsTableModel = new DefaultTableModel(Config.RulesSettingsTable.COLUMNS, 0);

        inputTable.setModel(inputTableModel);
        outputTable.setModel(outputTableModel);
        inputSettingsTable.setModel(inputSettingsTableModel);
        outputSettingsTable.setModel(outputSettingsTableModel);
        rulesSettingsTable.setModel(rulesSettingsTableModel);

        addInputButton.addActionListener(e -> {
            inputSettingsTableModel.addRow(new Object[inputSettingsTableModel.getColumnCount()]);
        });

        deleteInputButton.addActionListener(e -> {
            int i = inputSettingsTable.getSelectedRow();
            if (i >= 0 && i < inputSettingsTable.getRowCount()) {
                inputSettingsTableModel.removeRow(i);
            }
        });

        addOutputButton.addActionListener(e -> {
            outputSettingsTableModel.addRow(new Object[outputSettingsTableModel.getColumnCount()]);
        });

        deleteOutputButton.addActionListener(e -> {
            int i = outputSettingsTable.getSelectedRow();
            if (i >= 0 && i < outputSettingsTable.getRowCount()) {
                outputSettingsTableModel.removeRow(i);
            }
        });

        addRuleButton.addActionListener(e -> {
            rulesSettingsTableModel.addRow(new Object[rulesSettingsTableModel.getColumnCount()]);
        });

        deleteRuleButton.addActionListener(e -> {
            int i = rulesSettingsTable.getSelectedRow();
            if (i >= 0 && i < rulesSettingsTable.getRowCount()) {
                rulesSettingsTableModel.removeRow(i);
            }
        });

        loadInputButton.addActionListener(e -> {
            File file = loadFile();
            if (file == null) return;
            List<String> rows = readFileRows(file);
            if (rows == null) return;

            for (int i = inputSettingsTableModel.getRowCount() - 1; i >= 0; i--) {
                inputSettingsTableModel.removeRow(i);
            }

            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                row = row.trim();
                List<String> strings = Arrays.asList(row.split("\\s+"));
                inputSettingsTableModel.addRow(new Object[inputSettingsTableModel.getColumnCount()]);
                if (strings.size() >= 1) {
                    inputSettingsTableModel.setValueAt(strings.get(0), i, Config.InputSettingsTable.getIndex("name"));
                }
                if (strings.size() >= 2) {
                    inputSettingsTableModel.setValueAt(strings.get(1), i, Config.InputSettingsTable.getIndex("type"));
                }
                if (strings.size() >= 3) {
                    StringBuilder values = new StringBuilder();
                    for (int j = 2; j < strings.size(); j++) {
                        values.append(strings.get(j));
                        if (j != strings.size() - 1) {
                            values.append(";");
                        }
                    }
                    inputSettingsTableModel.setValueAt(values, i, Config.InputSettingsTable.getIndex("enum values"));
                }
            }
        });

        loadOutputButton.addActionListener(e -> {
            File file = loadFile();
            if (file == null) return;
            List<String> rows = readFileRows(file);
            if (rows == null) return;

            for (int i = outputSettingsTableModel.getRowCount() - 1; i >= 0; i--) {
                outputSettingsTableModel.removeRow(i);
            }

            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                row = row.trim();
                List<String> strings = Arrays.asList(row.split("\\s+"));
                outputSettingsTableModel.addRow(new Object[outputSettingsTableModel.getColumnCount()]);
                if (strings.size() >= 1) {
                    outputSettingsTableModel.setValueAt(strings.get(0), i, Config.OutputSettingsTable.getIndex("name"));
                }
                if (strings.size() >= 2) {
                    outputSettingsTableModel.setValueAt(strings.get(1), i, Config.OutputSettingsTable.getIndex("type"));
                }
                if (strings.size() >= 3) {
                    StringBuilder values = new StringBuilder();
                    for (int j = 2; j < strings.size(); j++) {
                        values.append(strings.get(j));
                        if (j != strings.size() - 1) {
                            values.append(";");
                        }
                    }
                    outputSettingsTableModel.setValueAt(values, i, Config.OutputSettingsTable.getIndex("enum values"));
                }
            }
        });

        loadRuleButton.addActionListener(e -> {
            File file = loadFile();
            if (file == null) return;
            List<String> rows = readFileRows(file);
            if (rows == null) return;

            for (int i = rulesSettingsTableModel.getRowCount() - 1; i >= 0; i--) {
                rulesSettingsTableModel.removeRow(i);
            }

            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                row = row.trim();
                if (row.toLowerCase().startsWith("if")) {
                    row = row.substring(2);
                }
                if (row.toLowerCase().startsWith("если")) {
                    row = row.substring(4);
                }
                row = row.trim();
                List<String> strings = Arrays.asList(row.split(/*"\\b(?i)(if|then|else|если|то|иначе)\\b"*/Pattern.compile("\\b(?i)(if|then|else|если|то|иначе|Если|То|Иначе|ЕСЛИ|ТО|ИНАЧЕ)\\b", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE).toString()));
                rulesSettingsTableModel.addRow(new Object[rulesSettingsTableModel.getColumnCount()]);
                if (strings.size() >= 1) {
                    rulesSettingsTableModel.setValueAt(strings.get(0), i, Config.RulesSettingsTable.getIndex("if"));
                }
                if (strings.size() >= 2) {
                    rulesSettingsTableModel.setValueAt(strings.get(1), i, Config.RulesSettingsTable.getIndex("then"));
                }
                if (strings.size() >= 3) {
                    rulesSettingsTableModel.setValueAt(strings.get(2), i, Config.RulesSettingsTable.getIndex("else"));
                }
            }
        });

        applySettings();
        log.setText("");

    }
    
    private void applySettings() {
        try {
            this.iterations = Integer.parseInt(iterationsTextField.getText());
        } catch (NumberFormatException e) {
            log.append(System.lineSeparator() + "Error: iterations must be an integer");
            return;
        }

        inputSettingsVariables.clear();
        outputSettingsVariables.clear();
        rulesSettings.clear();

        //Input settings
        for (int i = inputTableModel.getRowCount() - 1; i >= 0; i--) {
            inputTableModel.removeRow(i);
        }
        for (int i = 0; i < inputSettingsTableModel.getRowCount(); i++) {
            String name = Optional.ofNullable(inputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("name"))).orElse("").toString();
            if (name == null || name.length() == 0) {
                log.append("Warning: empty input variable name");
                continue;
            }
            name = name.toLowerCase();
            Symbol.Type type = Symbol.parseType(Optional.ofNullable(inputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("type"))).orElse("").toString());
            if (type == null) {
                log.append(System.lineSeparator() + "Warning: input variable \"" + name + "\": invalid type");
                continue;
            }
            Symbol symbol = new Symbol(name, type, "");
            if (type == Symbol.Type.ENUM) {
                String valuesString = Optional.ofNullable(inputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("enum values"))).orElse("").toString();
                String[] values = valuesString.trim().split(";");
                symbol.setAllowedValues(new HashSet<>(Arrays.asList(values)));
            }
            inputSettingsVariables.put(name, symbol);
            inputTableModel.addRow(new Object[]{symbol.getName(), symbol.getType(), ""});
        }

        //Output settings
        for (int i = outputTableModel.getRowCount() - 1; i >= 0; i--) {
            outputTableModel.removeRow(i);
        }
        for (int i = 0; i < outputSettingsTableModel.getRowCount(); i++) {
            String name = Optional.ofNullable(outputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("name"))).orElse("").toString();
            if (name == null || name.length() == 0) {
                log.append("Warning: empty output variable name");
                continue;
            }
            name = name.toLowerCase();
            Symbol.Type type = Symbol.parseType(Optional.ofNullable(outputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("type"))).orElse("").toString());
            if (type == null) {
                log.append(System.lineSeparator() + "Warning: output variable \"" + name + "\": invalid type");
                continue;
            }
            Symbol symbol = new Symbol(name, type, "");
            if (type == Symbol.Type.ENUM) {
                String valuesString = Optional.ofNullable(outputSettingsTableModel.getValueAt(i, Config.InputSettingsTable.getIndex("enum values"))).orElse("").toString();
                String[] values = valuesString.trim().split(";");
                symbol.setAllowedValues(new HashSet<>(Arrays.asList(values)));
            }
            outputSettingsVariables.put(name, symbol);
            outputTableModel.addRow(new Object[]{symbol.getName(), symbol.getType(), ""});
        }

        //Rules settings
        for (int i = 0; i < rulesSettingsTableModel.getRowCount(); i++) {
            String ruleString = Optional.ofNullable(rulesSettingsTableModel.getValueAt(i, Config.RulesSettingsTable.getIndex("if"))).orElse("").toString();
            if (ruleString == null || ruleString.length() == 0) {
                log.append("Warning: empty rule");
                continue;
            }
            String thenString = Optional.ofNullable(rulesSettingsTableModel.getValueAt(i, Config.RulesSettingsTable.getIndex("then"))).orElse("").toString();
            String elseString = Optional.ofNullable(rulesSettingsTableModel.getValueAt(i, Config.RulesSettingsTable.getIndex("else"))).orElse("").toString();
            Rule rule = Rule.parseRule(ruleString, thenString, elseString);

            rulesSettings.put(String.valueOf(i), rule);
        }

        log.append(System.lineSeparator() + "Settings applied");
    }

    
    private void calculate() {
        Set<String> inputVariableNames = inputSettingsVariables.keySet();
        Set<String> outputVariableNames = outputSettingsVariables.keySet();
        inputVariables.clear();
        inputVariables.putAll(inputSettingsVariables);
        for (int i = 0; i < inputTableModel.getRowCount(); i++) {
            String name = Optional.ofNullable(inputTableModel.getValueAt(i, Config.InputTable.getIndex("name"))).orElse("").toString();
            if (inputTableModel.getValueAt(i, Config.InputTable.getIndex("value")) == null) {
                inputVariables.remove(name);
                continue;
            }
            if (inputVariableNames.contains(name) && inputSettingsVariables.get(name) != null) {
                String value = Optional.ofNullable(inputTableModel.getValueAt(i, Config.InputTable.getIndex("value"))).orElse("").toString();
                if (value.length() == 0) {
                    inputVariables.remove(name);
                    continue;
                }
                try {
                    inputSettingsVariables.get(name).parse(value);
                } catch (IllegalArgumentException e) {
                    log.append(System.lineSeparator() + "Error: variable \"" + name + "\" of type " + inputSettingsVariables.get(name).getType() + ": value \"" + value + "\" is invalid");
                }

            }
        }

        Map<String, Symbol> variables = new HashMap<>();
        variables.putAll(inputVariables);

        for (int i = 0; i < iterations; i++) {
            Map<String, Symbol> resultSum = new HashMap<>();
            for (Rule rule : rulesSettings.values()) {
                try {
                    Map<String, Symbol> result = rule.evaluate(variables);
                    if (result != null) {
                        resultSum.putAll(result);
                    }
                } catch (SymbolException e) {

                }

            }
            variables.putAll(resultSum);
            resultSum.clear();
        }



        for (int i = 0; i < outputTableModel.getRowCount(); i++) {
            String name = Optional.ofNullable(outputTableModel.getValueAt(i, Config.OutputTable.getIndex("name"))).orElse("").toString();
            if (outputTableModel.getValueAt(i, Config.InputTable.getIndex("value")) == null) {
                inputVariables.remove(name);
                continue;
            }
            if (outputVariableNames.contains(name) && variables.get(name) != null) {
                outputTableModel.setValueAt(variables.get(name).getValue(), i, Config.OutputTable.getIndex("value"));
            }
        }

        /*try {
            log.setText("");

            }


        }
        catch (NumberFormatException e) {
            log.append("\nInvalid input format");
        }*/
    }


    private File loadFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT",  "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(rootPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private List<String> readFileRows(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> l = new ArrayList<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                l.add(line);
            }
            return l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static void main(String[] args) {
        BasicLookAndFeel darcula = new DarculaLaf();
        try {
            UIManager.setLookAndFeel(darcula);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame(TITLE);
        MainWindow gui = new MainWindow();
        frame.setContentPane(gui.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
