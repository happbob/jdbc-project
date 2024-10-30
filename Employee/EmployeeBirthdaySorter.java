package Employee;

import Database.DatabaseConnection;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class EmployeeBirthdaySorter {
    public void sortEmployeesByBirthday() {
        Scanner scanner = new Scanner(System.in);

        // 사용자에게 정렬 방식 선택 받기
        System.out.print("정렬 방식을 선택하세요 (1: 생일 빠른순, 2: 생일 느린순): ");
        int choice = scanner.nextInt();

        // 정렬 방식에 따른 ORDER BY 절 설정
        String order = (choice == 1) ? "ASC" : "DESC";
        String query = "SELECT Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified " +
                "FROM employee ORDER BY Bdate " + order;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 쿼리 실행 및 결과 출력
            ResultSet rs = pstmt.executeQuery();
            // 테이블 형식 헤더 출력
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

