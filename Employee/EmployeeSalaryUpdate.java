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

        executeSalaryUpdate("UPDATE EMPLOYEE SET Salary = Salary * (1 + ?) WHERE Ssn = ?", percent / 100, ssn);
    }

    // 입력받은 부서명에 속한 모든 직원의 연봉을 인상
    private void updateSalaryByDepartment(Scanner scanner) {
        System.out.print("연봉을 인상할 부서명을 입력하세요: ");
        String departmentName = scanner.nextLine();
        double percent = getValidPercentage(scanner);

        try (PreparedStatement deptStmt = DatabaseConnection.connection.prepareStatement("SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?")) {
            deptStmt.setString(1, departmentName);
            ResultSet rs = deptStmt.executeQuery();

            if (rs.next()) {
                int departmentId = rs.getInt("Dnumber");
                executeSalaryUpdate("UPDATE EMPLOYEE SET Salary = Salary * (1 + ?) WHERE Dno = ?", percent / 100, departmentId);
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

    // 연봉 업데이트를 공통으로 처리하는 메서드
    private void executeSalaryUpdate(String sql, double percent, Object identifier) {
        // 업데이트 이전의 급여를 조회하기 위한 쿼리 작성
        String selectSql = "SELECT Fname, Minit, Lname, Salary FROM EMPLOYEE WHERE " +
                (identifier instanceof String ? "Ssn = ?" : "Dno = ?");
        boolean isDepartment = identifier instanceof Integer; // 부서 인상인지 여부
        boolean updateSuccess = false;

        try (PreparedStatement selectStmt = DatabaseConnection.connection.prepareStatement(selectSql)) {
            // identifier가 SSN-문자열인 경우와 부서-부서 번호(int)인 경우에 따라 설정
            if (isDepartment) {
                selectStmt.setInt(1, (Integer) identifier);
            } else {
                selectStmt.setString(1, (String) identifier);
            }

            ResultSet resultSet = selectStmt.executeQuery();

            // 변경 전후 급여 출력
            while (resultSet.next()) {
                String firstName = resultSet.getString("Fname");
                String middleInit = resultSet.getString("Minit");
                String lastName = resultSet.getString("Lname");
                double oldSalary = resultSet.getDouble("Salary");
                double newSalary = oldSalary * (1 + percent);

                // 각 직원의 급여 업데이트
                try (PreparedStatement updateStmt = DatabaseConnection.connection.prepareStatement(sql)) {
                    updateStmt.setDouble(1, percent);
                    if (isDepartment) {
                        updateStmt.setInt(2, (Integer) identifier);
                    } else {
                        updateStmt.setString(2, (String) identifier);
                    }

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.printf("\n직원 이름: %s %s %s\n변경 전 연봉: %.2f\n변경 후 연봉: %.2f\n",
                                firstName, middleInit, lastName, oldSalary, newSalary);
                        updateSuccess = true;

                        // SSN 옵션의 경우, 업데이트 성공 메시지 출력
                        if (!isDepartment) {
                            System.out.println("\n직원의 연봉이 성공적으로 인상되었습니다.\n");
                        }
                    }
                }
            }

            if (!updateSuccess) {
                System.out.println("\n해당 직원 또는 부서를 찾을 수 없습니다!\n");
            } else if (isDepartment) {
                System.out.println("\n모든 직원의 연봉이 성공적으로 인상되었습니다.\n");
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }


    // SQL 예외 처리 메시지 통합
    private void printSQLException(SQLException ex) {
        System.out.println("SQL 예외 발생: " + ex.getMessage());
    }
}
