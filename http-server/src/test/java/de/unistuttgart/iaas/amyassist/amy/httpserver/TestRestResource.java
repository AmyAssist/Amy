package de.unistuttgart.iaas.amyassist.amy.httpserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 
 * @author Leon Kiefer
 */
@Path("/")
public class TestRestResource {
	@GET
	public String ping(String s) {
		return s;
	}
}
