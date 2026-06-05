package model;


public class Transaction {

    public static final String STATUS_BORROWED = "BORROWED";
    public static final String STATUS_RETURNED = "RETURNED";

    public static final double FINE_PER_DAY = 5.0;


    private int    txnId;        
    private int    bookId;       
    private int    memberId;     
    private String borrowDate;   
    private String dueDate;      
    private String returnDate;   
    private double fineAmount;   
    private String status;       

    private Book   book;         
    private Member member;       

    public Transaction() {
        this.status     = STATUS_BORROWED;
        this.fineAmount = 0.00;
    }

    public Transaction(int bookId, int memberId,
                       String borrowDate, String dueDate) {
        this.bookId     = bookId;
        this.memberId   = memberId;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.status     = STATUS_BORROWED;
        this.fineAmount = 0.00;
    }

    public boolean isOverdue() {
        if (status.equals(STATUS_RETURNED)) return false;
        if (dueDate == null) return false;

        String today = java.time.LocalDate.now().toString();
        return today.compareTo(dueDate) > 0;
    }

    public boolean isActive() {
        return STATUS_BORROWED.equals(status);
    }

    public int    getTxnId()      { return txnId;      }
    public int    getBookId()     { return bookId;      }
    public int    getMemberId()   { return memberId;    }
    public String getBorrowDate() { return borrowDate;  }
    public String getDueDate()    { return dueDate;     }
    public String getReturnDate() { return returnDate;  }
    public double getFineAmount() { return fineAmount;  }
    public String getStatus()     { return status;      }
    public Book   getBook()       { return book;        }
    public Member getMember()     { return member;      }

    public void setTxnId     (int    txnId)      { this.txnId      = txnId;      }
    public void setBookId    (int    bookId)      { this.bookId     = bookId;     }
    public void setMemberId  (int    memberId)    { this.memberId   = memberId;   }
    public void setBorrowDate(String borrowDate)  { this.borrowDate = borrowDate; }
    public void setDueDate   (String dueDate)     { this.dueDate    = dueDate;    }
    public void setReturnDate(String returnDate)  { this.returnDate = returnDate; }
    public void setFineAmount(double fineAmount)  { this.fineAmount = fineAmount; }
    public void setStatus    (String status)      { this.status     = status;     }
    public void setBook      (Book   book)        { this.book       = book;       }
    public void setMember    (Member member)      { this.member     = member;     }


    @Override
    public String toString() {
        return "Transaction{" +
               "txnId="       + txnId      +
               ", bookId="    + bookId     +
               ", memberId="  + memberId   +
               ", status='"   + status     + '\'' +
               ", dueDate='"  + dueDate    + '\'' +
               ", fine="      + fineAmount +
               '}';
    }
}
