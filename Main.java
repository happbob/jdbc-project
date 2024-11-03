import Database.DatabaseConnection;
import Employee.EmployeeController;

import java.util.Scanner;
import java.util.Objects;

public class Main {
    private static final EmployeeController employeeController = new EmployeeController();
    public static void main(String[] args) {
        boolean exit_flag = false;
        boolean database_flag = false;

        System.out.println("[도우미] : 안녕하세요, 직원 관리 시스템입니다.");
        while(!database_flag){
            System.out.println("[도우미] : 데이터베이스 연결을 시도합니다.");

            DatabaseConnection.getConnection();

            if(Objects.isNull(DatabaseConnection.connection)){
                continue;
            }
            database_flag = true;
        }

        while(!exit_flag) {
            Scanner scanner = new Scanner(System.in);

            // 예시
            System.out.println("[도우미] : 관리 카테고리를 입력해주세요.");
            System.out.println("1. 직원 조회");
            System.out.println("2. 직원 관리");

            System.out.println();

            System.out.print("[나] : ");
            String menu_number = scanner.nextLine();
            System.out.println();
            switch (menu_number) {
                case "1":
                    employeeController.getController();
                    break;
                case "2":
                    employeeController.manageController();
                    break;
                default:
                    System.out.println("[도우미] 잘못된 입력입니다. 1, 2의 숫자만 입력해주세요.");
                    continue;
            }

            System.out.println("[도우미] : 시스템 사용을 종료하시려면 1, 메뉴로 돌아가시려면 2를 눌러주세요.");
            System.out.print("[나] : ");
            int a = scanner.nextInt();
            if(a == 1) {
                exit_flag = true;
            }
        }
    }
}
