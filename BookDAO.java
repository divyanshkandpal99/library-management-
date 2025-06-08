package dao;

import model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookDAO extends BaseDAO<Book> {
    
    public BookDAO() {
        super("books.txt");
    }
    
    public List<Book> getAllBooks() {
        return readAllLines().stream()
                .map(this::parseLine)
                .collect(Collectors.toList());
    }
    
    public Book getBookById(String bookId) {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(book -> book.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(lowerQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerQuery) ||
                    book.getIsbn().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    public void addBook(Book book) {
        List<String> lines = readAllLines();
        lines.add(formatLine(book));
        writeAllLines(lines);
    }
    
    public void updateBook(Book updatedBook) {
        List<String> lines = readAllLines();
        List<String> updatedLines = lines.stream()
                .map(line -> {
                    Book book = parseLine(line);
                    return book.getBookId().equals(updatedBook.getBookId()) 
                            ? formatLine(updatedBook) 
                            : line;
                })
                .collect(Collectors.toList());
        writeAllLines(updatedLines);
    }
    
    public void deleteBook(String bookId) {
        List<String> lines = readAllLines();
        List<String> updatedLines = lines.stream()
                .filter(line -> !parseLine(line).getBookId().equals(bookId))
                .collect(Collectors.toList());
        writeAllLines(updatedLines);
    }
    
    @Override
    protected Book parseLine(String line) {
        String[] parts = line.split(",");
        Book book = new Book(parts[0], parts[1], parts[2], parts[3]);
        book.setAvailable(Boolean.parseBoolean(parts[4]));
        return book;
    }
    
    @Override
    protected String formatLine(Book book) {
        return book.toString();
    }
} 