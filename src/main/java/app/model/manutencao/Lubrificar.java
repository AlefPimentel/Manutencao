package app.model.manutencao;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Lubrificar {

    private IntegerProperty id = new SimpleIntegerProperty();
    private ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private StringProperty responsavel = new SimpleStringProperty();
    private IntegerProperty horimetro = new SimpleIntegerProperty();
    private StringProperty graxa = new SimpleStringProperty();
    private IntegerProperty idLinha = new SimpleIntegerProperty();

    // Esse campo vai exibir o NOME DA LINHA na tabela
    private StringProperty nomeEquipamento = new SimpleStringProperty();

    public Lubrificar() {
    }

    public Lubrificar(int id, LocalDate data, String responsavel, int horimetro, String graxa, int idLinha, String nomeLinha) {
        this.id.set(id);
        this.data.set(data);
        this.responsavel.set(responsavel);
        this.horimetro.set(horimetro);
        this.graxa.set(graxa);
        this.idLinha.set(idLinha);
        this.nomeEquipamento.set(nomeLinha);
    }

    // Getters e Setters
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

    public String getGraxa() { return graxa.get(); }
    public void setGraxa(String graxa) { this.graxa.set(graxa); }
    public StringProperty graxaProperty() { return graxa; }

    public int getIdLinha() { return idLinha.get(); }
    public void setIdLinha(int idLinha) { this.idLinha.set(idLinha); }
    public IntegerProperty idLinhaProperty() { return idLinha; }

    // Getter/Setter para a tabela
    public String getNomeEquipamento() { return nomeEquipamento.get(); }
    public void setNomeEquipamento(String nomeEquipamento) { this.nomeEquipamento.set(nomeEquipamento); }
    public StringProperty nomeEquipamentoProperty() { return nomeEquipamento; }
}