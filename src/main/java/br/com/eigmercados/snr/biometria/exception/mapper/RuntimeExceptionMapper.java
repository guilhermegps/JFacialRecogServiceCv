package br.com.eigmercados.snr.biometria.exception.mapper;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

	@Override
	public Response toResponse(RuntimeException exception) {
		String message = exception.getMessage();
		LOG.error(message);
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(response).build();
	}

}