package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeeReport {

    public void printAllEmployees() {
        Scanner scanner = new Scanner(System.in);
        Connection conn = DatabaseConnection.connection;

        // 1. 검색 항목 선택
        System.out.println("조회할 항목을 선택하세요 (콤마로 구분):");
        System.out.println("예: Ssn, Fname, Lname, Salary, Dname, Bdate, Address, Sex, super_ssn");
        String[] columns = scanner.nextLine().split(",");

        List<String> selectedColumns = new ArrayList<>();
        for (String column : columns) {
            selectedColumns.add(column.trim());
        }

        String selectClause = String.join(", ", selectedColumns);

        // SELECT 절에 선택한 컬럼만 포함
        String query = "SELECT " + selectClause + " FROM EMPLOYEE E " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            //테이블 출력 결과
            // 각 행의 데이터를 저장할 리스트와 열 너비 계산을 위한 맵
            List<Map<String, String>> rows = new ArrayList<>();
            Map<String, Integer> columnWidths = new HashMap<>();

            // 초기 열 너비를 각 열 이름 길이로 설정
            for (String column : selectedColumns) {
                columnWidths.put(column, column.length());
            }

            // 결과 데이터를 한 번 순회하며 열 너비 계산 및 행 저장
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (String column : selectedColumns) {
                    String value = rs.getString(column);
                    row.put(column, value);

                    // 각 열의 최대 너비 갱신
                    int length = value.length();
                    if (length > columnWidths.get(column)) {
                        columnWidths.put(column, length);
                    }
                }
                rows.add(row); // 현재 행을 리스트에 추가
            }

            // 테이블 헤더 출력
            for (String column : selectedColumns) {
                System.out.printf("%-" + columnWidths.get(column) + "s | ", column);
            }
            System.out.println("\n" + "-".repeat(columnWidths.values().stream().mapToInt(Integer::intValue).sum() + selectedColumns.size() * 3));

            // 저장된 각 행 데이터 출력
            for (Map<String, String> row : rows) {
                for (String column : selectedColumns) {
                    System.out.printf("%-" + columnWidths.get(column) + "s | ", row.get(column));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
