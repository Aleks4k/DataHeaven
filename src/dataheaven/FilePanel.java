package dataheaven;

import static dataheaven.DataHeaven.cardPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;
//TODO: Vidi da li one skrivene kolone služe ičemu...
public class FilePanel extends JPanel {
    private Icon addFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
    private Icon deleteIcon = UIManager.getIcon("FileView.computerIcon");
    private Icon addFileIcon = UIManager.getIcon("FileView.fileIcon");
    private Icon downloadIcon = UIManager.getIcon("FileView.floppyDriveIcon");
    private String threePath;
    private FilePanel fp_this;
    private String latest_file;
    private DefaultTableModel model;
    private JTree fl;
    private final JSplitPane splitPane;
    private JScrollPane treeScrollPane;
    private byte[] binaryKey;
    public FilePanel() {
        binaryKey = hexStringToByteArray(DataHeaven.usersecretkey);
        fp_this = this;
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets(0, 0, 0, 0);
        cs.fill = GridBagConstraints.BOTH;
        cs.anchor = GridBagConstraints.NORTHWEST;
        cs.weightx = 1;
        cs.weighty = 1;
        JMenuBar bar = new JMenuBar();
        bar.setLayout(new GridBagLayout());
        //BUTTONI I LISTENERI ZA NJIH.
        JButton add_Folder = new JButton("", addFolderIcon);
        add_Folder.setToolTipText("Add Folder");
        bar.add(add_Folder, cs);
        JButton add_File = new JButton("", addFileIcon);
        add_File.setToolTipText("Add File");
        bar.add(add_File, cs);
        JButton deleteButton = new JButton("", deleteIcon);
        deleteButton.setToolTipText("Delete");
        bar.add(deleteButton, cs);
        JButton downloadButton = new JButton("", downloadIcon);
        downloadButton.setToolTipText("Decrypt and download");
        bar.add(downloadButton, cs);
        add_Folder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String inputValue = JOptionPane.showInputDialog(fp_this, "Please, input folder name.");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    SecureRandom random = new SecureRandom();
                    //IZ ZA ŠIFROVANJE FOLDER IMENA.
                    byte[] iv_fn = new byte[16];
                    random.nextBytes(iv_fn);
                    GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv_fn);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
                    byte[] ciphertextBytes = cipher.doFinal(inputValue.getBytes());
                    StringBuilder hexStringBuilder = new StringBuilder(2 * ciphertextBytes.length + 2 * iv_fn.length);
                    for (byte b : iv_fn) {
                        hexStringBuilder.append(String.format("%02X", b));
                    }
                    for (byte b : ciphertextBytes) {
                        hexStringBuilder.append(String.format("%02X", b));
                    }
                    if (Files.exists(Path.of(latest_file + "\\!" + hexStringBuilder.toString()))) {
                        JOptionPane.showMessageDialog(fp_this, "There is already one folder with same name.", "Folder write error.", 0);
                    } else {
                        Files.createDirectory(Path.of(latest_file + "\\!" + hexStringBuilder.toString()));
                        initFileList(model, latest_file, binaryKey);
                        DefaultTreeModel model = (DefaultTreeModel) fl.getModel();
                        DefaultMutableTreeNode rootToRemove = (DefaultMutableTreeNode) model.getRoot();
                        rootToRemove.removeAllChildren();
                        model.setRoot(getFileTree(new File(threePath), binaryKey));
                        model.reload();
                    }
                } catch (Exception ex) {
                }
            }
        });
        add_File.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Prvo pravimo ime.
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(fp_this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        StringBuilder sb_name = new StringBuilder();
                        //IV ZA ŠIFROVANJE FAJL PODATAKA.
                        byte[] iv = new byte[16]; // IV size is 16 bytes for AES
                        SecureRandom random = new SecureRandom();
                        random.nextBytes(iv);
                        IvParameterSpec ivSpec = new IvParameterSpec(iv);
                        //IZ ZA ŠIFROVANJE FAJL IMENA.
                        byte[] iv_fn = new byte[16];
                        random.nextBytes(iv_fn);
                        sb_name.append("|");
                        sb_name.append(selectedFile.getName());
                        sb_name.append("|");
                        sb_name.append(Base64.getEncoder().encodeToString(ivSpec.getIV()));
                        sb_name.append("|");
                        GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv_fn);
                        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
                        byte[] ciphertextBytes = cipher.doFinal(sb_name.toString().getBytes());
                        StringBuilder hexStringBuilder = new StringBuilder(2 * ciphertextBytes.length + 2 * iv_fn.length);
                        for (byte b : iv_fn) {
                            hexStringBuilder.append(String.format("%02X", b));
                        }
                        for (byte b : ciphertextBytes) {
                            hexStringBuilder.append(String.format("%02X", b));
                        }
                        if (Files.exists(Path.of(latest_file + "\\!" + hexStringBuilder.toString()))) {
                            JOptionPane.showMessageDialog(fp_this, "There is already one file with same name.", "File write error.", 0);
                        } else {
                            //Krecemo da upisujemo sa AES CBC i imenom koje smo gore dobili (hexStringBuilder.toString())
                            encryptFile(secretKeySpec, ivSpec, selectedFile.getAbsolutePath(), Path.of(latest_file + "\\!" + hexStringBuilder.toString()));
                            initFileList(model, latest_file, binaryKey);
                            DefaultTreeModel model = (DefaultTreeModel) fl.getModel();
                            DefaultMutableTreeNode rootToRemove = (DefaultMutableTreeNode) model.getRoot();
                            rootToRemove.removeAllChildren();
                            model.setRoot(getFileTree(new File(threePath), binaryKey));
                            model.reload();
                        }
                    }
                } catch (Exception ex) {
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TreePath selectedPath = fl.getSelectionPath();
                    if (selectedPath != null && !selectedPath.getLastPathComponent().toString().equals("Your Files")) {
                        int result = JOptionPane.showConfirmDialog(
                                null,
                                "You are about to delete: " + selectedPath.getLastPathComponent(),
                                "Are you sure?",
                                JOptionPane.YES_NO_OPTION
                        );
                        StringBuilder fpath = new StringBuilder();
                        if (result == JOptionPane.YES_OPTION) {
                            fpath.append(threePath);
                            for (Object o : selectedPath.getPath()){
                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) o;
                                if(!selectedNode.getUserObject().equals("Your Files")){
                                    fpath.append("\\!");
                                    NodeInfo nodeInfoObject = (NodeInfo) selectedNode.getUserObject();
                                    SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                                    byte[] iv_fn = hexStringToByteArray(nodeInfoObject.getIvFileName());
                                    GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv_fn);
                                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
                                    StringBuilder sb_name = new StringBuilder();
                                    if (!nodeInfoObject.getIvFile().isEmpty()) { //Ovo znači da je file izabran.
                                        sb_name.append("|");
                                        sb_name.append(nodeInfoObject.getVisibleMessage());
                                        sb_name.append("|");
                                        sb_name.append(nodeInfoObject.getIvFile());
                                        sb_name.append("|");
                                    } else {
                                        sb_name.append(nodeInfoObject.getVisibleMessage());
                                    }
                                    byte[] ciphertextBytes = cipher.doFinal(sb_name.toString().getBytes());
                                    StringBuilder hexStringBuilder = new StringBuilder(2 * ciphertextBytes.length + 2 * iv_fn.length);
                                    for (byte b : iv_fn) {
                                        hexStringBuilder.append(String.format("%02X", b));
                                    }
                                    for (byte b : ciphertextBytes) {
                                        hexStringBuilder.append(String.format("%02X", b));
                                    }
                                    fpath.append(hexStringBuilder.toString());
                                }
                            }
                            var file = new File(fpath.toString());
                            if(file.isDirectory()){
                                deleteFolder(file);
                            } else {
                                file.delete();
                            }
                            initFileList(model, threePath, binaryKey);
                            DefaultTreeModel model = (DefaultTreeModel) fl.getModel();
                            DefaultMutableTreeNode rootToRemove = (DefaultMutableTreeNode) model.getRoot();
                            rootToRemove.removeAllChildren();
                            model.setRoot(getFileTree(new File(threePath), binaryKey));
                            model.reload();
                        }
                    } else {
                        JOptionPane.showMessageDialog(fp_this, "Please select file first.", "Delete error.", 0);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    TreePath selectedPath = fl.getSelectionPath();
                    StringBuilder fpath = new StringBuilder();
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                    if(selectedNode.getUserObject().equals("Your Files")){
                        JOptionPane.showMessageDialog(fp_this, "Please select file.", "Download error.", 0);
                        return;
                    }
                    NodeInfo nodeInfoObject = (NodeInfo) selectedNode.getUserObject();
                    if(nodeInfoObject.getIvFile().isEmpty()){
                        JOptionPane.showMessageDialog(fp_this, "Please select file not folder.", "Download error.", 0);
                        return;
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(nodeInfoObject.getIvFile()));
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(selectedNode.toString()));
                    int result = fileChooser.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        fpath.append(threePath);
                        for (Object o : selectedPath.getPath()){
                            selectedNode = (DefaultMutableTreeNode) o;
                            if(!selectedNode.getUserObject().equals("Your Files")){
                                fpath.append("\\!");
                                nodeInfoObject = (NodeInfo) selectedNode.getUserObject();
                                SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                                byte[] iv_fn = hexStringToByteArray(nodeInfoObject.getIvFileName());
                                GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv_fn);
                                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
                                StringBuilder sb_name = new StringBuilder();
                                if (!nodeInfoObject.getIvFile().isEmpty()) { //Ovo znači da je file izabran.
                                    sb_name.append("|");
                                    sb_name.append(nodeInfoObject.getVisibleMessage());
                                    sb_name.append("|");
                                    sb_name.append(nodeInfoObject.getIvFile());
                                    sb_name.append("|");
                                } else {
                                    sb_name.append(nodeInfoObject.getVisibleMessage());
                                }
                                byte[] ciphertextBytes = cipher.doFinal(sb_name.toString().getBytes());
                                StringBuilder hexStringBuilder = new StringBuilder(2 * ciphertextBytes.length + 2 * iv_fn.length);
                                for (byte b : iv_fn) {
                                    hexStringBuilder.append(String.format("%02X", b));
                                }
                                for (byte b : ciphertextBytes) {
                                    hexStringBuilder.append(String.format("%02X", b));
                                }
                                fpath.append(hexStringBuilder.toString());
                            }
                        }
                        SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                        decryptFile(secretKeySpec, ivSpec, fpath.toString(), Path.of(fileChooser.getSelectedFile().getAbsolutePath()));
                    }
                }catch(Exception ex){}
            }
        });
        //KRAJ BUTTONA I LISTENERA.
        add(bar, BorderLayout.NORTH);
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("user.dir"));
        sb.append("\\users\\");
        sb.append(DataHeaven.usersecretpath);
        threePath = latest_file = sb.toString();
        fl = new JTree(getFileTree(new File(threePath), binaryKey));
        treeScrollPane = new JScrollPane(fl);
        String[] columnName = {"Name", "Date modified", "Type", "Size", "IV-Hidden", "IV-FN-Hidden"};
        Object[][] data = {};
        model = new DefaultTableModel(data, columnName) {
            @Override
            public Class getColumnClass(int column) {
                return switch (column) {
                    case 0 ->
                        String.class;
                    case 1 ->
                        Date.class;
                    case 2 ->
                        String.class;
                    case 3 ->
                        Long.class;
                    case 4 ->
                        String.class;
                    case 5 ->
                        String.class;
                    default ->
                        Long.class;
                };
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable files = new JTable(model);
        hideColumn(files, 4);
        hideColumn(files, 5);
        files.setAutoCreateRowSorter(true);
        initFileList(model, threePath, binaryKey);
        TreeSelectionListener tsl = (TreeSelectionEvent event) -> {
            TreePath path = event.getPath();
            StringBuilder strb = new StringBuilder();
            strb.append(threePath);
            for (Object o : path.getPath()){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) o;
                if(!selectedNode.getUserObject().equals("Your Files")){
                    try {
                        NodeInfo nodeInfoObject = (NodeInfo) selectedNode.getUserObject();
                        String iv_fn_String = nodeInfoObject.getIvFileName();
                        String iv_String = nodeInfoObject.getIvFile();
                        byte[] iv_fn = hexStringToByteArray(iv_fn_String);
                        GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv_fn);
                        strb.append("\\");
                        StringBuilder sb_name = new StringBuilder();
                        if (!iv_String.isEmpty()) { //Ovo znači da je file izabran.
                            sb_name.append("|");
                            sb_name.append(nodeInfoObject.getVisibleMessage());
                            sb_name.append("|");
                            sb_name.append(iv_String);
                            sb_name.append("|");
                        } else {
                            sb_name.append(nodeInfoObject.getVisibleMessage());
                        }
                        SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
                        byte[] ciphertextBytes = cipher.doFinal(sb_name.toString().getBytes());
                        StringBuilder hexStringBuilder = new StringBuilder(2 * ciphertextBytes.length + 2 * iv_fn.length);
                        for (byte b : iv_fn) {
                            hexStringBuilder.append(String.format("%02X", b));
                        }
                        for (byte b : ciphertextBytes) {
                            hexStringBuilder.append(String.format("%02X", b));
                        }
                        strb.append("!");
                        strb.append(hexStringBuilder.toString());
                    }
                    catch (Exception ex){
                        
                    }
                }
            }
            File dir = new File(strb.toString());
            if (dir.isDirectory()) {
                latest_file = strb.toString();
                initFileList(model, strb.toString(), binaryKey);
            } else {
                latest_file = dir.getParent();
                initFileList(model, dir.getParent(), binaryKey);
            }
        };
        fl.addTreeSelectionListener(tsl);
        JScrollPane tableScrollPane = new JScrollPane(files, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(100);
        add(splitPane, BorderLayout.CENTER);
    }
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    public static void encryptFile(SecretKeySpec secretKey, IvParameterSpec ivSpec, String inputFile, Path outputFile) throws Exception {
        try (CipherOutputStream cipherOutputStream = createCipherOutputStream(secretKey, ivSpec, outputFile, Cipher.ENCRYPT_MODE); FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
                cipherOutputStream.flush();
            }
            cipherOutputStream.close();
        }
    }
    public static void decryptFile(SecretKeySpec secretKey, IvParameterSpec ivSpec, String inputFile, Path outputFile) throws Exception {
        try (CipherOutputStream cipherOutputStream = createCipherOutputStream(secretKey, ivSpec, outputFile, Cipher.DECRYPT_MODE); FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
                cipherOutputStream.flush();
            }
            cipherOutputStream.close();
        }
    }
    public static CipherOutputStream createCipherOutputStream(SecretKeySpec secretKey, IvParameterSpec ivSpec, Path outputFile, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ivSpec);
        Files.createFile(Path.of(outputFile.toString()));
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile.toString());
        return new CipherOutputStream(fileOutputStream, cipher);
    }
    public static void initFileList(DefaultTableModel model, String path, byte[] binaryKey) {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File file : files) {
            Vector<Object> row = new Vector<>();
            if (file.getName().startsWith("!")) {
                try {
                    SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                    String tempName = file.getName().replace("!", "");
                    String iv_fn = tempName.substring(0, 32); //Prvih 32 znaka su IV u hex formatu.
                    tempName = tempName.substring(32); //Uklanjamo prvih 32 znaka koji su IV u hex formatu.
                    byte[] ciphertextBytes = hexStringToByteArray(tempName);
                    byte[] iv = hexStringToByteArray(iv_fn);
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv);
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
                    byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
                    if (file.isDirectory()) {
                        row.add(new String(decryptedBytes));
                        row.add(file.lastModified());
                        row.add("Folder");
                        row.add(0L);
                        row.add("");
                        row.add(iv_fn);
                    } else {
                        ArrayList<String> isolatedTexts = new ArrayList<>();
                        String[] substrings = new String(decryptedBytes).split("\\|");
                        for (String substring : substrings) {
                            if (!substring.isEmpty()) {
                                isolatedTexts.add(substring);
                            }
                        }
                        row.add(isolatedTexts.get(0));
                        row.add(file.lastModified());
                        row.add(isolatedTexts.get(0).substring(isolatedTexts.get(0).lastIndexOf(".") + 1));
                        row.add(file.length());
                        row.add(isolatedTexts.get(1));
                        row.add(iv_fn);
                    }
                } catch (Exception ex) {
                }
            } else {
                row.add(file.getName());
                row.add(file.lastModified());
                if (file.isDirectory()) {
                    row.add("Folder");
                    row.add(0L);
                    row.add("");
                    row.add("");
                } else {
                    row.add(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                    row.add(file.length());
                    row.add("");
                    row.add("");
                }
            }
            model.addRow(row);
        }
    }
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            String byteInHex = hexString.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(byteInHex, 16);
        }
        return byteArray;
    }
    public static DefaultMutableTreeNode getFileTree(File dir, byte[] binaryKey) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Your Files");
        addFilesAndSubdirectories(root, dir, binaryKey);
        return root;
    }
    public static void addFilesAndSubdirectories(DefaultMutableTreeNode parent, File dir, byte[] binaryKey) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode node = null;
                if (file.getName().startsWith("!")) {
                    try {
                        SecretKeySpec secretKeySpec = new SecretKeySpec(binaryKey, "AES");
                        String tempName = file.getName().replace("!", "");
                        String iv_fn = tempName.substring(0, 32); //Prvih 32 znaka su IV u hex formatu.
                        tempName = tempName.substring(32); //Uklanjamo prvih 32 znaka koji su IV u hex formatu.
                        byte[] ciphertextBytes = hexStringToByteArray(tempName);
                        byte[] iv = hexStringToByteArray(iv_fn);
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv);
                        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
                        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
                        if (file.isDirectory()) {
                            NodeInfo nodeInfo = new NodeInfo(new String(decryptedBytes), "", iv_fn);
                            node = new DefaultMutableTreeNode(nodeInfo);
                        } else {
                            ArrayList<String> isolatedTexts = new ArrayList<>();
                            String[] substrings = new String(decryptedBytes).split("\\|");
                            for (String substring : substrings) {
                                if (!substring.isEmpty()) {
                                    isolatedTexts.add(substring);
                                }
                            }
                            //Da možemo da vidimo i IV u Node ako npr. imamo dva fajla sa istim imenom što može da se desi treba nam on za raspoznavanje.
                            NodeInfo nodeInfo = new NodeInfo(isolatedTexts.get(0), isolatedTexts.get(1), iv_fn);
                            node = new DefaultMutableTreeNode(nodeInfo);
                        }
                    } catch (Exception ex) {
                    }
                } else {
                    node = new DefaultMutableTreeNode(file.getName());
                }
                if (file.isDirectory()) {
                    addFilesAndSubdirectories(node, file, binaryKey);
                }
                parent.add(node);
            }
        }
    }
    public static void hideColumn(JTable table, int columnIndex) {
        TableColumnModel tcm = table.getColumnModel();
        TableColumn column = tcm.getColumn(columnIndex);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setWidth(0);
        JTableHeader header = table.getTableHeader();
        TableColumnModel headerModel = header.getColumnModel();
        TableColumn headerColumn = headerModel.getColumn(columnIndex);
        headerColumn.setMaxWidth(0);
        headerColumn.setMinWidth(0);
        headerColumn.setPreferredWidth(0);
        headerColumn.setWidth(0);
    }
}