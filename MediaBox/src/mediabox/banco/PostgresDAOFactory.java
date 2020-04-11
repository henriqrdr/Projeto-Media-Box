package mediabox.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mediabox.interfaces.DAOFactory;
import mediabox.interfaces.ListaDeMusicasDAO;
import mediabox.interfaces.PlaylistsDAO;
import mediabox.interfaces.UsuarioDAO;
import mediabox.interfaces.ArquivoDAO;

public class PostgresDAOFactory extends DAOFactory {

	private Connection conn;

	public PostgresDAOFactory() {
		this.conn = this.getConnection("org.postgresql.Driver",
				"jdbc:postgresql://localhost:5432/MediaBox", "postgres", "ucs");
	}

	@Override
	public void closeConnection() {
		if(this.conn!=null) {
			try {
				this.conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private Connection getConnection(String driver, String url, String user,
			String pwd) {

		Connection conn = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Não foi possível encontrar o driver JDBC");
		} catch (SQLException se) {
		}

		return conn;
	}

	@Override
	public ArquivoDAO getArquivoDAO() {
		return new PostgresArquivoDAO2(conn);
	}
	@Override
	public UsuarioDAO getUsuarioDAO() {
		return new PostgresUsuarioDAO(conn);
	}
	public ListaDeMusicasDAO getListaDeMusicasDAO() {
		return new PostgresListaDeMusicasDAO(conn);
	}
	public PlaylistsDAO getPlaylistsDAO() {
		return new PostgresPlaylistDAO(conn);
	}
	
}
