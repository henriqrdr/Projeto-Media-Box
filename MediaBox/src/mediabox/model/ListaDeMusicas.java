package mediabox.model;

public class ListaDeMusicas {
	private Integer id_playlist;
	private Integer id_song;

	public Integer getId_playlist() {
		return id_playlist;
	}

	public void setId_playlist(Integer id_playlist) {
		this.id_playlist = id_playlist;
	}

	@Override
	public String toString() {
		return "ListaDeMusicas [id_playlist=" + id_playlist + ", id_song=" + id_song + "]";
	}

	public Integer getId_song() {
		return id_song;
	}

	public void setId_song(Integer id_song) {
		this.id_song = id_song;
	}

}
