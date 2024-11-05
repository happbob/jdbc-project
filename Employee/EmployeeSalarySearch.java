package Employee;

import Database.DatabaseConnection;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class EmployeeSalarySearch {
    public void findEmployeesBySalaryRange() {
        Scanner scanner = new Scanner(System.in);
        Connection conn = DatabaseConnection.connection;

        System.out.print("검색할 최소 급여를 입력하세요: ");
        double minSalary = scanner.nextDouble();
        System.out.print("검색할 최대 급여를 입력하세요: ");
        double maxSalary = scanner.nextDouble();

        String query = "SELECT Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified " +
                "FROM employee WHERE Salary BETWEEN ? AND ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, minSalary);
            pstmt.setDouble(2, maxSalary);

            ResultSet rs = pstmt.executeQuery();

            System.out.printf("%-15s %-5s %-15s %-12s %-12s %-30s %-5s %-10s %-12s %-5s %-21s %-20s%n",
                    "Fname", "Minit", "Lname", "Ssn", "Bdate", "Address", "Sex", "Salary", "Super_ssn", "Dno", "Created", "Modified");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                String fname = rs.getString("Fname");
                String minit = rs.getString("Minit");
                String lname = rs.getString("Lname");
                String ssn = rs.getString("Ssn");
                Date bdate = rs.getDate("Bdate");
                String address = rs.getString("Address");
                String sex = rs.getString("Sex");
                double salary = rs.getDouble("Salary");
                String superSsn = rs.getString("Super_ssn");
                int dno = rs.getInt("Dno");
                Timestamp created = rs.getTimestamp("created");
                Timestamp modified = rs.getTimestamp("modified");

                // 테이블 형식으로 직원 정보 출력
                System.out.printf("%-15s %-5s %-15s %-12s %-12s %-30s %-5s %-10.2f %-12s %-5d %-20s %-20s%n",
                        fname, minit, lname, ssn, bdate, address, sex, salary, superSsn, dno, created, modified);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
