package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import app.dao.daomanutencao.TrocaDAO;
import app.model.manutencao.FichaTecnica;
import app.model.manutencao.Troca;
import java.time.LocalDate;

public class RegistrarTrocaController {

    @FXML private Label lblTitulo, lblNomeBritador, lblTipoManutencao;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHorimetro, txtResponsavel, txtEspecificacao;
    @FXML private VBox boxOleo;
    @FXML private CheckBox chkCfsf;

    private FichaTecnica fichaTecnicaAtual;
    private String tipoManutencao;

    // MUDANÇA: Singleton
    private final TrocaDAO trocaDAO = TrocaDAO.getInstance();

    @FXML
    public void initialize() {
        dpData.setValue(LocalDate.now());
        txtHorimetro.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtHorimetro.setText(newV.replaceAll("[^\\d]", ""));
        });
    }

    public void setFichaTecnica(FichaTecnica fichaTecnica) {
        this.fichaTecnicaAtual = fichaTecnica;
        if (fichaTecnica != null && fichaTecnica.getModelo() != null) {
            lblNomeBritador.setText(fichaTecnica.getModelo().getNome());
        }
    }

    public void setTipo(String tipo) {
        this.tipoManutencao = tipo;
        lblTipoManutencao.setText("Tipo: " + formatarTipo(tipo));
        lblTitulo.setText("REGISTRAR " + formatarTipo(tipo).toUpperCase());

        boolean isOleo = tipo != null && tipo.toLowerCase().contains("oleo");
        boxOleo.setVisible(isOleo);
        boxOleo.setManaged(isOleo);

        if (fichaTecnicaAtual != null) {
            if ("Revestimento".equals(tipo)) txtEspecificacao.setText(fichaTecnicaAtual.getRevestimento());
            else if ("Correia".equals(tipo)) txtEspecificacao.setText(fichaTecnicaAtual.getCorreia());
            else if ("OleoHidraulico".equals(tipo)) txtEspecificacao.setText(fichaTecnicaAtual.getOleoHidr());
            else if ("OleoLubrificacao".equals(tipo)) txtEspecificacao.setText(fichaTecnicaAtual.getOleoLub());
        }
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (txtHorimetro.getText().isEmpty() || txtResponsavel.getText().isEmpty() || txtEspecificacao.getText().isEmpty()) {
            alertaErro("Preencha todos os campos obrigatórios.");
            return;
        }

        try {
            Troca novaTroca = new Troca();
            novaTroca.setData(dpData.getValue());
            novaTroca.setHorimetro(Integer.parseInt(txtHorimetro.getText()));
            novaTroca.setResponsavel(txtResponsavel.getText());
            novaTroca.setTipo(this.tipoManutencao);
            novaTroca.setEspecificacao(txtEspecificacao.getText());

            if (fichaTecnicaAtual != null && fichaTecnicaAtual.getModelo() != null) {
                novaTroca.setNomeEquipamento(fichaTecnicaAtual.getModelo().getNome());
            }

            novaTroca.setCfsf(boxOleo.isVisible() && chkCfsf.isSelected());

            if (trocaDAO.inserir(novaTroca)) fecharJanela(event);
            else alertaErro("Erro ao salvar no banco de dados.");

        } catch (NumberFormatException e) {
            alertaErro("Horímetro deve ser um número válido.");
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private void fecharJanela(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void alertaErro(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        switch (tipo) {
            case "OleoHidraulico": return "Óleo Hidráulico";
            case "OleoLubrificacao": return "Óleo Lubrificação";
            default: return tipo;
        }
    }
}