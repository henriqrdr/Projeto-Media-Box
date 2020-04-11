package mediabox.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.InputMismatchException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mediabox.banco.PostgresDAOFactory;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.DAOFactory;
import mediabox.interfaces.UsuarioDAO;
import mediabox.model.Arquivo;
import mediabox.model.Usuario;

public class LoginScreen extends JFrame implements ActionListener {
	private JPanel fundo, grid, botoes;
	private JButton bLogin, bCriaAcc;
	private JTextField vUser;
	private JPasswordField vSenha;
	public static Integer userID;
	public static Usuario joao;

	public static void main(String[] args) {
		LoginScreen p = new LoginScreen();
		p.executar();
	}

	public void executar() {
		this.setTitle("MediaBox");
		this.setPreferredSize(new Dimension(350, 400));
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JOptionPane.showConfirmDialog(LoginScreen.this, "Realmente deseja sair?");
				System.exit(0);
			}
		});
		// paineis
		this.fundo = new JPanel(new BorderLayout());
		this.grid = new JPanel(new GridLayout(5, 2));
		this.botoes = new JPanel(new FlowLayout());

		ImageIcon icone = new ImageIcon(getClass().getResource("/imagem/pessoa.png"));
		this.vUser = new JTextField();
		this.vSenha = new JPasswordField();

		this.grid.add(new JLabel(icone));
		this.grid.add(new JLabel("Usuario :"));
		this.grid.add(this.vUser);
		this.grid.add(new JLabel("Senha :"));
		this.grid.add(this.vSenha);

		this.bLogin = new JButton("Login");
		this.bLogin.addActionListener(this);

		this.bCriaAcc = new JButton("Criar conta");
		this.bCriaAcc.addActionListener(this);

		this.botoes.add(this.bLogin);
		this.botoes.add(this.bCriaAcc);

		this.fundo.add(grid, BorderLayout.CENTER);
		this.fundo.add(botoes, BorderLayout.SOUTH);
		this.getContentPane().add(fundo);
		this.pack();
		this.setVisible(true);
	}

	public void login() {
		Usuario joao = null;
		try {
			DAOFactory factory = new PostgresDAOFactory();
			UsuarioDAO dao = factory.getUsuarioDAO();

			try {
				if (dao.checkLogin(vUser.getText(), vSenha.getText())) {
					joao = new Usuario();
					userID = dao.checkUserRetornoId(vUser.getText());
					joao = dao.getUsuarioPorCodigo(userID);
					HomeScreen hs = new HomeScreen();
					hs.setVisible(true);
					this.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Falha ao logar");
				}
			} catch (FalhaAcessoAosDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (InputMismatchException e) {
			e.printStackTrace();
		} finally {
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bLogin) {
			login();
		} else if (e.getSource() == bCriaAcc) {
			NovaContaScreen frame = new NovaContaScreen();
			frame.setVisible(true);
		}
	}

}