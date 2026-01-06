package app.model.manutencao;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Troca {

    private IntegerProperty id = new SimpleIntegerProperty();
    private ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private StringProperty responsavel = new SimpleStringProperty();
    private IntegerProperty horimetro = new SimpleIntegerProperty();
    private StringProperty tipo = new SimpleStringProperty();
    private StringProperty especificacao = new SimpleStringProperty();
    private BooleanProperty cfsf = new SimpleBooleanProperty();

    // Mantemos o nome para compatibilidade visual e casos onde não há ID
    private StringProperty nomeEquipamento = new SimpleStringProperty();

    // NOVO CAMPO: ID do Equipamento (Foreign Key para TC)
    private IntegerProperty idEquipamento = new SimpleIntegerProperty();

    public Troca() {}

    // Construtor atualizado (com idEquipamento opcional no final)
    public Troca(int id, LocalDate data, String responsavel, int horimetro, String tipo,
                 String especificacao, boolean cfsf, String nomeEquipamento, int idEquipamento) {
        this.id.set(id);
        this.data.set(data);
        this.responsavel.set(responsavel);
        this.horimetro.set(horimetro);
        this.tipo.set(tipo);
        this.especificacao.set(especificacao);
        this.cfsf.set(cfsf);
        this.nomeEquipamento.set(nomeEquipamento);
        this.idEquipamento.set(idEquipamento);
    }

    // --- Getters, Setters e Properties ---

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public LocalDate getData() { return data.get(); }
    public void setData(LocalDate data) { this.data.set(data); }
    public ObjectProperty<LocalDate> dataProperty() { return data; }

    public String getResponsavel() { return responsavel.get(); }
    public void setResponsavel(String responsavel) { this.responsavel.set(responsavel); }
    public StringProperty responsavelProperty() { return responsavel; }

    public int getHorimetro() { return horimetro.get(); }
    public void setHorimetro(int horimetro) { this.horimetro.set(horimetro); }
    public IntegerProperty horimetroProperty() { return horimetro; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }

    public String getEspecificacao() { return especificacao.get(); }
    public void setEspecificacao(String especificacao) { this.especificacao.set(especificacao); }
    public StringProperty especificacaoProperty() { return especificacao; }

    public boolean isCfsf() { return cfsf.get(); }
    public void setCfsf(boolean cfsf) { this.cfsf.set(cfsf); }
    public BooleanProperty cfsfProperty() { return cfsf; }

    public String getNomeEquipamento() { return nomeEquipamento.get(); }
    public void setNomeEquipamento(String nomeEquipamento) { this.nomeEquipamento.set(nomeEquipamento); }
    public StringProperty nomeEquipamentoProperty() { return nomeEquipamento; }

    // Getter/Setter do novo campo
    public int getIdEquipamento() { return idEquipamento.get(); }
    public void setIdEquipamento(int idEquipamento) { this.idEquipamento.set(idEquipamento); }
    public IntegerProperty idEquipamentoProperty() { return idEquipamento; }
}