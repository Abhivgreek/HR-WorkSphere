package com.hr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hr.entity.Employee;
import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer>{

	public Employee findByIdAndPassword(int empId,String password);
	//
	public Employee findByIdAndPasswordAndRole(int empId,String password,String role);
	
	public Employee findByEmailAndPassword(String email, String password);
	
	// Additional methods needed by the service layer
	public Employee findByEmail(String email);
	
	public List<Employee> findByActiveTrue();
}
