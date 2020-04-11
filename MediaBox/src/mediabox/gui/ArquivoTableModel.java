package mediabox.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mediabox.model.Arquivo;

public class ArquivoTableModel extends AbstractTableModel {

	private List<Arquivo> arquivos;
	private static final String nomes[] = {"Id","Titulo","Faixa","Artista","Album","Ano"};

	public ArquivoTableModel() {
		this.arquivos = new ArrayList<Arquivo>();
	}
	
	public ArquivoTableModel(List<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}
	
	public List<Arquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<Arquivo> arquivos) {
		this.arquivos = arquivos;
		this.fireTableDataChanged();
	}
	
	public String getColumnName(int number) {
		return nomes[number];
	}

	@Override
	public int getRowCount() {
		if (arquivos != null) {
			return arquivos.size();
		} else {
			return 0;
		}
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Arquivo arquivo = arquivos.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return arquivo.getId();
		case 1:
			return arquivo.getTitulo();
		case 2:
			return arquivo.getFaixa();
		case 3:
			return arquivo.getArtista();
		case 4:
			return arquivo.getAlbum();
		case 5:
			return arquivo.getAno();
		}

		return null;
	}
	
	public Arquivo getArquivo(int rowIndex) {
		return arquivos.get(rowIndex);
	}
	
	public void adicionaArquivo(Arquivo arquivo) {
		this.arquivos.add(arquivo);
		this.fireTableDataChanged();
	}
	public void removeArquivo(Arquivo arquivo) {
		this.arquivos.remove(arquivo);
		this.fireTableDataChanged();
	}
	
	public void clear() {
		this.arquivos.clear();
		this.fireTableDataChanged();
	}

}
