package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import app.dao.daomanutencao.LubrificarDAO;
import app.model.manutencao.Linha;
import app.model.manutencao.Lubrificar;
import java.time.LocalDate;

public class RegistrarLubrificacaoController {

    @FXML private DatePicker dpData;
    @FXML private TextField txtHorimetro, txtResponsavel, txtGraxa;

    // MUDANÇA: Singleton
    private final LubrificarDAO lubrificarDAO = LubrificarDAO.getInstance();
    private Linha linhaAtual;

    @FXML
    public void initialize() {
        dpData.setValue(LocalDate.now());
    }

    public void setLinha(Linha linha) {
        this.linhaAtual = linha;
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (validarCampos()) {
            try {
                Lubrificar lub = new Lubrificar();
                lub.setData(dpData.getValue());
                lub.setResponsavel(txtResponsavel.getText());
                lub.setGraxa(txtGraxa.getText());

                String horimText = txtHorimetro.getText().replaceAll("[^0-9]", "");
                lub.setHorimetro(horimText.isEmpty() ? 0 : Integer.parseInt(horimText));
                lub.setIdLinha(linhaAtual.getId());

                if (lubrificarDAO.inserir(lub)) {
                    mostrarAlerta("Sucesso", "Lubrificação da linha registrada com sucesso!");
                    fecharJanela(event);
                } else {
                    mostrarAlerta("Erro", "Erro ao salvar no banco de dados.");
                }
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro no processo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private boolean validarCampos() {
        if (linhaAtual == null) {
            mostrarAlerta("Erro", "Linha não identificada.");
            return false;
        }
        if (dpData.getValue() == null) {
            mostrarAlerta("Atenção", "Selecione a Data.");
            return false;
        }
        return true;
    }

    private void fecharJanela(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}