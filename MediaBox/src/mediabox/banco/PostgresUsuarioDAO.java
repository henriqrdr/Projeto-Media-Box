package mediabox.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.UsuarioDAO;
import mediabox.model.Usuario;

public class PostgresUsuarioDAO implements UsuarioDAO{
private Connection conn;
	

	PostgresUsuarioDAO(Connection conn) {
		this.conn = conn;
	}
	

	@Override
	public Usuario getUsuarioPorCodigo(int cod) throws FalhaAcessoAosDadosException {
		//System.out.println("Buscando o Usuario com o id = " + cod);

		Usuario usuario = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("select id, nome, usuario, senha from usuario where id = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				usuario = new Usuario();
				usuario.setId(rs.getInt("id"));
				usuario.setNome(rs.getString("nome"));
				usuario.setUser(rs.getString("usuario"));
				usuario.setSenha(rs.getString("senha"));
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return usuario;
	}

	@Override
	public List<Usuario> buscaTodos() throws FalhaAcessoAosDadosException {

		List<Usuario> usuarios = new ArrayList<Usuario>();
		Statement stmt = null;
		ResultSet rs = null;

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery("select id, nome, usuario, senha, from usuario");

			while (rs.next()) {
				Usuario usuario = new Usuario();
				usuario.setId(rs.getInt("id"));
				usuario.setNome((rs.getString("nome")));
				usuario.setUser(rs.getString("usuario"));
			//	Usuario.setSenha(rs.getString("senha"));
				usuarios.add(usuario);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaStatement(stmt);
		}

		return usuarios;
	}

	@Override
	public int insereUsuario(Usuario usuario) throws FalhaAcessoAosDadosException {
		System.out.println("Inserindo Usuario " + usuario);
		Integer id = null;
		if (usuario != null) {

			PreparedStatement pstmt = null;
			try {
					pstmt = conn.prepareStatement(
							"insert into usuario(id, nome, usuario, senha)"
					+ "values (nextval('usuario_seq'), ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
					
					pstmt.setString(1, usuario.getNome());
					pstmt.setString(2, usuario.getUser());
					pstmt.setString(3, usuario.getSenha());
					
					
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
	public int alteraUsuario(Usuario Usuario) throws FalhaAcessoAosDadosException {
		System.out.println("Alterando Usuario " + Usuario);

		int alterou;

		if (Usuario != null) {
			alterou = Usuario.getId();

			PreparedStatement pstmt = null;

			try {

				pstmt = conn.prepareStatement(
						"update usuario set user = ?, senha = ?, where id =  ?");

				pstmt.setString(1, Usuario.getUser());
				pstmt.setString(2, Usuario.getSenha());
				pstmt.setInt(6, Usuario.getId());

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
			retorno = stmt.executeUpdate("delete from usuario");

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaStatement(stmt);
		}

		return retorno;

	}

	@Override
	public int removeUsuario(Integer id) throws FalhaAcessoAosDadosException {

		System.out.println("Removendo o produto com código = " + id);

		int apagou = 0;

		if (id != null) {

			PreparedStatement pstmt = null;

			try {
				pstmt = conn.prepareStatement("delete from usuario where id =  ?");
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

	private void fechaPreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	@Override
	public boolean checkLogin(String user, String senha) throws FalhaAcessoAosDadosException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean check  = false;
		try {
			pstmt = conn.prepareStatement("select * from usuario where usuario = ? and senha = ?");
			pstmt.setString(1, user);
			pstmt.setString(2, senha);
			rs = pstmt.executeQuery();
			 if(rs.next()) {
				 check = true;
			 }

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}
		return check;
	}


	@Override
	public boolean checkUser(String user) throws FalhaAcessoAosDadosException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean check  = false;
		try {
			pstmt = conn.prepareStatement("select * from usuario where usuario = ?");
			pstmt.setString(1, user);
			rs = pstmt.executeQuery();
			 if(rs.next()) {
				 check = true;
			 }

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}
		return check;
	}
	public int checkUserRetornoId(String user) throws FalhaAcessoAosDadosException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer id = null;
		try {
			pstmt = conn.prepareStatement("select * from usuario where usuario = ?");
			pstmt.setString(1, user);
			rs = pstmt.executeQuery();
			 if(rs.next()) {
				 id = rs.getInt("id");
			 }

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}
		return id;
	}
	

}
