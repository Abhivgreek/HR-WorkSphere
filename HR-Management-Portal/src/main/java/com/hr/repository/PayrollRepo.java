package com.hr.repository;

import com.hr.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepo extends JpaRepository<Payroll, Integer> {

    // Find all payrolls for a specific employee
    List<Payroll> findByEmployeeId(Integer employeeId);

    // Find payroll by employee ID, month and year
    Optional<Payroll> findByEmployeeIdAndPayrollMonthAndPayrollYear(Integer employeeId, String payrollMonth, Integer payrollYear);

    // Find all payrolls for a specific month and year
    List<Payroll> findByPayrollMonthAndPayrollYear(String payrollMonth, Integer payrollYear);

    // Find all payrolls by status
    List<Payroll> findByStatus(String status);

    // Find all payrolls for a specific year
    List<Payroll> findByPayrollYear(Integer payrollYear);

    // Get latest payroll for an employee
    @Query("SELECT p FROM Payroll p WHERE p.employeeId = :employeeId ORDER BY p.payrollYear DESC, p.payrollMonth DESC")
    List<Payroll> findLatestPayrollByEmployeeId(@Param("employeeId") Integer employeeId);

    // Check if payroll exists for employee in a specific month/year
    boolean existsByEmployeeIdAndPayrollMonthAndPayrollYear(Integer employeeId, String payrollMonth, Integer payrollYear);

    // Get total payroll cost for a month/year
    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.payrollMonth = :month AND p.payrollYear = :year AND p.status = 'APPROVED'")
    Double getTotalPayrollCost(@Param("month") String month, @Param("year") Integer year);

    // Get average salary by department (joining with Employee table would be ideal, but for simplicity using this approach)
    @Query("SELECT AVG(p.netSalary) FROM Payroll p WHERE p.status = 'APPROVED'")
    Double getAverageSalary();

    // Count payrolls by status
    long countByStatus(String status);

    // Delete all payrolls for an employee (useful when employee is deleted)
    void deleteByEmployeeId(Integer employeeId);
}
