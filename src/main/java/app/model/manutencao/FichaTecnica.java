package app.model.manutencao;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FichaTecnica {

    // A identidade é o Enum, não um iID numérico
    private ObjectProperty<ModeloBritador> modelo = new SimpleObjectProperty<>();

    private StringProperty correia = new SimpleStringProperty();
    private StringProperty revestimento = new SimpleStringProperty();
    private StringProperty oleoLub = new SimpleStringProperty();
    private StringProperty oleoHidr = new SimpleStringProperty();

    public FichaTecnica() {}

    public FichaTecnica(ModeloBritador modelo, String correia, String revestimento, String oleoLub, String oleoHidr) {
        this.modelo.set(modelo);
        this.correia.set(correia);
        this.revestimento.set(revestimento);
        this.oleoLub.set(oleoLub);
        this.oleoHidr.set(oleoHidr);
    }

    // --- Getters e Setters ---

    public ModeloBritador getModelo() { return modelo.get(); }
    public void setModelo(ModeloBritador modelo) { this.modelo.set(modelo); }
    public ObjectProperty<ModeloBritador> modeloProperty() { return modelo; }

    public String getCorreia() { return correia.get(); }
    public void setCorreia(String correia) { this.correia.set(correia); }
    public StringProperty correiaProperty() { return correia; }

    public String getRevestimento() { return revestimento.get(); }
    public void setRevestimento(String revestimento) { this.revestimento.set(revestimento); }
    public StringProperty revestimentoProperty() { return revestimento; }

    public String getOleoLub() { return oleoLub.get(); }
    public void setOleoLub(String oleoLub) { this.oleoLub.set(oleoLub); }
    public StringProperty oleoLubProperty() { return oleoLub; }

    public String getOleoHidr() { return oleoHidr.get(); }
    public void setOleoHidr(String oleoHidr) { this.oleoHidr.set(oleoHidr); }
    public StringProperty oleoHidrProperty() { return oleoHidr; }
}