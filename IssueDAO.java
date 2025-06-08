package dao;

import model.Issue;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class IssueDAO extends BaseDAO<Issue> {
    
    public IssueDAO() {
        super("issues.txt");
    }
    
    public List<Issue> getAllIssues() {
        return readAllLines().stream()
                .map(this::parseLine)
                .collect(Collectors.toList());
    }
    
    public Issue getIssueById(String issueId) {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(issue -> issue.getIssueId().equals(issueId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Issue> getActiveIssues() {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(issue -> issue.getReturnDate() == null)
                .collect(Collectors.toList());
    }
    
    public List<Issue> getIssuesByMemberId(String memberId) {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(issue -> issue.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
    
    public List<Issue> getIssuesByBookId(String bookId) {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(issue -> issue.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }
    
    public void addIssue(Issue issue) {
        List<String> lines = readAllLines();
        lines.add(formatLine(issue));
        writeAllLines(lines);
    }
    
    public void updateIssue(Issue updatedIssue) {
        List<String> lines = readAllLines();
        List<String> updatedLines = lines.stream()
                .map(line -> {
                    Issue issue = parseLine(line);
                    return issue.getIssueId().equals(updatedIssue.getIssueId()) 
                            ? formatLine(updatedIssue) 
                            : line;
                })
                .collect(Collectors.toList());
        writeAllLines(updatedLines);
    }
    
    @Override
    protected Issue parseLine(String line) {
        String[] parts = line.split(",");
        Issue issue = new Issue(parts[0], parts[1], parts[2], LocalDate.parse(parts[3]));
        if (!parts[4].equals("null")) {
            issue.setReturnDate(LocalDate.parse(parts[4]));
        }
        return issue;
    }
    
    @Override
    protected String formatLine(Issue issue) {
        return issue.toString();
    }
} 