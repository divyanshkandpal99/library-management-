# Library Management System

A complete GUI-based Library Management System built with Java Swing and file-based storage.


### Login Credentials
- Username: admin
- Password: admin123


## Features

### Book Management
- Add new books with details (ID, Title, Author, ISBN)
- View all books in a searchable table
- Search books by ID, title, or author
- Update book information
- Delete books
- Track book availability

### Member Management
- Register new members
- View all members in a searchable table
- Search members by ID or name
- Update member information
- Delete members

### Issue & Return Management
- Issue books to members with date tracking
- Prevent issuing unavailable books
- Return books and update availability
- View issue history
- Filter active and returned issues

### Admin Features
- Secure login system
- Dashboard with navigation
- Modern and responsive UI
- Data persistence using text files

## Technical Details

### Technology Stack
- Java (JDK 17+)
- Swing for GUI
- File handling using java.io
- MVC Architecture

### Project Structure
```
src/
├── model/
│   ├── Book.java
│   ├── Member.java
│   └── Issue.java
├── dao/
│   ├── BaseDAO.java
│   ├── BookDAO.java
│   ├── MemberDAO.java
│   └── IssueDAO.java
├── ui/
│   ├── LoginScreen.java
│   ├── Dashboard.java
│   ├── BookManagementPanel.java
│   ├── MemberManagementPanel.java
│   └── IssueManagementPanel.java
└── utils/
    └── UIUtils.java
```

### Data Storage
The system uses three text files for data persistence:
- `books.txt`: Stores book information
- `members.txt`: Stores member information
- `issues.txt`: Stores issue records


## Getting Started

1. Ensure you have JDK 17 or higher installed
2. Clone the repository
3. Compile the project:
   ```bash
   javac -d bin src/**/*.java
   ```
4. Run the application:
   ```bash
   java -cp bin ui.LoginScreen
   ```

## Features Implemented

- [x] Complete MVC architecture
- [x] File-based data persistence
- [x] Modern and responsive UI
- [x] Search functionality
- [x] CRUD operations for books and members
- [x] Issue and return management
- [x] Input validation
- [x] Error handling
- [x] User feedback messages
- [x] Secure login system

## Optional Features (Bonus)

- [x] Search filters using JComboBox
- [x] Sort functionality
- [x] Clean and modern UI design
- [x] Responsive layout
- [x] Tooltips and accessibility features

## Contributing

Feel free to submit issues and enhancement requests! 
