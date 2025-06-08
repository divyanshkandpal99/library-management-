package ui;

import utils.UIUtils;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public Dashboard() {
        setTitle("Library Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        // Create content panel with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Add different panels to content
        contentPanel.add(new BookManagementPanel(), "BOOKS");
        contentPanel.add(new MemberManagementPanel(), "MEMBERS");
        contentPanel.add(new IssueManagementPanel(), "ISSUES");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Center the window
        UIUtils.centerWindow(this);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Add title
        JLabel titleLabel = new JLabel("Library System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Add navigation buttons
        String[] navItems = {"Books", "Members", "Issues"};
        String[] cardNames = {"BOOKS", "MEMBERS", "ISSUES"};
        
        for (int i = 0; i < navItems.length; i++) {
            JButton navButton = new JButton(navItems[i]);
            navButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            navButton.setForeground(Color.WHITE);
            navButton.setBackground(UIUtils.PRIMARY_COLOR);
            navButton.setBorderPainted(false);
            navButton.setFocusPainted(false);
            navButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            navButton.setMaximumSize(new Dimension(180, 40));
            
            final String cardName = cardNames[i];
            navButton.addActionListener(e -> cardLayout.show(contentPanel, cardName));
            
            sidebar.add(navButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Add logout button at the bottom
        sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(UIUtils.PRIMARY_COLOR);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.addActionListener(e -> handleLogout());
        
        sidebar.add(logoutButton);
        
        return sidebar;
    }
    
    private void handleLogout() {
        if (UIUtils.showConfirm(this, "Are you sure you want to logout?")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            });
        }
    }
} 