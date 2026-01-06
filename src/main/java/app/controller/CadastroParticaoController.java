package app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import app.dao.ParticaoDAO;
import app.model.Particao;

public class CadastroParticaoController {

    @FXML
    private TextField txtNomeParticao;

    @FXML
    void handleSalvar(ActionEvent event) {
        String nome = txtNomeParticao.getText().trim();

        if (nome.isEmpty()) {
            mostrarAlerta("Atenção", "O nome da partição é obrigatório.");
            return;
        }

        Particao p = new Particao();
        p.setNomeParticao(nome);

        ParticaoDAO dao = new ParticaoDAO();

        if (dao.inserir(p)) {
            // Sucesso! Fecha a janela
            System.out.println("Partição salva: " + nome);
            fecharJanela(event);
        } else {
            mostrarAlerta("Erro", "Não foi possível salvar a partição no banco de dados.");
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        fecharJanela(event);
    }

    private void fecharJanela(ActionEvent event) {
        // Pega a referência do Stage (janela) atual e fecha
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Ou AlertType.WARNING
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
