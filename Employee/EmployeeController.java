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
    private static final EmployeeHourlyWage employeeHourlyWage = new EmployeeHourlyWage();
    private static final EmployeeProjectUpdate employeeProjectUpdate = new EmployeeProjectUpdate();

    public void getController(){
        boolean flag = true;
        System.out.println("[도우미] : 직원 조회 메뉴 번호를 선택해주세요...");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. 직원 전체 조회");
        System.out.println("2. 직원 조건 검색");
        System.out.println("3. 그룹별 평균 급여 조회");
        while(flag){
            flag = false;
            System.out.println();
            System.out.print("[나] : ");
            String menu_number = scanner.nextLine();
            System.out.println();

            switch (menu_number){
                case "1":
                    employeeReport.printAllEmployees();
                    break;
                case "2":
                    employeeSalarySearch.findEmployeesBySalaryRange();
                    break;
                case "3":
                    employeeSearch.searchEmployeesWithOptions();
                    break;
                default:
                    System.out.println("[도우미] 잘못된 입력입니다. 1~3사이의 숫자만 입력해주세요.");
                    flag = true;
            }
        }
    }

    public void manageController() {
        boolean flag = true;
        System.out.println("[도우미] : 직원 관리 메뉴 번호를 선택해주세요...");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. 직원 수정");
        System.out.println("2. 직원 삭제");
        System.out.println("3. 직원 추가");
        System.out.println("4. 직원 급여 인상");
        System.out.println("5. 시급 계산");
        System.out.println("6. 직원 프로젝트 업데이트");
        while(flag){
            flag = false;
            System.out.println();
            System.out.print("[나] : ");
            String menu_number = scanner.nextLine();
            System.out.println();

            switch (menu_number){
                case "1":
                    employeeUpdate.updateEmployeeAttribute();
                    break;
                case "2":
                    employeeDelete.deleteEmployeeByCondition();
                    break;
                case "3":
                    employeeAdd.addEmployee();
                    break;
                case "4":
                    employeeSalaryUpdate.increaseSalary();
                    break;
                case "5":
                    employeeHourlyWage.calculateHourlyWage();
                    break;
                case "6":
                    employeeProjectUpdate.updateEmployeeProject();
                    break;
                default:
                    System.out.println("[도우미] 잘못된 입력입니다. 1~5사이의 숫자만 입력해주세요.");
                    flag = true;
            }
        }
    }
}
