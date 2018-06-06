package de.unistuttgart.iaas.amyassist.amy.httpserver;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable t) {
		ResponseBuilder rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		String message = t.toString();
		
		//TODO change later only for debugging/ testing
		try {
			message = message + " " + t.getStackTrace()[0].toString();			
		} catch (Exception e) {
			//no stacktrace
		}
		Response r = rb.entity(message).type(MediaType.TEXT_PLAIN).build();
		return r;
	}

}
