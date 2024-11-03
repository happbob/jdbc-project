package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// 김묘수의 코드
public class EmployeeSalaryUpdate {

    // 연봉 인상 프로그램을 시작하는 메서드
    public void increaseSalary() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("인상 방식을 선택해주세요. \n(1) SSN, (2) 부서, (기타) 종료: ");
            String choice = scanner.nextLine().trim(); // 선택 공백 제거

            if ("ssn".equalsIgnoreCase(choice) || "1".equals(choice)) {
                updateSalaryBySSN(scanner);
            } else if ("부서".equalsIgnoreCase(choice) || "2".equals(choice)) {
                updateSalaryByDepartment(scanner);
            } else {
                System.out.println("\n직원 연봉 인상 프로그램을 종료합니다.\n");
                return; // 프로그램 종료
            }
        }
    }

    // 입력받은 SSN에 해당하는 직원의 연봉을 인상
    private void updateSalaryBySSN(Scanner scanner) {
        System.out.print("직원의 SSN을 입력하세요. : ");
        String ssn = scanner.nextLine();
        double percent = getValidPercentage(scanner);

        displaySalaryBeforeAfter("Ssn", ssn, percent);
    }

    // 입력받은 부서명에 속한 모든 직원의 연봉을 인상
    private void updateSalaryByDepartment(Scanner scanner) {
        System.out.print("연봉을 인상할 부서명을 입력하세요: ");
        String departmentName = scanner.nextLine();
        double percent = getValidPercentage(scanner);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deptStmt = conn.prepareStatement("SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?")) {
            deptStmt.setString(1, departmentName);
            ResultSet rs = deptStmt.executeQuery();

            if (rs.next()) {
                int departmentId = rs.getInt("Dnumber");
                displaySalaryBeforeAfter("Dno", departmentId, percent);
            } else {
                System.out.println("부서 \"" + departmentName + "\"이 존재하지 않습니다.\n");
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    // 퍼센트 유효성 검사 메소드
    private double getValidPercentage(Scanner scanner) {
        double percentage = -1; // 초기화
        while (true) {
            try {
                System.out.print("연봉 인상 비율(퍼센트)을 입력하세요. (%을 제외한 숫자만 입력해주세요.): ");
                percentage = Double.parseDouble(scanner.nextLine().trim()); // 연봉 인상 비율을 Double 타입으로 변환

                if (percentage < 0 || percentage > 100) {
                    System.out.println("잘못된 입력입니다. 0에서 100 사이의 값만 입력해주세요.\n");
                } else {
                    break; // 유효한 값이면 루프 종료
                }
            } catch (NumberFormatException e) {
                System.out.println("유효한 숫자를 입력하세요.\n");
            }
        }
        return percentage;
    }

    // 연봉 인상 전과 후를 조회 및 출력하는 메서드
    private void displaySalaryBeforeAfter(String identifierType, Object identifier, double percent) {
        String selectQuery = "SELECT Ssn, Fname, Salary FROM EMPLOYEE WHERE " + identifierType + " = ?";
        String updateQuery = "UPDATE EMPLOYEE SET Salary = Salary * (1 + ?) WHERE " + identifierType + " = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            if (identifier instanceof String) {
                selectStmt.setString(1, (String) identifier);
                updateStmt.setString(2, (String) identifier);
            } else {
                selectStmt.setInt(1, (Integer) identifier);
                updateStmt.setInt(2, (Integer) identifier);
            }

            // 연봉 인상 전의 데이터 조회
            ResultSet rs = selectStmt.executeQuery();
            System.out.println("\n[연봉 인상 전]");
            System.out.printf("%-10s | %-10s | %-10s\n", "Ssn", "Fname", "Salary");
            System.out.println("-------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s | %-10s | %-10.2f\n", rs.getString("Ssn"), rs.getString("Fname"), rs.getDouble("Salary"));
            }

            // 연봉 업데이트 실행
            updateStmt.setDouble(1, percent / 100);
            int rowsUpdated = updateStmt.executeUpdate();

            // 업데이트 후의 데이터 조회
            ResultSet rsAfter = selectStmt.executeQuery();
            System.out.println("\n[연봉 인상 후]");
            System.out.printf("%-10s | %-10s | %-10s\n", "Ssn", "Fname", "Salary");
            System.out.println("-------------------------------");
            while (rsAfter.next()) {
                System.out.printf("%-10s | %-10s | %-10.2f\n", rsAfter.getString("Ssn"), rsAfter.getString("Fname"), rsAfter.getDouble("Salary"));
            }

            System.out.println(rowsUpdated > 0 ? "\n연봉이 성공적으로 업데이트되었습니다.\n" : "\n해당 직원을 찾을 수 없습니다!\n");

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    // SQL 예외 처리 메시지 통합
    private void printSQLException(SQLException ex) {
        System.out.println("SQL 예외 발생: " + ex.getMessage());
    }
}
