import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * College Student Registration System (JDBC)
 * 
 * Features:
 * 1. Insert a new student record (student ID, name, department, year of study,
 * email)
 * 2. Display all registered students
 * 3. Search by student ID
 * 4. Prevent duplicate student IDs
 */
public class StudentRegistrationApp {

    // =========================================================================
    // DATABASE CONFIGURATION (EDIT HERE IF NEEDED)
    // =========================================================================
    // Mode options:
    // "auto" -> Tries MySQL first; if unavailable, uses SQLite automatically
    // "mysql" -> Strictly uses MySQL
    // "sqlite" -> Strictly uses SQLite (college_students.db)
    private static final String DB_MODE = "auto";

    // MySQL Settings
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/college_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "root";

    // SQLite Settings (local file, zero setup needed)
    private static final String SQLITE_URL = "jdbc:sqlite:college_students.db";

    private static String activeDbType = "UNKNOWN";
    private static final Scanner scanner = new Scanner(System.in);

    // =========================================================================
    // STUDENT MODEL CLASS
    // =========================================================================
    public static class Student {
        private String studentId;
        private String name;
        private String department;
        private int yearOfStudy;
        private String email;

        public Student(String studentId, String name, String department, int yearOfStudy, String email) {
            this.studentId = studentId;
            this.name = name;
            this.department = department;
            this.yearOfStudy = yearOfStudy;
            this.email = email;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public int getYearOfStudy() {
            return yearOfStudy;
        }

        public void setYearOfStudy(int yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return String.format("Student [ID: %s, Name: %s, Dept: %s, Year: %d, Email: %s]",
                    studentId, name, department, yearOfStudy, email);
        }
    }

    // =========================================================================
    // MAIN ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        // Initialize database table
        initializeDatabase();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerStudent();
                    break;
                case "2":
                    displayAllStudents();
                    break;
                case "3":
                    searchStudentById();
                    break;
                case "4":
                    updateStudent();
                    break;
                case "5":
                    deleteStudent();
                    break;
                case "6":
                    System.out.println("\nThank you for using College Student Registration System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please enter a number between 1 and 6.");
            }

            if (running) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    // =========================================================================
    // MENU DISPLAY
    // =========================================================================
    private static void printMenu() {
        System.out.println("\n========================================================");
        System.out.println("          COLLEGE STUDENT REGISTRATION SYSTEM           ");
        System.out.println("                 (JDBC Lab Application)                 ");
        System.out.println("========================================================");
        System.out.println("  Connected Database Engine: " + activeDbType);
        System.out.println("--------------------------------------------------------");
        System.out.println("  1. Register New Student (Insert Record)");
        System.out.println("  2. Display All Registered Students");
        System.out.println("  3. Search Student by Student ID");
        System.out.println("  4. Update Student Record");
        System.out.println("  5. Delete Student Record");
        System.out.println("  6. Exit");
        System.out.println("========================================================");
        System.out.print("Enter your choice (1-6): ");
    }

    // =========================================================================
    // 1. REGISTER NEW STUDENT (With Duplicate ID Prevention)
    // =========================================================================
    private static void registerStudent() {
        System.out.println("\n--- [1] REGISTER NEW STUDENT ---");

        // 1. Student ID
        String studentId;
        while (true) {
            System.out.print("Enter Student ID (e.g., 2024CS001) [or 'cancel' to exit]: ");
            studentId = scanner.nextLine().trim();

            if (studentId.equalsIgnoreCase("cancel")) {
                System.out.println("Registration cancelled.");
                return;
            }

            if (studentId.isEmpty()) {
                System.out.println("[!] Student ID cannot be empty.");
                continue;
            }

            // REQUIRED FEATURE: Prevent duplicate student IDs (Pre-check)
            if (isStudentIdExists(studentId)) {
                System.out.println("\n[ERROR] Duplicate Student ID detected!");
                System.out.println("        A student with ID '" + studentId + "' already exists in the database.");
                System.out.println("        Please enter a unique Student ID.\n");
                continue;
            }

            break;
        }

        // 2. Name
        String name;
        while (true) {
            System.out.print("Enter Student Name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty())
                break;
            System.out.println("[!] Name cannot be empty.");
        }

        // 3. Department
        String department;
        while (true) {
            System.out.print("Enter Department (e.g., Computer Science, Mechanical, ECE): ");
            department = scanner.nextLine().trim();
            if (!department.isEmpty())
                break;
            System.out.println("[!] Department cannot be empty.");
        }

        // 4. Year of Study
        int yearOfStudy = 1;
        while (true) {
            System.out.print("Enter Year of Study (1 - 5): ");
            String input = scanner.nextLine().trim();
            try {
                yearOfStudy = Integer.parseInt(input);
                if (yearOfStudy >= 1 && yearOfStudy <= 5)
                    break;
                System.out.println("[!] Please enter a valid year between 1 and 5.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number. Please try again.");
            }
        }

        // 5. Email Address
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = scanner.nextLine().trim();
            if (email.contains("@") && email.contains(".") && !email.contains(" "))
                break;
            System.out.println("[!] Invalid email format (must contain '@' and domain dot).");
        }

        // Insert into database using PreparedStatement
        String sql = "INSERT INTO students (student_id, name, department, year_of_study, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, department);
            ps.setInt(4, yearOfStudy);
            ps.setString(5, email);

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("\n[SUCCESS] Student registered successfully!");
                System.out.println("Registered Details: Student ID=" + studentId + ", Name=" + name +
                        ", Dept=" + department + ", Year=" + yearOfStudy + ", Email=" + email);
            }
        } catch (SQLException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Duplicate") || e.getMessage().contains("UNIQUE"))) {
                System.out.println("\n[ERROR] Duplicate Student ID! A record with this ID already exists.");
            } else {
                System.out.println("\n[ERROR] Database insertion failed: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // 2. DISPLAY ALL REGISTERED STUDENTS
    // =========================================================================
    private static void displayAllStudents() {
        System.out.println("\n--- [2] ALL REGISTERED STUDENTS ---");

        String sql = "SELECT student_id, name, department, year_of_study, email FROM students ORDER BY student_id ASC";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            List<Student> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("year_of_study"),
                        rs.getString("email")));
            }

            if (list.isEmpty()) {
                System.out.println("[i] No students registered in the database yet.");
                return;
            }

            // Print formatted table
            String separator = "+--------------------+----------------------+--------------------+------+--------------------------------+";
            System.out.println(separator);
            System.out.printf("| %-18s | %-20s | %-18s | %-4s | %-30s |\n",
                    "Student ID", "Full Name", "Department", "Year", "Email Address");
            System.out.println(separator);

            for (Student s : list) {
                System.out.printf("| %-18s | %-20s | %-18s | %-4d | %-30s |\n",
                        s.getStudentId(), s.getName(), s.getDepartment(), s.getYearOfStudy(), s.getEmail());
            }
            System.out.println(separator);
            System.out.println("Total Registered Students: " + list.size());

        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to retrieve students: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. SEARCH BY STUDENT ID
    // =========================================================================
    private static void searchStudentById() {
        System.out.println("\n--- [3] SEARCH STUDENT BY ID ---");
        System.out.print("Enter Student ID to search: ");
        String searchId = scanner.nextLine().trim();

        if (searchId.isEmpty()) {
            System.out.println("[!] Student ID cannot be empty.");
            return;
        }

        String sql = "SELECT student_id, name, department, year_of_study, email FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n[MATCH FOUND] Student Details:");
                    System.out.println("----------------------------------------------");
                    System.out.println("  Student ID    : " + rs.getString("student_id"));
                    System.out.println("  Full Name     : " + rs.getString("name"));
                    System.out.println("  Department    : " + rs.getString("department"));
                    System.out.println("  Year of Study : " + rs.getInt("year_of_study"));
                    System.out.println("  Email Address : " + rs.getString("email"));
                    System.out.println("----------------------------------------------");
                } else {
                    System.out.println("\n[NOT FOUND] No student record found with ID: '" + searchId + "'");
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Search query failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. UPDATE STUDENT RECORD
    // =========================================================================
    private static void updateStudent() {
        System.out.println("\n--- [4] UPDATE STUDENT RECORD ---");
        System.out.print("Enter Student ID to update: ");
        String searchId = scanner.nextLine().trim();

        String selectSql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql)) {

            ps.setString(1, searchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("[!] No student found with ID: '" + searchId + "'");
                    return;
                }

                String currentName = rs.getString("name");
                String currentDept = rs.getString("department");
                int currentYear = rs.getInt("year_of_study");
                String currentEmail = rs.getString("email");

                System.out.println("Current Record: " + currentName + " (" + currentDept + ", Year " + currentYear
                        + ", " + currentEmail + ")");
                System.out.println("(Press ENTER to keep the current value)\n");

                System.out.print("New Name [" + currentName + "]: ");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty())
                    newName = currentName;

                System.out.print("New Department [" + currentDept + "]: ");
                String newDept = scanner.nextLine().trim();
                if (newDept.isEmpty())
                    newDept = currentDept;

                System.out.print("New Year of Study [" + currentYear + "]: ");
                String yearInput = scanner.nextLine().trim();
                int newYear = currentYear;
                if (!yearInput.isEmpty()) {
                    try {
                        newYear = Integer.parseInt(yearInput);
                    } catch (NumberFormatException ignored) {
                    }
                }

                System.out.print("New Email Address [" + currentEmail + "]: ");
                String newEmail = scanner.nextLine().trim();
                if (newEmail.isEmpty())
                    newEmail = currentEmail;

                String updateSql = "UPDATE students SET name = ?, department = ?, year_of_study = ?, email = ? WHERE student_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setString(1, newName);
                    updatePs.setString(2, newDept);
                    updatePs.setInt(3, newYear);
                    updatePs.setString(4, newEmail);
                    updatePs.setString(5, searchId);

                    int rows = updatePs.executeUpdate();
                    if (rows > 0) {
                        System.out.println("\n[SUCCESS] Student record updated successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Update failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. DELETE STUDENT RECORD
    // =========================================================================
    private static void deleteStudent() {
        System.out.println("\n--- [5] DELETE STUDENT RECORD ---");
        System.out.print("Enter Student ID to delete: ");
        String searchId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete student '" + searchId + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchId);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("[SUCCESS] Student record deleted successfully.");
            } else {
                System.out.println("[!] No student found with ID '" + searchId + "'.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Deletion failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // HELPER: PREVENT DUPLICATE STUDENT IDS (Pre-check)
    // =========================================================================
    public static boolean isStudentIdExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // Error checking, will default to false and let database constraint catch it
        }
        return false;
    }

    // =========================================================================
    // DATABASE CONNECTION & INITIALIZATION
    // =========================================================================
    public static Connection getConnection() throws SQLException {
        if ("sqlite".equalsIgnoreCase(DB_MODE)) {
            activeDbType = "SQLite";
            return DriverManager.getConnection(SQLITE_URL);
        }

        if ("mysql".equalsIgnoreCase(DB_MODE)) {
            activeDbType = "MySQL";
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        }

        // Default 'auto' mode: tries MySQL, falls back to SQLite
        try {
            Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            activeDbType = "MySQL";
            return conn;
        } catch (SQLException e) {
            activeDbType = "SQLite";
            return DriverManager.getConnection(SQLITE_URL);
        }
    }

    private static void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // SQL compatible with both MySQL and SQLite
            String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                    "student_id VARCHAR(20) NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "department VARCHAR(50) NOT NULL, " +
                    "year_of_study INT NOT NULL, " +
                    "email VARCHAR(100) NOT NULL" +
                    ")";
            stmt.executeUpdate(createTableSQL);

        } catch (SQLException e) {
            System.err.println("[WARN] Table initialization notice: " + e.getMessage());
        }
    }
}
