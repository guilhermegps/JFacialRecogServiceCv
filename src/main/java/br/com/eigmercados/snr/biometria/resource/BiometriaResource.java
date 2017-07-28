package br.com.eigmercados.snr.biometria.resource;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.eigmercados.snr.biometria.servico.BiometriaServico;
import br.com.eigmercados.snr.tools.RequisicaoEncriptada;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BiometriaResource {

	@Inject
	private BiometriaServico servico;

	@POST
	public Response reconhecimento(RequisicaoEncriptada requisicao) {
		return Response.ok().build();
	}

}
