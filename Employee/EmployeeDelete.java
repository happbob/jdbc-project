package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
public class EmployeeDelete {

    public void deleteEmployeeByCondition() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("삭제할 직원의 Ssn을 입력하세요: ");
        String ssn = scanner.nextLine();

        String query = "DELETE FROM EMPLOYEE WHERE Ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ssn);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("직원 삭제 완료");
            } else {
                System.out.println("해당 Ssn을 가진 직원이 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
