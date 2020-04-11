package mediabox.interfaces;

import java.io.File;
import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.model.Arquivo;

public interface ArquivoDAO {

	public abstract Arquivo getArquivoPorCodigo(int cod) throws FalhaAcessoAosDadosException;

	public abstract List<Arquivo> buscaTodos() throws FalhaAcessoAosDadosException;

	public abstract int insereArquivo(File arquivo) throws FalhaAcessoAosDadosException;

	public abstract int alteraArquivo(Arquivo arquivo) throws FalhaAcessoAosDadosException;

	public abstract int apagaTodos() throws FalhaAcessoAosDadosException;

	public abstract int removeArquivo(Integer codigo) throws FalhaAcessoAosDadosException;

	public List<Arquivo> buscaTitulo(String titulo) throws FalhaAcessoAosDadosException;



}