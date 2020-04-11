package mediabox.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.InputMismatchException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mediabox.banco.PostgresDAOFactory;
import mediabox.excecoes.FalhaAcessoAosDadosException;
import mediabox.interfaces.ArquivoDAO;
import mediabox.interfaces.DAOFactory;
import mediabox.interfaces.UsuarioDAO;
import mediabox.model.Usuario;

public class NovaContaScreen extends JFrame implements ActionListener {
	private JPanel fundo, grid, botoes;
	private JButton bCriaNovaConta;
	private JTextField nNome, nUser;
	private JPasswordField nSenha, nSenha2;

	public NovaContaScreen() {
		this.setTitle("Nova conta");
		this.setPreferredSize(new Dimension(350, 400));
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JOptionPane.showConfirmDialog(NovaContaScreen.this, "Realmente deseja sair?");
				NovaContaScreen.this.dispose();
			}
		});
		
		// paineis
		this.fundo = new JPanel(new BorderLayout());
		this.grid = new JPanel(new GridLayout(5, 2));
		this.botoes = new JPanel(new FlowLayout());

		this.nNome = new JTextField();
		this.nUser = new JTextField();
		this.nSenha = new JPasswordField();
		this.nSenha2 = new JPasswordField();

		this.grid.add(new JLabel("Nome :"));
		this.grid.add(this.nNome);
		this.grid.add(new JLabel("Usuario :"));
		this.grid.add(this.nUser);
		this.grid.add(new JLabel("Senha :"));
		this.grid.add(this.nSenha);
		this.grid.add(new JLabel("Confirma senha :"));
		this.grid.add(this.nSenha2);

		this.bCriaNovaConta = new JButton("Criar conta");
		this.bCriaNovaConta.addActionListener(this);

		this.botoes.add(this.bCriaNovaConta);

		this.fundo.add(grid, BorderLayout.CENTER);
		this.fundo.add(botoes, BorderLayout.SOUTH);
		this.getContentPane().add(fundo);
		this.pack();
		// this.setVisible(true);
		

	}

	public void newAcc() {
		String userx;
		try {
			userx = nUser.getText();
			int resposta = JOptionPane.showConfirmDialog(NovaContaScreen.this,
					"Deseja cadastrar usuario :" + userx + "?");
			if (resposta == JOptionPane.YES_OPTION) {
				Usuario user = new Usuario();
				if (nUser != null) {
					if (nSenha != null && nSenha2.getText().equals(nSenha.getText())) {
						user.setSenha(nSenha.getText());
						user.setUser(nUser.getText());
						user.setNome(nNome.getText());
						DAOFactory factory = new PostgresDAOFactory();
						UsuarioDAO dao = factory.getUsuarioDAO();
						try {
							if(!dao.checkUser(nUser.getText())) {	
								try {
									dao.insereUsuario(user);
									System.out
									.println("Novo usuario : " + nNome.getText() + nUser.getText() + nSenha.getPassword());
								} catch (FalhaAcessoAosDadosException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							else {
								JOptionPane.showMessageDialog(null, "User já cadastrado");
							}
						} catch (FalhaAcessoAosDadosException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "senhas não batem");
					}
				}
			}
		} catch (InputMismatchException e) {
			e.printStackTrace();
		}
		this.dispose();

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bCriaNovaConta) {
			newAcc();
		}
	}

}
