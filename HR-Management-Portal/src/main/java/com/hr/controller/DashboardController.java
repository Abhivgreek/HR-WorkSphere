package com.hr.controller;

/*import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hr.entity.Compose;
import com.hr.repository.ComposeRepo;

@Controller
public class DashboardController {

	@Autowired
	private ComposeRepo composeRepo;

	@GetMapping("/dashboard")
	public String showDashboard(Model model) {

	    List<Compose> statusList = composeRepo.findAll();
	    model.addAttribute("statusList", statusList);

	    // Department-wise counts (assuming "status" holds department name)
	    model.addAttribute("developmentCount", composeRepo.countByStatus("Development"));
	    model.addAttribute("qaTestingCount", composeRepo.countByStatus("QA Testing"));
	    model.addAttribute("networkingCount", composeRepo.countByStatus("Networking"));
	    model.addAttribute("hrTeamCount", composeRepo.countByStatus("HR Team"));
	    model.addAttribute("securityCount", composeRepo.countByStatus("Security"));
	    model.addAttribute("salesMarketCount", composeRepo.countByStatus("Seals Market"));

	    // Status-wise counts
	    model.addAttribute("pendingCount", composeRepo.countByStatus("Pending"));
	    model.addAttribute("approvedCount", composeRepo.countByStatus("Approved"));
	    model.addAttribute("canceledCount", composeRepo.countByStatus("Canceled"));
	    model.addAttribute("deniedCount", composeRepo.countByStatus("Denied"));
	    model.addAttribute("allCount", composeRepo.countAll());
	    model.addAttribute("remainderCount", composeRepo.countByStatus("Remainder"));

	    return "user/dashboard"; // or whatever your view name is
	}

}*/
