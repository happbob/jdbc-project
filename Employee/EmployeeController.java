package Employee;

import java.util.Scanner;

public class EmployeeController {
    private static final EmployeeAdd employeeAdd = new EmployeeAdd();
    private static final EmployeeDelete employeeDelete = new EmployeeDelete();
    private static final EmployeeSearch employeeSearch = new EmployeeSearch();
    private static final EmployeeReport employeeReport = new EmployeeReport();
    private static final EmployeeSalarySearch employeeSalarySearch = new EmployeeSalarySearch();
    private static final EmployeeSalaryUpdate employeeSalaryUpdate = new EmployeeSalaryUpdate();
    private static final EmployeeUpdate employeeUpdate = new EmployeeUpdate();

    public void init(){
        boolean flag = true;
        System.out.println("[도우미] : EMPLOYEE 테이블 관리 메뉴 번호를 선택해주세요...");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. 생성");
        System.out.println("2. 조회");
        System.out.println("3. 수정");
        System.out.println("4. 삭제");
        while(flag){
            flag = false;
            System.out.println();
            System.out.print("[나] : ");
            String menu_number = scanner.nextLine();
            System.out.println();

            switch (menu_number){
                case "1":
                    employeeAdd.addEmployee();
                    break;
                case "2":
                    employeeSearch.searchEmployeesWithOptions();
                    break;
                case "3":
                    employeeUpdate.updateEmployeeAttribute();
                    break;
                case "4":
                    employeeDelete.deleteEmployeeByCondition();
                    break;
                default:
                    System.out.println("[도우미] 잘못된 입력입니다. 1~4사이의 숫자만 입력해주세요.");
                    flag = true;
            }
        }

    }
}
