package mediabox.model;

public class Playlists {

	@Override
	public String toString() {
		return "Playlists [id=" + id + ", id_user=" + id_user + ", nome=" + nome + "]";
	}

	private Integer id;
	private Integer id_user;
	private String nome;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_user() {
		return id_user;
	}

	public void setId_user(Integer id_user) {
		this.id_user = id_user;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
