package ui;

import javax.swing.*;
import java.awt.*;
import dao.DBConnection;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final Color SIDEBAR_BG    = new Color(28,  37,  65);
    public static final Color SIDEBAR_BTN   = new Color(40,  53,  90);
    public static final Color SIDEBAR_HOVER = new Color(67, 104, 186);
    public static final Color ACCENT_BLUE   = new Color(67, 104, 186);
    public static final Color HEADER_BG     = new Color(245, 246, 250);
    public static final Color WHITE         = Color.WHITE;
    public static final Color TEXT_DARK     = new Color(33,  37,  41);
    public static final Color TEXT_GRAY     = new Color(108, 117, 125);
    public static final Color SUCCESS_GREEN = new Color(40,  167,  69);
    public static final Color DANGER_RED    = new Color(220,  53,  69);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);

    private JPanel           contentPanel;
    private CardLayout       cardLayout;
    private DashboardPanel   dashboardPanel;
    private BookPanel        bookPanel;
    private MemberPanel      memberPanel;
    private TransactionPanel transactionPanel;

    private JButton btnDashboard;
    private JButton btnBooks;
    private JButton btnMembers;
    private JButton btnTransactions;
    private JButton activeButton;

    public MainFrame() {
        initializeFrame();
        buildSidebar();
        buildContentArea();
        showPanel("Dashboard", btnDashboard);
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Library Management System — IUB");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DBConnection.closeConnection();
            }
        });
    }

    private void buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(20, 27, 50));
        titlePanel.setPreferredSize(new Dimension(210, 70));
        titlePanel.setLayout(new GridBagLayout());
        JLabel appTitle = new JLabel("📚 LMS");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);
        titlePanel.add(appTitle);
        sidebar.add(titlePanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btnDashboard    = createSidebarButton("  Dashboard",    "📊");
        btnBooks        = createSidebarButton("  Books",        "📖");
        btnMembers      = createSidebarButton("  Members",      "👥");
        btnTransactions = createSidebarButton("  Transactions", "🔄");

        btnDashboard   .addActionListener(e -> showPanel("Dashboard",    btnDashboard));
        btnBooks       .addActionListener(e -> showPanel("Books",        btnBooks));
        btnMembers     .addActionListener(e -> showPanel("Members",      btnMembers));
        btnTransactions.addActionListener(e -> showPanel("Transactions", btnTransactions));

        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(btnDashboard);
        navPanel.add(Box.createVerticalStrut(3));
        navPanel.add(btnBooks);
        navPanel.add(Box.createVerticalStrut(3));
        navPanel.add(btnMembers);
        navPanel.add(Box.createVerticalStrut(3));
        navPanel.add(btnTransactions);

        sidebar.add(navPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(SIDEBAR_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        JLabel footer = new JLabel("<html><center>IUB — CS Department<br/>2nd Semester</center></html>");
        footer.setFont(FONT_SMALL);
        footer.setForeground(new Color(100, 120, 160));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.add(footer, BorderLayout.CENTER);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private void buildContentArea() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(HEADER_BG);

        dashboardPanel   = new DashboardPanel(this);
        bookPanel        = new BookPanel(this);
        memberPanel      = new MemberPanel(this);
        transactionPanel = new TransactionPanel(this);

        contentPanel.add(dashboardPanel,   "Dashboard");
        contentPanel.add(bookPanel,        "Books");
        contentPanel.add(memberPanel,      "Members");
        contentPanel.add(transactionPanel, "Transactions");

        add(contentPanel, BorderLayout.CENTER);
    }

    public void showPanel(String panelName, JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_BTN);
            activeButton.setForeground(new Color(180, 190, 210));
        }
        button.setBackground(SIDEBAR_HOVER);
        button.setForeground(Color.WHITE);
        activeButton = button;
        cardLayout.show(contentPanel, panelName);
        if (panelName.equals("Dashboard")) {
            dashboardPanel.refreshStats();
        }
    }

    private JButton createSidebarButton(String text, String icon) {
        JButton btn = new JButton(icon + text);
        btn.setFont(FONT_BTN);
        btn.setForeground(new Color(180, 190, 210));
        btn.setBackground(SIDEBAR_BTN);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(210, 48));
        btn.setPreferredSize(new Dimension(210, 48));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(new Color(50, 65, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_BTN);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}