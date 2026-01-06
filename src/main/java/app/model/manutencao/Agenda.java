package app.model.manutencao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Agenda {

    private IntegerProperty id = new SimpleIntegerProperty();
    private ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private StringProperty tipo = new SimpleStringProperty(); // Ex: "Correia", "OleoHidraulico"

    // Construtor vazio
    public Agenda() {
    }

    // Construtor completo
    public Agenda(int id, LocalDate data, String tipo) {
        this.id.set(id);
        this.data.set(data);
        this.tipo.set(tipo);
    }

    // --- Getters, Setters e Properties ---

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public LocalDate getData() { return data.get(); }
    public void setData(LocalDate data) { this.data.set(data); }
    public ObjectProperty<LocalDate> dataProperty() { return data; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }
}