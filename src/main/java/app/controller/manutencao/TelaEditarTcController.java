package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import app.dao.daomanutencao.TcDAO;
import app.model.manutencao.Tc;

public class TelaEditarTcController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome, txtMotor, txtCorreia, txtRoleteCarga, txtRoleteRetorno;
    @FXML private TextField txtCavaleteCarga, txtCavaleteRetorno, txtTambor, txtMancal;
    @FXML private TextArea txtDescricao;

    private final TcDAO tcDAO = TcDAO.getInstance();
    private Tc tcAtual;

    public void setTc(Tc tc) {
        this.tcAtual = tc;
        if (tc != null) {
            lblTitulo.setText("EDITAR: " + tc.getNome());
            txtNome.setText(tc.getNome());
            txtMotor.setText(tc.getMotor());
            txtCorreia.setText(tc.getCorreia());
            txtRoleteCarga.setText(tc.getRoletec());
            txtRoleteRetorno.setText(tc.getRoleter());
            txtCavaleteCarga.setText(tc.getCavaletec());
            txtCavaleteRetorno.setText(tc.getCavaleter());
            txtTambor.setText(tc.getRolo());
            txtMancal.setText(tc.getMancal());
            txtDescricao.setText(tc.getDesc());
        }
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (tcAtual == null) return;
        if (txtNome.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Nome é obrigatório.").show();
            return;
        }

        tcAtual.setNome(txtNome.getText());
        tcAtual.setMotor(txtMotor.getText());
        tcAtual.setCorreia(txtCorreia.getText());
        tcAtual.setRoletec(txtRoleteCarga.getText());
        tcAtual.setRoleter(txtRoleteRetorno.getText());
        tcAtual.setCavaletec(txtCavaleteCarga.getText());
        tcAtual.setCavaleter(txtCavaleteRetorno.getText());
        tcAtual.setRolo(txtTambor.getText());
        tcAtual.setMancal(txtMancal.getText());
        tcAtual.setDesc(txtDescricao.getText());

        if (tcDAO.atualizar(tcAtual)) {
            new Alert(Alert.AlertType.INFORMATION, "TC atualizada com sucesso!").showAndWait();
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar no banco.").show();
        }
    }

    @FXML
    void handleExcluir(ActionEvent event) {
        if (tcAtual == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja excluir esta TC?\nIsso apagará o histórico dela.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (tcDAO.deletar(tcAtual.getId())) {
                ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir.").show();
            }
        }
    }

    @FXML void handleCancelar(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}