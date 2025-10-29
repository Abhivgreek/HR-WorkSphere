package com.hr.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hr.dto.LeaveTrackerDTO;
import com.hr.entity.Compose;
import com.hr.entity.CreatePost;
import com.hr.entity.Employee;
import com.hr.entity.LeaveTracker;
import com.hr.repository.ComposeRepo;
import com.hr.repository.CreatePostRepo;
import com.hr.repository.EmployeeRepo;
import com.hr.service.HrService;
import com.hr.service.LeaveService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HrController {
	
	@Autowired
	private HrService service;
	

	@Autowired
	private CreatePostRepo createPostRepo;
	
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@Autowired
	private ComposeRepo composeRepo;
	
	@Autowired
	private LeaveService leaveService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}
	
	@GetMapping("/home")
	public String home(@RequestParam("username") String username, @RequestParam("password") String password, Model model,HttpSession session) {
		
		System.out.println("User Name and Password: "+username+ " - "+password);
		
	
		try {
			String empId = username.substring(3);
			System.out.println("empId:- "+empId);
			
			Employee employee = employeeRepo.findByIdAndPassword(Integer.parseInt(empId), password);
			
			if(employee != null) {
				model.addAttribute("error", false);
				session.setAttribute("userId", Integer.parseInt(empId));
				session.setAttribute("name", employee.getEmployeeName());
				session.setAttribute("desg", employee.getDesignation());
				
				if(employee.getRole().equals("USER")) {
					System.out.println("Enter in user "+empId);
					return "redirect:/user-dashboard";
				}
                
				else if(employee.getRole().equals("ADMIN")) {
					List<Compose> findAll = composeRepo.findAll();
					System.out.println("List"+findAll);
					findAll.stream()
					.forEach(k->{
						int id=k.getParentUkid();
						String designation = employeeRepo.findById(id).get().getDesignation();
						
						k.setPosition(designation);
						
					});
					
					model.addAttribute("statusList", findAll);
					return "dash-board";
				}else {
					return "redirect:/login";
				}
				
				
				
			}else {
				model.addAttribute("error", true);
				return "login";
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			
			//e.printStackTrace();
			System.err.println(e.getMessage());
			return "redirect:/login";
		}
		 
		
	}
	
	@GetMapping("/dash-board")
	public String dashBard() {
		
		return "dash-board";
	}
	
	@GetMapping("/add-employee")
	public String addEmployee(Model model) {
		
		model.addAttribute("employee", new Employee());
		
		return "add-employee";
	}
	
	@GetMapping("/all-employee")
	public String allEmployee(Model model) {
		
		//get all record
		List<Employee> allEmployee = service.getAllEmployee();
		model.addAttribute("allEmployee", allEmployee);
		
		return "all-employee";
	}
	
	@GetMapping("/create-post")
	public String createPost(Model model) {
		
		List<CreatePost> findAll = createPostRepo.findAll();
		model.addAttribute("post", findAll);
		
		return "create-post";
	}
	
	@GetMapping("/status")
	public String status(Model model) {
		
		List<Compose> findAll = composeRepo.findAll();
		System.out.println("List"+findAll);
		findAll.stream()
		.forEach(k->{
			int id=k.getParentUkid();
			String designation = employeeRepo.findById(id).get().getDesignation();
			
			k.setPosition(designation);
			
		});
		
		model.addAttribute("statusList", findAll);
		
		
		return "status";
	}
	
	
	@GetMapping("/my-profile")
	public String myProfile(HttpSession session,Model model) {
		
		Object attribute = session.getAttribute("userId");
		
		int userId = Integer.parseInt(attribute.toString());
		
		Employee employee = employeeRepo.findById(userId).get();
		model.addAttribute("employee", employee);
		
		System.out.println("Object:- "+attribute);
		
		return "my-profile";
	}
	
	
	@GetMapping("/setting")
	public String setting() {
		
		
		return "setting";
	}
	
	@PostMapping("/save-employee")
	public String saveEmployee(@Valid @ModelAttribute Employee employee,BindingResult result,Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("employee", employee);
			
			return "add-employee";
		}
		
		
		employee.setPassword(employee.getDateOfBirth());
		
		Employee save=service.addEmaployee(employee);
		
		//return "redirect:/all-employee";
		return "dash-board";
	}
	
	@PostMapping("/save-post")
	public String savePost(@ModelAttribute CreatePost createPost) {
		
		createPost.setAddedDate(new Date().toString());
		CreatePost addPost = service.addPost(createPost);
		
		return "redirect:/create-post";
		
	}
	
	@PostMapping("/update-password")
	public String updatePassword(@RequestParam("password")String password,@RequestParam("newPassword1")String newPassword1,@RequestParam("newPassword2")String newPassword2, HttpSession session, Model model) {
		
		System.out.println(password+" - "+newPassword1+" - "+newPassword2);
         Object attribute = session.getAttribute("userId");
		
		int userId = Integer.parseInt(attribute.toString());
		
		Employee employee = employeeRepo.findByIdAndPassword(userId, password);
		
		if(employee != null && newPassword1.equals(newPassword2)) {
			
			employee.setPassword(newPassword2);
			employeeRepo.save(employee);
			model.addAttribute("error", false);
			
		}else {
			
			model.addAttribute("error", true);
			return "setting";
		}
		
		return "redirect:/login";
	}
	
	@GetMapping("/edit-record")
	public String editRecord(@RequestParam("id") int id,Model model) {
		
		System.out.println("ID: "+id);
		Employee employee = employeeRepo.findById(id).get();
		model.addAttribute("employee", employee);
		
		return "edit-record";
	}
	
	@PostMapping("/edit-employee")
	public String updateRecord(@ModelAttribute Employee employee) {
		
		int id = employee.getId();
		
		Employee getEmp = employeeRepo.findById(id).get();
		
		if(getEmp != null) {
			employeeRepo.save(employee);
		}
		
		return "redirect:/all-employee";
	}
	
	@GetMapping("/deleteRecord-byId")
	public String deleteRecordById(@RequestParam("id") int id) {
		
		employeeRepo.deleteById(id);
		
		return "redirect:/all-employee";
	}
	
	@GetMapping("/user-dashboard")
	public String usertDashBord(Model model, HttpSession session) {
		
		
		 Object attribute = session.getAttribute("userId");
			
	   int userId = Integer.parseInt(attribute.toString());
		
		List<Compose> findAll = composeRepo.findByParentUkid(userId);
		System.out.println("List"+findAll);
		findAll.stream()
		.forEach(k->{
			int id=k.getParentUkid();
			String designation = employeeRepo.findById(id).get().getDesignation();
			
			k.setPosition(designation);
			
		});
		
		model.addAttribute("statusList", findAll);
		
		// Add leave balance information
		LeaveTrackerDTO leaveBalance = leaveService.getLeaveBalance(userId);
		model.addAttribute("leaveBalance", leaveBalance);
		
		return "user-dash-board";
	}
	
	@GetMapping("/user-profile")
	public String userProfile(HttpSession session,Model model) {
		
		Object attribute = session.getAttribute("userId");
		
		int userId = Integer.parseInt(attribute.toString());
		
		Employee employee = employeeRepo.findById(userId).get();
		model.addAttribute("employee", employee);
		
		System.out.println("Object:- "+attribute);
		
		return "user-profile";
	}
	
	@GetMapping("/user-setting")
	public String userSetting() {
		
		
		return "user-setting";
	}
	
	
	
	@GetMapping("/user-compose")
	public String compose(HttpSession session, Model model) {
		
		Object attribute = session.getAttribute("userId");
		int userId = Integer.parseInt(attribute.toString());
		
		// Add leave balance information
		LeaveTrackerDTO leaveBalance = leaveService.getLeaveBalance(userId);
		model.addAttribute("leaveBalance", leaveBalance);
		
		return "user-compose";
	}
	
	@PostMapping("/compose")
	public String addCompose(@RequestParam("subject")String subject, @RequestParam("text") String text, @RequestParam(value = "leaveDays", defaultValue = "1") Integer leaveDays, HttpSession session, Model model) {
		
		 try {
			Object attribute = session.getAttribute("userId");
				
			int userId = Integer.parseInt(attribute.toString());
			
			// Check if user has enough leaves
			if (!leaveService.hasEnoughLeaves(userId, leaveDays)) {
				model.addAttribute("error", "Insufficient leave balance. You requested " + leaveDays + " days but only have " + leaveService.getLeaveBalance(userId).getAvailableLeaves() + " days available.");
				LeaveTrackerDTO leaveBalance = leaveService.getLeaveBalance(userId);
				model.addAttribute("leaveBalance", leaveBalance);
				return "user-compose";
			}
			
			Employee employee = employeeRepo.findById(userId).get();
			
			Compose com=new Compose();
			
			com.setEmpName(employee.getEmployeeName());
			com.setSubject(subject);
			com.setText(text);
			com.setLeaveDays(leaveDays);
			com.setParentUkid(userId);
			com.setAddedDate(new Date().toString());
			com.setStatus("PENDING");
			
			Compose save = composeRepo.save(com);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	    
		
		return "redirect:/user-compose";
	}
	
	@GetMapping("/approve-byId")
	public String appeove(@RequestParam("id")int id,@RequestParam("type") String type) {
		
		System.out.println(id+" === "+type);
		Compose compose = composeRepo.findById(id).get();
		compose.setStatus(type);
		
		// If approved, deduct leaves from user's balance
		if ("APPROVED".equals(type)) {
			try {
				Integer leaveDays = compose.getLeaveDays() != null ? compose.getLeaveDays() : 1;
				leaveService.deductLeaves(compose.getParentUkid(), leaveDays);
				System.out.println("Deducted " + leaveDays + " leave days from employee " + compose.getParentUkid());
			} catch (RuntimeException e) {
				System.err.println("Error deducting leaves: " + e.getMessage());
				// Optionally, you could redirect to an error page or show an error message
			}
		}
		
		composeRepo.save(compose);
		
		return "redirect:/status";
		
	}
	
	@GetMapping("/leave-balances")
	public String leaveBalances(Model model) {
		
		// Get all employees
		List<Employee> allEmployees = service.getAllEmployee();
		
		// Add leave balance for each employee
		allEmployees.forEach(emp -> {
			LeaveTrackerDTO leaveBalance = leaveService.getLeaveBalance(emp.getId());
			// Store leave balance temporarily in employee object or create a DTO
		});
		
		model.addAttribute("allEmployees", allEmployees);
		model.addAttribute("leaveService", leaveService); // To access leave balance in template
		
		return "leave-balances";
	}
	
}














