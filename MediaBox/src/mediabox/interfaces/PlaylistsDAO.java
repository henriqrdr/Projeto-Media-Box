package mediabox.interfaces;

import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;

import mediabox.model.Playlists;

public interface PlaylistsDAO {

	public Playlists getPlaylistPorCodigo(int cod) throws FalhaAcessoAosDadosException;

	public List<Playlists> buscaTodos() throws FalhaAcessoAosDadosException;

	int inserePlaylist(Playlists playlists) throws FalhaAcessoAosDadosException;

	int alteraPlaylist(Playlists playlists) throws FalhaAcessoAosDadosException;

	int apagaTodos() throws FalhaAcessoAosDadosException;

	int removePlaylist(Integer id) throws FalhaAcessoAosDadosException;

	public List<Playlists> getPlayListsPorCod(int cod) throws FalhaAcessoAosDadosException;
}
