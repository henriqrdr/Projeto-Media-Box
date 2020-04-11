package mediabox.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.PlaylistsDAO;
import mediabox.model.Playlists;

public class PostgresPlaylistDAO implements PlaylistsDAO {
	private Connection conn;

	PostgresPlaylistDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Playlists getPlaylistPorCodigo(int cod) throws FalhaAcessoAosDadosException {
		System.out.println("Buscando o Playlist com o id = " + cod);

		Playlists playlist = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("select id, id_user, nome from playlists where id = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				playlist = new Playlists();
				playlist.setId(rs.getInt("id"));
				playlist.setId_user(rs.getInt("id_user"));
				playlist.setNome(rs.getString("nome"));

			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return playlist;
	}
	
	public List<Playlists> getPlayListsPorCod(int cod) throws FalhaAcessoAosDadosException {


		List<Playlists> playlists = new ArrayList<Playlists>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("select id, id_user, nome from playlists where id_user = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Playlists playlist = new Playlists();
				playlist.setId(rs.getInt("id"));
				playlist.setId_user(rs.getInt("id_user"));
				playlist.setNome(rs.getString("nome"));
				playlists.add(playlist);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return playlists;
	}

	@Override
	public List<Playlists> buscaTodos() throws FalhaAcessoAosDadosException {
		List<Playlists> playlists = new ArrayList<Playlists>();
		Statement stmt = null;
		ResultSet rs = null;

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery("select id, id_user, nome from playlists");

			while (rs.next()) {
				Playlists playlist = new Playlists();
				playlist.setId(rs.getInt("id"));
				playlist.setId_user(rs.getInt("id_user"));
				playlist.setNome((rs.getString("nome")));
				System.out.println(playlist);
				playlists.add(playlist);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaStatement(stmt);
		}

		return playlists;
	}
	

	@Override
	public int inserePlaylist(Playlists playlist) throws FalhaAcessoAosDadosException {

		Integer id = null;
		if (playlist != null) {

			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("insert into playlists(id, id_user, nome)"
						+ "values (nextval('playlist_seq'), ?, ?)", Statement.RETURN_GENERATED_KEYS);

				
				pstmt.setInt(1, playlist.getId_user());
				pstmt.setString(2, playlist.getNome());
				int affectedRows = pstmt.executeUpdate();

				if (affectedRows == 0) {
					System.out.println("Falha ao inserir dados.");
				}

				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					id = generatedKeys.getInt(1);
					System.out.println("Inserido o registro com id = " + id);
				} else {
					System.out.println("O insert falhou, não consegui o id");
				}

			} catch (SQLException e) {
				throw new FalhaAcessoAosDadosException("Falha ao inserir registro", e);
			} finally {
				this.fechaPreparedStatement(pstmt);
			}
		} else {
			throw new FalhaAcessoAosDadosException("Registro nulo");
		}
		return id;

	}

	@Override
	public int alteraPlaylist(Playlists playlist) throws FalhaAcessoAosDadosException {

		int alterou;

		if (playlist != null) {
			alterou = playlist.getId();

			PreparedStatement pstmt = null;

			try {

				pstmt = conn.prepareStatement("update playlists set nome = ?, id_user = ?, where id =  ?");

				pstmt.setString(1, playlist.getNome());
				pstmt.setInt(2, playlist.getId_user());
				pstmt.setInt(3, playlist.getId());

				alterou = pstmt.executeUpdate();

			} catch (SQLException e) {
				throw new FalhaAcessoAosDadosException("Falha ao alterar registro", e);
			} finally {
				this.fechaPreparedStatement(pstmt);
			}
		} else {
			throw new FalhaAcessoAosDadosException("Registro nulo");
		}
		return alterou;
	}

	@Override
	public int apagaTodos() throws FalhaAcessoAosDadosException {
		Statement stmt = null;
		int retorno = 0;

		try {

			stmt = conn.createStatement();
			retorno = stmt.executeUpdate("delete from playlists");

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaStatement(stmt);
		}

		return retorno;
	}

	@Override
	public int removePlaylist(Integer id) throws FalhaAcessoAosDadosException {

		int apagou = 0;

		if (id != null) {

			PreparedStatement pstmt = null;

			try {
				pstmt = conn.prepareStatement("delete from playlists where id = ?");
				pstmt.setInt(1, id);
				pstmt.execute();
				apagou = id;
				System.out.println("apagou");
			} catch (SQLException e) {
				throw new FalhaAcessoAosDadosException("Falha removendo o registro", e);
			} finally {	
				fechaPreparedStatement(pstmt);
			}
		} else {
			throw new FalhaAcessoAosDadosException("ID nulo");
		}
		return apagou;

	}

	private void fechaPreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void fechaResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void fechaStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
