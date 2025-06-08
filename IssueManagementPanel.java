package ui;

import dao.BookDAO;
import dao.MemberDAO;
import dao.IssueDAO;
import model.Book;
import model.Member;
import model.Issue;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class IssueManagementPanel extends JPanel {
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private IssueDAO issueDAO;
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    
    public IssueManagementPanel() {
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        issueDAO = new IssueDAO();
        
        setLayout(new BorderLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Create top panel with filter and issue button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel filterLabel = new JLabel("Filter:");
        UIUtils.setLabelStyle(filterLabel);
        filterPanel.add(filterLabel);
        
        String[] filters = {"All Issues", "Active Issues"};
        filterComboBox = new JComboBox<>(filters);
        filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterComboBox.addActionListener(e -> refreshTable());
        filterPanel.add(filterComboBox);
        
        topPanel.add(filterPanel, BorderLayout.WEST);
        
        // Issue book button
        JButton issueButton = new JButton("Issue Book");
        UIUtils.setButtonStyle(issueButton);
        issueButton.addActionListener(e -> showIssueBookDialog());
        topPanel.add(issueButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Issue ID", "Book ID", "Book Title", "Member ID", "Member Name", 
                          "Issue Date", "Return Date", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only action buttons are editable
            }
        };
        
        issueTable = new JTable(tableModel);
        UIUtils.setTableStyle(issueTable);
        
        // Add action buttons column
        issueTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        issueTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Issue> issues;
        
        if (filterComboBox.getSelectedIndex() == 1) {
            issues = issueDAO.getActiveIssues();
        } else {
            issues = issueDAO.getAllIssues();
        }
        
        for (Issue issue : issues) {
            Book book = bookDAO.getBookById(issue.getBookId());
            Member member = memberDAO.getMemberById(issue.getMemberId());
            
            if (book != null && member != null) {
                Object[] row = {
                    issue.getIssueId(),
                    book.getBookId(),
                    book.getTitle(),
                    member.getMemberId(),
                    member.getName(),
                    issue.getIssueDate(),
                    issue.getReturnDate(),
                    issue.getReturnDate() == null ? "Active" : "Returned",
                    issue.getReturnDate() == null ? "Return" : "View"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showIssueBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Issue Book", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Book selection
        JLabel bookLabel = new JLabel("Book:");
        UIUtils.setLabelStyle(bookLabel);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(bookLabel, gbc);
        
        JComboBox<Book> bookComboBox = new JComboBox<>();
        bookComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        List<Book> availableBooks = bookDAO.getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
        for (Book book : availableBooks) {
            bookComboBox.addItem(book);
        }
        gbc.gridx = 1;
        panel.add(bookComboBox, gbc);
        
        // Member selection
        JLabel memberLabel = new JLabel("Member:");
        UIUtils.setLabelStyle(memberLabel);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(memberLabel, gbc);
        
        JComboBox<Member> memberComboBox = new JComboBox<>();
        memberComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        List<Member> members = memberDAO.getAllMembers();
        for (Member member : members) {
            memberComboBox.addItem(member);
        }
        gbc.gridx = 1;
        panel.add(memberComboBox, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JButton issueButton = new JButton("Issue");
        UIUtils.setButtonStyle(issueButton);
        issueButton.addActionListener(e -> {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            Member selectedMember = (Member) memberComboBox.getSelectedItem();
            
            if (selectedBook == null || selectedMember == null) {
                UIUtils.showError(dialog, "Please select both book and member!");
                return;
            }
            
            // Create issue
            String issueId = "I" + System.currentTimeMillis();
            Issue issue = new Issue(issueId, selectedBook.getBookId(), 
                                  selectedMember.getMemberId(), LocalDate.now());
            issueDAO.addIssue(issue);
            
            // Update book availability
            selectedBook.setAvailable(false);
            bookDAO.updateBook(selectedBook);
            
            refreshTable();
            dialog.dispose();
            UIUtils.showSuccess(this, "Book issued successfully!");
        });
        
        JButton cancelButton = new JButton("Cancel");
        UIUtils.setButtonStyle(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(issueButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // Button renderer and editor for table actions
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String issueId = (String) issueTable.getValueAt(issueTable.getSelectedRow(), 0);
                String action = (String) issueTable.getValueAt(issueTable.getSelectedRow(), 8);
                
                if (action.equals("Return")) {
                    handleReturn(issueId);
                } else {
                    showIssueDetails(issueId);
                }
            }
            isPushed = false;
            return label;
        }
    }
    
    private void handleReturn(String issueId) {
        Issue issue = issueDAO.getIssueById(issueId);
        if (issue == null) return;
        
        if (UIUtils.showConfirm(this, "Are you sure you want to return this book?")) {
            // Update issue
            issue.setReturnDate(LocalDate.now());
            issueDAO.updateIssue(issue);
            
            // Update book availability
            Book book = bookDAO.getBookById(issue.getBookId());
            if (book != null) {
                book.setAvailable(true);
                bookDAO.updateBook(book);
            }
            
            refreshTable();
            UIUtils.showSuccess(this, "Book returned successfully!");
        }
    }
    
    private void showIssueDetails(String issueId) {
        Issue issue = issueDAO.getIssueById(issueId);
        if (issue == null) return;
        
        Book book = bookDAO.getBookById(issue.getBookId());
        Member member = memberDAO.getMemberById(issue.getMemberId());
        
        if (book == null || member == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   "Issue Details", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add details
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Book:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(book.getTitle()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Member:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(member.getName()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Issue Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(issue.getIssueDate().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Return Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(issue.getReturnDate() != null ? 
                           issue.getReturnDate().toString() : "Not returned"), gbc);
        
        // Close button
        JButton closeButton = new JButton("Close");
        UIUtils.setButtonStyle(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(closeButton, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}  
