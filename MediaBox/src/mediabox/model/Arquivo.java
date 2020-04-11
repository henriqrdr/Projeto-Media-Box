package mediabox.model;

public class Arquivo  {
	
	private Integer id;
	private String faixa;
	private String titulo;
	private String artista;
	private String album;
	private String ano;
	private byte[] conteudo;
	
	
	public Arquivo(Integer id, String titulo, String faixa, String artista, String album, String ano) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.faixa = faixa;
		this.artista = artista;
		this.album = album;
		this.ano = ano;
	}

	public Arquivo() {
		super();
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getFaixa() {
		return faixa;
	}

	public void setFaixa(String faixa) {
		this.faixa = faixa;
	}

	public String getArtista() {
		return artista;
	}

	public void setArtista(String artista) {
		this.artista = artista;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}
	public byte[] getConteudo() {
		return conteudo;
	}

	public void setConteudo(byte[] conteudo) {
		this.conteudo = conteudo;
	}

	
	@Override
	public String toString() {
		return "Arquivo [id=" + id + "titulo = "+titulo+", faixa=" + faixa + ", artista=" + artista + ", album=" + album + ", ano=" + ano
				+ "]";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arquivo other = (Arquivo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
