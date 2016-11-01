package de.hska.uilab.mefiro.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class LoginController {

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public ResponseEntity<String> login() {
		return new ResponseEntity<String>("hello world", HttpStatus.OK);
	}
}
