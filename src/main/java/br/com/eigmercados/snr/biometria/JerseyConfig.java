package br.com.eigmercados.snr.biometria;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import br.com.eigmercados.snr.biometria.exception.mapper.RuntimeExceptionMapper;
import br.com.eigmercados.snr.biometria.resource.BiometriaResource;

@ApplicationPath("/rest")
@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(BiometriaResource.class);
		filters();
		uploadSupport();
		exceptionMapper();
	}

	private void filters() {
		register(CORSFilter.class);
	}

	private void uploadSupport() {
		register(MultiPartFeature.class);
	}

	private void exceptionMapper() {
		register(RuntimeExceptionMapper.class);
	}

}
