package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testContextLoads() {
        assertNotNull(employeeRepository);
    }

    @Test
    void testSaveAndFindEmployee() {
        // Create an employee with unique Employee ID
        Employee employee = new Employee();
        employee.setName("Test");
        employee.setSurname("Employee");
        employee.setEmployeeId("EMP999"); // Unique Employee ID to avoid conflict
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);
        assertNotNull(savedEmployee.getId());
        assertEquals("Test", savedEmployee.getName());

        // Find by ID
        Employee foundEmployee = employeeRepository.findById(savedEmployee.getId()).orElse(null);
        assertNotNull(foundEmployee);
        assertEquals("Test", foundEmployee.getName());
    }

    @Test
    void testFindByEmployeeId() {
        // Create an employee
        Employee employee = new Employee();
        employee.setName("Jane");
        employee.setSurname("Smith");
        employee.setEmployeeId("EMP888");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeRepository.save(employee);

        // Find by Employee ID
        Employee foundEmployee = employeeRepository.findByEmployeeId("EMP888").orElse(null);
        assertNotNull(foundEmployee);
        assertEquals("Jane", foundEmployee.getName());
        assertEquals("EMP888", foundEmployee.getEmployeeId());
    }

    @Test
    void testFindByRole() {
        // Create employees with different roles
        Employee employee1 = new Employee();
        employee1.setName("Employee1");
        employee1.setSurname("Test");
        employee1.setEmployeeId("EMP777");
        employee1.setPassword("password");
        employee1.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeRepository.save(employee1);

        Employee employee2 = new Employee();
        employee2.setName("Manager1");
        employee2.setSurname("Test");
        employee2.setEmployeeId("EMP666");
        employee2.setPassword("password");
        employee2.setRole(Employee.EmployeeRole.MANAGER);
        employeeRepository.save(employee2);

        // Find by role
        List<Employee> employees = employeeRepository.findByRole(Employee.EmployeeRole.EMPLOYEE);
        assertEquals(1, employees.size());
        assertEquals("Employee1", employees.get(0).getName());

        List<Employee> managers = employeeRepository.findByRole(Employee.EmployeeRole.MANAGER);
        assertEquals(1, managers.size());
        assertEquals("Manager1", managers.get(0).getName());
    }

    @Test
    void testExistsByEmployeeId() {
        // Create an employee
        Employee employee = new Employee();
        employee.setName("Test");
        employee.setSurname("Employee");
        employee.setEmployeeId("EMP555");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeRepository.save(employee);

        // Test exists
        assertTrue(employeeRepository.existsByEmployeeId("EMP555"));
        assertFalse(employeeRepository.existsByEmployeeId("NONEXISTENT"));
    }
} 