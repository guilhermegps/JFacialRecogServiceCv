package br.com.eigmercados.snr.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

public class Paginador<T> {

	private final Logger LOGGER = LoggerFactory.getLogger(Paginador.class);

	private Number numeroPagina;
	private Number qtdePorPagina;
	private Number qtdeRegistros;
	private Iterable<T> pagina;

	public Paginador(Integer numeroPagina, Integer qtdePorPagina, Integer qtdeRegistros) {
		this.numeroPagina = numeroPagina == null ? 0 : numeroPagina;
		this.qtdePorPagina = qtdePorPagina == null ? 0 : qtdePorPagina;
		this.qtdeRegistros = qtdeRegistros == null ? 0 : qtdeRegistros;
	}

	public Paginador(Page<T> page) {
		this.numeroPagina = page.getNumber() + 1;
		this.qtdePorPagina = page.getSize();
		this.qtdeRegistros = page.getTotalElements();
		this.pagina = page.getContent();
	}

	public Number getNumeroPagina() {
		return numeroPagina;
	}

	public void setNumeroPagina(Integer numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

	public Number getQtdePorPagina() {
		return qtdePorPagina;
	}

	public void setQtdePorPagina(Integer qtdePorPagina) {
		this.qtdePorPagina = qtdePorPagina;
	}

	public Number getQtdeRegistros() {
		return qtdeRegistros;
	}

	public void setQtdeRegistros(Integer qtdeRegistros) {
		this.qtdeRegistros = qtdeRegistros;
	}

	public Number limit() {
		return this.qtdePorPagina;
	}

	public Long offset() {
		return (this.numeroPagina.longValue() - 1) * this.qtdePorPagina.intValue();
	}

	public Number getQtdePaginas() {
		try {
			long qtdePaginas = this.qtdeRegistros.longValue() / this.qtdePorPagina.longValue();
			long resto = this.qtdeRegistros.longValue() % this.qtdePorPagina.longValue();
			return resto > 0 ? qtdePaginas + 1 : qtdePaginas;
		} catch (ArithmeticException e) {
			LOGGER.warn(e.getMessage(), e.getCause());
			return 0;
		}
	}

	public List<T> getPagina() {
		List<T> lista = new ArrayList<>();
		for (T t : pagina) {
			lista.add(t);
		}
		return lista;
	}

	public void setPagina(Iterable<T> pagina) {
		this.pagina = pagina;
	}

}