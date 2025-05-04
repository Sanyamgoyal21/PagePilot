package Frontend;

import Backend.Admin;
import Backend.Database;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class AdminPanel {
    public static void displayAdminPage() {
        // Create the main frame for the Admin Dashboard
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full-screen mode
        adminFrame.setLayout(new BorderLayout());

        // Left-side navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(5, 1, 5, 5)); // 5 buttons with spacing
        navigationPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        navigationPanel.setPreferredSize(new Dimension(200, 0)); // Fixed width for navigation

        // Buttons for navigation
        // JButton manageLibrarianButton = new JButton("Add/View/Delete Librarian");
        // JButton viewFineReportButton = new JButton("View Fine Report");
        // JButton generateReportButton = new JButton("Generate Overall System Report");
        // JButton manageStudentButton = new JButton("Manage User Account");
        // JButton logoutButton = new JButton("Logout");


        JButton manageLibrarianButton = new JButton("Manage Librarians"); // Add/View/Delete Librarian
        JButton viewFineReportButton = new JButton("Fine Report");
        JButton generateReportButton = new JButton("System Report");
        JButton manageStudentButton = new JButton("Manage User Accounts");
        JButton logoutButton = new JButton("Log Out");



        // Add buttons to the navigation panel
        navigationPanel.add(manageLibrarianButton);
        navigationPanel.add(viewFineReportButton);
        navigationPanel.add(generateReportButton);
        navigationPanel.add(manageStudentButton);
        navigationPanel.add(logoutButton);

        // Main content area with CardLayout
        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Panels for each feature
        JPanel librarianPanel = createFeaturePanel("Manage Librarian Section");
        JPanel fineReportPanel = createFeaturePanel("View Fine Report Section");
        JPanel systemReportPanel = createFeaturePanel("Generate Overall System Report Section");
        JPanel studentPanel = createFeaturePanel("Manage Student Account Section");

        // Add feature panels to the content panel
        contentPanel.add(librarianPanel, "Librarian");
        contentPanel.add(fineReportPanel, "FineReport");
        contentPanel.add(systemReportPanel, "SystemReport");
        contentPanel.add(studentPanel, "Student");

        manageStudentButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Student");
            studentPanel.removeAll(); // Clear the panel before adding new components
            studentPanel.setLayout(new BorderLayout());

            // Table for displaying students
            DefaultTableModel studentTableModel = new DefaultTableModel(
                    new String[] { "Student ID", "Name", "Email", "Phone", "Active" }, 0);
            JTable studentTable = new JTable(studentTableModel);
            JScrollPane studentScrollPane = new JScrollPane(studentTable);
            studentPanel.add(studentScrollPane, BorderLayout.CENTER);

            // Search bar
            JPanel searchPanel = new JPanel(new BorderLayout());
            JTextField searchField = new JTextField();
            JButton searchButton = new JButton("Search");
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);
            studentPanel.add(searchPanel, BorderLayout.NORTH);

            // Buttons for Add, Delete, and Toggle Status
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            JButton addStudentButton = new JButton("Add Student");
            JButton deleteStudentButton = new JButton("Delete Student");
            JButton toggleStudentStatusButton = new JButton("Toggle Student Status");
            buttonPanel.add(addStudentButton);
            buttonPanel.add(deleteStudentButton);
            buttonPanel.add(toggleStudentStatusButton);
            studentPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Fetch and display all students
            Admin localAdmin = new Admin();
            try {
                List<Object[]> students = localAdmin.getAllStudents();
                studentTableModel.setRowCount(0); // Clear the table
                for (Object[] student : students) {
                    studentTableModel.addRow(student);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error fetching student data: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Add functionality to the "Add Student" button
            addStudentButton.addActionListener(addEvent -> {
                JTextField nameField = new JTextField();
                JTextField emailField = new JTextField();
                JTextField phoneField = new JTextField();
                JPasswordField passwordField = new JPasswordField();

                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Phone:"));
                panel.add(phoneField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "All fields are required!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Admin admin = new Admin();
                    try {
                        boolean success = admin.insertStudent(name, email, phone, password);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Student added successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> students = admin.getAllStudents();
                            studentTableModel.setRowCount(0);
                            for (Object[] student : students) {
                                studentTableModel.addRow(student);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add student.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error adding student: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add functionality to the "Delete Student" button
            deleteStudentButton.addActionListener(deleteEvent -> {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a student to delete.", "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int studentId = (int) studentTableModel.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this student?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                Admin admin = new Admin();
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = admin.deleteStudent(studentId); // Assume this method exists in Admin.java
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Student deleted successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> students = admin.getAllStudents();
                            studentTableModel.setRowCount(0);
                            for (Object[] student : students) {
                                studentTableModel.addRow(student);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete student.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting student: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add functionality to the "Toggle Student Status" button
            toggleStudentStatusButton.addActionListener(toggleEvent -> {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a student to toggle status.", "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int studentId = (int) studentTableModel.getValueAt(selectedRow, 0);
                boolean currentStatus = studentTableModel.getValueAt(selectedRow, 4).equals("Yes");

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to toggle the status of this student?", "Confirm Toggle",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Admin admin = new Admin();
                        boolean success = admin.updateAccountStatus("student", studentId, !currentStatus);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Student status updated successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> students = admin.getAllStudents();
                            studentTableModel.setRowCount(0);
                            for (Object[] student : students) {
                                studentTableModel.addRow(student);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update student status.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error updating student status: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            studentPanel.revalidate();
            studentPanel.repaint();
        });

        // Similar functionality for "Manage Librarian Account"
        manageLibrarianButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Librarian");
            librarianPanel.removeAll(); // Clear the panel before adding new components
            librarianPanel.setLayout(new BorderLayout());

            // Table for displaying librarians
            DefaultTableModel librarianTableModel = new DefaultTableModel(
                    new String[] { "Librarian ID", "Name", "Password", "Active" }, 0);
            JTable librarianTable = new JTable(librarianTableModel);
            JScrollPane librarianScrollPane = new JScrollPane(librarianTable);
            librarianPanel.add(librarianScrollPane, BorderLayout.CENTER);

            // Search bar
            JPanel searchPanel = new JPanel(new BorderLayout());
            JTextField searchField = new JTextField();
            JButton searchButton = new JButton("Search");
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);
            librarianPanel.add(searchPanel, BorderLayout.NORTH);

            // Buttons for Add, Delete, and Toggle Status
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            JButton addLibrarianButton = new JButton("Add Librarian");
            JButton deleteLibrarianButton = new JButton("Delete Librarian");
            JButton toggleLibrarianStatusButton = new JButton("Toggle Librarian Status");
            buttonPanel.add(addLibrarianButton);
            buttonPanel.add(deleteLibrarianButton);
            buttonPanel.add(toggleLibrarianStatusButton);
            librarianPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Fetch and display all librarians
            Admin admin = new Admin();
            try {
                List<Object[]> librarians = admin.getAllLibrarians();
                librarianTableModel.setRowCount(0); // Clear the table
                for (Object[] librarian : librarians) {
                    librarianTableModel.addRow(librarian);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error fetching librarian data: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Add functionality to the "Add Librarian" button
            addLibrarianButton.addActionListener(addEvent -> {
                JTextField nameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();

                JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Add Librarian", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();

                    if (name.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "All fields are required!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        boolean success = admin.insertLibrarian(name, password);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Librarian added successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> librarians = admin.getAllLibrarians();
                            librarianTableModel.setRowCount(0);
                            for (Object[] librarian : librarians) {
                                librarianTableModel.addRow(librarian);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add librarian.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error adding librarian: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add functionality to the "Delete Librarian" button
            deleteLibrarianButton.addActionListener(deleteEvent -> {
                int selectedRow = librarianTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a librarian to delete.", "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int librarianId = (int) librarianTableModel.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this librarian?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = admin.deleteLibrarian(librarianId); // Assume this method exists in Admin.java
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Librarian deleted successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> librarians = admin.getAllLibrarians();
                            librarianTableModel.setRowCount(0);
                            for (Object[] librarian : librarians) {
                                librarianTableModel.addRow(librarian);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete librarian.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting librarian: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add functionality to the "Toggle Librarian Status" button
            toggleLibrarianStatusButton.addActionListener(toggleEvent -> {
                int selectedRow = librarianTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a librarian to toggle status.", "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int librarianId = (int) librarianTableModel.getValueAt(selectedRow, 0);
                boolean currentStatus = librarianTableModel.getValueAt(selectedRow, 3).equals("Yes");

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to toggle the status of this librarian?", "Confirm Toggle",
                        JOptionPane.YES_NO_OPTION);
                        
                if (confirm == JOptionPane.YES_OPTION) {
                    Admin admins = new Admin();
                    try {
                        
                        boolean success = admin.updateAccountStatus("librarian", librarianId, !currentStatus);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Librarian status updated successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Refresh the table
                            List<Object[]> librarians = admin.getAllLibrarians();
                            librarianTableModel.setRowCount(0);
                            for (Object[] librarian : librarians) {
                                librarianTableModel.addRow(librarian);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update librarian status.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error updating librarian status: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            librarianPanel.revalidate();
            librarianPanel.repaint();
        });

        viewFineReportButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "FineReport"); // Show the Fine Report panel
            fineReportPanel.removeAll(); // Clear the panel before adding new components
            fineReportPanel.setLayout(new BorderLayout());

            // Panel for Individual Fine Report
            JPanel individualFinePanel = new JPanel(new BorderLayout());
            DefaultTableModel individualFineTableModel = new DefaultTableModel(
                    new String[] { "Issue ID", "Book ID", "Fine", "Issue Date", "Due Date", "Status" }, 0);
            JTable individualFineTable = new JTable(individualFineTableModel);
            JScrollPane individualFineScrollPane = new JScrollPane(individualFineTable);
            individualFinePanel.add(new JLabel("Individual Fine Report", SwingConstants.CENTER), BorderLayout.NORTH);
            individualFinePanel.add(individualFineScrollPane, BorderLayout.CENTER);

            // Panel for Monthly Fine Report
            JPanel monthlyFinePanel = new JPanel(new BorderLayout());
            DefaultTableModel monthlyFineTableModel = new DefaultTableModel(
                    new String[] { "Month", "Total Fine" }, 0);
            JTable monthlyFineTable = new JTable(monthlyFineTableModel);
            JScrollPane monthlyFineScrollPane = new JScrollPane(monthlyFineTable);
            monthlyFinePanel.add(new JLabel("Monthly Fine Report", SwingConstants.CENTER), BorderLayout.NORTH);
            monthlyFinePanel.add(monthlyFineScrollPane, BorderLayout.CENTER);

            // Add both tables to the Fine Report panel
            JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            tablesPanel.add(individualFinePanel);
            tablesPanel.add(monthlyFinePanel);
            fineReportPanel.add(tablesPanel, BorderLayout.CENTER);

            // Panel for Buttons
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JButton fetchIndividualFineButton = new JButton("Fetch Individual Fine");
            JButton fetchMonthlyFineButton = new JButton("Fetch Monthly Fine");
            buttonPanel.add(fetchIndividualFineButton);
            buttonPanel.add(fetchMonthlyFineButton);
            fineReportPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add functionality to fetch individual fine report
            fetchIndividualFineButton.addActionListener(fetchIndividualEvent -> {
                String studentIdStr = JOptionPane.showInputDialog("Enter Student ID:");
                if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Student ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int studentId = Integer.parseInt(studentIdStr.trim());
                    Admin admin = new Admin();
                    List<Object[]> individualFines = admin.getIndividualFineReport(studentId);

                    individualFineTableModel.setRowCount(0); // Clear the table
                    for (Object[] fine : individualFines) {
                        individualFineTableModel.addRow(fine);
                    }

                    if (individualFines.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No fines found for Student ID: " + studentId,
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Student ID format!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // Add functionality to fetch monthly fine report
            fetchMonthlyFineButton.addActionListener(fetchMonthlyEvent -> {
                Admin admin = new Admin();
                List<Double> monthlyFines = admin.getMonthlyFineReport();

                monthlyFineTableModel.setRowCount(0); // Clear the table
                for (int i = 0; i < monthlyFines.size(); i++) {
                    monthlyFineTableModel.addRow(new Object[] { getMonthName(i + 1), monthlyFines.get(i) });
                }
            });

            fineReportPanel.revalidate();
            fineReportPanel.repaint();
        });

        generateReportButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "SystemReport"); // Show the System Report panel
            systemReportPanel.removeAll(); // Clear the panel before adding new components
            systemReportPanel.setLayout(new BorderLayout());

            // Panel for Tables
            JPanel tablesPanel = new JPanel(new GridLayout(3, 1, 10, 10));

            // Book Table
            DefaultTableModel bookTableModel = new DefaultTableModel(
                    new String[] { "Book ID", "Title", "Author", "Total Copies", "Available Copies" }, 0);
            JTable bookTable = new JTable(bookTableModel);
            JScrollPane bookScrollPane = new JScrollPane(bookTable);
            JPanel bookPanel = new JPanel(new BorderLayout());
            bookPanel.add(new JLabel("Books", SwingConstants.CENTER), BorderLayout.NORTH);
            bookPanel.add(bookScrollPane, BorderLayout.CENTER);
            tablesPanel.add(bookPanel);

            // Librarian Table
            DefaultTableModel librarianTableModel = new DefaultTableModel(
                    new String[] { "Librarian ID", "Name", "Password" }, 0);
            JTable librarianTable = new JTable(librarianTableModel);
            JScrollPane librarianScrollPane = new JScrollPane(librarianTable);
            JPanel librarianPanels = new JPanel(new BorderLayout());
            librarianPanels.add(new JLabel("Librarians", SwingConstants.CENTER), BorderLayout.NORTH);
            librarianPanels.add(librarianScrollPane, BorderLayout.CENTER);
            tablesPanel.add(librarianPanels);

            // Student Table
            DefaultTableModel studentTableModel = new DefaultTableModel(
                    new String[] { "Student ID", "Name", "Email", "Phone", "Active" }, 0);
            JTable studentTable = new JTable(studentTableModel);
            JScrollPane studentScrollPane = new JScrollPane(studentTable);
            JPanel studentPanels = new JPanel(new BorderLayout());
            studentPanels.add(new JLabel("Students", SwingConstants.CENTER), BorderLayout.NORTH);
            studentPanels.add(studentScrollPane, BorderLayout.CENTER);
            tablesPanel.add(studentPanels);

            systemReportPanel.add(tablesPanel, BorderLayout.CENTER);

            // Summary Panel
            JPanel summaryPanel = new JPanel(new GridLayout(5, 1, 10, 10));
            JLabel totalStudentsLabel = new JLabel("Total Students: ");
            JLabel totalLibrariansLabel = new JLabel("Total Librarians: ");
            JLabel totalBooksLabel = new JLabel("Total Books: ");
            JLabel totalIssuedBooksLabel = new JLabel("Total Issued Books: ");
            JLabel totalFinesLabel = new JLabel("Total Fines Collected: ");
            summaryPanel.add(totalStudentsLabel);
            summaryPanel.add(totalLibrariansLabel);
            summaryPanel.add(totalBooksLabel);
            summaryPanel.add(totalIssuedBooksLabel);
            summaryPanel.add(totalFinesLabel);
            systemReportPanel.add(summaryPanel, BorderLayout.SOUTH);

            // Fetch Data and Populate Tables and Summary
            Admin admin = new Admin();
            try {
                // Populate Book Table
                List<Object[]> books = admin.getAllBooks();
                bookTableModel.setRowCount(0); // Clear the table
                for (Object[] book : books) {
                    bookTableModel.addRow(book);
                }

                // Populate Librarian Table
                List<Object[]> librarians = admin.getAllLibrarians();
                librarianTableModel.setRowCount(0); // Clear the table
                for (Object[] librarian : librarians) {
                    librarianTableModel.addRow(librarian);
                }

                // Populate Student Table
                List<Object[]> students = admin.getAllStudents();
                studentTableModel.setRowCount(0); // Clear the table
                for (Object[] student : students) {
                    studentTableModel.addRow(student);
                }

                // Populate Summary
                totalStudentsLabel.setText("Total Students: " + admin.getTotalStudents());
                totalLibrariansLabel.setText("Total Librarians: " + admin.getTotalLibrarians());
                totalBooksLabel.setText("Total Books: " + admin.getTotalBooks());
                totalIssuedBooksLabel.setText("Total Issued Books: " + admin.getTotalIssuedBooks());
                totalFinesLabel.setText("Total Fines Collected: " + admin.getTotalFinesCollected());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error generating report: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            systemReportPanel.revalidate();
            systemReportPanel.repaint();
        });

        logoutButton.addActionListener(e -> adminFrame.dispose()); // Close the admin dashboard

        // Add navigation and content panels to the frame
        adminFrame.add(navigationPanel, BorderLayout.WEST);
        adminFrame.add(contentPanel, BorderLayout.CENTER);

        // Make the frame visible
        adminFrame.setVisible(true);
    }

    // Helper method to create a feature panel
    private static JPanel createFeaturePanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private static void refreshLibrarianTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear the table
        Admin admin = new Admin();
        try {
            List<Object[]> librarians = admin.getAllLibrarians();
            for (Object[] librarian : librarians) {
                tableModel.addRow(librarian);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching librarian data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to get month name
    private static String getMonthName(int month) {
        return new java.text.DateFormatSymbols().getMonths()[month - 1];
    }

    public static void main(String[] args) {
        // Test the Admin Panel
        SwingUtilities.invokeLater(() -> displayAdminPage());
    }
}