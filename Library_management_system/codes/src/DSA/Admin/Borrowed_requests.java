package DSA.Admin;

import DSA.Objects.Books;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrowed_requests {
    private static Queue<BorrowRequest> borrowRequests = new LinkedList<>();
    private static List<BorrowRequest> confirmedRequests = new ArrayList<>();

    // Class to represent a borrow request
    public static class BorrowRequest {
        private Books book;  // Add reference to Books object
        private int Id;
        private String title;
        private String user;
        private String author;
        private String status;
        private int copies;
        private LocalDateTime borrowReqDate;

        // Updated constructor to take Books object
        public BorrowRequest(Books book, String user, int copies, String status, LocalDateTime borrowReqDate) {
            this.book = book;
            this.Id = book.getISBN();  // Use book's ISBN
            this.title = book.getTitle();
            this.user = user;
            this.author = book.getAuthor();
            this.status = status;
            this.copies = copies;
            this.borrowReqDate = borrowReqDate;
        }

        // Getters
        public Books getBook() {
            return book;  // Return the Books object
        }

        public int getBookISBN() {
            return book.getISBN();  // Return ISBN from Books object
        }

        public int getId() {
            return Id;
        }

        public String getTitle() {
            return title;
        }

        public String getUser() {
            return user;
        }

        public String getAuthor() {
            return author;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCopies() {
            return copies;
        }

        public LocalDateTime getBorrowReqDate() {
            return borrowReqDate;
        }

        public void setBorrowReqDate(LocalDateTime borrowReqDate) {
            this.borrowReqDate = borrowReqDate;
        }
    }

    // Add a borrow request for a book
    public static boolean addBorrowRequest(Books book, String user) {
        if (book != null && user != null && !user.trim().isEmpty()) {
            if (book.isAvailable()) {  // Check availability before adding request
                int copy = book.getAvailableCopy();
                BorrowRequest request = new BorrowRequest(book, user, copy, "PENDING", LocalDateTime.now());
                borrowRequests.offer(request);
                return true;
            }
        }
        return false;
    }

    // Confirm a borrow request
    public static boolean confirmRequest(String bookTitle, String user, int copy) {
        BorrowRequest request = findPendingRequest(bookTitle, user);
        if (request != null && request.getBook().isAvailable()) {  // Verify availability
            request.setStatus("CONFIRMED");
            borrowRequests.remove(request);
            confirmedRequests.add(request);

            Books book = request.getBook();  // Get the Books object
            book.setBorrowed(true);
            book.setBorrower(user);
            book.setAvailableCopy(book.getAvailableCopy() - copy);

            return true;
        }
        return false;
    }

    // Reject a borrow request
    public static boolean rejectRequest(String bookTitle, String user) {
        BorrowRequest request = findPendingRequest(bookTitle, user);
        if (request != null) {
            request.setStatus("REJECTED");
            borrowRequests.remove(request);
            return true;
        }
        return false;
    }

    // Find a pending request by book title and user
    private static BorrowRequest findPendingRequest(String bookTitle, String user) {
        return borrowRequests.stream()
                .filter(req -> req.getTitle().equals(bookTitle) && req.getUser().equals(user))
                .findFirst()
                .orElse(null);
    }

    // Get a list of all pending requests
    public static List<BorrowRequest> getPendingRequests() {
        return new ArrayList<>(borrowRequests);
    }

    // Get a list of all confirmed requests
    public static List<BorrowRequest> getConfirmedRequests() {
        return new ArrayList<>(confirmedRequests);
    }

    // Process the next request in queue
    public static BorrowRequest processNextRequest() {
        return borrowRequests.poll();
    }

    // Get the number of pending requests
    public static int getPendingRequestsCount() {
        return borrowRequests.size();
    }

    // Check if there are any pending requests
    public static boolean hasPendingRequests() {
        return !borrowRequests.isEmpty();
    }

    // View the next request without removing it
    public static BorrowRequest peekNextRequest() {
        return borrowRequests.peek();
    }

    // Clear all requests
    public static void clearAllRequests() {
        borrowRequests.clear();
    }
}
