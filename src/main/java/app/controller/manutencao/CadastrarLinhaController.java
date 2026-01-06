package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import app.dao.daomanutencao.LinhaDAO;
import app.model.manutencao.Linha;

public class CadastrarLinhaController {

    @FXML private TextField txtNome;

    // MUDANÇA: Singleton
    private final LinhaDAO linhaDAO = LinhaDAO.getInstance();

    @FXML
    void handleSalvar(ActionEvent event) {
        if (txtNome.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Digite o nome da linha.");
            alert.show();
            return;
        }

        Linha novaLinha = new Linha(0, txtNome.getText());

        if (linhaDAO.inserir(novaLinha)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro ao salvar no banco.");
            alert.show();
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}