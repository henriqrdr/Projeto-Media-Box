package mediabox.interfaces;

import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.model.ListaDeMusicas;

public interface ListaDeMusicasDAO {

	int removeListaDeMusicas(Integer id) throws FalhaAcessoAosDadosException;

	int apagaTodos() throws FalhaAcessoAosDadosException;

	int alteraListaDeMusicas(ListaDeMusicas listademusicas) throws FalhaAcessoAosDadosException;

	int insereListaDeMusicas(ListaDeMusicas listademusicas) throws FalhaAcessoAosDadosException;
	
	public ListaDeMusicas getListaDeMusicasPorCodigo(int cod) throws FalhaAcessoAosDadosException;
	
	public List<ListaDeMusicas> buscaTodos() throws FalhaAcessoAosDadosException ;
	
	public List<ListaDeMusicas> buscaTodosPorCodigo(int cod) throws FalhaAcessoAosDadosException;

}
