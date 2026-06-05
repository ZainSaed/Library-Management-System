package ui;

import model.Book;
import service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class BookPanel extends JPanel {

	private static final long serialVersionUID = 1L;

    private BookService bookService;

    private JTable            bookTable;
    private DefaultTableModel tableModel;

    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtIsbn;
    private JTextField txtCategory;
    private JTextField txtCopies;
    private JTextField txtSearch;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private int selectedBookId = -1; 
    private MainFrame mainFrame;

    public BookPanel(MainFrame mainFrame) {
        this.setMainFrame(mainFrame);
        this.bookService = new BookService();

        setBackground(MainFrame.HEADER_BG);
        setLayout(new BorderLayout());

        buildHeader();
        buildMainContent();
        loadBooks();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainFrame.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel title = new JLabel("📖 Book Management");
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
        txtSearch.setToolTipText("Search by title...");

        JButton btnSearch = createButton("🔍 Search", MainFrame.ACCENT_BLUE, Color.WHITE);
        JButton btnShowAll = createButton("Show All", new Color(108, 117, 125), Color.WHITE);

        btnSearch .addActionListener(e -> searchBooks());
        btnShowAll.addActionListener(e -> loadBooks());

        txtSearch.addActionListener(e -> searchBooks());

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

        tableModel = new DefaultTableModel(bookService.getBookTableColumns(), 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setFont(MainFrame.FONT_BODY);
        bookTable.setRowHeight(32);
        bookTable.setSelectionBackground(new Color(232, 240, 255));
        bookTable.setSelectionForeground(MainFrame.TEXT_DARK);
        bookTable.setGridColor(new Color(235, 237, 240));
        bookTable.setShowGrid(true);
        bookTable.getTableHeader().setFont(MainFrame.FONT_HEADER);
        bookTable.getTableHeader().setBackground(new Color(245, 246, 250));
        bookTable.getTableHeader().setForeground(MainFrame.TEXT_DARK);
        bookTable.getTableHeader().setReorderingAllowed(false);

        bookTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Category
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(50);  // Total
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Available

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                populateFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(new Color(245, 246, 250));
        labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 222, 230)));
        JLabel lbl = new JLabel("All Books");
        lbl.setFont(MainFrame.FONT_HEADER);
        lbl.setForeground(MainFrame.TEXT_DARK);
        labelPanel.add(lbl);

        panel.add(labelPanel,  BorderLayout.NORTH);
        panel.add(scrollPane,  BorderLayout.CENTER);

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

        JLabel formTitle = new JLabel("Book Details");
        formTitle.setFont(MainFrame.FONT_HEADER);
        formTitle.setForeground(MainFrame.TEXT_DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(15));

        txtTitle    = addFormField(panel, "Title *",       "Enter book title");
        txtAuthor   = addFormField(panel, "Author *",      "Enter author name");
        txtIsbn     = addFormField(panel, "ISBN",          "e.g. 978-0000000000");
        txtCategory = addFormField(panel, "Category",      "e.g. Computer Science");
        txtCopies   = addFormField(panel, "Total Copies *","e.g. 3");

        panel.add(Box.createVerticalStrut(20));

        btnAdd    = createButton("➕ Add Book",     MainFrame.ACCENT_BLUE,   Color.WHITE);
        btnUpdate = createButton("✏️ Update",       MainFrame.SUCCESS_GREEN, Color.WHITE);
        btnDelete = createButton("🗑️ Delete",       MainFrame.DANGER_RED,    Color.WHITE);
        btnClear  = createButton("🔄 Clear Form",   new Color(108, 117, 125), Color.WHITE);

        btnAdd   .setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUpdate.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClear .setAlignmentX(Component.LEFT_ALIGNMENT);

        btnAdd   .setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnDelete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnClear .setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnAdd   .addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear .addActionListener(e -> clearForm());

        panel.add(btnAdd);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnUpdate);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnDelete);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnClear);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void addBook() {
        try {
            Book book = getBookFromForm();
            String result = bookService.addBook(book);
            showMessage(result);

            if (result.startsWith("✅")) {
                clearForm();
                loadBooks();
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Total copies must be a valid number.");
        }
    }

    private void updateBook() {
        if (selectedBookId == -1) {
            showMessage("❌ Please select a book from the table first.");
            return;
        }
        try {
            Book book = getBookFromForm();
            book.setBookId(selectedBookId);
            String result = bookService.updateBook(book);
            showMessage(result);

            if (result.startsWith("✅")) {
                clearForm();
                loadBooks();
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Total copies must be a valid number.");
        }
    }

    private void deleteBook() {
        if (selectedBookId == -1) {
            showMessage("❌ Please select a book from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this book?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String result = bookService.deleteBook(selectedBookId);
            showMessage(result);
            if (result.startsWith("✅")) {
                clearForm();
                loadBooks();
            }
        }
    }

    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        List<Book> results = bookService.searchBooks(keyword);
        populateTable(results);
    }


    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        populateTable(books);
    }

    
    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0); // Clear existing rows
        Object[][] data = bookService.getBooksAsTableData(books);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void populateFormFromTable() {
        int row = bookTable.getSelectedRow();
        if (row == -1) return;

        selectedBookId = (int) tableModel.getValueAt(row, 0);
        txtTitle   .setText((String) tableModel.getValueAt(row, 1));
        txtAuthor  .setText((String) tableModel.getValueAt(row, 2));
        txtIsbn    .setText(tableModel.getValueAt(row, 3) != null
                            ? tableModel.getValueAt(row, 3).toString() : "");
        txtCategory.setText(tableModel.getValueAt(row, 4) != null
                            ? tableModel.getValueAt(row, 4).toString() : "");
        txtCopies  .setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private Book getBookFromForm() throws NumberFormatException {
        String title    = txtTitle   .getText().trim();
        String author   = txtAuthor  .getText().trim();
        String isbn     = txtIsbn    .getText().trim();
        String category = txtCategory.getText().trim();
        int    copies   = Integer.parseInt(txtCopies.getText().trim());

        return new Book(title, author, isbn, category, copies);
    }

    private void clearForm() {
        txtTitle   .setText("");
        txtAuthor  .setText("");
        txtIsbn    .setText("");
        txtCategory.setText("");
        txtCopies  .setText("");
        selectedBookId = -1;
        bookTable.clearSelection();
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
