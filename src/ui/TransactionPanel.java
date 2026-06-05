package ui;

import model.Transaction;
import service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class TransactionPanel extends JPanel {

    
	private static final long serialVersionUID = 1L;

    private TransactionService transactionService;

    private JTextField txtBorrowBookId;
    private JTextField txtBorrowMemberId;

    private JTextField txtReturnTxnId;

    private JTable            activeTable;
    private DefaultTableModel activeModel;
    private JTable            historyTable;
    private DefaultTableModel historyModel;

    private MainFrame mainFrame;

    public TransactionPanel(MainFrame mainFrame) {
        this.setMainFrame(mainFrame);
        this.transactionService = new TransactionService();

        setBackground(MainFrame.HEADER_BG);
        setLayout(new BorderLayout());

        buildHeader();
        buildTabbedContent();
        loadActiveLoans();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainFrame.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel title = new JLabel("🔄 Borrow & Return");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.TEXT_DARK);

        JLabel subtitle = new JLabel("Manage book loans and returns");
        subtitle.setFont(MainFrame.FONT_BODY);
        subtitle.setForeground(MainFrame.TEXT_GRAY);

        JPanel titleBox = new JPanel();
        titleBox.setBackground(MainFrame.WHITE);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    // ── Tabbed Content ────────────────────────────────────────
    private void buildTabbedContent() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(MainFrame.FONT_HEADER);
        tabs.setBackground(MainFrame.HEADER_BG);

        tabs.addTab("📖 Borrow / Return", buildBorrowReturnTab());
        tabs.addTab("📋 Full History",    buildHistoryTab());

        tabs.addChangeListener(e -> {
            loadActiveLoans();
            if (tabs.getSelectedIndex() == 1) loadHistory();
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        wrapper.setBackground(MainFrame.HEADER_BG);
        wrapper.add(tabs, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel buildBorrowReturnTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MainFrame.HEADER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.setBackground(MainFrame.HEADER_BG);
        formsPanel.setPreferredSize(new Dimension(300, 0));

        formsPanel.add(buildBorrowForm());
        formsPanel.add(Box.createVerticalStrut(15));
        formsPanel.add(buildReturnForm());
        formsPanel.add(Box.createVerticalGlue());

        panel.add(formsPanel,            BorderLayout.WEST);
        panel.add(buildActiveLoansTable(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBorrowForm() {
        JPanel panel = new JPanel();
        panel.setBackground(MainFrame.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("📖 Issue Book");
        title.setFont(MainFrame.FONT_HEADER);
        title.setForeground(new Color(67, 104, 186));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));

        JLabel note = new JLabel("<html><i>Borrow period: 14 days | Fine: Rs. 5/day</i></html>");
        note.setFont(MainFrame.FONT_SMALL);
        note.setForeground(MainFrame.TEXT_GRAY);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(note);
        panel.add(Box.createVerticalStrut(15));

        txtBorrowBookId   = addFormField(panel, "Book ID *",   "Enter Book ID from Books tab");
        txtBorrowMemberId = addFormField(panel, "Member ID *", "Enter Member ID from Members tab");

        panel.add(Box.createVerticalStrut(10));

        JButton btnBorrow = createButton("📖 Issue Book", MainFrame.ACCENT_BLUE, Color.WHITE);
        btnBorrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBorrow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnBorrow.addActionListener(e -> borrowBook());
        panel.add(btnBorrow);

        return panel;
    }

    private JPanel buildReturnForm() {
        JPanel panel = new JPanel();
        panel.setBackground(MainFrame.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("🔄 Return Book");
        title.setFont(MainFrame.FONT_HEADER);
        title.setForeground(MainFrame.SUCCESS_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));

        JLabel note = new JLabel("<html><i>Click a row in the table to get Txn ID</i></html>");
        note.setFont(MainFrame.FONT_SMALL);
        note.setForeground(MainFrame.TEXT_GRAY);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(note);
        panel.add(Box.createVerticalStrut(15));

        txtReturnTxnId = addFormField(panel, "Transaction ID *", "Enter Txn ID from the table");

        panel.add(Box.createVerticalStrut(10));

        JButton btnReturn = createButton("✅ Return Book", MainFrame.SUCCESS_GREEN, Color.WHITE);
        btnReturn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReturn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnReturn.addActionListener(e -> returnBook());
        panel.add(btnReturn);

        return panel;
    }

    private JPanel buildActiveLoansTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 222, 230)));

        // Label
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(new Color(245, 246, 250));
        labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)));
        JLabel lbl = new JLabel("Active Loans (Not Yet Returned)");
        lbl.setFont(MainFrame.FONT_HEADER);
        lbl.setForeground(MainFrame.TEXT_DARK);
        labelPanel.add(lbl);

        JButton btnRefresh = createButton("🔄 Refresh", new Color(108,117,125), Color.WHITE);
        btnRefresh.addActionListener(e -> loadActiveLoans());
        labelPanel.add(btnRefresh);

        activeModel = new DefaultTableModel(
            transactionService.getActiveLoanColumns(), 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        activeTable = new JTable(activeModel);
        styleTable(activeTable);

        activeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activeTable.getSelectedRow() != -1) {
                int txnId = (int) activeModel.getValueAt(activeTable.getSelectedRow(), 0);
                txtReturnTxnId.setText(String.valueOf(txnId));
            }
        });

        JScrollPane scroll = new JScrollPane(activeTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(scroll,     BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(new Color(245, 246, 250));
        labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)));
        JLabel lbl = new JLabel("Complete Transaction History");
        lbl.setFont(MainFrame.FONT_HEADER);
        lbl.setForeground(MainFrame.TEXT_DARK);
        labelPanel.add(lbl);

        historyModel = new DefaultTableModel(
            transactionService.getAllTransactionColumns(), 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        historyTable = new JTable(historyModel);
        styleTable(historyTable);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(scroll,     BorderLayout.CENTER);
        return panel;
    }

    private void borrowBook() {
        try {
            int bookId   = Integer.parseInt(txtBorrowBookId  .getText().trim());
            int memberId = Integer.parseInt(txtBorrowMemberId.getText().trim());

            String result = transactionService.borrowBook(bookId, memberId);
            showMessage(result);

            if (result.startsWith("✅")) {
                txtBorrowBookId  .setText("");
                txtBorrowMemberId.setText("");
                loadActiveLoans();
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Book ID and Member ID must be valid numbers.\nCheck the Books and Members tabs for IDs.");
        }
    }

    private void returnBook() {
        try {
            int txnId = Integer.parseInt(txtReturnTxnId.getText().trim());

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm return for Transaction ID: " + txnId + "?\nFine will be calculated automatically if overdue.",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String result = transactionService.returnBook(txnId);
                showMessage(result);

                if (result.startsWith("✅")) {
                    txtReturnTxnId.setText("");
                    activeTable.clearSelection();
                    loadActiveLoans();
                }
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Transaction ID must be a valid number.\nClick a row in the Active Loans table.");
        }
    }

    private void loadActiveLoans() {
        List<Transaction> loans = transactionService.getActiveLoans();
        activeModel.setRowCount(0);
        Object[][] data = transactionService.getActiveLoansTableData(loans);
        for (Object[] row : data) activeModel.addRow(row);
    }

    private void loadHistory() {
        List<Transaction> all = transactionService.getAllTransactions();
        historyModel.setRowCount(0);
        Object[][] data = transactionService.getAllTransactionsTableData(all);
        for (Object[] row : data) historyModel.addRow(row);
    }

    private void styleTable(JTable table) {
        table.setFont(MainFrame.FONT_BODY);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(MainFrame.TEXT_DARK);
        table.setGridColor(new Color(235, 237, 240));
        table.getTableHeader().setFont(MainFrame.FONT_HEADER);
        table.getTableHeader().setBackground(new Color(245, 246, 250));
        table.getTableHeader().setForeground(MainFrame.TEXT_DARK);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private JTextField addFormField(JPanel panel, String label, String tooltip) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(MainFrame.FONT_SMALL);
        lbl.setForeground(MainFrame.TEXT_GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(MainFrame.FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 215)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setToolTipText(tooltip);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return field;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(MainFrame.FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Library System",
            message.startsWith("✅")
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.WARNING_MESSAGE);
    }

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
