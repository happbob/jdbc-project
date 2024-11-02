package Database;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
    static String database, user, password;
    static String baseUrl = "jdbc:mysql://localhost:3306/";
    public static Connection connection = null;

    public static Connection getConnection() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("[도우미] : database 이름을 입력해주세요.");
            database = scanner.nextLine();
            System.out.println("[도우미] : 데이터베이스 유저명을 입력해주세요.");
            user = scanner.nextLine();
            System.out.println("[도우미] : 데이터베이스 비밀번호을 입력해주세요.");
            password = scanner.nextLine();

            connection = DriverManager.getConnection(baseUrl + database, user, password);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
        return connection;
    }

    public ResultSet runQuery(Connection conn, String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
}

