package de.hska.uilab.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class LoginController {

	@RequestMapping(value="/{username}/{password}", method=RequestMethod.GET)
	public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
		
		String res = "false";
		
		if (username.equals("admin") && password.equals("admin")) res = "true";
		
		return new ResponseEntity<String>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Hello! This is login-microservice", HttpStatus.OK);
	}
}
