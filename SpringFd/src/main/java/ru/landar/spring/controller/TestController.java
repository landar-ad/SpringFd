package ru.landar.spring.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.landar.spring.model.IFile;
import ru.landar.spring.repository.UserRepositoryCustomImpl;
import ru.landar.spring.service.ObjService;

@Controller
public class TestController {

	@Autowired
	ObjService objService;
	
	@RequestMapping(value = "/test/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, Model model) {
		model.addAttribute("username", "Мама655647657");
		
		
		
		return "loginPage";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String navigate(HttpServletRequest request, Model model) {
		return "loadPage";
	}
	
	@RequestMapping(value = "/test/encode", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String encode(@RequestParam("p") String p) {
		return UserRepositoryCustomImpl.encoder.encode(p);
	}
	
	@RequestMapping(value = "/test/file", method = RequestMethod.GET, produces = "text/plain")
	public String test() {
		return "";
	}
	
	@RequestMapping(value = "/test/pagination")
	public String pagination(
			@RequestParam("p_off") Optional<Integer> offParam, 
			@RequestParam("p_page") Optional<Integer> pageParam,
			@RequestParam("p_total") Optional<Integer> totalParam,
			@RequestParam("p_block") Optional<Integer> blockParam,
			Model model) {
		
		int off = offParam.orElse(0), page = offParam.orElse(15), n = blockParam.orElse(5);
		int totalPages = totalParam.orElse(121);
		model.addAttribute("p_total", totalPages);
		model.addAttribute("p_totalRows", 121);
		model.addAttribute("p_off", Math.min(off, totalPages - 1));
		model.addAttribute("p_page", page);
		model.addAttribute("p_block", n);
		int start = Math.min((off / n) * n + 1, totalPages - n + 1);
		List<Integer> pageNumbers = IntStream.rangeClosed(start , Math.min(start + n - 1, totalPages)).boxed().collect(Collectors.toList());
		model.addAttribute("p_pageNumbers", pageNumbers);
		model.addAttribute("p_countPages", new Integer[]{15, 30, 50, 100});
		return "testPage.html";
	}
}
