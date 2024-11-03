package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeHourlyWage {

    public void calculateHourlyWage() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("시급을 조회할 대상을 선택해주세요.");
        System.out.println("1 - 전체 직원");
        System.out.println("2 - 직원 한명 (Ssn)");
        System.out.println("3 - 부서 (Dname)");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String query = "";
        String param = null;

        switch (choice) {
            case 1 -> {
                // No specific filter
                query = """
                        SELECT e.Fname, e.Minit, e.Lname,
                               COUNT(w.Pno) AS ProjectCount,
                               e.Salary,
                               IFNULL(SUM(w.Hours), 0) AS TotalHours,
                               IF(SUM(w.Hours) > 0, e.Salary / SUM(w.Hours), 0) AS HourlyWage
                        FROM EMPLOYEE e
                        LEFT JOIN WORKS_ON w ON e.Ssn = w.Essn
                        GROUP BY e.Fname, e.Minit, e.Lname, e.Salary
                        """;
            }
            case 2 -> {
                System.out.print("Enter Employee Ssn: ");
                param = scanner.nextLine();
                query = """
                        SELECT e.Fname, e.Minit, e.Lname,
                               COUNT(w.Pno) AS ProjectCount,
                               e.Salary,
                               IFNULL(SUM(w.Hours), 0) AS TotalHours,
                               IF(SUM(w.Hours) > 0, e.Salary / SUM(w.Hours), 0) AS HourlyWage
                        FROM EMPLOYEE e
                        LEFT JOIN WORKS_ON w ON e.Ssn = w.Essn
                        WHERE e.Ssn = ?
                        GROUP BY e.Fname, e.Minit, e.Lname, e.Salary
                        """;
            }
            case 3 -> {
                System.out.print("Enter Department Name: ");
                param = scanner.nextLine();
                query = """
                        SELECT e.Fname, e.Minit, e.Lname,
                               COUNT(w.Pno) AS ProjectCount,
                               e.Salary,
                               IFNULL(SUM(w.Hours), 0) AS TotalHours,
                               IF(SUM(w.Hours) > 0, e.Salary / SUM(w.Hours), 0) AS HourlyWage
                        FROM EMPLOYEE e
                        JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                        LEFT JOIN WORKS_ON w ON e.Ssn = w.Essn
                        WHERE d.Dname = ?
                        GROUP BY e.Fname, e.Minit, e.Lname, e.Salary
                        """;
            }
            default -> {
                System.out.println("잘못된 입력입니다.");
                return;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (choice == 2 || choice == 3) {
                pstmt.setString(1, param);
            }

            ResultSet rs = pstmt.executeQuery();
            System.out.printf("%-10s | %-10s | %-10s | %-15s | %-15s | %-15s | %-15s\n",
                    "First Name", "M", "Last Name", "Project Count", "Salary", "Total Hours", "Hourly Wage");
            System.out.println("------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s | %-10s | %-10s | %-15s | %-15s | %-15s | %-15s\n",
                        rs.getString("Fname"),
                        rs.getString("Minit"),
                        rs.getString("Lname"),
                        rs.getInt("ProjectCount"),
                        rs.getDouble("Salary"),
                        rs.getDouble("TotalHours"),
                        rs.getDouble("HourlyWage"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


