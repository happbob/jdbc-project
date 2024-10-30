import Employee.EmployeeController;

import java.util.Scanner;

public class Main {
    private static final EmployeeController employeeController = new EmployeeController();
    public static void main(String[] args) {
        boolean exit_flag = false;
        System.out.println("[도우미] : 안녕하세요, Company Database 관리 시스템입니다.");
        while(!exit_flag) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("[도우미] : 관리가 필요한 테이블 번호를 입력해주세요.");
            System.out.println("1. DEPARTMENT");
            System.out.println("2. DEPENDENT");
            System.out.println("3. DEPT_LOCATIONS");
            System.out.println("4. EMPLOYEE");
            System.out.println("5. PROJECT");
            System.out.println("6. WORKS_ON");

            System.out.println();

            System.out.print("[나] : ");
            String menu_number = scanner.nextLine();
            System.out.println();
            switch (menu_number) {
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    employeeController.init();
                    break;
                case "5":
                    break;
                default:
                    System.out.println("[도우미] 잘못된 입력입니다. 1~6사이의 숫자만 입력해주세요.");
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
