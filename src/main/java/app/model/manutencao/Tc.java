package app.model.manutencao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tc {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nome = new SimpleStringProperty();
    private StringProperty roletec = new SimpleStringProperty();
    private StringProperty roleter = new SimpleStringProperty();
    private StringProperty correia = new SimpleStringProperty();
    private StringProperty cavaletec = new SimpleStringProperty();
    private StringProperty cavaleter = new SimpleStringProperty();
    private StringProperty desc = new SimpleStringProperty();
    private StringProperty rolo = new SimpleStringProperty();
    private StringProperty mancal = new SimpleStringProperty();
    private StringProperty motor = new SimpleStringProperty();

    // NOVO CAMPO: ID DA LINHA
    private IntegerProperty idLinha = new SimpleIntegerProperty();

    public Tc() {
    }

    // Construtor completo
    public Tc(int id, String nome, String roletec, String roleter, String correia,
              String cavaletec, String cavaleter, String desc, String rolo,
              String mancal, String motor, int idLinha) {
        this.id.set(id);
        this.nome.set(nome);
        this.roletec.set(roletec);
        this.roleter.set(roleter);
        this.correia.set(correia);
        this.cavaletec.set(cavaletec);
        this.cavaleter.set(cavaleter);
        this.desc.set(desc);
        this.rolo.set(rolo);
        this.mancal.set(mancal);
        this.motor.set(motor);
        this.idLinha.set(idLinha);
    }

    // --- Getters & Setters ---

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }
    public StringProperty nomeProperty() { return nome; }

    public String getRoletec() { return roletec.get(); }
    public void setRoletec(String roletec) { this.roletec.set(roletec); }
    public StringProperty roletecProperty() { return roletec; }

    public String getRoleter() { return roleter.get(); }
    public void setRoleter(String roleter) { this.roleter.set(roleter); }
    public StringProperty roleterProperty() { return roleter; }

    public String getCorreia() { return correia.get(); }
    public void setCorreia(String correia) { this.correia.set(correia); }
    public StringProperty correiaProperty() { return correia; }

    public String getCavaletec() { return cavaletec.get(); }
    public void setCavaletec(String cavaletec) { this.cavaletec.set(cavaletec); }
    public StringProperty cavaletecProperty() { return cavaletec; }

    public String getCavaleter() { return cavaleter.get(); }
    public void setCavaleter(String cavaleter) { this.cavaleter.set(cavaleter); }
    public StringProperty cavaleterProperty() { return cavaleter; }

    public String getDesc() { return desc.get(); }
    public void setDesc(String desc) { this.desc.set(desc); }
    public StringProperty descProperty() { return desc; }

    public String getRolo() { return rolo.get(); }
    public void setRolo(String rolo) { this.rolo.set(rolo); }
    public StringProperty roloProperty() { return rolo; }

    public String getMancal() { return mancal.get(); }
    public void setMancal(String mancal) { this.mancal.set(mancal); }
    public StringProperty mancalProperty() { return mancal; }

    public String getMotor() { return motor.get(); }
    public void setMotor(String motor) { this.motor.set(motor); }
    public StringProperty motorProperty() { return motor; }

    public int getIdLinha() { return idLinha.get(); }
    public void setIdLinha(int idLinha) { this.idLinha.set(idLinha); }
    public IntegerProperty idLinhaProperty() { return idLinha; }
}