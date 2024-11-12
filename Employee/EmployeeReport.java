package Employee;

import Database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeeReport {

    // 전체 직원 - 전체 속성값 출력
    public void printAllEmployeesWithoutCondition() {
        Connection conn = DatabaseConnection.connection;
        String query = "SELECT * FROM EMPLOYEE E " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> allColumns = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                allColumns.add(metaData.getColumnName(i));
            }

            List<Map<String, String>> rows = extractData(rs, allColumns);
            printTable(rows, allColumns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 전체 직원 - 입력받은 속성값만 출력
    public void printAllEmployees() {
        Scanner scanner = new Scanner(System.in);
        Connection conn = DatabaseConnection.connection;

        // 1. 검색 항목 선택
        System.out.println("조회할 항목을 선택하세요 (콤마로 구분):");
        System.out.println("예: Fname, Minit, Lname, Ssn, Salary, Dname, Bdate, Address, Sex, super_ssn");
        String[] columns = scanner.nextLine().split(",");

        List<String> selectedColumns = new ArrayList<>();
        for (String column : columns) {
            selectedColumns.add(column.trim());
        }

        String selectClause = String.join(", ", selectedColumns);
        String query = "SELECT " + selectClause + " FROM EMPLOYEE E " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            List<Map<String, String>> rows = extractData(rs, selectedColumns);
            printTable(rows, selectedColumns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 데이터 추출 메소드
    private List<Map<String, String>> extractData(ResultSet rs, List<String> columns) throws SQLException {
        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, Integer> columnWidths = new HashMap<>();

        for (String column : columns) {
            columnWidths.put(column, column.length()); // 초기 열 너비 설정
        }

        while (rs.next()) {
            Map<String, String> row = new HashMap<>();
            for (String column : columns) {
                String value = rs.getString(column) != null ? rs.getString(column) : "NULL";
                row.put(column, value);

                // 열 너비 갱신
                columnWidths.put(column, Math.max(columnWidths.get(column), value.length()));
            }
            rows.add(row);
        }

        return rows;
    }

    // 테이블 출력
    private void printTable(List<Map<String, String>> rows, List<String> columns) {
        Map<String, Integer> columnWidths = new HashMap<>();

        // 열 너비 계산
        for (String column : columns) {
            columnWidths.put(column, column.length());
        }

        for (Map<String, String> row : rows) {
            for (String column : columns) {
                int length = row.get(column).length();
                columnWidths.put(column, Math.max(columnWidths.get(column), length));
            }
        }

        // 테이블 헤더 출력
        for (String column : columns) {
            System.out.printf("%-" + columnWidths.get(column) + "s | ", column);
        }
        System.out.println("\n" + "-".repeat(columnWidths.values().stream().mapToInt(Integer::intValue).sum() + columns.size() * 3));

        // 데이터 출력
        for (Map<String, String> row : rows) {
            for (String column : columns) {
                System.out.printf("%-" + columnWidths.get(column) + "s | ", row.get(column));
            }
            System.out.println();
        }
    }

    // 입력에 따라 메소드를 호출
    public void executeReport() {
        System.out.println("전체 직원 출력 기능입니다. 메뉴에 따라 1또는 2를 입력하여 출력 방식을 정해주세요.\n(1) - 전체 출력, (2) - 선택한 속성만으로 전체 출력");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                printAllEmployeesWithoutCondition();
                break;
            case "2":
                printAllEmployees();
                break;
            default:
                System.out.println("잘못된 입력입니다. 1 또는 2를 입력하세요.");
                break;
        }
    }
}
