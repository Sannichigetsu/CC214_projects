package SettersAndGetters;

import DB.MySQLbookDb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Books {

    public static final String LIBRARY_FILE = "library_books.csv";
    public static final String BORROW_HISTORY_FILE = "borrow_history.csv";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static int ISBN;
    private static String title;
    private static String author;
    private static LocalDateTime datePublished;
    private static int availableCopy;
    private static int totalCopy;
    private String currentBorrower;
    private LocalDateTime borrowDate;

    public Books(int ISBN, String title, String author, LocalDateTime datePublished, int availableCopy, int totalCopy) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.datePublished = datePublished;
        this.availableCopy = availableCopy;
        this.totalCopy = totalCopy;
        this.currentBorrower = "";
        this.borrowDate = LocalDateTime.now();
        this.borrowed = false;
        this.borrower = "";
    }
public Books(){};

    public static void setISBN(int ISBN) {
        Books.ISBN = ISBN;
    }

    public static void setTitle(String title) {
        Books.title = title;
    }

    public static void setAuthor(String author) {
        Books.author = author;
    }

    public static void setDatePublished(LocalDateTime datePublished) {
        Books.datePublished = datePublished;
    }

    public static void setTotalCopy(int totalCopy) {
        Books.totalCopy = totalCopy;
    }

    public void setCurrentBorrower(String currentBorrower) {
        this.currentBorrower = currentBorrower;
    }


    private boolean borrowed;
    private String borrower;
    private boolean isAvailable;


    public Books() {}

    public static void setISBN(int ISBN) {
        Books.ISBN = ISBN;
    }

    public static void setTitle(String title) {
        Books.title = title;
    }

    public static void setAuthor(String author) {
        Books.author = author;
    }

    public static void setDatePublished(LocalDateTime datePublished) {
        Books.datePublished = datePublished;
    }

    public static void setTotalCopy(int totalCopy) {
        Books.totalCopy = totalCopy;
    }

    public void setCurrentBorrower(String currentBorrower) {
        this.currentBorrower = currentBorrower;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }




    // Getters and setters
    public int getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getDatePublished() {
        return datePublished;
    }

    public int getAvailableCopy() {
        return availableCopy;
    }

    public int getTotalCopy() {
        return totalCopy;
    }

    public void setAvailableCopy(int availableCopy) {this.availableCopy = availableCopy;}


    public String getCurrentBorrower() {
        return currentBorrower;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = (borrower != null) ? borrower : "";
    }


    public boolean isAvailable() {
        return availableCopy > 0;
    }

    public String toCSV() {
        return String.join(",",
                String.valueOf(ISBN),
                escapeCSV(title),
                escapeCSV(author),
                String.valueOf(datePublished),
                String.valueOf(availableCopy),
                String.valueOf(totalCopy)
        );
    }


    public static Books fromCSV(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }

        String[] parts = csvLine.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV format");
        }

        return new Books(
                Integer.parseInt(parts[0].trim()),
                unescapeCSV(parts[1]),
                unescapeCSV(parts[2]),
                LocalDateTime.parse(parts[3].trim()),
                Integer.parseInt(parts[4].trim()),
                Integer.parseInt(parts[5].trim())
        );
    }

    public static String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
    public static String unescapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\\,", ",")
                .replace("\\n", "\n")
                .replace("\\r", "\r");
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void addBooks() {  // Make this non-static
        String genre = "";  // You might want to add genre as a class property

        if (!MySQLbookDb.validateaddedBooks(this.ISBN, this.title)) {  // Check if book doesn't exist
            if (MySQLbookDb.AddBooks(this.ISBN, this.title, genre, this.author,
                    this.datePublished, this.availableCopy, this.totalCopy)) {
                System.out.println("Books added Successfully");
            }
        } else {
            System.out.println("Book already exists: add some copies");
        }
    }
}


