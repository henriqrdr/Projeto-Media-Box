package mediabox.excecoes;

public class FalhaAcessoAosDadosException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Ocorreu uma falha acessando os dados";
	
	public FalhaAcessoAosDadosException() {
		super(MESSAGE);
	}
	
	public FalhaAcessoAosDadosException(String message, Throwable cause) {
		super(MESSAGE + " " + message, cause);
	}
	
	public FalhaAcessoAosDadosException(String message) {
		super(MESSAGE + " " + message);
	}
	
	public FalhaAcessoAosDadosException(Throwable cause) {
		super(MESSAGE, cause);
	}
}
