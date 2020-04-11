package mediabox.interfaces;

import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.model.Usuario;

public interface UsuarioDAO {
	public abstract Usuario getUsuarioPorCodigo(int cod) throws FalhaAcessoAosDadosException;

	public abstract List<Usuario> buscaTodos() throws FalhaAcessoAosDadosException;

	public abstract int insereUsuario(Usuario Usuario) throws FalhaAcessoAosDadosException;

	public abstract boolean checkLogin(String user, String senha) throws FalhaAcessoAosDadosException;

	public abstract boolean checkUser(String user) throws FalhaAcessoAosDadosException;

	public abstract int alteraUsuario(Usuario Usuario) throws FalhaAcessoAosDadosException;

	public abstract int apagaTodos() throws FalhaAcessoAosDadosException;

	public abstract int removeUsuario(Integer codigo) throws FalhaAcessoAosDadosException;

	public int checkUserRetornoId(String user) throws FalhaAcessoAosDadosException;

}
