package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeSalaryUpdate {

    // 연봉 인상 프로그램을 시작하는 메서드
    public void increaseSalary() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("직원의 연봉을 인상하시겠습니까? (Y/N)");
            String userInput = scanner.nextLine().trim();;

            if ("Y".equalsIgnoreCase(userInput)) {
                System.out.println("인상 방식을 선택해주세요. \n(1) SSN, (2) 부서, (기타) 종료: ");
                String choice = scanner.nextLine().trim(); // 선택 공백 제거

                if ("ssn".equalsIgnoreCase(choice) || "1".equals(choice)) {
                    updateSalaryBySSN(scanner);
                } else if ("부서".equalsIgnoreCase(choice) || "2".equals(choice)) {
                    updateSalaryByDepartment(scanner);
                } else {
                    System.out.println("직원 연봉 인상 프로그램을 종료합니다.");
                    return; // 프로그램 종료
                }
            } else if ("N".equalsIgnoreCase(userInput)) {
                System.out.println("직원 연봉 인상 프로그램을 종료합니다.");
                return; // 프로그램 종료
            }
        }
    }

    // 입력받은 SSN에 해당하는 직원의 연봉을 인상
    private void updateSalaryBySSN(Scanner scanner) {
        System.out.println("직원의 SSN을 입력하세요. :");
        String ssn = scanner.nextLine();
        double percent = getValidPercentage(scanner);

        executeSalaryUpdate("UPDATE EMPLOYEE SET Salary = Salary * (1 + ?) WHERE Ssn = ?", percent / 100, ssn);
    }

    // 입력받은 부서명에 속한 모든 직원의 연봉을 인상
    private void updateSalaryByDepartment(Scanner scanner) {
        System.out.println("연봉을 인상할 부서명을 입력하세요: ");
        String departmentName = scanner.nextLine();
        double percent = getValidPercentage(scanner);

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement deptStmt = conn.prepareStatement("SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?");
            deptStmt.setString(1, departmentName);
            ResultSet rs = deptStmt.executeQuery();

            if (rs.next()) {
                int departmentId = rs.getInt("Dnumber");
                executeSalaryUpdate("UPDATE EMPLOYEE SET Salary = Salary * (1 + ?) WHERE Dno = ?", percent / 100, departmentId);
            } else {
                System.out.println("부서 \"" + departmentName + "\"이 존재하지 않습니다.");
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
                System.out.println("연봉 인상 비율(퍼센트)을 입력하세요. (%을 제외한 숫자만 입력해주세요.)");
                percentage = Double.parseDouble(scanner.nextLine().trim()); // 연봉 인상 비율을 Double로 변환

                if (percentage < 0 || percentage > 100) {
                    System.out.println("잘못된 입력입니다. 0에서 100 사이의 값만 입력해주세요.");
                } else {
                    break; // 유효한 값이면 루프 종료
                }
            } catch (NumberFormatException e) {
                System.out.println("유효한 숫자를 입력하세요.");
            }
        }
        return percentage;
    }


    // 연봉 업데이트를 공통으로 처리하는 메서드
    private void executeSalaryUpdate(String sql, double percent, Object identifier) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDouble(1, percent);

            if (identifier instanceof String) {
                preparedStatement.setString(2, (String) identifier);
            } else {
                preparedStatement.setInt(2, (Integer) identifier);
            }

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "연봉이 성공적으로 업데이트되었습니다.\n" : "해당 직원을 찾을 수 없습니다!\n");
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    // SQL 예외 처리 메시지 통합
    private void printSQLException(SQLException ex) {
        System.out.println("SQL 예외 발생: " + ex.getMessage());
    }
}
