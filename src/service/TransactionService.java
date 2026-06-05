package service;

import dao.TransactionDAO;
import dao.BookDAO;
import dao.MemberDAO;
import model.Transaction;
import model.Book;
import model.Member;
import exception.BookNotFoundException;
import exception.MemberNotFoundException;
import exception.FineCalculationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class TransactionService {

    private TransactionDAO transactionDAO;
    private BookDAO        bookDAO;
    private MemberDAO      memberDAO;

    private static final int    BORROW_DAYS  = 14;           
    private static final double FINE_PER_DAY = 5.0;          

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.bookDAO        = new BookDAO();
        this.memberDAO      = new MemberDAO();
    }

    public String borrowBook(int bookId, int memberId) {

        try {
            Book book = bookDAO.getBookById(bookId);

            Member member = memberDAO.getMemberById(memberId);

            if (book.getAvailable() <= 0) {
                return "❌ Sorry, '" + book.getTitle() + "' has no available copies right now.";
            }

            if (transactionDAO.isAlreadyBorrowed(bookId, memberId)) {
                return "❌ " + member.getName() + " has already borrowed this book and not returned it yet.";
            }

            String borrowDate = LocalDate.now().toString();
            String dueDate    = LocalDate.now().plusDays(BORROW_DAYS).toString();

            Transaction txn = new Transaction(bookId, memberId, borrowDate, dueDate);

            boolean success = transactionDAO.borrowBook(txn);

            if (success) {
                return "✅ Book borrowed successfully!\n" +
                       "Book: "     + book.getTitle()   + "\n" +
                       "Member: "   + member.getName()  + "\n" +
                       "Due Date: " + dueDate           + "\n" +
                       "Please return by " + dueDate + " to avoid fine.";
            } else {
                return "❌ Failed to record borrowing. Please try again.";
            }

        } catch (BookNotFoundException e) {
            return "❌ Book not found: " + e.getMessage();
        } catch (MemberNotFoundException e) {
            return "❌ Member not found: " + e.getMessage();
        }
    }

    public String returnBook(int txnId) {
        try {
            String returnDate = LocalDate.now().toString();

            List<Transaction> active = transactionDAO.getActiveLoans();
            Transaction target = null;

            for (Transaction t : active) {
                if (t.getTxnId() == txnId) {
                    target = t;
                    break;
                }
            }

            if (target == null) {
                return "❌ Transaction ID " + txnId + " not found or already returned.";
            }

            double fine = calculateFine(target.getDueDate(), returnDate);

            boolean success = transactionDAO.returnBook(txnId, returnDate, fine);

            if (success) {
                String msg = "✅ Book returned successfully!\n" +
                             "Book: "        + target.getBook().getTitle() + "\n" +
                             "Returned on: " + returnDate;
                if (fine > 0) {
                    msg += "\n⚠️ Overdue Fine: Rs. " + String.format("%.2f", fine);
                } else {
                    msg += "\nNo fine — returned on time!";
                }
                return msg;
            } else {
                return "❌ Failed to record return. Please try again.";
            }

        } catch (FineCalculationException e) {
            return "❌ Fine calculation error: " + e.getMessage();
        }
    }

    public double calculateFine(String dueDate, String returnDate)
            throws FineCalculationException {
        try {
            LocalDate due    = LocalDate.parse(dueDate);
            LocalDate actual = LocalDate.parse(returnDate);

            long overdueDays = ChronoUnit.DAYS.between(due, actual);

            if (overdueDays <= 0) {
                return 0.0; 
            }

            double fine = overdueDays * FINE_PER_DAY;

            System.out.println("Fine calculated: " + overdueDays +
                               " days × Rs." + FINE_PER_DAY +
                               " = Rs." + fine);
            return fine;

        } catch (Exception e) {
            throw new FineCalculationException(
                "Could not calculate fine. Invalid date format: " + e.getMessage(), e
            );
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getActiveLoans() {
        return transactionDAO.getActiveLoans();
    }

    public List<Transaction> getMemberHistory(int memberId) {
        return transactionDAO.getTransactionsByMember(memberId);
    }

    public int getActiveLoanCount() {
        return transactionDAO.getActiveLoanCount();
    }

    public Object[][] getActiveLoansTableData(List<Transaction> list) {
        Object[][] data = new Object[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            data[i][0] = t.getTxnId();
            data[i][1] = t.getBook()   != null ? t.getBook().getTitle()  : "N/A";
            data[i][2] = t.getMember() != null ? t.getMember().getName() : "N/A";
            data[i][3] = t.getBorrowDate();
            data[i][4] = t.getDueDate();
            data[i][5] = t.isOverdue() ? "⚠️ Overdue" : "On Time";
            data[i][6] = t.getStatus();
        }
        return data;
    }

    public String[] getActiveLoanColumns() {
        return new String[]{"Txn ID", "Book Title", "Member", "Borrow Date", "Due Date", "Status", "State"};
    }

    public Object[][] getAllTransactionsTableData(List<Transaction> list) {
        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            data[i][0] = t.getTxnId();
            data[i][1] = t.getBook()   != null ? t.getBook().getTitle()  : "N/A";
            data[i][2] = t.getMember() != null ? t.getMember().getName() : "N/A";
            data[i][3] = t.getBorrowDate();
            data[i][4] = t.getDueDate();
            data[i][5] = t.getReturnDate() != null ? t.getReturnDate() : "-";
            data[i][6] = t.getFineAmount() > 0
                         ? "Rs. " + String.format("%.2f", t.getFineAmount()) : "-";
            data[i][7] = t.getStatus();
        }
        return data;
    }

    public String[] getAllTransactionColumns() {
        return new String[]{"Txn ID", "Book", "Member", "Borrow Date", "Due Date", "Return Date", "Fine", "Status"};
    }
}
