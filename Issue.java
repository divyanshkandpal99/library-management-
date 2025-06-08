package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Issue implements Serializable {
    private String issueId;
    private String bookId;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public Issue(String issueId, String bookId, String memberId, LocalDate issueDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = null;
    }

    // Getters and Setters
    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    @Override
    public String toString() {
        return issueId + "," + bookId + "," + memberId + "," + 
               issueDate.toString() + "," + 
               (returnDate != null ? returnDate.toString() : "null");
    }
} 