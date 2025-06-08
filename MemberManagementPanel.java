package ui;

import dao.MemberDAO;
import model.Member;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberManagementPanel extends JPanel {
    private MemberDAO memberDAO;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public MemberManagementPanel() {
        memberDAO = new MemberDAO();
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
        searchButton.addActionListener(e -> searchMembers());
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Add member button
        JButton addButton = new JButton("Add New Member");
        UIUtils.setButtonStyle(addButton);
        addButton.addActionListener(e -> showAddMemberDialog());
        topPanel.add(addButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Member ID", "Name", "Email", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only action buttons are editable
            }
        };
        
        memberTable = new JTable(tableModel);
        UIUtils.setTableStyle(memberTable);
        
        // Add action buttons column
        memberTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        memberTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Member> members = memberDAO.getAllMembers();
        for (Member member : members) {
            Object[] row = {
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchMembers() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Member> members = memberDAO.searchMembers(query);
        for (Member member : members) {
            Object[] row = {
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddMemberDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Member", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add fields
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Save");
        UIUtils.setButtonStyle(saveButton);
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty()) {
                UIUtils.showError(dialog, "All fields are required!");
                return;
            }
            
            String memberId = "M" + System.currentTimeMillis();
            Member member = new Member(memberId, name, email);
            memberDAO.addMember(member);
            refreshTable();
            dialog.dispose();
            UIUtils.showSuccess(this, "Member added successfully!");
        });
        
        JButton cancelButton = new JButton("Cancel");
        UIUtils.setButtonStyle(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
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
                String memberId = (String) memberTable.getValueAt(memberTable.getSelectedRow(), 0);
                showEditDeleteDialog(memberId);
            }
            isPushed = false;
            return label;
        }
    }
    
    private void showEditDeleteDialog(String memberId) {
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Member", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add fields
        JTextField nameField = new JTextField(member.getName(), 20);
        JTextField emailField = new JTextField(member.getEmail(), 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Save");
        UIUtils.setButtonStyle(saveButton);
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty()) {
                UIUtils.showError(dialog, "All fields are required!");
                return;
            }
            
            member.setName(name);
            member.setEmail(email);
            memberDAO.updateMember(member);
            refreshTable();
            dialog.dispose();
            UIUtils.showSuccess(this, "Member updated successfully!");
        });
        
        JButton deleteButton = new JButton("Delete");
        UIUtils.setButtonStyle(deleteButton);
        deleteButton.addActionListener(e -> {
            if (UIUtils.showConfirm(dialog, "Are you sure you want to delete this member?")) {
                memberDAO.deleteMember(memberId);
                refreshTable();
                dialog.dispose();
                UIUtils.showSuccess(this, "Member deleted successfully!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        UIUtils.setButtonStyle(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 