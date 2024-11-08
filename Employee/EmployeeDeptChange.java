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

        try {

            if (!isEmplExists(ssn)) {
                System.out.printf("해당 ssn을 가진 직원은 존재하지 않습니다.\n\n", ssn);
                return;
            }

            if (isEmplEqualMgr(ssn)) {
                System.out.printf("%s 직원은 한 부서의 매니저이므로, 타 부서로의 이동이 불가능합니다.\n\n", getEmplName(ssn));
                return;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        System.out.print("이동할 부서명을 입력하세요: ");
        String deptName = scanner.nextLine().trim();

        try {

            if (!isDeptExists(deptName)) {
                System.out.printf("부서 \"%s\"이 존재하지 않습니다.\n\n", deptName);
                return;
            }

            if (isEmplInDept(ssn, deptName)) {
                System.out.printf("%s 직원은 이미 %s 부서에 속해있습니다.\n\n", getEmplName(ssn), deptName);
                return;
            }

            updateEmplDept(ssn, deptName);
            System.out.printf("%s 직원이 성공적으로 %s 부서로 이동했습니다.\n\n", getEmplName(ssn), deptName);

        } catch (SQLException e) {
            printSQLException(e);
        }
    }


    // 직원 존재 여부 확인하기
    private boolean isEmplExists(String ssn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EMPLOYEE WHERE ssn = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // 파라미터로 받은 ssn을 가진 직원이 DB에 존재하면 true 반환
            }
        }
        return false; // 부서가 존재하지 않으면 false 반환
    }

    // 부서 존재 여부 확인하기
    private boolean isDeptExists(String deptName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DEPARTMENT WHERE Dname = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
            stmt.setString(1, deptName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // 파라미터로 받은 부서명을 가진 부서가 DB에 존재하면 true 반환
            }
        }
        return false; // 부서가 존재하지 않으면 false 반환
    }

    // SSN으로 직원 이름 가져오기
    private String getEmplName(String ssn) throws SQLException {
        String name = "";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement("SELECT Fname, Minit, Lname FROM EMPLOYEE WHERE Ssn = ?")) {
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("Fname") + " " + rs.getString("Minit") + " " + rs.getString("Lname");
            }
        }
        return name;
    }

    // 직원이 이미 해당 부서에 속해있는지 확인
    private boolean isEmplInDept(String ssn, String deptName) throws SQLException {
        String sql = "SELECT Dname FROM DEPARTMENT d " +
                "JOIN EMPLOYEE e ON d.Dnumber = e.Dno " +
                "WHERE e.Ssn = ? AND d.Dname = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, deptName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // 매니저직을 맡고 있는 직원인지 확인
    private boolean isEmplEqualMgr(String ssn) throws SQLException {
        String sql = "SELECT CASE WHEN COUNT(*) > 0 THEN 'true' ELSE 'false' END AS IsMgr FROM DEPARTMENT WHERE Mgr_ssn = ?";

        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("IsMgr").equals("true");
            }
        }
        return false;
    }

    // 부서 업데이트 메소드
    private void updateEmplDept(String ssn, String deptName) throws SQLException {
        String sql = "UPDATE EMPLOYEE SET Dno = (SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?) WHERE Ssn = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
            stmt.setString(1, deptName);
            stmt.setString(2, ssn);
            stmt.executeUpdate();
        }
    }

    // SQL 예외 처리 메시지 통합
    private void printSQLException(SQLException ex) {
        System.out.println("SQL 예외 발생: " + ex.getMessage());
    }
}
