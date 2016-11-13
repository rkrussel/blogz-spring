package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@Controller
public class PostController extends AbstractController {

	@Autowired 
	 private HttpSession httpSession;
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		String error = "";
		if(title.equals("") || body.equals("")){
			error = "Please enter a title and body";
			model.addAttribute("error", error);
			model.addAttribute("title", title);
			model.addAttribute("body", body);
			return "newpost";
		}
		User user = getUserFromSession(httpSession);
		Post newPost = new Post(title, body, user);
		postDao.save(newPost);
		String redirect = "redirect:/blog/" + user.getUsername() + "/" + newPost.getUid(); 
		return redirect;	
	}
	
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		
		Post post = postDao.findByUid(uid);
		model.addAttribute("title", post.getTitle());
		model.addAttribute("body", post.getBody());
		model.addAttribute("created", post.getCreated());
		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		
		User user = userDao.findByUsername(username);
		List<Post>posts = user.getPosts();
		model.addAttribute("posts", posts);  
		return "blog";
	}
	
}
