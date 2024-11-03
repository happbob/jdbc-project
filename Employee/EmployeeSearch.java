package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeeSearch {

    // 검색 범위와 그룹별 평균 급여 조건을 모두 적용하여 출력
    public void searchEmployeesWithOptions() {
        Scanner scanner = new Scanner(System.in);

        // 1. 검색 항목 선택
        System.out.println("조회할 항목을 선택하세요 (콤마로 구분):");
        System.out.println("예: Ssn, Fname, Lname, Salary, Dname, Bdate, Address, Sex, super_ssn");
        String[] columns = scanner.nextLine().split(",");

        List<String> selectedColumns = new ArrayList<>();
        for (String column : columns) {
            selectedColumns.add(column.trim());
        }
        String selectClause = String.join(", ", selectedColumns);

        // 2. 검색 범위 선택
        System.out.print("검색 범위를 선택하세요 (전체/부서/성별/상급자/연봉): ");
        String rangeType = scanner.nextLine();

        String whereClause = "";
        if (rangeType.equalsIgnoreCase("부서")) {
            System.out.print("검색할 부서명을 입력하세요: ");
            whereClause = "D.Dname = ?";
        } else if (rangeType.equalsIgnoreCase("성별")) {
            System.out.print("검색할 성별을 입력하세요 (M/F): ");
            whereClause = "E.Sex = ?";
        } else if (rangeType.equalsIgnoreCase("상급자")) {
            System.out.print("상급자의 Ssn을 입력하세요: ");
            whereClause = "E.super_ssn = ?";
        } else if (rangeType.equalsIgnoreCase("전체")) {
            System.out.println("전체 직원 정보를 검색합니다.");
            whereClause = "";
        } else {
            System.out.println("잘못된 검색 범위입니다.");
            return;
        }

        // 검색 결과를 표시하기 위해 쿼리 작성
        String query = "SELECT " + selectClause + " FROM EMPLOYEE E " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                (whereClause.isEmpty() ? "" : "WHERE " + whereClause);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 검색 범위에 대한 입력값 설정
            if (!whereClause.isEmpty() && !rangeType.equalsIgnoreCase("전체")) {
                System.out.print("입력값: ");
                String inputValue = scanner.nextLine();
                pstmt.setString(1, inputValue);
            }

            ResultSet rs = pstmt.executeQuery();

            //테이블 출력 결과
            // 열 너비 계산
            Map<String, Integer> columnWidths = new HashMap<>();
            for (String column : selectedColumns) {
                columnWidths.put(column, column.length());
            }

            List<Map<String, String>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (String column : selectedColumns) {
                    String value = rs.getString(column);
                    row.put(column, value);
                    columnWidths.put(column, Math.max(columnWidths.get(column), value.length()));
                }
                rows.add(row);
            }

            // 검색 결과 출력
            System.out.println("\n[검색 결과]");
            if (rows.isEmpty()) {
                System.out.println("검색 결과가 없습니다.");
            } else {
                for (String column : selectedColumns) {
                    System.out.printf("%-" + columnWidths.get(column) + "s | ", column);
                }
                System.out.println("\n" + "-".repeat(columnWidths.values().stream().mapToInt(Integer::intValue).sum() + selectedColumns.size() * 3));

                for (Map<String, String> row : rows) {
                    for (String column : selectedColumns) {
                        System.out.printf("%-" + columnWidths.get(column) + "s | ", row.get(column));
                    }
                    System.out.println();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. 평균 급여 조건 추가
        System.out.print("\n그룹별 평균 급여를 보시겠습니까? (Y/N): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            System.out.print("평균 급여를 계산할 그룹을 선택하세요 (부서/성별/상급자): ");
            String groupType = scanner.nextLine();

            String groupByClause = "";
            if (groupType.equalsIgnoreCase("부서")) {
                groupByClause = "D.Dname";
            } else if (groupType.equalsIgnoreCase("성별")) {
                groupByClause = "E.Sex";
            } else if (groupType.equalsIgnoreCase("상급자")) {
                groupByClause = "E.super_ssn";
            } else {
                System.out.println("잘못된 입력입니다.");
                return;
            }

            // 그룹별 평균 급여를 위한 쿼리 작성
            String avgQuery = "SELECT " + groupByClause + " AS GroupField, AVG(E.Salary) AS AverageSalary " +
                    "FROM EMPLOYEE E " +
                    "JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                    "GROUP BY " + groupByClause;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement avgStmt = conn.prepareStatement(avgQuery)) {

                ResultSet avgRs = avgStmt.executeQuery();

                // 열 너비 설정
                int groupWidth = Math.max(groupByClause.length(), "Group".length());
                int avgWidth = "Average Salary".length();

                List<Map<String, String>> avgRows = new ArrayList<>();
                while (avgRs.next()) {
                    Map<String, String> avgRow = new HashMap<>();
                    String groupField = avgRs.getString("GroupField");
                    String avgSalary = String.format("%.2f", avgRs.getDouble("AverageSalary"));

                    avgRow.put("Group", groupField);
                    avgRow.put("Average Salary", avgSalary);

                    groupWidth = Math.max(groupWidth, groupField.length());
                    avgWidth = Math.max(avgWidth, avgSalary.length());
                    avgRows.add(avgRow);
                }

                // 그룹별 평균 급여 출력
                System.out.println("\n[그룹별 평균 급여 결과]");
                System.out.printf("%-" + groupWidth + "s | %-" + avgWidth + "s\n", "Group", "Average Salary");
                System.out.println("-".repeat(groupWidth + avgWidth + 3));

                for (Map<String, String> avgRow : avgRows) {
                    System.out.printf("%-" + groupWidth + "s | %-" + avgWidth + "s\n", avgRow.get("Group"), avgRow.get("Average Salary"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("프로그램을 종료합니다.");
        }
    }
}
