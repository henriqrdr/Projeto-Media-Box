package mediabox.interfaces;

public abstract class DAOFactory {

	public abstract ArquivoDAO getArquivoDAO();
	
	public abstract UsuarioDAO getUsuarioDAO();
	
	public abstract PlaylistsDAO getPlaylistsDAO();
	
	public abstract ListaDeMusicasDAO getListaDeMusicasDAO();
	
	public void closeConnection() {
		
	}
	
}
