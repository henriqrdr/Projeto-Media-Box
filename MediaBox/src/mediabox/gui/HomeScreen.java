package mediabox.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import javazoom.jl.decoder.JavaLayerException;
import mediabox.banco.PostgresDAOFactory;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ArquivoDAO;
import mediabox.interfaces.DAOFactory;
import mediabox.model.Arquivo;
import mediabox.model.Usuario;
import playermp3thread.PausablePlayer;

public class HomeScreen extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenu mArquivo, mPlayList, mConfiguracao, mAjuda;
	private JMenuItem miAbrir, miImportar, miSair,miPl;
	private JPanel topo, fundo, botoes, menulateral, pesquisa;
	private JButton bIniciar, bPausar, bParar, bBuscar;
	private JLabel songName, lTopo;
//	private JProgressBar jprogressBar;
	private JTextField vBusca;

	private File arquivo;
	private boolean paused = true;
	private PausablePlayer player;

	private JTable tabelaDeArquivos;
	private JScrollPane scroll;
	private ArquivoTableModel model;

	InputStream musica;

	private static final String tituloApp = "MediaBox";
	DAOFactory factory = new PostgresDAOFactory();

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(miAbrir)) {
			this.abrir();
		} else if (e.getSource().equals(miImportar)) {
			this.importar();
		} else if (e.getSource().equals(miSair)) {
			System.exit(0);
		} else if (e.getSource().equals(bIniciar)) {
			this.iniciar(musica);
		} else if (e.getSource().equals(bParar)) {
			this.parar();
		} else if (e.getSource().equals(bPausar)) {
			this.pausar();
		} else if (e.getSource().equals(miPl)) {
			this.playLists();
		} else if (e.getSource().equals(bBuscar)) {
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
		}

	}

	private void abrir() {
		JFileChooser jfc;
		if (arquivo == null) {
			jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		} else {
			jfc = new JFileChooser(arquivo.getParentFile());
		}
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			arquivo = jfc.getSelectedFile();

			try {
				MP3File song = new MP3File(arquivo);
				String artista = null, titulo = null;
				if (song.hasID3v2Tag()) {
				
					AbstractID3v2 tag = song.getID3v2Tag();
					artista = tag.getLeadArtist();
					titulo = tag.getSongTitle();
					this.setTitle(tituloApp + " : " + artista + " - " + titulo);
					this.songName.setText(artista + " - " + titulo);
					musica = new FileInputStream(arquivo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TagException e) {
				e.printStackTrace();
			}
			this.bIniciar.setEnabled(true);
			this.bParar.setEnabled(false);
		}
	}

	private void importar() {
		JFileChooser jfc;
		if (arquivo == null) {
			jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		} else {
			jfc = new JFileChooser(arquivo.getParentFile());
		}
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			arquivo = jfc.getSelectedFile();
			this.showInfo();
		}
	}

	public void mostraInfos(Arquivo a1) {
		this.setTitle(tituloApp + " : " + a1.getArtista() + " - " + a1.getTitulo());
		this.songName.setText(a1.getArtista() + " - " + a1.getTitulo());
		this.bIniciar.setEnabled(true);
		this.bParar.setEnabled(false);
	}

	private void showInfo() {

		String artista = null, titulo = null, ano = null, faixa = null, album = null;
		String tituloMusica = arquivo.getName();
		try {
			MP3File song = new MP3File(arquivo);
			if (song.hasID3v2Tag()) {
				AbstractID3v2 tag = song.getID3v2Tag();
				artista = tag.getLeadArtist();
				album = tag.getAlbumTitle();
				ano = tag.getYearReleased();
				faixa = tag.getTrackNumberOnAlbum();
				titulo = tag.getSongTitle();
				tituloMusica = titulo;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		}
		this.setTitle(tituloApp + " : " + tituloMusica);
		int resposta = JOptionPane.showConfirmDialog(null,
				"Deseja gravar no banco ?\n\n" + "Numero da faixa : " + faixa + "\nTitulo da Música : " + titulo
						+ "\nNome do artista : " + artista + "\nNome do album : " + album + "\nData de Lançamento : "
						+ ano);

		if (resposta == JOptionPane.YES_OPTION) {
			gravar();
		} else {
			System.out.println("amarelou");
		}
		this.songName.setText(tituloMusica + " " + artista + " " + album + " " + ano);
	}

	private void gravar() {
		try {
			DAOFactory factory = new PostgresDAOFactory();
			ArquivoDAO dao = factory.getArquivoDAO();
			dao.insereArquivo(arquivo);
			atualizaLista();
		} catch (FalhaAcessoAosDadosException xe) {
			JOptionPane.showMessageDialog(null, "Erro ao gravar arquivo");
		}

	}

	private void iniciar(InputStream musica2) {
		try {
			this.player = new PausablePlayer(musica2);
			this.bIniciar.setEnabled(false);
			this.bParar.setEnabled(true);
			this.bPausar.setEnabled(true);
			this.player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}

	private void parar() {
		this.player.stop();
		this.bIniciar.setEnabled(true);
		this.bParar.setEnabled(false);
		this.bPausar.setEnabled(false);
		this.paused = false;
	}

	private void pausar() {
		ImageIcon playpause = new ImageIcon(getClass().getResource("/imagem/playpause.png"));
		if (this.player != null) {
			if (paused) {
				this.player.pause();
				this.bPausar.setIcon(playpause);
				;
			} else {
				this.player.resume();
				ImageIcon pause = new ImageIcon(getClass().getResource("/imagem/pause.png"));
				this.bPausar.setIcon(pause);
			}
			paused = !paused;
		}
	}

	public HomeScreen() {

		super(tituloApp);
		this.setSize(1280, 720);
		// Menu
		this.menuBar = new JMenuBar();
		this.mArquivo = new JMenu("Arquivo");
		this.miAbrir = new JMenuItem("Abrir");
		this.miAbrir.addActionListener(this);
		this.mArquivo.add(miAbrir);
		this.miImportar = new JMenuItem("Importar");
		this.miImportar.addActionListener(this);
		this.mArquivo.add(miImportar);
		this.miSair = new JMenuItem("Sair");
		this.miSair.addActionListener(this);
		this.mArquivo.add(miSair);

		// menu playlist
		this.mPlayList = new JMenu("PlayLists");
		this.miPl=new JMenuItem("PlayList Editor");
		this.miPl.addActionListener(this);
		this.mPlayList.add(miPl);
		// menu configuracoes
		this.mConfiguracao = new JMenu("Configurações");
		// menu ajuda
		this.mAjuda = new JMenu("Ajuda");

		// barra menu
		this.menuBar.add(mArquivo);
		this.menuBar.add(mPlayList);
		this.menuBar.add(mConfiguracao);
		this.menuBar.add(mAjuda);
		this.setJMenuBar(this.menuBar);

		// Campos e Botões
		ImageIcon stop = new ImageIcon(getClass().getResource("/imagem/stop.png"));
		this.bParar = new JButton(stop);
		this.bParar.addActionListener(this);

		ImageIcon play = new ImageIcon(getClass().getResource("/imagem/play.png"));
		this.bIniciar = new JButton(play);
		this.bIniciar.addActionListener(this);

		ImageIcon pause = new ImageIcon(getClass().getResource("/imagem/pause.png"));
		this.bPausar = new JButton(pause);
		this.bPausar.addActionListener(this);

		ImageIcon lupa = new ImageIcon(getClass().getResource("/imagem/lupa.png"));
		this.bBuscar = new JButton("Pesquisar", lupa);
		this.bBuscar.addActionListener(this);

		this.bIniciar.setEnabled(false);
		this.bParar.setEnabled(false);
		this.bPausar.setEnabled(false);

		this.topo = new JPanel(new BorderLayout());
		this.fundo = new JPanel(new BorderLayout());
		this.botoes = new JPanel(new FlowLayout());
		this.menulateral = new JPanel(new GridLayout(8, 1));
		this.pesquisa = new JPanel(new GridLayout(1, 2));

		// labels
		this.lTopo = new JLabel("\t\t\t\t\t\t");
		this.songName = new JLabel("");

		this.vBusca = new JTextField();
//		this.progress.add(songName);
//		this.jprogressBar = new JProgressBar();
//		this.jprogressBar.setMinimum(0);
//		this.progress.add(jprogressBar);
		//
		this.pesquisa.add(lTopo);
		this.pesquisa.add(vBusca);
		this.pesquisa.add(bBuscar);

		this.botoes.add(bParar);
		this.botoes.add(bIniciar);
		this.botoes.add(bPausar);
		this.botoes.add(songName);
		

		this.model = new ArquivoTableModel();
		this.tabelaDeArquivos = new JTable(model);
		this.tabelaDeArquivos.setAutoCreateRowSorter(true);
		this.scroll = new JScrollPane(this.tabelaDeArquivos);
		this.tabelaDeArquivos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int index = tabelaDeArquivos.getSelectedRow();
				if (index >= 0) {
					try {
						Arquivo arquivo = model.getArquivo(index);
						DAOFactory factory = new PostgresDAOFactory();
						ArquivoDAO dao = factory.getArquivoDAO();
						Arquivo a1;
						a1 = dao.getArquivoPorCodigo(arquivo.getId());
						mostraInfos(a1);
						//System.out.println(a1);
						musica = new ByteArrayInputStream(a1.getConteudo());
					} catch (FalhaAcessoAosDadosException e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		this.topo.add(pesquisa, BorderLayout.EAST);
		this.fundo.add(topo, BorderLayout.NORTH);
		this.fundo.add(scroll, BorderLayout.CENTER);
		this.fundo.add(menulateral, BorderLayout.WEST);
		this.fundo.add(botoes, BorderLayout.SOUTH);
		this.getContentPane().add(fundo);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				;
			}

		});

		try {
			atualizaLista();
		} catch (FalhaAcessoAosDadosException e1) {
			e1.printStackTrace();
		}
	}

	private void atualizaLista() throws FalhaAcessoAosDadosException {
		this.model.clear();
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		List<Arquivo> arquivos = dao.buscaTodos();
		this.model.setArquivos(arquivos);
	}
	public void tocarPlaylist(List<Arquivo> pl) {
		this.model.clear();
		this.model.setArquivos(pl);
		System.out.println("entro pl");
	}

	private void buscaLista() throws FalhaAcessoAosDadosException {
		this.model.clear();
		DAOFactory factory = new PostgresDAOFactory();
		ArquivoDAO dao = factory.getArquivoDAO();
		List<Arquivo> arquivos = dao.buscaTitulo(vBusca.getText());
		this.model.setArquivos(arquivos);
	}

	private void playLists() {
		PlayListScreen p = new PlayListScreen();
		p.executar();

	}

	public static void main(String[] args) {
		HomeScreen m = new HomeScreen();
		m.setVisible(true);
	}

}
