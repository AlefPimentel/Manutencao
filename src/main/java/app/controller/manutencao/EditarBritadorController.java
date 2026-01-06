package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import app.dao.daomanutencao.AvisoDAO;
import app.dao.daomanutencao.FichaTecnicaDAO;
import app.model.manutencao.FichaTecnica;

public class EditarBritadorController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtSpecRev, txtSpecCor, txtSpecHidr, txtSpecLub;
    @FXML private TextField txtVidaRev, txtVidaCor, txtVidaHidr, txtVidaLub;

    // MUDANÇA: Singleton nos dois DAOs
    private final FichaTecnicaDAO fichaTecnicaDAO = FichaTecnicaDAO.getInstance();
    private final AvisoDAO avisoDAO = AvisoDAO.getInstance();

    private FichaTecnica fichaAtual;

    public void setFichaTecnica(FichaTecnica ficha) {
        this.fichaAtual = ficha;
        if (ficha != null) {
            lblTitulo.setText("Editar " + ficha.getModelo().getNome());

            txtSpecRev.setText(ficha.getRevestimento());
            txtSpecCor.setText(ficha.getCorreia());
            txtSpecHidr.setText(ficha.getOleoHidr());
            txtSpecLub.setText(ficha.getOleoLub());

            carregarVidaUtil("Revestimento", txtVidaRev);
            carregarVidaUtil("Correia", txtVidaCor);
            carregarVidaUtil("OleoHidraulico", txtVidaHidr);
            carregarVidaUtil("OleoLubrificacao", txtVidaLub);
        }
    }

    private void carregarVidaUtil(String componente, TextField campo) {
        int horas = avisoDAO.getVidaUtilConfigurada(fichaAtual.getModelo().getNome(), componente);
        campo.setText(String.valueOf(horas));
        campo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) campo.setText(newVal.replaceAll("[^\\d]", ""));
        });
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (fichaAtual == null) return;

        try {
            fichaAtual.setRevestimento(txtSpecRev.getText());
            fichaAtual.setCorreia(txtSpecCor.getText());
            fichaAtual.setOleoHidr(txtSpecHidr.getText());
            fichaAtual.setOleoLub(txtSpecLub.getText());

            boolean sucessoSpec = fichaTecnicaDAO.atualizar(fichaAtual);

            salvarVidaUtil("Revestimento", txtVidaRev);
            salvarVidaUtil("Correia", txtVidaCor);
            salvarVidaUtil("OleoHidraulico", txtVidaHidr);
            salvarVidaUtil("OleoLubrificacao", txtVidaLub);

            if (sucessoSpec) {
                mostrarAlerta("Sucesso", "Dados atualizados com sucesso!");
                fecharJanela(event);
            } else {
                mostrarAlerta("Erro", "Erro ao salvar especificações.");
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao processar dados: " + e.getMessage());
        }
    }

    private void salvarVidaUtil(String componente, TextField campo) {
        String texto = campo.getText();
        int horas = (texto == null || texto.isEmpty()) ? 0 : Integer.parseInt(texto);
        avisoDAO.setVidaUtilConfigurada(fichaAtual.getModelo().getNome(), componente, horas);
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}