package app.model;

import java.time.LocalDate; // Import Correto

public class Registro {
    private int idRegistro;
    private String tipo;        // RENOMEADO de 'nome' para 'tipo' (Entrada/Saída)
    private int quantidade;
    private String responsavel;
    private LocalDate data;     // Mudou de Date para LocalDate
    private int idItem;

    // Auxiliar (não vai pro banco na tabela Registro)
    private String nomeItem;

    public Registro() {}

    public Registro(int idRegistro, String tipo, int quantidade, String responsavel, LocalDate data, int idItem) {
        this.idRegistro = idRegistro;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.responsavel = responsavel;
        this.data = data;
        this.idItem = idItem;
    }

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public String getNomeItem() { return nomeItem; }
    public void setNomeItem(String nomeItem) { this.nomeItem = nomeItem; }
}