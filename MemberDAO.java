package dao;

import model.Member;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDAO extends BaseDAO<Member> {
    
    public MemberDAO() {
        super("members.txt");
    }
    
    public List<Member> getAllMembers() {
        return readAllLines().stream()
                .map(this::parseLine)
                .collect(Collectors.toList());
    }
    
    public Member getMemberById(String memberId) {
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Member> searchMembers(String query) {
        String lowerQuery = query.toLowerCase();
        return readAllLines().stream()
                .map(this::parseLine)
                .filter(member -> 
                    member.getName().toLowerCase().contains(lowerQuery) ||
                    member.getEmail().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    public void addMember(Member member) {
        List<String> lines = readAllLines();
        lines.add(formatLine(member));
        writeAllLines(lines);
    }
    
    public void updateMember(Member updatedMember) {
        List<String> lines = readAllLines();
        List<String> updatedLines = lines.stream()
                .map(line -> {
                    Member member = parseLine(line);
                    return member.getMemberId().equals(updatedMember.getMemberId()) 
                            ? formatLine(updatedMember) 
                            : line;
                })
                .collect(Collectors.toList());
        writeAllLines(updatedLines);
    }
    
    public void deleteMember(String memberId) {
        List<String> lines = readAllLines();
        List<String> updatedLines = lines.stream()
                .filter(line -> !parseLine(line).getMemberId().equals(memberId))
                .collect(Collectors.toList());
        writeAllLines(updatedLines);
    }
    
    @Override
    protected Member parseLine(String line) {
        String[] parts = line.split(",");
        return new Member(parts[0], parts[1], parts[2]);
    }
    
    @Override
    protected String formatLine(Member member) {
        return member.toString();
    }
} 