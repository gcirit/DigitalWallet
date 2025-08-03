package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void testCreateEmployee() {
        // Create employee with unique Employee ID
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmployeeId("EMP111"); // Unique Employee ID to avoid conflict
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);

        Employee savedEmployee = employeeService.createEmployee(employee);
        assertNotNull(savedEmployee.getId());
        assertEquals("John", savedEmployee.getName());
        assertEquals("EMP111", savedEmployee.getEmployeeId());
    }

    @Test
    void testCreateEmployeeWithDuplicateEmployeeId() {
        // Create first employee
        Employee employee1 = new Employee();
        employee1.setName("John");
        employee1.setSurname("Doe");
        employee1.setEmployeeId("EMP222");
        employee1.setPassword("password");
        employee1.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeService.createEmployee(employee1);

        // Try to create second employee with same Employee ID
        Employee employee2 = new Employee();
        employee2.setName("Jane");
        employee2.setSurname("Smith");
        employee2.setEmployeeId("EMP222"); // Same Employee ID for duplicate test
        employee2.setPassword("password");
        employee2.setRole(Employee.EmployeeRole.EMPLOYEE);

        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(employee2);
        });
    }

    @Test
    void testFindEmployeeById() {
        // Create employee
        Employee employee = new Employee();
        employee.setName("Alice");
        employee.setSurname("Johnson");
        employee.setEmployeeId("EMP333");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.MANAGER);
        Employee savedEmployee = employeeService.createEmployee(employee);

        // Find by ID
        Optional<Employee> foundEmployee = employeeService.findEmployeeById(savedEmployee.getId());
        assertTrue(foundEmployee.isPresent());
        assertEquals("Alice", foundEmployee.get().getName());
        assertEquals(Employee.EmployeeRole.MANAGER, foundEmployee.get().getRole());
    }

    @Test
    void testFindEmployeeByEmployeeId() {
        // Create employee
        Employee employee = new Employee();
        employee.setName("Bob");
        employee.setSurname("Wilson");
        employee.setEmployeeId("EMP444");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.ADMIN);
        employeeService.createEmployee(employee);

        // Find by Employee ID
        Optional<Employee> foundEmployee = employeeService.findEmployeeByEmployeeId("EMP444");
        assertTrue(foundEmployee.isPresent());
        assertEquals("Bob", foundEmployee.get().getName());
        assertEquals(Employee.EmployeeRole.ADMIN, foundEmployee.get().getRole());
    }

    @Test
    void testGetAllEmployees() {
        // Create multiple employees
        Employee employee1 = new Employee();
        employee1.setName("Employee1");
        employee1.setSurname("Test");
        employee1.setEmployeeId("EMP555");
        employee1.setPassword("password");
        employee1.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeService.createEmployee(employee1);

        Employee employee2 = new Employee();
        employee2.setName("Employee2");
        employee2.setSurname("Test");
        employee2.setEmployeeId("EMP666");
        employee2.setPassword("password");
        employee2.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeService.createEmployee(employee2);

        // Get all employees
        List<Employee> employees = employeeService.getAllEmployees();
        assertTrue(employees.size() >= 2);
    }

    @Test
    void testGetEmployeesByRole() {
        // Create employees with different roles
        Employee employee1 = new Employee();
        employee1.setName("Employee1");
        employee1.setSurname("Test");
        employee1.setEmployeeId("EMP777");
        employee1.setPassword("password");
        employee1.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeService.createEmployee(employee1);

        Employee employee2 = new Employee();
        employee2.setName("Manager1");
        employee2.setSurname("Test");
        employee2.setEmployeeId("EMP888");
        employee2.setPassword("password");
        employee2.setRole(Employee.EmployeeRole.MANAGER);
        employeeService.createEmployee(employee2);

        // Get employees by role
        List<Employee> employees = employeeService.getEmployeesByRole(Employee.EmployeeRole.EMPLOYEE);
        assertTrue(employees.size() >= 1);
        assertEquals(Employee.EmployeeRole.EMPLOYEE, employees.get(0).getRole());

        List<Employee> managers = employeeService.getEmployeesByRole(Employee.EmployeeRole.MANAGER);
        assertTrue(managers.size() >= 1);
        assertEquals(Employee.EmployeeRole.MANAGER, managers.get(0).getRole());
    }

    @Test
    void testUpdateEmployee() {
        // Create employee
        Employee employee = new Employee();
        employee.setName("Original");
        employee.setSurname("Name");
        employee.setEmployeeId("EMP999");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        Employee savedEmployee = employeeService.createEmployee(employee);

        // Update employee
        Employee updateDetails = new Employee();
        updateDetails.setName("Updated");
        updateDetails.setSurname("Name");
        updateDetails.setEmployeeId("EMP999");
        updateDetails.setPassword("newpassword");
        updateDetails.setRole(Employee.EmployeeRole.MANAGER);

        Employee updatedEmployee = employeeService.updateEmployee(savedEmployee.getId(), updateDetails);
        assertEquals("Updated", updatedEmployee.getName());
        assertEquals(Employee.EmployeeRole.MANAGER, updatedEmployee.getRole());
    }

    @Test
    void testDeleteEmployee() {
        // Create employee
        Employee employee = new Employee();
        employee.setName("ToDelete");
        employee.setSurname("Employee");
        employee.setEmployeeId("EMP000");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        Employee savedEmployee = employeeService.createEmployee(employee);

        // Delete employee
        employeeService.deleteEmployee(savedEmployee.getId());

        // Verify deletion
        Optional<Employee> foundEmployee = employeeService.findEmployeeById(savedEmployee.getId());
        assertFalse(foundEmployee.isPresent());
    }

    @Test
    void testExistsByEmployeeId() {
        // Create employee
        Employee employee = new Employee();
        employee.setName("Test");
        employee.setSurname("Employee");
        employee.setEmployeeId("EMP123");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeService.createEmployee(employee);

        // Test exists
        assertTrue(employeeService.existsByEmployeeId("EMP123"));
        assertFalse(employeeService.existsByEmployeeId("NONEXISTENT"));
    }
} 