package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import app.dao.daomanutencao.TcDAO;
import app.model.manutencao.Linha;
import app.model.manutencao.Tc;

public class CadastrarTcController {

    @FXML private Label lblNomeLinha;
    @FXML private TextField txtNome;
    @FXML private TextField txtMotor;
    @FXML private TextField txtCorreia;
    @FXML private TextField txtRoleteCarga;
    @FXML private TextField txtRoleteRetorno;
    @FXML private TextField txtCavaleteCarga;
    @FXML private TextField txtCavaleteRetorno;
    @FXML private TextField txtTambor;
    @FXML private TextField txtMancal;
    @FXML private TextArea txtDescricao;

    // MUDANÇA: Singleton
    private final TcDAO tcDAO = TcDAO.getInstance();

    private Linha linhaAlvo;

    public void setLinha(Linha linha) {
        this.linhaAlvo = linha;
        if (linha != null) {
            if (lblNomeLinha != null) {
                lblNomeLinha.setText("VINCULADO À: " + linha.getNome().toUpperCase());
            }
        }
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (txtNome.getText().isEmpty()) {
            alerta("O nome da TC é obrigatório.");
            return;
        }

        if (linhaAlvo == null) {
            alerta("Erro interno: Nenhuma linha de produção foi vinculada.");
            return;
        }

        Tc novaTc = new Tc();
        novaTc.setNome(txtNome.getText());
        novaTc.setMotor(txtMotor.getText());
        novaTc.setCorreia(txtCorreia.getText());
        novaTc.setRoletec(txtRoleteCarga.getText());
        novaTc.setRoleter(txtRoleteRetorno.getText());
        novaTc.setCavaletec(txtCavaleteCarga.getText());
        novaTc.setCavaleter(txtCavaleteRetorno.getText());
        novaTc.setRolo(txtTambor.getText());
        novaTc.setMancal(txtMancal.getText());
        novaTc.setDesc(txtDescricao.getText());
        novaTc.setIdLinha(linhaAlvo.getId());

        if (tcDAO.inserir(novaTc)) {
            fecharJanela(event);
        } else {
            alerta("Erro ao salvar no banco de dados.");
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private void fecharJanela(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}