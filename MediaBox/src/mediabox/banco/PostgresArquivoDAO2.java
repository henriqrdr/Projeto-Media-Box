package mediabox.banco;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ArquivoDAO;
import mediabox.model.Arquivo;

public class PostgresArquivoDAO2 implements ArquivoDAO {
	private Connection conn;

	PostgresArquivoDAO2(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Arquivo getArquivoPorCodigo(int cod) throws FalhaAcessoAosDadosException {
//		System.out.println("Buscando o arquivo com o id = " + cod);

		Arquivo arquivo = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(
					"select id, titulo, faixa, artista, album, ano, conteudo from arquivo where id = ?");
			pstmt.setInt(1, cod);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				arquivo = new Arquivo();
				arquivo.setId(rs.getInt("id"));
				arquivo.setTitulo(rs.getString("titulo"));
				arquivo.setFaixa(rs.getString("faixa"));
				arquivo.setArtista(rs.getString("artista"));
				arquivo.setAlbum(rs.getString("album"));
				arquivo.setAno(rs.getString("ano"));
				arquivo.setConteudo(rs.getBytes("conteudo"));
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}

		return arquivo;
	}

	public List<Arquivo> buscaTitulo(String titulo) throws FalhaAcessoAosDadosException {
		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			pstmt = conn.prepareStatement("select id, titulo, faixa, artista, album, ano, conteudo from arquivo "
					+ "where upper(titulo) like ? or upper(artista)like ? or upper(album) like ? ");
			pstmt.setString(1, "%" + titulo.toUpperCase() + "%");
			pstmt.setString(2, "%" + titulo.toUpperCase() + "%");
			pstmt.setString(3, "%" + titulo.toUpperCase() + "%");
			rs = pstmt.executeQuery();

			while (rs.next()) {

				Arquivo arquivo = new Arquivo();
				arquivo.setId(rs.getInt("id"));
				arquivo.setTitulo(rs.getString("titulo"));
				arquivo.setFaixa(rs.getString("faixa"));
				arquivo.setArtista(rs.getString("artista"));
				arquivo.setAlbum(rs.getString("album"));
				arquivo.setAno(rs.getString("ano"));
				arquivo.setConteudo(rs.getBytes("conteudo"));
				arquivos.add(arquivo);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando o registro por código", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaPreparedStatement(pstmt);
		}
		return arquivos;
	}

	@Override
	public List<Arquivo> buscaTodos() throws FalhaAcessoAosDadosException {

		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		Statement stmt = null;
		ResultSet rs = null;

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery("select id, titulo, faixa, artista, album, ano from arquivo");

			while (rs.next()) {
				Arquivo arquivo = new Arquivo();
				arquivo.setId(rs.getInt("id"));
				arquivo.setTitulo((rs.getString("titulo")));
				arquivo.setFaixa(rs.getString("faixa"));
				arquivo.setArtista(rs.getString("artista"));
				arquivo.setAlbum(rs.getString("album"));
				arquivo.setAno(rs.getString("ano"));
				arquivos.add(arquivo);
			}

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaResultSet(rs);
			this.fechaStatement(stmt);
		}

		return arquivos;
	}

	@Override
	public int insereArquivo(File arquivo) throws FalhaAcessoAosDadosException {
		System.out.println("Inserindo Arquivo " + arquivo);
		// String artista = null, titulo = null, ano = null, faixa = null, album = null;
		Integer id = null;
		byte[] conteudo;
		Long tamanho;
		if (arquivo != null) {

			PreparedStatement pstmt = null;
			try {

				MP3File song = new MP3File(arquivo);
				conteudo = Files.readAllBytes(arquivo.toPath());
				tamanho = arquivo.length();
				InputStream is = new ByteArrayInputStream(conteudo);
				if (song.hasID3v2Tag()) {
					AbstractID3v2 tag = song.getID3v2Tag();

					pstmt = conn.prepareStatement(
							"insert into arquivo(id, titulo, faixa, artista, album, ano, conteudo)"
									+ "values (nextval('arquivo_seq'), ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);

					pstmt.setString(1, tag.getSongTitle());
					pstmt.setString(2, tag.getTrackNumberOnAlbum());
					pstmt.setString(3, tag.getLeadArtist());
					pstmt.setString(4, tag.getAlbumTitle());
					pstmt.setString(5, tag.getYearReleased());
					pstmt.setBinaryStream(6, is, tamanho);

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
				}
			} catch (SQLException e) {
				throw new FalhaAcessoAosDadosException("Falha ao inserir registro", e);
			} catch (TagException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				this.fechaPreparedStatement(pstmt);
			}
		} else {
			throw new FalhaAcessoAosDadosException("Registro nulo");
		}
		return id;
	}

	@Override
	public int alteraArquivo(Arquivo arquivo) throws FalhaAcessoAosDadosException {
		System.out.println("Alterando Arquivo " + arquivo);

		int alterou;

		if (arquivo != null) {
			alterou = arquivo.getId();

			PreparedStatement pstmt = null;

			try {

				pstmt = conn.prepareStatement(
						"update arquivo set faixa = ?, titulo = ?, artista = ?, album = ?, ano = +, where id =  ?");

				pstmt.setString(1, arquivo.getFaixa());
				pstmt.setString(2, arquivo.getTitulo());
				pstmt.setString(3, arquivo.getArtista());
				pstmt.setString(4, arquivo.getAlbum());
				pstmt.setString(5, arquivo.getAno());
				pstmt.setInt(6, arquivo.getId());

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
			retorno = stmt.executeUpdate("delete from arquivo");

		} catch (SQLException e) {
			throw new FalhaAcessoAosDadosException("Falha buscando todos os registros", e);
		} finally {
			this.fechaStatement(stmt);
		}

		return retorno;

	}

	@Override
	public int removeArquivo(Integer id) throws FalhaAcessoAosDadosException {

		System.out.println("Removendo o produto com código = " + id);

		int apagou = 0;

		if (id != null) {

			PreparedStatement pstmt = null;

			try {
				pstmt = conn.prepareStatement("delete from arquivo where id =  ?");
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

}
