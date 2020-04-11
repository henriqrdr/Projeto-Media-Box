package mediabox.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mediabox.banco.PostgresDAOFactory;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ArquivoDAO;
import mediabox.interfaces.DAOFactory;
import mediabox.interfaces.ListaDeMusicasDAO;
import mediabox.interfaces.PlaylistsDAO;
import mediabox.interfaces.UsuarioDAO;
import mediabox.model.Arquivo;
import mediabox.model.ListaDeMusicas;
import mediabox.model.Playlists;

public class PlayListScreen extends JFrame implements ActionListener {

	private JTable tabelaDeArquivos, tabelaDePlaylist;
	private JScrollPane scroll, scroll2;
	private ArquivoTableModel model, model2;
	private JPanel fundo, botoes, lista, tLista, banco, pesquisar;
	private JButton bAdicionar, bExcluir, bSalvar, bExcluirPl, bCriarPl, bBuscar, bTocar, bZerarPl;
	private JLabel lSelecionaPl, ltBanco;
	private JComboBox<String> cbListas;
	private JTextField vBusca;

	private static Integer indice;
	private static Integer indicePl;
	private List<Arquivo> novaPlx = new ArrayList<Arquivo>();
	private List<Playlists> plscriadas = new ArrayList<Playlists>();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bCriarPl) {
			String nome = JOptionPane.showInputDialog("Digite o nome de sua nova playlist:");
			if (nome != null) {
				criaNovaPl(nome);
			}
		} else if (e.getSource() == bBuscar) {
			String busca = vBusca.getText();
			if (!busca.isEmpty()) {
				try {
					buscaLista();
				} catch (FalhaAcessoAosDadosException e1) {
					e1.printStackTrace();
				}
			} else {
				try {
					atualizaLista();
				} catch (FalhaAcessoAosDadosException e1) {
					e1.printStackTrace();
				}
			}
			busca = null;
		} else if (e.getSource() == bAdicionar) {
			try {
				adicionaMusicaLista();
			} catch (FalhaAcessoAosDadosException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == bExcluir) {
			try {
				deletaMusicaLista();
			} catch (FalhaAcessoAosDadosException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == bSalvar) {
			salvarPl();

		} else if (e.getSource() == bExcluirPl) {
			excluirPl();
		} else if (e.getSource() == bZerarPl) {
			zerarPl();
		} else if (e.getSource() == bTocar) {
			tocarPl();
		}

	};

	public void executar() {
		this.setTitle("Playlist Editor");
		this.setSize(1280, 720);
		// botoes
		ImageIcon lupa = new ImageIcon(getClass().getResource("/imagem/lupa.png"));
		this.bBuscar = new JButton("Pesquisar", lupa);
		this.bBuscar.addActionListener(this);

		this.bAdicionar = new JButton("Adicionar música");
		this.bAdicionar.addActionListener(this);
		this.bExcluir = new JButton("Excluir música");
		this.bExcluir.addActionListener(this);
		this.bSalvar = new JButton("Salvar PlayList");
		this.bSalvar.addActionListener(this);
		this.bExcluirPl = new JButton("Excluir PlayList");
		this.bExcluirPl.addActionListener(this);
		this.bExcluirPl.setEnabled(false);
		this.bCriarPl = new JButton("Criar nova Playlist");
		this.bCriarPl.addActionListener(this);
		this.bTocar = new JButton("Tocar Playlist");
		this.bTocar.addActionListener(this);
		this.bTocar.setEnabled(false);
	
		this.bZerarPl = new JButton("Zerar Playlist");
		this.bZerarPl.addActionListener(this);

		this.bAdicionar.setEnabled(false);
		this.bExcluir.setEnabled(false);
		this.bSalvar.setEnabled(false);

		this.banco = new JPanel(new BorderLayout());
		this.fundo = new JPanel(new BorderLayout());
		this.lista = new JPanel(new BorderLayout());
		this.tLista = new JPanel(new FlowLayout());
		this.botoes = new JPanel(new FlowLayout());
		this.pesquisar = new JPanel(new GridLayout(1, 3));

		this.vBusca = new JTextField();

		this.botoes.add(bAdicionar);
		this.botoes.add(bExcluir);
		this.botoes.add(bSalvar);
		this.botoes.add(bCriarPl);

	
		this.botoes.add(bZerarPl);

		this.lSelecionaPl = new JLabel("Seleciona PlayList ");
		this.ltBanco = new JLabel("Música disponíveis no banco \n");

		this.pesquisar.add(ltBanco);
		this.pesquisar.add(vBusca);
		this.pesquisar.add(bBuscar);

		this.banco.add(pesquisar, BorderLayout.NORTH);

		this.tLista.add(lSelecionaPl);
		this.cbListas = new JComboBox();
		inicializaPls();
		this.tLista.add(cbListas);
		this.tLista.add(bTocar);
		this.tLista.add(bExcluirPl);

		this.model = new ArquivoTableModel();
		this.tabelaDeArquivos = new JTable(model);

		this.model2 = new ArquivoTableModel();

		this.tabelaDePlaylist = new JTable(model2);

		this.tabelaDePlaylist.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int index = tabelaDePlaylist.getSelectedRow();
				if (index >= 0) {
					Arquivo arquivo = model2.getArquivo(index);
				//	System.out.println(arquivo);
					bAdicionar.setEnabled(false);
					bExcluir.setEnabled(true);
					indice = index;

				}
			}
		});

		this.scroll = new JScrollPane(this.tabelaDeArquivos);
		this.tabelaDeArquivos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int index = tabelaDeArquivos.getSelectedRow();
				if (index >= 0) {
					Arquivo arquivo = model.getArquivo(index);
					// System.out.println(arquivo);
					bAdicionar.setEnabled(true);
					bExcluir.setEnabled(false);
					indice = index;

				}
			}
		});

		this.scroll2 = new JScrollPane(this.tabelaDePlaylist);

		this.banco.add(scroll, BorderLayout.CENTER);

		this.lista.add(tLista, BorderLayout.NORTH);
		this.lista.add(scroll2, BorderLayout.CENTER);

		this.fundo.add(banco, BorderLayout.NORTH);
		this.fundo.add(lista, BorderLayout.CENTER);
		this.fundo.add(botoes, BorderLayout.SOUTH);
		this.getContentPane().add(fundo);
		this.setVisible(true);
		try {
			atualizaLista();
		} catch (FalhaAcessoAosDadosException e1) {
			e1.printStackTrace();
		}
		// Menu
	}

	private void zerarPl() {
		novaPlx.clear();
		this.model2.clear();
		this.model2.setArquivos(novaPlx);
		this.bTocar.setEnabled(false);
		this.bExcluirPl.setEnabled(false);
		this.bSalvar.setEnabled(false);
		indicePl = null;
	}

	private void criaNovaPl(String nome) {
		Integer id_playlist = null;
		if (!nome.equals("")) {
			Playlists nPl = new Playlists();
			nPl.setNome(nome);
			nPl.setId_user(LoginScreen.userID);
			DAOFactory factory1 = new PostgresDAOFactory();
			PlaylistsDAO dao1 = factory1.getPlaylistsDAO();
			try {
				id_playlist = dao1.inserePlaylist(nPl);
			//	System.out.println(id_playlist);
			} catch (FalhaAcessoAosDadosException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			List<ListaDeMusicas> nLM = new ArrayList<ListaDeMusicas>();
			for (Arquivo arquivo : novaPlx) {
				// cria lista de musicas a partir da playlist
				ListaDeMusicas song = new ListaDeMusicas();
				song.setId_song(arquivo.getId());
				song.setId_playlist(id_playlist);
				nLM.add(song);
			}
			for (ListaDeMusicas song : nLM) {
		//		System.out.println("ID da playlist = " + song.getId_playlist());
			//	System.out.println("ID do som = " + song.getId_song());
			}
			for (ListaDeMusicas song : nLM) {
				// grava lista de musicas no banco
				DAOFactory factory = new PostgresDAOFactory();
				ListaDeMusicasDAO dao = factory.getListaDeMusicasDAO();
				try {
					dao.insereListaDeMusicas(song);
				} catch (FalhaAcessoAosDadosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		inicializaPls();
	}

	private void salvarPl() {
		String nome = null;
		if (indicePl != null) {
			DAOFactory factory1 = new PostgresDAOFactory();
			PlaylistsDAO dao1 = factory1.getPlaylistsDAO();
			Playlists pls;
			try {
				pls = dao1.getPlaylistPorCodigo(indicePl);
				nome = pls.getNome();
				//System.out.println("nome da pl ser alterada  = " + nome + "cujo id = " + indicePl);
			} catch (FalhaAcessoAosDadosException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (indicePl != null) {
			//	System.out.println("Excluindo pl id = " + indicePl);
				ListaDeMusicasDAO dao = factory1.getListaDeMusicasDAO();
				try {
					dao.removeListaDeMusicas(indicePl);
				} catch (FalhaAcessoAosDadosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					dao1.removePlaylist(indicePl);
				} catch (FalhaAcessoAosDadosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			criaNovaPl(nome);
		}
		System.out.println(nome);
		inicializaPls();
	}

	private void tocarPl() {
		PlayListPlayerScreen hs;
		try {
			hs = new PlayListPlayerScreen(novaPlx);
			hs.setVisible(true);
		} catch (FalhaAcessoAosDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private void excluirPl() {
		if (indicePl != null) {

			DAOFactory factory1 = new PostgresDAOFactory();
			PlaylistsDAO dao1 = factory1.getPlaylistsDAO();
			// indicePl = 14;
			//System.out.println("Excluindo pl id = " + indicePl);
			ListaDeMusicasDAO dao = factory1.getListaDeMusicasDAO();
			try {
				dao.removeListaDeMusicas(indicePl);
			} catch (FalhaAcessoAosDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				dao1.removePlaylist(indicePl);
			} catch (FalhaAcessoAosDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		inicializaPls();

	}

	private void adicionaMusicaLista() throws FalhaAcessoAosDadosException {

		// System.out.println(indice);
		Arquivo arquivo = model.getArquivo(indice);
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		Arquivo musica = dao.getArquivoPorCodigo(arquivo.getId());
		novaPlx.add(musica);
		// System.out.println(musica.getId());
		this.model2.setArquivos(novaPlx);
	}

	private void deletaMusicaLista() throws FalhaAcessoAosDadosException {
	//	System.out.println(indice);
		Arquivo arquivo = model2.getArquivo(indice);
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		Arquivo musica = dao.getArquivoPorCodigo(arquivo.getId());
		try {
			this.novaPlx.remove(musica);
			this.model2.removeArquivo(musica);
			this.model2.setArquivos(novaPlx);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private void inicializaPls() {
		cbListas.removeAllItems();
		plscriadas.clear();
		DAOFactory factory1 = new PostgresDAOFactory();
		PlaylistsDAO dao1 = factory1.getPlaylistsDAO();
		try {
			plscriadas = dao1.getPlayListsPorCod(LoginScreen.userID);
		} catch (FalhaAcessoAosDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Playlists pls : plscriadas) {
			cbListas.addItem(pls.getNome());
		}
		cbListas.addItemListener(new ItemChangeListener() {

			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					// System.out.println(cbListas.getSelectedItem().toString());
					int indice = cbListas.getSelectedIndex();
					getSelectedView(indice);

				}
			}
		});

	}

	private void getSelectedView(int indice) {
		List<Playlists> alllists = new ArrayList<Playlists>();
		DAOFactory factory1 = new PostgresDAOFactory();
		PlaylistsDAO dao1 = factory1.getPlaylistsDAO();
		try {
			alllists = dao1.getPlayListsPorCod(LoginScreen.userID);
		} catch (FalhaAcessoAosDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Playlists selectedPl = alllists.get(indice);
		indicePl = selectedPl.getId();
	//	System.out.println("ID DA LISTA SELECIONADA = " + indicePl);
		bExcluirPl.setEnabled(true);
		bTocar.setEnabled(true);
		getSelectedPlaylist(selectedPl.getId());

	}

	private void getSelectedPlaylist(int selected) {
		List<ListaDeMusicas> songs = new ArrayList<ListaDeMusicas>();
		DAOFactory factory1 = new PostgresDAOFactory();
		ListaDeMusicasDAO dao1 = factory1.getListaDeMusicasDAO();
		try {
			songs = dao1.buscaTodosPorCodigo(selected);
		} catch (FalhaAcessoAosDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			carregaPlaylist(songs);
		} catch (FalhaAcessoAosDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void carregaPlaylist(List<ListaDeMusicas> lista) throws FalhaAcessoAosDadosException {
		List<Arquivo> plcarregada = new ArrayList<Arquivo>();
		for (ListaDeMusicas song : lista) {
			Arquivo music = new Arquivo();
			DAOFactory factory = new PostgresDAOFactory();
			ArquivoDAO dao = factory.getArquivoDAO();
			try {
				music = dao.getArquivoPorCodigo(song.getId_song());
			} catch (FalhaAcessoAosDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plcarregada.add(music);
		}

		atualizaListaPl(plcarregada);
		bSalvar.setEnabled(true);
	}

	private void atualizaListaPl(List<Arquivo> lista) {
		this.model2.clear();
		this.model2.setArquivos(lista);
		novaPlx = lista;
	}

	private void atualizaLista() throws FalhaAcessoAosDadosException {
		this.model.clear();
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		List<Arquivo> arquivos = dao.buscaTodos();
		this.model.setArquivos(arquivos);
	}

	private void buscaLista() throws FalhaAcessoAosDadosException {
		this.model.clear();
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		List<Arquivo> arquivos = dao.buscaTitulo(vBusca.getText());
		this.model.setArquivos(arquivos);
	}

	public static void main(String[] args) {

		PlayListScreen p = new PlayListScreen();
		p.executar();
	}
}
