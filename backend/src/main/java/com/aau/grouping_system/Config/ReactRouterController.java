package com.aau.grouping_system.Config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactRouterController {
	// This matches any path NOT starting with /api/, /static/, or /ws
	@RequestMapping(value = "/{path:^(?!api/|static/|ws).*}")
	public String forward() {
		return "forward:/index.html"; // Forwards to React's entry point
	}
}