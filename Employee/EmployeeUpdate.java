package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeUpdate {

    public void updateEmployeeAttribute() {
        Scanner scanner = new Scanner(System.in);

        // 수정할 직원의 Ssn, 속성 이름, 새로운 값 입력받기
        System.out.print("수정할 직원의 Ssn을 입력하세요: ");
        String ssn = scanner.nextLine();

        System.out.print("수정할 속성의 이름을 입력하세요 (Fname, Minit, Lname, Bdate, Address, Sex, Salary, Super_ssn, Dno): ");
        String attribute = scanner.nextLine();

        String newValue;
        while (true) {
            System.out.print("새로운 값을 입력하세요: ");
            newValue = scanner.nextLine();

            // 도메인 검증: 각 속성에 대한 유효성 검사
            if (isValid(attribute, newValue)) {
                break;
            } else {
                System.out.println("입력한 값이 해당 속성의 도메인과 일치하지 않습니다. 다시 입력하세요.");
            }
        }

        String query = "UPDATE employee SET " + attribute + " = ? WHERE Ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // PreparedStatement에 파라미터 설정
            setPreparedStatementParameter(pstmt, 1, attribute, newValue);
            pstmt.setString(2, ssn);

            // 업데이트 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("직원 정보가 성공적으로 수정되었습니다.");
            } else {
                System.out.println("해당 Ssn을 가진 직원이 없습니다.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid(String attribute, String value) {
        try {
            switch (attribute) {
                case "Fname":
                case "Lname":
                    return value.length() <= 15;  // 이름 최대 길이 검증
                case "Minit":
                case "Sex":
                    return value.length() == 1;  // 성별과 중간 이름의 길이 검증
                case "Ssn":
                case "Super_ssn":
                    return value.length() == 9;  // SSN 길이 검증
                case "Bdate":
                    Date.valueOf(value);  // 날짜 형식 검증 (YYYY-MM-DD)
                    return true;
                case "Address":
                    return value.length() <= 30;  // 주소 최대 길이 검증
                case "Salary":
                    Double.parseDouble(value);  // 급여 숫자 검증
                    return true;
                case "Dno":
                    Integer.parseInt(value);  // 부서 번호 정수 검증
                    return true;
                default:
                    return false;
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private void setPreparedStatementParameter(PreparedStatement pstmt, int paramIndex, String attribute, String value) throws SQLException {
        switch (attribute) {
            case "Fname":
            case "Lname":
            case "Address":
                pstmt.setString(paramIndex, value);
                break;
            case "Minit":
            case "Sex":
                pstmt.setString(paramIndex, value);
                break;
            case "Ssn":
            case "Super_ssn":
                pstmt.setString(paramIndex, value);
                break;
            case "Bdate":
                pstmt.setDate(paramIndex, Date.valueOf(value));
                break;
            case "Salary":
                pstmt.setDouble(paramIndex, Double.parseDouble(value));
                break;
            case "Dno":
                pstmt.setInt(paramIndex, Integer.parseInt(value));
                break;
        }
    }
}
