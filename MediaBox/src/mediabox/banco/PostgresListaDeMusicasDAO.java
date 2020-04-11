package mediabox.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ListaDeMusicasDAO;
import mediabox.model.ListaDeMusicas;

public class PostgresListaDeMusicasDAO implements ListaDeMusicasDAO {
	private Connection conn;

	PostgresListaDeMusicasDAO(Connection conn) {
		this.conn = conn;
	}

	public ListaDeMusicas getListaDeMusicasPorCodigo(int cod) throws FalhaAcessoAosDadosException {
		System.out.println("Buscando o Playlist com o id = " + cod);

		ListaDeMusicas listademusica = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("select id_playlist, id_song, from listademusicas where id = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				listademusica = new ListaDeMusicas();
				listademusica.setId_playlist(rs.getInt("id_playlist"));
				listademusica.setId_song(rs.getInt("id_song"));

			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return listademusica;
	}

	public List<ListaDeMusicas> buscaTodosPorCodigo(int cod) throws FalhaAcessoAosDadosException {
		List<ListaDeMusicas> listademusicas = new ArrayList<ListaDeMusicas>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("select id_playlist, id_song from listademusicas where id_playlist = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ListaDeMusicas listademusica = new ListaDeMusicas();
				listademusica.setId_playlist(rs.getInt("id_playlist"));
				listademusica.setId_song(rs.getInt("id_song"));
				listademusicas.add(listademusica);

			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return listademusicas;
	}

	public List<ListaDeMusicas> buscaTodos() throws FalhaAcessoAosDadosException {
		List<ListaDeMusicas> listasdemusicas = new ArrayList<ListaDeMusicas>();
		Statement stmt = null;
		ResultSet rs = null;

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery("select id_playlist, id_song, from listasdemusica");

			while (rs.next()) {
				ListaDeMusicas listademusica = new ListaDeMusicas();
				listademusica.setId_playlist(rs.getInt("id_playlist"));
				listademusica.setId_song(rs.getInt("id_song"));
				listasdemusicas.add(listademusica);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaStatement(stmt);
		}

		return listasdemusicas;
	}

	@Override
	public int insereListaDeMusicas(ListaDeMusicas listademusica) throws FalhaAcessoAosDadosException {

		Integer id = null;
		if (listademusica != null) {

			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("insert into listademusicas(id, id_playlist, id_song)"
						+ "values (nextval('listademusicas_seq'), ?, ?)", Statement.RETURN_GENERATED_KEYS);

				pstmt.setInt(1, listademusica.getId_playlist());
				pstmt.setInt(2, listademusica.getId_song());
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
	public int alteraListaDeMusicas(ListaDeMusicas listademusicas) throws FalhaAcessoAosDadosException {

		int alterou;

		if (listademusicas != null) {
			alterou = listademusicas.getId_playlist();

			PreparedStatement pstmt = null;

			try {

				pstmt = conn.prepareStatement(
						"update listademusicas set id_playlist = ?, id_song = ?, where id_playlist =  ?");

				pstmt.setInt(1, listademusicas.getId_playlist());
				pstmt.setInt(2, listademusicas.getId_song());
				pstmt.setInt(3, listademusicas.getId_playlist());

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
			retorno = stmt.executeUpdate("delete from listademusicas");

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaStatement(stmt);
		}

		return retorno;
	}

	@Override
	public int removeListaDeMusicas(Integer id) throws FalhaAcessoAosDadosException {

		int apagou = 0;

		if (id != null) {

			PreparedStatement pstmt = null;

			try {
				pstmt = conn.prepareStatement("delete from listademusicas where id_playlist =  ?");
				pstmt.setInt(1, id);
				pstmt.execute();
				apagou = id;
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
