package model;

public class Book {

    private String title;         
    private int    bookId;        
    private String author;        
    private String isbn;          
    private String category;      
    private int    totalCopies;   
    private int    available;     
    private String addedDate;     

    public Book() {}

    public Book(int bookId, String title, String author, String isbn,
                String category, int totalCopies, int available,
                String addedDate) {
        this.bookId       = bookId;
        this.title        = title;
        this.author       = author;
        this.isbn         = isbn;
        this.category     = category;
        this.totalCopies  = totalCopies;
        this.available    = available;
        this.addedDate    = addedDate;
    }

    public Book(String title, String author, String isbn,
                String category, int totalCopies) {
        this.title       = title;
        this.author      = author;
        this.isbn        = isbn;
        this.category    = category;
        this.totalCopies = totalCopies;
        this.available   = totalCopies; 
    }


    public int    getBookId()      { return bookId;      }
    public String getTitle()       { return title;       }
    public String getAuthor()      { return author;      }
    public String getIsbn()        { return isbn;        }
    public String getCategory()    { return category;    }
    public int    getTotalCopies() { return totalCopies; }
    public int    getAvailable()   { return available;   }
    public String getAddedDate()   { return addedDate;   }


    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.title = title;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }
        this.totalCopies = totalCopies;
    }

    public void setAvailable(int available) {
        if (available < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }
        this.available = available;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public boolean isAvailable() {
        return available > 0;
    }


    @Override
    public String toString() {
        return "Book{" +
               "bookId="     + bookId      +
               ", title='"   + title       + '\'' +
               ", author='"  + author      + '\'' +
               ", isbn='"    + isbn        + '\'' +
               ", category='"+ category    + '\'' +
               ", available="+ available   +
               "/" + totalCopies +
               '}';
    }
}
