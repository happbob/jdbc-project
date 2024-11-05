package Employee;

import Database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeDeptChange {

    // 부서 변경 프로그램을 시작하는 메서드
    public void changeEmployeeDept() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("부서를 변경할 직원의 SSN을 입력하세요: ");
        String ssn = scanner.nextLine().trim();

        System.out.print("이동할 부서명을 입력하세요: ");
        String departmentName = scanner.nextLine().trim();

        try {
            if (isEmployeeInDepartment(ssn, departmentName)) {
                System.out.printf("%s 직원은 이미 %s 부서에 속해있습니다.\n\n", getEmployeeName(ssn), departmentName);
                return;
            }

            if (isEmployeeManager(ssn)) {
                System.out.printf("%s 직원은 다른 부서의 매니저이므로, 타 부서로의 이동이 불가능합니다.\n\n", getEmployeeName(ssn), departmentName);
                return;
            }

            updateEmployeeDepartment(ssn, departmentName);
            System.out.printf("%s 직원이 성공적으로 %s 부서로 이동했습니다.\n\n", getEmployeeName(ssn), departmentName);

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    // SSN으로 직원 이름 가져오기
    private String getEmployeeName(String ssn) throws SQLException {
        String name = "";
        try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement("SELECT Fname, Lname FROM EMPLOYEE WHERE Ssn = ?")) {
            pstmt.setString(1, ssn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("Fname") + " " + rs.getString("Lname");
            }
        }
        return name;
    }

    // 직원이 이미 해당 부서에 속해있는지 확인
    private boolean isEmployeeInDepartment(String ssn, String departmentName) throws SQLException {
        String sql = "SELECT Dname FROM DEPARTMENT d " +
                "JOIN EMPLOYEE e ON d.Dnumber = e.Dno " +
                "WHERE e.Ssn = ? AND d.Dname = ?";
        try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(sql)) {
            pstmt.setString(1, ssn);
            pstmt.setString(2, departmentName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    // 매니저직을 맡고 있는 직원인지 확인
    private boolean isEmployeeManager(String ssn) throws SQLException {
        String sql = "SELECT CASE WHEN COUNT(*) > 0 THEN 'true' ELSE 'false' END AS IsManager FROM DEPARTMENT WHERE Mgr_ssn = ?";

        try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(sql)) {
            pstmt.setString(1, ssn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("IsManager").equals("true");
            }
        }
        return false;
    }

    // 부서 업데이트 메소드
    private void updateEmployeeDepartment(String ssn, String departmentName) throws SQLException {
        String sql = "UPDATE EMPLOYEE SET Dno = (SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?) WHERE Ssn = ?";
        try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(sql)) {
            pstmt.setString(1, departmentName);
            pstmt.setString(2, ssn);
            pstmt.executeUpdate();
        }
    }

    // SQL 예외 처리 메시지 통합
    private void printSQLException(SQLException ex) {
        System.out.println("SQL 예외 발생: " + ex.getMessage());
    }
}
