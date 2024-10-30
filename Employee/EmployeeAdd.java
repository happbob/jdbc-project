package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeAdd {

    public void addEmployee() {
        Scanner scanner = new Scanner(System.in);

        // 직원 정보 입력 받기
        System.out.print("First Name을 입력하세요: ");
        String fname = scanner.nextLine();

        System.out.print("Middle Initial을 입력하세요 (없으면 공백): ");
        String minit = scanner.nextLine();

        System.out.print("Last Name을 입력하세요: ");
        String lname = scanner.nextLine();

        System.out.print("Ssn을 입력하세요: ");
        String ssn = scanner.nextLine();

        System.out.print("Birth Date을 입력하세요 (YYYY-MM-DD 형식): ");
        String bdate = scanner.nextLine();

        System.out.print("주소를 입력하세요: ");
        String address = scanner.nextLine();

        System.out.print("성별을 입력하세요 (M/F): ");
        String sex = scanner.nextLine();

        System.out.print("급여를 입력하세요: ");
        double salary = scanner.nextDouble();

        System.out.print("상급자의 Ssn을 입력하세요 (없으면 공백): ");
        scanner.nextLine(); // consume the newline
        String superSsn = scanner.nextLine();

        System.out.print("부서 번호를 입력하세요: ");
        int dno = scanner.nextInt();

        // SQL 쿼리 수정
        String query = "INSERT INTO EMPLOYEE (Fname, minit, Lname, Ssn, Bdate, Address, Sex, Salary, super_ssn, Dno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, minit);
            pstmt.setString(3, lname);
            pstmt.setString(4, ssn);
            pstmt.setString(5, bdate);
            pstmt.setString(6, address);
            pstmt.setString(7, sex);
            pstmt.setDouble(8, salary);
            pstmt.setString(9, superSsn.isEmpty() ? null : superSsn); // null 처리
            pstmt.setInt(10, dno);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("직원 추가 완료");
            } else {
                System.out.println("직원 추가 실패");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
