package Employee;

import Database.DatabaseConnection;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class EmployeeProjectUpdate {
    public void updateEmployeeProject() {
        Scanner scanner = new Scanner(System.in);
        boolean project_flag = false;
        boolean update_flag = false;
        ResultSet rs;
        Connection conn = DatabaseConnection.connection;;
        while(!project_flag) {
            System.out.println("[도우미] : 프로젝트에 참여 중인 직원 리스트 입니다.");
            String project_employee = "SELECT Pno, Pname, Essn, Fname, Minit, Lname, Hours From WORKS_ON join project on WORKS_ON.Pno = project.Pnumber join EMPLOYEE on WORKS_ON.Essn = EMPLOYEE.Ssn;";

            String project_list_query = "SELECT * From project";
            try {
                PreparedStatement project_list = conn.prepareStatement(project_employee);
                rs = project_list.executeQuery();
                System.out.printf("%-10s %-20s %-20s %-20s %-6s %-20s %-5s %n",
                        "Pno", "Pname", "Essn", "Fname", "Minit", "Lname", "Hours");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    String pno = rs.getString("Pno");
                    String pname = rs.getString("Pname");
                    String essn = rs.getString("Essn");
                    String fname = rs.getString("Fname");
                    String minit = rs.getString("Minit");
                    String lname = rs.getString("Lname");
                    String hours = rs.getString("Hours");
                    // 테이블 형식으로 직원 정보 출력
                    System.out.printf("%-10s %-20s %-20s %-20s %-6s %-20s %-5s %n",
                            pno, pname, essn, fname, minit, lname, hours);
                }

                System.out.println("[도우미] : 프로젝트 리스트를 출력하시겠습니까? Y / N 으로 대답해주세요");
                String answer = scanner.nextLine();
                if(answer.equalsIgnoreCase("Y")){
                    System.out.println("[도우미] : 프로젝트 리스트 입니다.");
                }else{
                    project_flag = true;
                    break;
                }

                PreparedStatement pstmt = conn.prepareStatement(project_list_query);
                // 쿼리 실행 및 결과 출력
                rs = pstmt.executeQuery();
                System.out.printf("%-10s %-20s %-15s%n",
                        "Pnumber", "Pname", "Plocation");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    String pname = rs.getString("Pname");
                    String pnumber = rs.getString("Pnumber");
                    String plocation = rs.getString("Plocation");

                    // 테이블 형식으로 직원 정보 출력
                    System.out.printf("%-10s %-20s %-15s%n",
                            pnumber,pname, plocation);
                }
                project_flag = true;


            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        while(!update_flag) {
            // 1. 입력받은 Pno가 Project 테이블에 존재하는지 확인
            String projectCheckQuery = "SELECT * FROM project WHERE Pnumber = ?";


            System.out.println("[도우미] : 업데이트 할 직원의 Essn과 Pno를 입력해주세요.");
            System.out.println("예시) 123456789 / 3");
            String answer = scanner.nextLine();
            String[] input = answer.split("/");
            if (input.length < 2) {
                System.out.println("[도우미] : 잘못된 입력입니다. 다시 입력해주세요.");
                continue;
            }

            String essn = input[0].trim();
            String pno = input[1].trim();

            try {
                // 1. Essn과 Pno를 가지고 WORKS_ON 테이블에서 해당 데이터가 있는지 체크
                String checkQuery = "SELECT * FROM WORKS_ON WHERE Essn = ? AND Pno = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, essn);
                checkStmt.setString(2, pno);
                rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("[도우미] : 입력한 Essn과 Pno에 해당하는 데이터가 없습니다.");
                    continue;
                }

                String newPno;
                while(true){
                    // 2. 새로운 프로젝트 번호 입력받기
                    System.out.println("[도우미] : 변경할 프로젝트 번호를 입력하세요.");
                    newPno = scanner.nextLine().trim();
                    PreparedStatement projectCheckStmt = conn.prepareStatement(projectCheckQuery);
                    projectCheckStmt.setString(1, newPno);
                    ResultSet projectCheckResult = projectCheckStmt.executeQuery();

                    if (!projectCheckResult.next()) {
                        System.out.println("[도우미] : 입력한 Pno에 해당하는 프로젝트가 존재하지 않습니다. 다시 입력해주세요.");
                        continue;
                    }

                    // 2-2. 새로 입력받은 Pno가 기존 Pno와 같은지 확인
                    if (newPno.equals(pno)) {
                        // 기존 Pno와 동일하면 변경을 허용
                        System.out.println("[도우미] : 입력한 Pno가 현재 참여 중인 프로젝트와 동일합니다. 그대로 업데이트를 진행합니다.");
                        break;
                    }

                    // 2-3. 새로운 Pno가 다른 프로젝트 번호인 경우, 직원이 이미 참여 중인지 확인
                    String worksOnCheckQuery = "SELECT * FROM WORKS_ON WHERE Essn = ? AND Pno = ?";
                    PreparedStatement worksOnCheckStmt = conn.prepareStatement(worksOnCheckQuery);
                    worksOnCheckStmt.setString(1, essn); // 기존 입력받은 직원의 Essn 사용
                    worksOnCheckStmt.setString(2, newPno);
                    ResultSet worksOnCheckResult = worksOnCheckStmt.executeQuery();

                    if (worksOnCheckResult.next()) {
                        System.out.println("[도우미] : 입력한 Pno는 해당 직원이 이미 참여 중인 다른 프로젝트입니다. 다른 프로젝트 번호를 입력해주세요.");
                        continue;
                    }

                    // 유효한 프로젝트 번호가 입력되었으면 반복문 종료
                    break;
                }

                // 3. Hours 입력받기
                System.out.println("[도우미] : 새로운 Hours 값을 입력하세요.");
                String hoursInput = scanner.nextLine().trim();
                double hours;

                // Hours가 소수나 정수인지 확인
                try {
                    hours = Double.parseDouble(hoursInput);
                } catch (NumberFormatException e) {
                    System.out.println("[도우미] : Hours 값이 올바르지 않습니다. 소수 또는 정수를 입력하세요.");
                    continue;
                }

                // 4. Hours와 Pno 값을 업데이트
                String updateQuery = "UPDATE WORKS_ON SET Pno = ?, Hours = ? WHERE Essn = ? AND Pno = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPno);
                updateStmt.setDouble(2, hours);
                updateStmt.setString(3, essn);
                updateStmt.setString(4, pno);

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("[도우미] : Hours가 성공적으로 업데이트되었습니다.");

                    // 변경 내용 알려주기.
                    // 5. 업데이트된 내역 확인 및 출력
                    System.out.println("[도우미] : 변경된 내역은 다음과 같습니다.");
                    System.out.printf(" - 직원 번호 (Essn): %s\n", essn);
                    System.out.printf(" - 새로운 프로젝트 번호 (Pno): %s\n", newPno);
                    System.out.printf(" - 새로운 작업 시간 (Hours): %.2f\n", hours);
                    update_flag = true;
                } else {
                    System.out.println("[도우미] : Hours 업데이트에 실패했습니다.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
