package br.com.eigmercados.snr.biometria.exception.mapper;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

	private static final Logger LOG = LoggerFactory.getLogger(NotAuthorizedExceptionMapper.class);

	@Override
	public Response toResponse(NotAuthorizedException exception) {
		Map<String, Object> response = new HashMap<>();
		StringBuilder messages = new StringBuilder();
		for (Object message : exception.getChallenges()) {
			messages.append(message.toString()).append(". ");
		}
		LOG.warn(messages.toString());
		response.put("message", messages.toString());
		return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(response).build();
	}
}