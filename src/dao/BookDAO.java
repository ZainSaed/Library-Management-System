package dao;

import model.Book;
import exception.BookNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {


    private static final String INSERT_BOOK =
        "INSERT INTO books (title, author, isbn, category, total_copies, available) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_BOOKS =
        "SELECT * FROM books ORDER BY title";

    private static final String SELECT_BY_ID =
        "SELECT * FROM books WHERE book_id = ?";

    private static final String SELECT_BY_TITLE =
        "SELECT * FROM books WHERE title LIKE ?";

    private static final String SELECT_BY_AUTHOR =
        "SELECT * FROM books WHERE author LIKE ?";

    private static final String UPDATE_BOOK =
        "UPDATE books SET title=?, author=?, isbn=?, category=?, " +
        "total_copies=?, available=? WHERE book_id=?";

    private static final String DELETE_BOOK =
        "DELETE FROM books WHERE book_id = ?";

    private static final String COUNT_BOOKS =
        "SELECT COUNT(*) FROM books";

    public boolean addBook(Book book) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_BOOK);

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getCategory());
            stmt.setInt   (5, book.getTotalCopies());
            stmt.setInt   (6, book.getAvailable());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();


            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error adding book: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try {
            Connection conn  = DBConnection.getConnection();
            Statement  stmt  = conn.createStatement();
            ResultSet  rs    = stmt.executeQuery(SELECT_ALL_BOOKS);

            while (rs.next()) {
                books.add(extractBook(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error fetching books: " + e.getMessage());
        }

        return books;
    }

    public Book getBookById(int bookId) throws BookNotFoundException {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setInt(1, bookId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Book book = extractBook(rs);
                rs.close();
                stmt.close();
                return book;
            } else {
                throw new BookNotFoundException(
                    "Book with ID " + bookId + " not found in the library."
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Error finding book: " + e.getMessage());
            throw new BookNotFoundException("Database error while finding book ID: " + bookId);
        }
    }

    public List<Book> searchByTitle(String title) {
        List<Book> books = new ArrayList<>();

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TITLE);

            stmt.setString(1, "%" + title + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error searching books: " + e.getMessage());
        }

        return books;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> books = new ArrayList<>();

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_AUTHOR);
            stmt.setString(1, "%" + author + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error searching by author: " + e.getMessage());
        }

        return books;
    }

    public boolean updateBook(Book book) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOK);

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getCategory());
            stmt.setInt   (5, book.getTotalCopies());
            stmt.setInt   (6, book.getAvailable());
            stmt.setInt   (7, book.getBookId());   // WHERE clause

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_BOOK);
            stmt.setInt(1, bookId);

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error deleting book: " + e.getMessage());
            return false;
        }
    }

    public int getTotalBookCount() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(COUNT_BOOKS);

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error counting books: " + e.getMessage());
        }
        return 0;
    }

    private Book extractBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId      (rs.getInt   ("book_id"));
        book.setTitle       (rs.getString("title"));
        book.setAuthor      (rs.getString("author"));
        book.setIsbn        (rs.getString("isbn"));
        book.setCategory    (rs.getString("category"));
        book.setTotalCopies (rs.getInt   ("total_copies"));
        book.setAvailable   (rs.getInt   ("available"));
        book.setAddedDate   (rs.getString("added_date"));
        return book;
    }
}
