package ui;

import model.Member;
import service.MemberService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class MemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;

	private MemberService memberService;

    private JTable            memberTable;
    private DefaultTableModel tableModel;

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtSearch;

    private JButton btnRegister;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private int selectedMemberId = -1;
    private MainFrame mainFrame;

    public MemberPanel(MainFrame mainFrame) {
        this.setMainFrame(mainFrame);
        this.memberService = new MemberService();

        setBackground(MainFrame.HEADER_BG);
        setLayout(new BorderLayout());

        buildHeader();
        buildMainContent();
        loadMembers();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainFrame.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel title = new JLabel("👥 Member Management");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.TEXT_DARK);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setBackground(MainFrame.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(MainFrame.FONT_BODY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.setToolTipText("Search by name...");
        txtSearch.addActionListener(e -> searchMembers());

        JButton btnSearch  = createButton("🔍 Search", MainFrame.ACCENT_BLUE, Color.WHITE);
        JButton btnShowAll = createButton("Show All", new Color(108, 117, 125), Color.WHITE);

        btnSearch .addActionListener(e -> searchMembers());
        btnShowAll.addActionListener(e -> loadMembers());

        searchPanel.add(txtSearch);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(btnShowAll);

        header.add(title,       BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setBackground(MainFrame.HEADER_BG);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        content.add(buildTablePanel(), BorderLayout.CENTER);
        content.add(buildFormPanel(),  BorderLayout.EAST);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 222, 230)));

        tableModel = new DefaultTableModel(memberService.getMemberTableColumns(), 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        memberTable.setFont(MainFrame.FONT_BODY);
        memberTable.setRowHeight(32);
        memberTable.setSelectionBackground(new Color(232, 240, 255));
        memberTable.setSelectionForeground(MainFrame.TEXT_DARK);
        memberTable.setGridColor(new Color(235, 237, 240));
        memberTable.getTableHeader().setFont(MainFrame.FONT_HEADER);
        memberTable.getTableHeader().setBackground(new Color(245, 246, 250));
        memberTable.getTableHeader().setForeground(MainFrame.TEXT_DARK);
        memberTable.getTableHeader().setReorderingAllowed(false);

        memberTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(160); // Name
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(110); // Phone
        memberTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Joined

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && memberTable.getSelectedRow() != -1) {
                populateFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(new Color(245, 246, 250));
        labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)));
        JLabel lbl = new JLabel("Registered Members");
        lbl.setFont(MainFrame.FONT_HEADER);
        lbl.setForeground(MainFrame.TEXT_DARK);
        labelPanel.add(lbl);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(MainFrame.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(280, 0));

        JLabel formTitle = new JLabel("Member Details");
        formTitle.setFont(MainFrame.FONT_HEADER);
        formTitle.setForeground(MainFrame.TEXT_DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(15));

        txtName    = addFormField(panel, "Full Name *",    "Enter full name");
        txtEmail   = addFormField(panel, "Email *",        "Enter email address");
        txtPhone   = addFormField(panel, "Phone *",        "e.g. 0300-1234567");
        txtAddress = addFormField(panel, "Address",        "Enter home address");

        panel.add(Box.createVerticalStrut(20));

        btnRegister = createButton("➕ Register",    MainFrame.ACCENT_BLUE,   Color.WHITE);
        btnUpdate   = createButton("✏️ Update",      MainFrame.SUCCESS_GREEN, Color.WHITE);
        btnDelete   = createButton("🗑️ Delete",      MainFrame.DANGER_RED,    Color.WHITE);
        btnClear    = createButton("🔄 Clear Form",  new Color(108,117,125),  Color.WHITE);

        for (JButton btn : new JButton[]{btnRegister, btnUpdate, btnDelete, btnClear}) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        }

        btnRegister.addActionListener(e -> registerMember());
        btnUpdate  .addActionListener(e -> updateMember());
        btnDelete  .addActionListener(e -> deleteMember());
        btnClear   .addActionListener(e -> clearForm());

        panel.add(btnRegister);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnUpdate);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnDelete);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnClear);
        panel.add(Box.createVerticalGlue());

        return panel;
    }


    private void registerMember() {
        Member member = getMemberFromForm();
        String result = memberService.registerMember(member);
        showMessage(result);

        if (result.startsWith("✅")) {
            clearForm();
            loadMembers();
        }
    }

    private void updateMember() {
        if (selectedMemberId == -1) {
            showMessage("❌ Please select a member from the table first.");
            return;
        }
        Member member = getMemberFromForm();
        member.setPersonId(selectedMemberId);
        String result = memberService.updateMember(member);
        showMessage(result);

        if (result.startsWith("✅")) {
            clearForm();
            loadMembers();
        }
    }

    private void deleteMember() {
        if (selectedMemberId == -1) {
            showMessage("❌ Please select a member from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this member?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String result = memberService.deleteMember(selectedMemberId);
            showMessage(result);
            if (result.startsWith("✅")) {
                clearForm();
                loadMembers();
            }
        }
    }

    private void searchMembers() {
        String keyword = txtSearch.getText().trim();
        List<Member> results = memberService.searchMembers(keyword);
        populateTable(results);
    }


    private void loadMembers() {
        List<Member> members = memberService.getAllMembers();
        populateTable(members);
    }

    private void populateTable(List<Member> members) {
        tableModel.setRowCount(0);
        Object[][] data = memberService.getMembersAsTableData(members);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void populateFormFromTable() {
        int row = memberTable.getSelectedRow();
        if (row == -1) return;

        selectedMemberId = (int) tableModel.getValueAt(row, 0);
        txtName   .setText((String) tableModel.getValueAt(row, 1));
        txtEmail  .setText((String) tableModel.getValueAt(row, 2));
        txtPhone  .setText((String) tableModel.getValueAt(row, 3));
        txtAddress.setText("");
    }


    private Member getMemberFromForm() {
        Member member = new Member();
        member.setName   (txtName   .getText().trim());
        member.setEmail  (txtEmail  .getText().trim());
        member.setPhone  (txtPhone  .getText().trim());
        member.setAddress(txtAddress.getText().trim());
        return member;
    }

    private void clearForm() {
        txtName   .setText("");
        txtEmail  .setText("");
        txtPhone  .setText("");
        txtAddress.setText("");
        selectedMemberId = -1;
        memberTable.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Library System",
            message.startsWith("✅")
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.WARNING_MESSAGE);
    }


    private JTextField addFormField(JPanel panel, String label, String placeholder) {
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
        field.setToolTipText(placeholder);

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

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 222, 230));
        return sep;
    }

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
