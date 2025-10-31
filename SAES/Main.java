import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class Main extends JFrame {
    // 组件声明（新增三重加密模式）
    private JTextField keyField;
    private JTextField inputField;
    private JTextField outputField;
    private JButton encryptBtn;
    private JButton decryptBtn;
    private JButton clearBtn;
    private JButton exitBtn;
    private JLabel statusLabel;
    private JRadioButton hexModeRadio;       // 十六进制模式
    private JRadioButton strModeRadio;       // 字符串模式
    private JRadioButton singleEncRadio;     // 单重加密
    private JRadioButton doubleEncRadio;     // 双重加密
    private JRadioButton tripleEncRadio;     // 三重加密（新增）

    // 颜色常量定义（不变）
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(231, 76, 60);
    private static final Color NEUTRAL_COLOR = new Color(149, 165, 166);
    private static final Color LIGHT_BG = new Color(245, 247, 250);
    private static final Color DARK_TEXT = new Color(44, 62, 80);

    public Main() {
        initializeUI();
    }

    private void initializeUI() {
        // 窗口属性（适当增大尺寸）
        setTitle("S-AES加解密工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // 主面板（不变）
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // 标题区域（更新副标题）
        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);

        // 中心面板（增加三重加密选项）
        mainPanel.add(createInputPanel(), BorderLayout.CENTER);

        // 底部按钮区域（不变）
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("S-AES 加解密工具", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subTitleLabel = new JLabel("(支持单重/双重/三重加密 · 十六进制/ASCII字符串)", JLabel.CENTER);
        subTitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subTitleLabel.setForeground(NEUTRAL_COLOR);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subTitleLabel);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "加解密参数",
                0, 0,
                new Font("微软雅黑", Font.PLAIN, 14),
                PRIMARY_COLOR
        ));
        panel.setBackground(Color.WHITE);

        // 1. 数据模式切换（不变）
        JPanel dataModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 5));
        dataModePanel.setBackground(Color.WHITE);
        JLabel dataModeLabel = new JLabel("数据模式:");
        dataModeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dataModeLabel.setPreferredSize(new Dimension(100, 30));
        dataModeLabel.setForeground(DARK_TEXT);

        hexModeRadio = new JRadioButton("十六进制", true);
        strModeRadio = new JRadioButton("ASCII字符串");
        ButtonGroup dataModeGroup = new ButtonGroup();
        dataModeGroup.add(hexModeRadio);
        dataModeGroup.add(strModeRadio);
        hexModeRadio.setBackground(Color.WHITE);
        strModeRadio.setBackground(Color.WHITE);

        dataModePanel.add(dataModeLabel);
        dataModePanel.add(hexModeRadio);
        dataModePanel.add(strModeRadio);

        // 2. 加密模式切换（新增三重加密选项）
        JPanel encModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        encModePanel.setBackground(Color.WHITE);
        JLabel encModeLabel = new JLabel("加密模式:");
        encModeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        encModeLabel.setPreferredSize(new Dimension(100, 30));
        encModeLabel.setForeground(DARK_TEXT);

        singleEncRadio = new JRadioButton("单重加密 (4位密钥)", true);
        doubleEncRadio = new JRadioButton("双重加密 (8位密钥)");
        tripleEncRadio = new JRadioButton("三重加密 (8位密钥)");
        ButtonGroup encModeGroup = new ButtonGroup();
        encModeGroup.add(singleEncRadio);
        encModeGroup.add(doubleEncRadio);
        encModeGroup.add(tripleEncRadio);
        singleEncRadio.setBackground(Color.WHITE);
        doubleEncRadio.setBackground(Color.WHITE);
        tripleEncRadio.setBackground(Color.WHITE);

        encModePanel.add(encModeLabel);
        encModePanel.add(singleEncRadio);
        encModePanel.add(doubleEncRadio);
        encModePanel.add(tripleEncRadio);

        // 3. 密钥输入（提示文字更新）
        JPanel keyPanel = createInputRow("密钥:", keyField = new JTextField(20),
                "单重: 4位十六进制 | 双重/三重: 8位十六进制");

        // 4. 输入数据（不变）
        JPanel inputPanel = createInputRow("输入数据:", inputField = new JTextField(20),
                "十六进制模式: 4位十六进制 | 字符串模式: ASCII字符");

        // 5. 输出数据（不变）
        JPanel outputPanel = createInputRow("输出结果:", outputField = new JTextField(20),
                "加密/解密结果将显示在这里");
        outputField.setEditable(false);
        outputField.setBackground(LIGHT_BG);
        outputField.setForeground(DARK_TEXT);

        // 6. 状态显示（不变）
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.WHITE);
        statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusLabel.setForeground(PRIMARY_COLOR);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        statusPanel.add(statusLabel);

        // 面板添加顺序（不变）
        panel.add(dataModePanel);
        panel.add(encModePanel);
        panel.add(keyPanel);
        panel.add(inputPanel);
        panel.add(outputPanel);
        panel.add(statusPanel);

        return panel;
    }

    // 创建输入行（保持不变）
    private JPanel createInputRow(String labelText, JTextField textField, String tooltip) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 5));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(100, 30));
        label.setForeground(DARK_TEXT);

        textField.setFont(new Font("Consolas", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(150, 35));
        textField.setToolTipText(tooltip);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(NEUTRAL_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        panel.add(label);
        panel.add(textField);
        return panel;
    }

    // 按钮面板（保持不变）
// 在 createButtonPanel() 方法中添加新按钮
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);

        encryptBtn = createStyledButton("加密", PRIMARY_COLOR);
        encryptBtn.addActionListener(e -> performEncryption());

        decryptBtn = createStyledButton("解密", SECONDARY_COLOR);
        decryptBtn.addActionListener(e -> performDecryption());

        // 添加新功能按钮
        JButton attackBtn = createStyledButton("中间相遇", new Color(155, 89, 182));
        attackBtn.addActionListener(e -> performMeetInTheMiddleAttack());

        JButton cbcBtn = createStyledButton("CBC模式", new Color(230, 126, 34));
        cbcBtn.addActionListener(e -> performCBCDemo());

        clearBtn = createStyledButton("清空", NEUTRAL_COLOR);
        clearBtn.addActionListener(e -> clearFields());

        exitBtn = createStyledButton("退出", WARNING_COLOR);
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(encryptBtn);
        panel.add(decryptBtn);
        panel.add(attackBtn);
        panel.add(cbcBtn);
        panel.add(clearBtn);
        panel.add(exitBtn);

        return panel;
    }
    // 创建按钮（保持不变）
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(90, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // 加密处理（核心：新增三重加密分支）
    private void performEncryption() {
        try {
            String keyStr = keyField.getText().trim();
            // 验证密钥（适配三重加密）
            if (singleEncRadio.isSelected()) {
                if (!validateHexInput(keyStr, "密钥", 4)) {
                    return;
                }
            } else {  // 双重/三重共用32位密钥
                if (!validateHexInput(keyStr, "密钥", 8)) {
                    return;
                }
            }

            // 处理输入数据
            if (hexModeRadio.isSelected()) {
                // 十六进制模式
                String inputStr = inputField.getText().trim();
                if (!validateHexInput(inputStr, "明文", 4)) {
                    return;
                }
                int data = Integer.parseInt(inputStr, 16);
                if (!validateRange(data)) {
                    return;
                }

                // 执行加密（新增三重加密逻辑）
                int result;
                if (singleEncRadio.isSelected()) {
                    int key = Integer.parseInt(keyStr, 16);
                    result = SAES.encrypt(data, key);  // 单重
                } else if (doubleEncRadio.isSelected()) {
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = doubleEncrypt(data, key1, key2);  // 双重
                } else {  // 三重加密
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = tripleEncrypt(data, key1, key2);  // 三重
                }
                outputField.setText(String.format("%04X", result));
            } else {
                // 字符串模式
                String inputStr = inputField.getText().trim();
                if (!validateAsciiInput(inputStr, "明文")) {
                    return;
                }

                // 执行加密（新增三重加密逻辑）
                List<Integer> encryptedGroups;
                if (singleEncRadio.isSelected()) {
                    int key = Integer.parseInt(keyStr, 16);
                    encryptedGroups = encryptString(inputStr, key, false, false);
                } else if (doubleEncRadio.isSelected()) {
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    encryptedGroups = encryptString(inputStr, key1, key2, true, false);
                } else {  // 三重加密
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    encryptedGroups = encryptString(inputStr, key1, key2, false, true);
                }

                // 拼接结果
                StringBuilder output = new StringBuilder();
                for (int group : encryptedGroups) {
                    output.append(String.format("%04X", group));
                }
                outputField.setText(output.toString());
            }

            statusLabel.setText("加密完成");
            statusLabel.setForeground(SECONDARY_COLOR);

        } catch (Exception ex) {
            showError("加密过程中出现错误: " + ex.getMessage());
        }
    }

    // 解密处理（核心：新增三重解密分支）
    private void performDecryption() {
        try {
            String keyStr = keyField.getText().trim();
            // 验证密钥（适配三重加密）
            if (singleEncRadio.isSelected()) {
                if (!validateHexInput(keyStr, "密钥", 4)) {
                    return;
                }
            } else {  // 双重/三重共用32位密钥
                if (!validateHexInput(keyStr, "密钥", 8)) {
                    return;
                }
            }

            // 处理输入数据
            if (hexModeRadio.isSelected()) {
                // 十六进制模式
                String inputStr = inputField.getText().trim();
                if (!validateHexInput(inputStr, "密文", 4)) {
                    return;
                }
                int data = Integer.parseInt(inputStr, 16);
                if (!validateRange(data)) {
                    return;
                }

                // 执行解密（新增三重解密逻辑）
                int result;
                if (singleEncRadio.isSelected()) {
                    int key = Integer.parseInt(keyStr, 16);
                    result = SAES.decrypt(data, key);  // 单重
                } else if (doubleEncRadio.isSelected()) {
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = doubleDecrypt(data, key1, key2);  // 双重
                } else {  // 三重解密
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = tripleDecrypt(data, key1, key2);  // 三重
                }
                outputField.setText(String.format("%04X", result));
            } else {
                // 字符串模式
                String inputStr = inputField.getText().trim();
                if (!validateHexInput(inputStr, "密文", 0) || inputStr.length() % 4 != 0) {
                    showError("密文必须是4的倍数长度的十六进制数");
                    return;
                }

                // 执行解密（新增三重解密逻辑）
                String result;
                if (singleEncRadio.isSelected()) {
                    int key = Integer.parseInt(keyStr, 16);
                    result = decryptToString(inputStr, key, false, false);
                } else if (doubleEncRadio.isSelected()) {
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = decryptToString(inputStr, key1, key2, true, false);
                } else {  // 三重解密
                    int key1 = Integer.parseInt(keyStr.substring(0, 4), 16);
                    int key2 = Integer.parseInt(keyStr.substring(4, 8), 16);
                    result = decryptToString(inputStr, key1, key2, false, true);
                }
                outputField.setText(result);
            }

            statusLabel.setText("解密完成");
            statusLabel.setForeground(SECONDARY_COLOR);

        } catch (Exception ex) {
            showError("解密过程中出现错误: " + ex.getMessage());
        }
    }

    // 三重加密实现（E-D-E模式：C = E(K1, D(K2, E(K1, P)))）
    private int tripleEncrypt(int plaintext, int key1, int key2) {
        int t1 = SAES.encrypt(plaintext, key1);    // 第一次加密（K1）
        int t2 = SAES.decrypt(t1, key2);           // 第二次解密（K2）
        return SAES.encrypt(t2, key1);             // 第三次加密（K1）
    }

    // 三重解密实现（D-E-D模式：P = D(K1, E(K2, D(K1, C)))）
    private int tripleDecrypt(int ciphertext, int key1, int key2) {
        int t1 = SAES.decrypt(ciphertext, key1);   // 第一次解密（K1）
        int t2 = SAES.encrypt(t1, key2);           // 第二次加密（K2）
        return SAES.decrypt(t2, key1);             // 第三次解密（K1）
    }

    // 双重加密（原有逻辑不变）
    private int doubleEncrypt(int plaintext, int key1, int key2) {
        int temp = SAES.encrypt(plaintext, key1);
        return SAES.encrypt(temp, key2);
    }

    // 双重解密（原有逻辑不变）
    private int doubleDecrypt(int ciphertext, int key1, int key2) {
        int temp = SAES.decrypt(ciphertext, key2);
        return SAES.decrypt(temp, key1);
    }

    // 字符串加密适配方法（支持单重/双重/三重）
    private List<Integer> encryptString(String str, int key, boolean isDouble, boolean isTriple) {
        List<Integer> groups = new ArrayList<>();
        if (str.length() % 2 != 0) str += '\0';
        for (int i = 0; i < str.length(); i += 2) {
            char c1 = str.charAt(i);
            char c2 = str.charAt(i + 1);
            int group = (c1 << 8) | (c2 & 0xFF);
            int encrypted;
            if (isTriple) encrypted = tripleEncrypt(group, key, key);  // 三重
            else if (isDouble) encrypted = doubleEncrypt(group, key, key);  // 双重
            else encrypted = SAES.encrypt(group, key);  // 单重
            groups.add(encrypted);
        }
        return groups;
    }

    // 字符串加密重载（双重/三重）
    private List<Integer> encryptString(String str, int key1, int key2, boolean isDouble, boolean isTriple) {
        List<Integer> groups = new ArrayList<>();
        if (str.length() % 2 != 0) str += '\0';
        for (int i = 0; i < str.length(); i += 2) {
            char c1 = str.charAt(i);
            char c2 = str.charAt(i + 1);
            int group = (c1 << 8) | (c2 & 0xFF);
            int encrypted = isTriple ? tripleEncrypt(group, key1, key2) : doubleEncrypt(group, key1, key2);
            groups.add(encrypted);
        }
        return groups;
    }

    // 字符串解密适配方法（支持单重/双重/三重）
    private String decryptToString(String hexStr, int key, boolean isDouble, boolean isTriple) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 4) {
            String groupHex = hexStr.substring(i, i + 4);
            int group = Integer.parseInt(groupHex, 16);
            int decrypted;
            if (isTriple) decrypted = tripleDecrypt(group, key, key);  // 三重
            else if (isDouble) decrypted = doubleDecrypt(group, key, key);  // 双重
            else decrypted = SAES.decrypt(group, key);  // 单重
            char c1 = (char) ((decrypted >> 8) & 0xFF);
            char c2 = (char) (decrypted & 0xFF);
            if (c1 != '\0') result.append(c1);
            if (c2 != '\0') result.append(c2);
        }
        return result.toString();
    }

    // 字符串解密重载（双重/三重）
    private String decryptToString(String hexStr, int key1, int key2, boolean isDouble, boolean isTriple) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 4) {
            String groupHex = hexStr.substring(i, i + 4);
            int group = Integer.parseInt(groupHex, 16);
            int decrypted = isTriple ? tripleDecrypt(group, key1, key2) : doubleDecrypt(group, key1, key2);
            char c1 = (char) ((decrypted >> 8) & 0xFF);
            char c2 = (char) (decrypted & 0xFF);
            if (c1 != '\0') result.append(c1);
            if (c2 != '\0') result.append(c2);
        }
        return result.toString();
    }

    // 验证方法（保持不变）
    private boolean validateHexInput(String input, String fieldName, int requiredLength) {
        if (input.isEmpty()) {
            showError(fieldName + "不能为空");
            return false;
        }
        if (!input.matches("[0-9A-Fa-f]+")) {
            showError(fieldName + "必须是十六进制格式(0-9, A-F)");
            return false;
        }
        if (requiredLength > 0 && input.length() != requiredLength) {
            showError(fieldName + "必须是" + requiredLength + "位十六进制数");
            return false;
        }
        return true;
    }

    private boolean validateAsciiInput(String input, String fieldName) {
        if (input.isEmpty()) {
            showError(fieldName + "不能为空");
            return false;
        }
        for (char c : input.toCharArray()) {
            if (c > 127) {
                showError(fieldName + "必须是ASCII字符（不支持中文/特殊符号）");
                return false;
            }
        }
        return true;
    }

    private boolean validateRange(int value) {
        if (value < 0 || value > 0xFFFF) {
            showError("数据必须在0000到FFFF范围内");
            return false;
        }
        return true;
    }

    // 清空方法（保持不变）
    private void clearFields() {
        keyField.setText("");
        inputField.setText("");
        outputField.setText("");
        statusLabel.setText("已清空");
        statusLabel.setForeground(PRIMARY_COLOR);
        keyField.requestFocus();
    }

    // 错误提示（保持不变）
    private void showError(String message) {
        statusLabel.setText("错误: " + message);
        statusLabel.setForeground(WARNING_COLOR);
        JOptionPane.showMessageDialog(this, message, "输入错误",
                JOptionPane.ERROR_MESSAGE);
    }

    // 中间相遇攻击演示 - 支持动态添加/删除明密文对
    private void performMeetInTheMiddleAttack() {
        try {
            // 明密文对列表
            List<PlainCipherPair> pairs = new ArrayList<>();

            // 创建主对话框
            JDialog mainDialog = new JDialog(this, "中间相遇攻击", true);
            mainDialog.setLayout(new BorderLayout(10, 10));
            mainDialog.setSize(700, 600);
            mainDialog.setLocationRelativeTo(this);

            // 创建表格模型
            String[] columnNames = {"明文", "密文", "操作"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // 只有操作列可编辑
                }
            };



            JTable pairsTable = new JTable(tableModel);
            pairsTable.setRowHeight(30);
            pairsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            pairsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            pairsTable.getColumnModel().getColumn(2).setPreferredWidth(80);

            // 设置按钮渲染器和编辑器
            pairsTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            pairsTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), pairs, tableModel));

            JScrollPane tableScrollPane = new JScrollPane(pairsTable);
            tableScrollPane.setBorder(BorderFactory.createTitledBorder("明密文对列表 (至少需要1对)"));

            // 控制面板
            JPanel controlPanel = new JPanel(new FlowLayout());

            JButton addPairBtn = createStyledButton("添加", PRIMARY_COLOR);
            JButton startAttackBtn = createStyledButton("开始攻击", SECONDARY_COLOR);
            JButton clearAllBtn = createStyledButton("清空所有", WARNING_COLOR);

            controlPanel.add(addPairBtn);
            controlPanel.add(startAttackBtn);
            controlPanel.add(clearAllBtn);

            // 结果区域 - 统一字体设置
            JTextArea resultArea = new JTextArea(15, 50);
            resultArea.setEditable(false);

            // 使用安全的字体设置
            Font safeFont = new Font("Monospaced", Font.PLAIN, 12);
            resultArea.setFont(safeFont);
            resultArea.setBackground(new Color(245, 245, 245));
            resultArea.setForeground(Color.BLACK);

            JScrollPane resultScrollPane = new JScrollPane(resultArea);
            resultScrollPane.setBorder(BorderFactory.createTitledBorder("攻击结果"));

            // 使用选项卡面板
            JTabbedPane tabbedPane = new JTabbedPane();
            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.add(tableScrollPane, BorderLayout.CENTER);
            inputPanel.add(controlPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("明密文对输入", inputPanel);
            tabbedPane.addTab("攻击结果", resultScrollPane);

            mainDialog.add(tabbedPane, BorderLayout.CENTER);

            // 添加明密文对按钮事件
            addPairBtn.addActionListener(e -> {
                addNewPairDialog(pairs, tableModel);
            });

            // 开始攻击按钮事件
            startAttackBtn.addActionListener(e -> {
                if (pairs.isEmpty()) {
                    JOptionPane.showMessageDialog(mainDialog, "请至少添加一对明密文对",
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 切换到结果标签页
                tabbedPane.setSelectedIndex(1);
                performAttack(pairs, resultArea);
            });

            // 清空所有按钮事件
            clearAllBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(mainDialog,
                        "确定要清空所有明密文对吗？", "确认清空",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    pairs.clear();
                    tableModel.setRowCount(0);
                    resultArea.setText("");
                }
            });

            mainDialog.setVisible(true);

        } catch (Exception ex) {
            showError("中间相遇攻击失败: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // 添加新的明密文对对话框
    private void addNewPairDialog(List<PlainCipherPair> pairs, DefaultTableModel tableModel) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setPreferredSize(new Dimension(300, 100));

        JTextField plaintextField = new JTextField();
        JTextField ciphertextField = new JTextField();

        panel.add(new JLabel("明文 (4位十六进制):"));
        panel.add(plaintextField);
        panel.add(new JLabel("密文 (4位十六进制):"));
        panel.add(ciphertextField);

        // 设置输入框的提示文本
        plaintextField.setToolTipText("例如: 1234, ABCD, 0000");
        ciphertextField.setToolTipText("例如: 5678, FFFF, 1111");

        int result = JOptionPane.showConfirmDialog(this, panel,
                "添加明密文对", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String plaintextStr = plaintextField.getText().trim().toUpperCase();
            String ciphertextStr = ciphertextField.getText().trim().toUpperCase();

            // 验证输入
            if (plaintextStr.isEmpty() || ciphertextStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "明文和密文都不能为空",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!plaintextStr.matches("[0-9A-F]{4}")) {
                JOptionPane.showMessageDialog(this, "明文必须是4位十六进制数(0-9, A-F)",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ciphertextStr.matches("[0-9A-F]{4}")) {
                JOptionPane.showMessageDialog(this, "密文必须是4位十六进制数(0-9, A-F)",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int plaintext = Integer.parseInt(plaintextStr, 16);
                int ciphertext = Integer.parseInt(ciphertextStr, 16);

                PlainCipherPair pair = new PlainCipherPair(plaintext, ciphertext);
                pairs.add(pair);

                // 添加到表格
                tableModel.addRow(new Object[]{
                        String.format("%04X", plaintext),
                        String.format("%04X", ciphertext),
                        "删除"
                });

                JOptionPane.showMessageDialog(this, "明密文对添加成功！",
                        "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "十六进制格式错误",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 在 performAttack 方法中修改结果显示
    private void performAttack(List<PlainCipherPair> pairs, JTextArea resultArea) {
        // 在新线程中执行攻击，避免界面卡顿
        new Thread(() -> {
            try {
                // 创建 final 的副本变量
                final int pairCount = pairs.size();
                final List<PlainCipherPair> finalPairs = new ArrayList<>(pairs);

                updateResultArea(resultArea, "开始中间相遇攻击...\n");
                updateResultArea(resultArea, "使用 " + pairCount + " 对明密文\n\n");

                // 显示所有明密文对
                for (int i = 0; i < finalPairs.size(); i++) {
                    PlainCipherPair pair = finalPairs.get(i);
                    String pairInfo = String.format("对%d: 明文=%04X, 密文=%04X\n",
                            i + 1, pair.plaintext, pair.ciphertext);
                    updateResultArea(resultArea, pairInfo);
                }
                updateResultArea(resultArea, "\n");

                // 使用第一对进行攻击
                PlainCipherPair firstPair = finalPairs.get(0);
                updateResultArea(resultArea, "正在执行中间相遇攻击...\n");
                updateResultArea(resultArea, "这可能需要一些时间，请耐心等待...\n\n");

                List<Integer> possibleKeys = AttackUtils.meetInTheMiddleAttack(
                        firstPair.plaintext, firstPair.ciphertext);

                updateResultArea(resultArea, "初步找到 " + possibleKeys.size() + " 个可能的密钥\n");

                // 如果有多个明密文对，进行验证
                if (finalPairs.size() > 1) {
                    updateResultArea(resultArea, "使用额外明密文对验证密钥...\n");

                    List<Integer> plaintexts = new ArrayList<>();
                    List<Integer> ciphertexts = new ArrayList<>();
                    for (PlainCipherPair pair : finalPairs) {
                        plaintexts.add(pair.plaintext);
                        ciphertexts.add(pair.ciphertext);
                    }

                    List<Integer> verifiedKeys = AttackUtils.verifyKeysWithMultiplePairs(
                            possibleKeys, plaintexts, ciphertexts);

                    updateResultArea(resultArea, "验证后剩余 " + verifiedKeys.size() + " 个可能的密钥\n\n");
                    possibleKeys = verifiedKeys;
                }

                // 显示最终结果
                displaySimpleResults(possibleKeys, finalPairs, resultArea);

            } catch (Exception ex) {
                updateResultArea(resultArea, "攻击过程中出现错误: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        }).start();
    }

    // 简化的结果显示方法 - 强制使用纯ASCII字符
    private void displaySimpleResults(List<Integer> possibleKeys, List<PlainCipherPair> pairs, JTextArea resultArea) {
        SwingUtilities.invokeLater(() -> {
            // 清空之前的内容
            resultArea.setText("");

            resultArea.append("=== 攻击结果 ===\n");
            resultArea.append("使用的明密文对数量: " + pairs.size() + "\n");
            resultArea.append("找到的可能密钥数量: " + possibleKeys.size() + "\n\n");

            if (possibleKeys.isEmpty()) {
                resultArea.append("*** 未找到有效密钥 ***\n");
                resultArea.append("可能原因:\n");
                resultArea.append("- 明密文对不正确\n");
                resultArea.append("- 使用的不是双重加密\n");
                resultArea.append("- 需要更多明密文对\n");
            } else {
                resultArea.append("可能的密钥对列表:\n");
                resultArea.append("--------------------\n");

                int validCount = 0;
                for (int key : possibleKeys) {
                    int k1 = (key >> 16) & 0xFFFF;
                    int k2 = key & 0xFFFF;

                    // 验证密钥
                    boolean isValid = true;
                    for (PlainCipherPair pair : pairs) {
                        int testCipher = AttackUtils.doubleEncrypt(pair.plaintext, k1, k2);
                        if (testCipher != pair.ciphertext) {
                            isValid = false;
                            break;
                        }
                    }

                    // 使用纯ASCII状态标识
                    String status = isValid ? "[VALID]" : "[INVALID]";
                    if (isValid) validCount++;

                    resultArea.append(String.format("K1=%04X, K2=%04X %s\n", k1, k2, status));
                }

                resultArea.append("--------------------\n");
                resultArea.append("有效密钥: " + validCount + " 个\n");

                if (validCount > 0) {
                    resultArea.append("\n攻击成功！找到了有效的双重加密密钥。\n");
                }
            }

            resultArea.append("\n*** 攻击完成 ***\n");

            // 自动滚动到底部
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }

    // 更新结果区域的方法 - 确保文本安全
    private void updateResultArea(JTextArea resultArea, String text) {
        // 确保文本只包含安全的ASCII字符
        String safeText = text.replace("✓", "[OK]")
                .replace("✗", "[X]")
                .replace("●", "*")
                .replace("■", "#")
                .replace("…", "...")
                .replace("—", "--");

        SwingUtilities.invokeLater(() -> {
            resultArea.append(safeText);
            // 自动滚动到底部
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }

    // 在创建结果区域时设置基本字体
    private JTextArea createResultArea() {
        JTextArea resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);

        // 使用系统基本字体，避免编码问题
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        resultArea.setFont(font);

        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setForeground(Color.BLACK);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return resultArea;
    }

    // 明密文对数据类
    private static class PlainCipherPair {
        int plaintext;
        int ciphertext;

        PlainCipherPair(int plaintext, int ciphertext) {
            this.plaintext = plaintext;
            this.ciphertext = ciphertext;
        }
    }

    // 表格按钮渲染器
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(231, 76, 60)); // 红色删除按钮
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // 表格按钮编辑器
    private static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private List<PlainCipherPair> pairs;
        private DefaultTableModel tableModel;

        public ButtonEditor(JCheckBox checkBox, List<PlainCipherPair> pairs, DefaultTableModel tableModel) {
            super(checkBox);
            this.pairs = pairs;
            this.tableModel = tableModel;

            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // 处理删除操作
                int row = table.getEditingRow();
                if (row >= 0 && row < pairs.size()) {
                    int confirm = JOptionPane.showConfirmDialog(table,
                            "确定要删除这对明密文吗？", "确认删除",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        pairs.remove(row);
                        tableModel.removeRow(row);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }


    // CBC模式演示
    // CBC模式演示 - 优化布局
    private void performCBCDemo() {
        // 创建CBC模式演示对话框
        JDialog cbcDialog = new JDialog(this, "S-AES CBC模式加密演示", true);
        cbcDialog.setLayout(new BorderLayout(15, 15));
        cbcDialog.setSize(500, 650);
        cbcDialog.setLocationRelativeTo(this);
        cbcDialog.getContentPane().setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("CBC模式分组加密", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        cbcDialog.add(titlePanel, BorderLayout.NORTH);

        // 主内容面板 - 使用GridLayout垂直排列
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // 1. 消息输入区域
        JPanel messagePanel = new JPanel(new BorderLayout(10, 5));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "输入消息",
                0, 0,
                new Font("微软雅黑", Font.PLAIN, 12),
                PRIMARY_COLOR
        ));

        JTextArea messageArea = new JTextArea(3, 30);
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // 添加焦点效果
        messageArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                messageArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                messageArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(NEUTRAL_COLOR),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });

        JScrollPane messageScroll = new JScrollPane(messageArea);
        messagePanel.add(messageScroll, BorderLayout.CENTER);
        mainPanel.add(messagePanel);

        // 2. 密钥输入区域
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        keyPanel.setBackground(Color.WHITE);
        keyPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "加密密钥",
                0, 0,
                new Font("微软雅黑", Font.PLAIN, 12),
                PRIMARY_COLOR
        ));

        JLabel keyLabel = new JLabel("4位密钥:");
        keyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        keyLabel.setPreferredSize(new Dimension(80, 30));
        keyLabel.setForeground(DARK_TEXT);

        JTextField keyField = new JTextField(10);
        keyField.setFont(new Font("Consolas", Font.PLAIN, 16));
        keyField.setPreferredSize(new Dimension(120, 35));
        keyField.setToolTipText("4位十六进制数 (如: 1A2B)");
        keyField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 密钥框焦点效果
        keyField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                keyField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                keyField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(NEUTRAL_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        keyPanel.add(keyLabel);
        keyPanel.add(keyField);
        mainPanel.add(keyPanel);

        // 3. IV区域
        JPanel ivPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        ivPanel.setBackground(Color.WHITE);
        ivPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "初始向量IV",
                0, 0,
                new Font("微软雅黑", Font.PLAIN, 12),
                PRIMARY_COLOR
        ));

        JLabel ivLabel = new JLabel("初始向量:");
        ivLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ivLabel.setPreferredSize(new Dimension(80, 30));
        ivLabel.setForeground(DARK_TEXT);

        JTextField ivField = new JTextField(10);
        ivField.setFont(new Font("Consolas", Font.PLAIN, 16));
        ivField.setPreferredSize(new Dimension(120, 35));
        ivField.setToolTipText("4位十六进制数");
        ivField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // IV框焦点效果
        ivField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ivField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                ivField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(NEUTRAL_COLOR),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        JButton genIvBtn = createStyledButton("生成随机IV", NEUTRAL_COLOR);
        genIvBtn.setPreferredSize(new Dimension(120, 35));
        genIvBtn.addActionListener(e -> {
            SecureRandom random = new SecureRandom();
            int iv = random.nextInt(0x10000);
            ivField.setText(String.format("%04X", iv));
        });

        ivPanel.add(ivLabel);
        ivPanel.add(ivField);
        ivPanel.add(genIvBtn);
        mainPanel.add(ivPanel);

        // 4. 结果显示区域
        JPanel resultPanel = new JPanel(new BorderLayout(10, 5));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "加密/解密结果",
                0, 0,
                new Font("微软雅黑", Font.PLAIN, 12),
                PRIMARY_COLOR
        ));

        JTextArea resultArea = new JTextArea(3, 30);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBackground(LIGHT_BG);
        resultArea.setForeground(DARK_TEXT);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultPanel.add(resultScroll, BorderLayout.CENTER);
        mainPanel.add(resultPanel);

        cbcDialog.add(mainPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton encryptBtn = createStyledButton("CBC加密", PRIMARY_COLOR);
        JButton decryptBtn = createStyledButton("CBC解密", SECONDARY_COLOR);
        JButton clearBtn = createStyledButton("清空", NEUTRAL_COLOR);
        JButton closeBtn = createStyledButton("关闭", WARNING_COLOR);

        // 加密按钮事件
        encryptBtn.addActionListener(e -> {
            try {
                String message = messageArea.getText().trim();
                String keyStr = keyField.getText().trim();
                String ivStr = ivField.getText().trim();

                // 输入验证
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(cbcDialog, "请输入要加密的消息",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validateHexInput(keyStr, "密钥", 4)) {
                    return;
                }

                if (!validateHexInput(ivStr, "初始向量IV", 4)) {
                    return;
                }

                // 将消息转换为十六进制分组
                byte[] messageBytes = message.getBytes("UTF-8");
                StringBuilder hexBuilder = new StringBuilder();
                for (byte b : messageBytes) {
                    hexBuilder.append(String.format("%02X", b));
                }
                String hexMessage = hexBuilder.toString();

                // 确保长度是4的倍数（16位的倍数）
                if (hexMessage.length() % 4 != 0) {
                    hexMessage += "00"; // 填充零
                }

                // CBC加密
                int key = Integer.parseInt(keyStr, 16);
                int iv = Integer.parseInt(ivStr, 16);
                StringBuilder cipherBuilder = new StringBuilder();
                int previousBlock = iv;

                for (int i = 0; i < hexMessage.length(); i += 4) {
                    String blockStr = hexMessage.substring(i, i + 4);
                    int plainBlock = Integer.parseInt(blockStr, 16);

                    // CBC加密: 明文块 XOR 前向块 -> 加密
                    int xorResult = plainBlock ^ previousBlock;
                    int cipherBlock = SAES.encrypt(xorResult, key);
                    previousBlock = cipherBlock;

                    cipherBuilder.append(String.format("%04X", cipherBlock));
                }

                resultArea.setText(cipherBuilder.toString());
                resultArea.setForeground(SECONDARY_COLOR);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(cbcDialog, "加密错误: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 解密按钮事件
        decryptBtn.addActionListener(e -> {
            try {
                String ciphertext = messageArea.getText().trim();
                String keyStr = keyField.getText().trim();
                String ivStr = ivField.getText().trim();

                // 输入验证
                if (ciphertext.isEmpty()) {
                    JOptionPane.showMessageDialog(cbcDialog, "请输入要解密的密文",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validateHexInput(keyStr, "密钥", 4)) {
                    return;
                }

                if (!validateHexInput(ivStr, "初始向量IV", 4)) {
                    return;
                }

                if (!ciphertext.matches("[0-9A-Fa-f]+") || ciphertext.length() % 4 != 0) {
                    JOptionPane.showMessageDialog(cbcDialog, "密文必须是4的倍数长度的十六进制数",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // CBC解密
                int key = Integer.parseInt(keyStr, 16);
                int iv = Integer.parseInt(ivStr, 16);
                StringBuilder plainBuilder = new StringBuilder();
                int previousBlock = iv;

                for (int i = 0; i < ciphertext.length(); i += 4) {
                    String blockStr = ciphertext.substring(i, i + 4);
                    int cipherBlock = Integer.parseInt(blockStr, 16);

                    // CBC解密: 密文块解密 -> XOR 前向块
                    int decryptResult = SAES.decrypt(cipherBlock, key);
                    int plainBlock = decryptResult ^ previousBlock;
                    previousBlock = cipherBlock;

                    plainBuilder.append(String.format("%04X", plainBlock));
                }

                // 将十六进制转换回字符串
                String hexResult = plainBuilder.toString();
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 0; i < hexResult.length(); i += 2) {
                    String byteStr = hexResult.substring(i, i + 2);
                    if (!byteStr.equals("00")) { // 忽略填充的零
                        int charCode = Integer.parseInt(byteStr, 16);
                        messageBuilder.append((char) charCode);
                    }
                }

                resultArea.setText(messageBuilder.toString());
                resultArea.setForeground(PRIMARY_COLOR);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(cbcDialog, "解密错误: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 清空按钮事件
        clearBtn.addActionListener(e -> {
            messageArea.setText("");
            keyField.setText("");
            ivField.setText("");
            resultArea.setText("");
        });

        // 关闭按钮事件
        closeBtn.addActionListener(e -> cbcDialog.dispose());

        btnPanel.add(encryptBtn);
        btnPanel.add(decryptBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(closeBtn);
        cbcDialog.add(btnPanel, BorderLayout.SOUTH);

        // 初始化时生成随机IV
        SecureRandom random = new SecureRandom();
        int initialIv = random.nextInt(0x10000);
        ivField.setText(String.format("%04X", initialIv));

        cbcDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}