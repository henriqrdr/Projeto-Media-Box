package mediabox.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



import javazoom.jl.decoder.JavaLayerException;
import mediabox.banco.PostgresDAOFactory;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ArquivoDAO;
import mediabox.interfaces.DAOFactory;
import mediabox.model.Arquivo;
import playermp3thread.PausablePlayer;

public class PlayListPlayerScreen extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel topo, fundo, botoes, menulateral, pesquisa;
	private JButton bIniciar, bPausar, bParar;
	private JLabel songName;
	private boolean paused = true;
	private PausablePlayer player;

	private JTable tabelaDeArquivos;
	private JScrollPane scroll;
	private ArquivoTableModel model;

	InputStream musica;

	private static final String tituloApp = "PlayList Player";
	DAOFactory factory = new PostgresDAOFactory();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bIniciar)) {
			this.iniciar(musica);
		} else if (e.getSource().equals(bParar)) {
			this.parar();
		} else if (e.getSource().equals(bPausar)) {
			this.pausar();

		}
	}

	public PlayListPlayerScreen(List<Arquivo> lista) throws FalhaAcessoAosDadosException {

		super(tituloApp);
		this.setSize(1280, 720);

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

		this.bIniciar.setEnabled(false);
		this.bParar.setEnabled(false);
		this.bPausar.setEnabled(false);

		this.topo = new JPanel(new BorderLayout());
		this.fundo = new JPanel(new BorderLayout());
		this.botoes = new JPanel(new FlowLayout());
		this.menulateral = new JPanel(new GridLayout(8, 1));
		this.pesquisa = new JPanel(new GridLayout(1, 2));

		// labels
		this.songName = new JLabel("");

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
				parar();
				PlayListPlayerScreen.this.dispose();
				;
			}

		});
		tocarPlaylist(lista);
	}

	public void tocarPlaylist(List<Arquivo> pl) {
		this.model.clear();
		this.model.setArquivos(pl);
		//System.out.println("entro pl");
	}

	public void mostraInfos(Arquivo a1) {
		this.setTitle(tituloApp + " : " + a1.getArtista() + " - " + a1.getTitulo());
		this.songName.setText(a1.getArtista() + " - " + a1.getTitulo());
		this.bIniciar.setEnabled(true);
		this.bParar.setEnabled(false);
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

//	public static void main(String[] args) {
//		PlayListPlayerScreen m = new PlayListPlayerScreen();
//		m.setVisible(true);
//	}

}
