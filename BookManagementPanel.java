package ui;

import dao.BookDAO;
import model.Book;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private BookDAO bookDAO;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public BookManagementPanel() {
        bookDAO = new BookDAO();
        setLayout(new BorderLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Create top panel with search and add button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Search:");
        UIUtils.setLabelStyle(searchLabel);
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        UIUtils.setTextFieldStyle(searchField);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        UIUtils.setButtonStyle(searchButton);
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Add book button
        JButton addButton = new JButton("Add New Book");
        UIUtils.setButtonStyle(addButton);
        addButton.addActionListener(e -> showAddBookDialog());
        topPanel.add(addButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Book ID", "Title", "Author", "ISBN", "Availability", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action buttons are editable
            }
        };
        
        bookTable = new JTable(tableModel);
        UIUtils.setTableStyle(bookTable);
        
        // Add action buttons column
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.isAvailable() ? "Available" : "Issued",
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.searchBooks(query);
        for (Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.isAvailable() ? "Available" : "Issued",
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add fields
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Save");
        UIUtils.setButtonStyle(saveButton);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                UIUtils.showError(dialog, "All fields are required!");
                return;
            }
            
            String bookId = "B" + System.currentTimeMillis();
            Book book = new Book(bookId, title, author, isbn);
            bookDAO.addBook(book);
            refreshTable();
            dialog.dispose();
            UIUtils.showSuccess(this, "Book added successfully!");
        });
        
        JButton cancelButton = new JButton("Cancel");
        UIUtils.setButtonStyle(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
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
            setText("Edit/Delete");
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
                String bookId = (String) bookTable.getValueAt(bookTable.getSelectedRow(), 0);
                showEditDeleteDialog(bookId);
            }
            isPushed = false;
            return label;
        }
    }
    
    private void showEditDeleteDialog(String bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add fields
        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField isbnField = new JTextField(book.getIsbn(), 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Save");
        UIUtils.setButtonStyle(saveButton);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                UIUtils.showError(dialog, "All fields are required!");
                return;
            }
            
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            bookDAO.updateBook(book);
            refreshTable();
            dialog.dispose();
            UIUtils.showSuccess(this, "Book updated successfully!");
        });
        
        JButton deleteButton = new JButton("Delete");
        UIUtils.setButtonStyle(deleteButton);
        deleteButton.addActionListener(e -> {
            if (UIUtils.showConfirm(dialog, "Are you sure you want to delete this book?")) {
                bookDAO.deleteBook(bookId);
                refreshTable();
                dialog.dispose();
                UIUtils.showSuccess(this, "Book deleted successfully!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        UIUtils.setButtonStyle(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 