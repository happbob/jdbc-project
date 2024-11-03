package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
public class EmployeeDelete {

    public void deleteEmployeeByCondition() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("삭제할 직원의 Ssn을 입력하세요: ");
        String ssn = scanner.nextLine();

        String selectQuery = "SELECT * FROM EMPLOYEE WHERE Ssn = ?";
        String deleteQuery = "DELETE FROM EMPLOYEE WHERE Ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // 삭제할 직원 정보 조회
            selectStmt.setString(1, ssn);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // 삭제할 직원 정보 출력 (테이블 형식)
                System.out.println("\n[삭제할 직원 정보]");
                System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s | %-22s | %-10s | %-10s | %-10s\n",
                        "Ssn", "Fname", "Minit", "Lname", "Bdate", "Address", "Sex", "Salary", "Dno");
                System.out.println("----------------------------------------------------------------------------------------------------------------------");
                System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s | %-22s | %-10s | %-10.2f | %-10s\n",
                        rs.getString("Ssn"), rs.getString("Fname"), rs.getString("Minit"), rs.getString("Lname"),
                        rs.getDate("Bdate"), rs.getString("Address"), rs.getString("Sex"),
                        rs.getDouble("Salary"), rs.getInt("Dno"));

                // 삭제 쿼리 실행
                deleteStmt.setString(1, ssn);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\n직원 삭제 완료");
                }
            } else {
                System.out.println("해당 Ssn을 가진 직원이 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
