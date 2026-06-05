package service;

import dao.BookDAO;
import model.Book;
import exception.BookNotFoundException;

import java.util.List;


public class BookService {

    private BookDAO bookDAO;
    public BookService() {
        this.bookDAO = new BookDAO(); 
    }

    public String addBook(Book book) {

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return "❌ Book title cannot be empty.";
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return "❌ Author name cannot be empty.";
        }

        if (book.getTotalCopies() <= 0) {
            return "❌ Total copies must be at least 1.";
        }

       boolean success = bookDAO.addBook(book);

        if (success) {
            return "✅ Book '" + book.getTitle() + "' added successfully.";
        } else {
            return "❌ Failed to add book. Please try again.";
        }
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public Book getBookById(int bookId) {
        try {
            return bookDAO.getBookById(bookId);
        } catch (BookNotFoundException e) {
            System.out.println("Service: " + e.getMessage());
            return null;
        }
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks(); 
        }
        return bookDAO.searchByTitle(keyword.trim());
    }

    public List<Book> searchByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchByAuthor(author.trim());
    }

    public String updateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return "❌ Book title cannot be empty.";
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return "❌ Author name cannot be empty.";
        }

        if (book.getTotalCopies() <= 0) {
            return "❌ Total copies must be at least 1.";
        }

        if (book.getAvailable() < 0) {
            return "❌ Available copies cannot be negative.";
        }

        if (book.getAvailable() > book.getTotalCopies()) {
            return "❌ Available copies cannot exceed total copies.";
        }

        boolean success = bookDAO.updateBook(book);
        return success
            ? "✅ Book updated successfully."
            : "❌ Failed to update book.";
    }

    public String deleteBook(int bookId) {
        if (bookId <= 0) {
            return "❌ Invalid book ID.";
        }

        boolean success = bookDAO.deleteBook(bookId);
        return success
            ? "✅ Book deleted successfully."
            : "❌ Failed to delete book. It may not exist.";
    }

    public int getTotalBookCount() {
        return bookDAO.getTotalBookCount();
    }

    public Object[][] getBooksAsTableData(List<Book> books) {
        Object[][] data = new Object[books.size()][7];

        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            data[i][0] = b.getBookId();
            data[i][1] = b.getTitle();
            data[i][2] = b.getAuthor();
            data[i][3] = b.getIsbn();
            data[i][4] = b.getCategory();
            data[i][5] = b.getTotalCopies();
            data[i][6] = b.getAvailable();
        }

        return data;
    }

    public String[] getBookTableColumns() {
        return new String[]{"ID", "Title", "Author", "ISBN", "Category", "Total", "Available"};
    }
}
