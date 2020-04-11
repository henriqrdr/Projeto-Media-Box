package mediabox.excecoes;

public class ProdutoNaoEncontradoException extends FalhaAcessoAosDadosException {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Produto não encontrado";

	public ProdutoNaoEncontradoException() {
		super(MESSAGE);
	}

	public ProdutoNaoEncontradoException(String message, Throwable cause) {
		super(MESSAGE + " : " + message, cause);
	}

	public ProdutoNaoEncontradoException(String message) {
		super(MESSAGE + " : " + message);
	}

	public ProdutoNaoEncontradoException(Throwable cause) {
		super(MESSAGE, cause);
	}


}
