package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.User;
import org.launchcode.blogz.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {
	@Autowired
	private UserDao UserDao;
	
	@Autowired 
	 private HttpSession httpSession;

	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {
		
		
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		String verify = request.getParameter("verify");
		
		model.addAttribute("username", name);
		if(name == null || password == null){
			String nullField = "Please fill in all fields.";
			model.addAttribute("empty_error", nullField);
			return "/signup";
		}
		if(UserDao.findByUsername(name) != null){
			String existingUser = "This username is already in use.";
			model.addAttribute("existing_error", existingUser);
			return "/signup";
		}
		User newUser = new User(name, password);
		UserDao.save(newUser);
		if(!newUser.isValidUsername(name)){
			String invalidName = "Invalid user name";
			model.addAttribute("inName", invalidName);
			return "/signup";
		}
		if(!newUser.isValidPassword(password)){
			String invalidPw = "Invalid Password";
			model.addAttribute("password_error", invalidPw);
			return "/signup";
		}
		if(!newUser.isMatchingPassword(verify)){
			String nonMatchPw = "Passwords do not match";
			model.addAttribute("verify_error", nonMatchPw);
			return "/signup";
		}
		
		setUserInSession(httpSession, newUser);
		return "redirect:/blog/newpost";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		
		
		String invalid = "";
		String noUser = "";
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		User user = UserDao.findByUsername(name);
		if(user == null){
			noUser = "User name not found";
			model.addAttribute("noUser", noUser);
			return "/login";
		}
		
		if(!user.isMatchingPassword(password)){
			invalid = "Invalid password";
			model.addAttribute("invalid", invalid);
			return "/login";
		}
		
		
		setUserInSession(httpSession, user);
		return "redirect:blog/newpost";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request){
        request.getSession().invalidate();
		return "redirect:/";
	}
}
