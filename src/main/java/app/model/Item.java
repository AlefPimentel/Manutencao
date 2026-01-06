package app.model;

import java.time.LocalDate; // Import Correto

public class Item {
    private int idItem;
    private String nome;
    private String foto;
    private int estoque;
    private String localAplicacao;
    private String especificacao;
    private String descricao;
    private LocalDate dataCadastro; // Mudou de Date para LocalDate
    private int idParticao;

    public Item() {}

    public Item(int idItem, String nome, String foto, int estoque, String localAplicacao,
                String especificacao, String descricao, LocalDate dataCadastro, int idParticao) {
        this.idItem = idItem;
        this.nome = nome;
        this.foto = foto;
        this.estoque = estoque;
        this.localAplicacao = localAplicacao;
        this.especificacao = especificacao;
        this.descricao = descricao;
        this.dataCadastro = dataCadastro;
        this.idParticao = idParticao;
    }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }

    public String getLocalAplicacao() { return localAplicacao; }
    public void setLocalAplicacao(String localAplicacao) { this.localAplicacao = localAplicacao; }

    public String getEspecificacao() { return especificacao; }
    public void setEspecificacao(String especificacao) { this.especificacao = especificacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public int getIdParticao() { return idParticao; }
    public void setIdParticao(int idParticao) { this.idParticao = idParticao; }
}