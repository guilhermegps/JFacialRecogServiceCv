package br.com.eigmercados.snr.biometria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private Logger logger = LoggerFactory.getLogger(CORSFilter.class);

	private String[] dominios = {"*", "http://localhost", "http://localhost:8080", "https://snrd.eigmercados.com.br" };

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String origin = requestContext.getHeaderString("Origin");
		if (!originAccept(origin)) {
			String message = "Header Origin deve ser definido.";
			logger.warn(message);
			requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).entity(message).build());
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String origin = requestContext.getHeaderString("Origin");
		responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", getOrigin(origin));
		responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		List<String> reqHead = requestContext.getHeaders().get("Access-Control-Request-Headers");
		if (null != reqHead) {
			responseContext.getHeaders().put("Access-Control-Allow-Headers", new ArrayList<Object>(reqHead));
		}
	}

	private boolean originAccept(String origin) {
		return StringUtils.isNotBlank(getOrigin(origin));
	}

	private String getOrigin(String origin) {
		for (String dominio : dominios) {
			if ("*".equals(dominio)) {
				return "*";
			}
			if (StringUtils.contains(origin, dominio)) {
				return dominio;
			}
		}
		return "";
	}

}