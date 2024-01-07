package com.example.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.Library;
import com.example.entity.Log;
import com.example.service.LibraryService;
import com.example.service.LogService;
import com.example.service.LoginUser;

@Controller
@RequestMapping("library")
public class LibraryController {

	private final LibraryService libraryService;
	private final LogService logService;

	@Autowired
	public LibraryController(LibraryService libraryService
			, LogService logService) {
		this.libraryService = libraryService;
		this.logService = logService;
	}

	@GetMapping
	public String index(Model model) {
		List<Library> libraries = this.libraryService.findAll();
		model.addAttribute("libraries", libraries);
		return "library/index";
	}

	@GetMapping("/borrow")
	public String borrowingForm(@RequestParam("id") Integer id
			, Model model) {
		Library library = this.libraryService.findById(id);
		model.addAttribute("id", id);
		model.addAttribute("library", library);
		library.setName(library.getName());
		library.setUserId(library.getUserId());
		return "library/borrowingForm";
	}

	@PostMapping("/borrow")
	public String borrow(@RequestParam("id") Integer id
				, @RequestParam("return_due_date") String returnDueDate
				, @AuthenticationPrincipal LoginUser loginUser) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(returnDueDate, format);
		Library library = this.libraryService.findById(id);
		library.setUserId(loginUser.getUser().getId());
		Log log = new Log();
	    log.setLibraryId(library.getId());
	    log.setUserId(loginUser.getUser().getId());
	    log.setRentDate(LocalDateTime.now());
	    log.setReturnDueDate(date.atStartOfDay());
	    log.setReturnDate(null);
		this.logService.save(log);
		return "redirect:/library";
	}

	@PostMapping("/return")
	public String returnBook(@RequestParam("id") Integer id
			, @AuthenticationPrincipal LoginUser loginUser) {
		Library library = this.libraryService.findById(id);
		library.setUserId(0);
		this.libraryService.save(library);
		this.logService.update(id, loginUser.getUser().getId());
		return "redirect:/library";
	}

}
