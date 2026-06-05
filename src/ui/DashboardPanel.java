package ui;

import service.BookService;
import service.MemberService;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
    private BookService   bookService;
    private MemberService memberService;

    private JLabel lblTotalBooks;
    private JLabel lblTotalMembers;
    private JLabel lblAvailableBooks;

    private MainFrame mainFrame;

    public DashboardPanel(MainFrame mainFrame) {
        this.setMainFrame(mainFrame);
        this.bookService   = new BookService();
        this.memberService = new MemberService();

        setBackground(MainFrame.HEADER_BG);
        setLayout(new BorderLayout());

        buildHeader();
        buildStatCards();
        buildWelcomeSection();

        refreshStats();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainFrame.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel title = new JLabel("Dashboard");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.TEXT_DARK);

        JLabel subtitle = new JLabel("Welcome to Library Management System");
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

    private void buildStatCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(MainFrame.HEADER_BG);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        JPanel booksCard     = createStatCard("📚", "Total Books",    "0", new Color(67, 104, 186));
        JPanel membersCard   = createStatCard("👥", "Total Members",  "0", new Color(40, 167,  69));
        JPanel availCard     = createStatCard("✅", "Available Books","0", new Color(23, 162, 184));

        lblTotalBooks     = (JLabel) ((JPanel) booksCard  .getComponent(1)).getComponent(1);
        lblTotalMembers   = (JLabel) ((JPanel) membersCard.getComponent(1)).getComponent(1);
        lblAvailableBooks = (JLabel) ((JPanel) availCard  .getComponent(1)).getComponent(1);

        cardsPanel.add(booksCard);
        cardsPanel.add(membersCard);
        cardsPanel.add(availCard);

        add(cardsPanel, BorderLayout.CENTER);
    }

    
    private JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 222, 230), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setBackground(new Color(
            color.getRed(), color.getGreen(), color.getBlue(), 30
        ));
        iconPanel.setPreferredSize(new Dimension(70, 0));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(MainFrame.WHITE);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(MainFrame.FONT_SMALL);
        lblLabel.setForeground(MainFrame.TEXT_GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(color);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 4));

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(lblLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblValue);
        textPanel.add(Box.createVerticalGlue());

        card.add(textPanel,  BorderLayout.CENTER);
        card.add(colorBar,   BorderLayout.SOUTH);

        return card;
    }

    private void buildWelcomeSection() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(MainFrame.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel infoTitle = new JLabel("Quick Guide");
        infoTitle.setFont(MainFrame.FONT_HEADER);
        infoTitle.setForeground(MainFrame.TEXT_DARK);

        JPanel guidePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        guidePanel.setBackground(MainFrame.WHITE);
        guidePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        guidePanel.add(createGuideItem("📖 Books",    "Add, edit, delete and search library books"));
        guidePanel.add(createGuideItem("👥 Members",  "Register and manage library members"));
        guidePanel.add(createGuideItem("🔍 Search",   "Use search bar in each section to find records"));

        infoPanel.add(infoTitle,  BorderLayout.NORTH);
        infoPanel.add(guidePanel, BorderLayout.CENTER);

        add(infoPanel, BorderLayout.SOUTH);
    }

    private JPanel createGuideItem(String title, String desc) {
        JPanel panel = new JPanel();
        panel.setBackground(MainFrame.HEADER_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel t = new JLabel(title);
        t.setFont(MainFrame.FONT_HEADER);
        t.setForeground(MainFrame.TEXT_DARK);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(MainFrame.FONT_SMALL);
        d.setForeground(MainFrame.TEXT_GRAY);

        panel.add(t);
        panel.add(Box.createVerticalStrut(5));
        panel.add(d);

        return panel;
    }

    public void refreshStats() {
        int totalBooks   = bookService.getTotalBookCount();
        int totalMembers = memberService.getTotalMemberCount();

        long availableBooks = bookService.getAllBooks()
            .stream()
            .filter(b -> b.getAvailable() > 0)
            .count();

        lblTotalBooks    .setText(String.valueOf(totalBooks));
        lblTotalMembers  .setText(String.valueOf(totalMembers));
        lblAvailableBooks.setText(String.valueOf(availableBooks));
    }
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
