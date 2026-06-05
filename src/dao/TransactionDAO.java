package dao;

import model.Transaction;
import model.Book;
import model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {


    private static final String INSERT_TRANSACTION =
        "INSERT INTO transactions (book_id, member_id, borrow_date, due_date, status) " +
        "VALUES (?, ?, ?, ?, 'BORROWED')";

    private static final String UPDATE_RETURN =
        "UPDATE transactions SET return_date=?, fine_amount=?, status='RETURNED' " +
        "WHERE txn_id=?";

    private static final String SELECT_ALL =
        "SELECT t.*, b.title, b.author, m.name AS member_name " +
        "FROM transactions t " +
        "JOIN books b   ON t.book_id   = b.book_id " +
        "JOIN members m ON t.member_id = m.member_id " +
        "ORDER BY t.txn_id DESC";

    private static final String SELECT_ACTIVE =
        "SELECT t.*, b.title, b.author, m.name AS member_name " +
        "FROM transactions t " +
        "JOIN books b   ON t.book_id   = b.book_id " +
        "JOIN members m ON t.member_id = m.member_id " +
        "WHERE t.status = 'BORROWED' " +
        "ORDER BY t.due_date ASC";

    private static final String SELECT_BY_MEMBER =
        "SELECT t.*, b.title, b.author, m.name AS member_name " +
        "FROM transactions t " +
        "JOIN books b   ON t.book_id   = b.book_id " +
        "JOIN members m ON t.member_id = m.member_id " +
        "WHERE t.member_id = ? " +
        "ORDER BY t.txn_id DESC";

    private static final String UPDATE_BOOK_AVAILABLE =
        "UPDATE books SET available = available + ? WHERE book_id = ?";

    private static final String CHECK_ALREADY_BORROWED =
        "SELECT COUNT(*) FROM transactions " +
        "WHERE book_id=? AND member_id=? AND status='BORROWED'";

    public boolean borrowBook(Transaction transaction) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSACTION);
            stmt.setInt   (1, transaction.getBookId());
            stmt.setInt   (2, transaction.getMemberId());
            stmt.setString(3, transaction.getBorrowDate());
            stmt.setString(4, transaction.getDueDate());

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                updateBookAvailability(transaction.getBookId(), -1);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error recording borrow: " + e.getMessage());
        }
        return false;
    }

    public boolean returnBook(int txnId, String returnDate, double fineAmount) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_RETURN);

            stmt.setString(1, returnDate);
            stmt.setDouble(2, fineAmount);
            stmt.setInt   (3, txnId);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                int bookId = getBookIdByTxnId(txnId);
                if (bookId > 0) {
                    updateBookAvailability(bookId, +1);
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error recording return: " + e.getMessage());
        }
        return false;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(SELECT_ALL);

            while (rs.next()) {
                list.add(extractTransaction(rs));
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error fetching transactions: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getActiveLoans() {
        List<Transaction> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(SELECT_ACTIVE);

            while (rs.next()) {
                list.add(extractTransaction(rs));
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error fetching active loans: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getTransactionsByMember(int memberId) {
        List<Transaction> list = new ArrayList<>();
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MEMBER);
            stmt.setInt(1, memberId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractTransaction(rs));
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error fetching member transactions: " + e.getMessage());
        }
        return list;
    }

    public boolean isAlreadyBorrowed(int bookId, int memberId) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(CHECK_ALREADY_BORROWED);
            stmt.setInt(1, bookId);
            stmt.setInt(2, memberId);

            ResultSet rs    = stmt.executeQuery();
            boolean   found = rs.next() && rs.getInt(1) > 0;
            rs.close();
            stmt.close();
            return found;

        } catch (SQLException e) {
            System.out.println("❌ Error checking borrow status: " + e.getMessage());
        }
        return false;
    }

    public int getActiveLoanCount() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(
                "SELECT COUNT(*) FROM transactions WHERE status='BORROWED'"
            );
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("❌ Error counting active loans: " + e.getMessage());
        }
        return 0;
    }

    private void updateBookAvailability(int bookId, int delta) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOK_AVAILABLE);
            stmt.setInt(1, delta);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error updating book availability: " + e.getMessage());
        }
    }

    private int getBookIdByTxnId(int txnId) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT book_id FROM transactions WHERE txn_id = ?"
            );
            stmt.setInt(1, txnId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("book_id");
                rs.close();
                stmt.close();
                return id;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting book ID: " + e.getMessage());
        }
        return -1;
    }

    private Transaction extractTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTxnId     (rs.getInt   ("txn_id"));
        t.setBookId    (rs.getInt   ("book_id"));
        t.setMemberId  (rs.getInt   ("member_id"));
        t.setBorrowDate(rs.getString("borrow_date"));
        t.setDueDate   (rs.getString("due_date"));
        t.setReturnDate(rs.getString("return_date"));
        t.setFineAmount(rs.getDouble("fine_amount"));
        t.setStatus    (rs.getString("status"));

        Book book = new Book();
        book.setBookId(rs.getInt   ("book_id"));
        book.setTitle (rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        t.setBook(book);

        Member member = new Member();
        member.setPersonId(rs.getInt   ("member_id"));
        member.setName    (rs.getString("member_name"));
        t.setMember(member);

        return t;
    }
}
