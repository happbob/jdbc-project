package Employee;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeReport {

    public void printAllEmployees() {
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

        // SELECT 절에 선택한 컬럼만 포함
        String query = "SELECT " + selectClause + " FROM EMPLOYEE E " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n[검색 결과]");
            while (rs.next()) {
                for (String column : selectedColumns) {
                    System.out.print(column + ": " + rs.getString(column) + " ");
                }
                System.out.println("\n-----------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
