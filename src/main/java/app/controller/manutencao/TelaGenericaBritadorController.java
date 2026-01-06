package app.controller.manutencao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import app.dao.daomanutencao.*;
import app.model.manutencao.*;
import app.service.JanelaService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

public class TelaGenericaBritadorController {

    @FXML private Label lblTituloTopo;
    @FXML private Label lblNome;
    @FXML private ImageView imgBritador;

    // --- ESPECIFICAÇÕES ---
    @FXML private Label lblSpecRevestimento, lblSpecCorreia, lblSpecOleoLub, lblSpecOleoHidr;

    // --- STATUS ---
    @FXML private Pane statusRev, statusCor, statusLub, statusHidr;

    // --- HORÍMETRO ---
    @FXML private Label lblHorimetroTotal, lblDataHorimetro;

    // --- HISTÓRICO ---
    @FXML private Label lblRevHorimetro, lblRevResp, lblRevSpec, lblRevData;
    @FXML private Label lblCorHorimetro, lblCorResp, lblCorSpec, lblCorData;
    @FXML private Label lblHidrHorimetro, lblHidrResp, lblHidrSpec, lblHidrData;
    @FXML private Label lblLubHorimetro, lblLubResp, lblLubSpec, lblLubData;

    // Singletons
    private final JanelaService janelaService = JanelaService.getInstance();
    private final FichaTecnicaDAO fichaTecnicaDAO = FichaTecnicaDAO.getInstance();
    private final TrocaDAO trocaDAO = TrocaDAO.getInstance();
    private final BritadorDAO britadorDAO = BritadorDAO.getInstance();
    private final AvisoDAO avisoDAO = AvisoDAO.getInstance();

    private FichaTecnica fichaTecnicaAtual;
    private ModeloBritador modeloAtual;

    // --- INICIALIZAÇÃO ---

    public void setModelo(ModeloBritador modelo) {
        this.modeloAtual = modelo;
        if (modelo != null) {
            if (lblNome != null) lblNome.setText(modelo.getNome());
            if (lblTituloTopo != null) lblTituloTopo.setText("MANUTENÇÃO - " + modelo.getNome());
        }
        carregarImagem();
        carregarDados();
    }

    private void carregarImagem() {
        if (modeloAtual == null) return;
        String nomeArquivo = switch (modeloAtual) {
            case HP400 -> "hp400.jpg";
            case HP4 -> "hp4.jpg";
            case C110 -> "C110.jpg";
        };
        try {
            var stream = getClass().getResourceAsStream("/images/" + nomeArquivo);
            if (stream != null) imgBritador.setImage(new Image(stream));
        } catch (Exception e) { System.err.println("Erro imagem: " + e.getMessage()); }
    }

    private void carregarDados() {
        if (modeloAtual == null) return;

        // Ficha Técnica
        this.fichaTecnicaAtual = fichaTecnicaDAO.buscarPorModelo(modeloAtual);
        if (fichaTecnicaAtual != null) {
            lblSpecRevestimento.setText(valida(fichaTecnicaAtual.getRevestimento()));
            lblSpecCorreia.setText(valida(fichaTecnicaAtual.getCorreia()));
            lblSpecOleoLub.setText(valida(fichaTecnicaAtual.getOleoLub()));
            lblSpecOleoHidr.setText(valida(fichaTecnicaAtual.getOleoHidr()));
        }

        // Horímetro
        BritadorDAO.DadosHorimetro dados = britadorDAO.getHorimetro(modeloAtual);
        if (lblHorimetroTotal != null) lblHorimetroTotal.setText(dados.valor + " H");
        if (lblDataHorimetro != null) {
            lblDataHorimetro.setText(dados.data != null ? dados.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-");
        }

        // Status
        atualizarStatus("Revestimento", statusRev);
        atualizarStatus("Correia", statusCor);
        atualizarStatus("OleoLubrificacao", statusLub);
        atualizarStatus("OleoHidraulico", statusHidr);

        // Histórico
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        atualizarLinha("Revestimento", lblRevHorimetro, lblRevResp, lblRevSpec, lblRevData, dtf);
        atualizarLinha("Correia", lblCorHorimetro, lblCorResp, lblCorSpec, lblCorData, dtf);
        atualizarLinha("OleoHidraulico", lblHidrHorimetro, lblHidrResp, lblHidrSpec, lblHidrData, dtf);
        atualizarLinha("OleoLubrificacao", lblLubHorimetro, lblLubResp, lblLubSpec, lblLubData, dtf);
    }

    private String valida(String t) { return (t == null || t.isEmpty()) ? "-" : t; }

    private void atualizarStatus(String comp, Pane pane) {
        if (pane == null) return;
        StatusAviso status = avisoDAO.calcularStatusBritador(modeloAtual, comp);
        pane.getStyleClass().removeAll("status-verde", "status-amarelo", "status-vermelho", "status-cinza");
        switch (status.getStatus()) {
            case "VERMELHO" -> pane.getStyleClass().add("status-vermelho");
            case "AMARELO" -> pane.getStyleClass().add("status-amarelo");
            case "VERDE" -> pane.getStyleClass().add("status-verde");
            default -> pane.getStyleClass().add("status-cinza");
        }
        Tooltip.install(pane, new Tooltip(status.getMensagem()));
    }

    private void atualizarLinha(String tipo, Label lblHor, Label lblResp, Label lblSpec, Label lblData, DateTimeFormatter dtf) {
        if (lblHor == null) return;
        trocaDAO.listarPorTipo(tipo).stream()
                .filter(t -> t.getNomeEquipamento() != null && t.getNomeEquipamento().equalsIgnoreCase(modeloAtual.getNome()))
                .max(Comparator.comparing(Troca::getId))
                .ifPresentOrElse(t -> {
                    lblHor.setText(String.valueOf(t.getHorimetro()));
                    lblResp.setText(valida(t.getResponsavel()));
                    lblSpec.setText(valida(t.getEspecificacao()));
                    lblData.setText(t.getData() != null ? t.getData().format(dtf) : "-");
                }, () -> {
                    lblHor.setText("-"); lblResp.setText("-"); lblSpec.setText("-"); lblData.setText("-");
                });
    }

    // --- REVERSÃO DO BOTÃO ATUALIZAR ---
    @FXML
    void handleAtualizarHorimetro(ActionEvent event) {
        if (modeloAtual == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Atualizar Horímetro - " + modeloAtual.getNome());
        dialog.setHeaderText("Horímetro Atual: " + lblHorimetroTotal.getText());
        dialog.setContentText("Novo valor (Horas):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(valor -> {
            try {
                String num = valor.replaceAll("[^0-9]", "");
                if (!num.isEmpty()) {
                    int novoValor = Integer.parseInt(num);
                    // Atualiza direto no banco
                    if (britadorDAO.atualizarHorimetro(modeloAtual, novoValor)) {
                        carregarDados(); // Recarrega tela
                    } else {
                        mostrarAlerta("Erro", "Erro ao salvar no banco.");
                    }
                }
            } catch (Exception e) { mostrarAlerta("Erro", "Valor inválido."); }
        });
    }

    @FXML void handleEditarBritador(ActionEvent event) { editarBritador(); }
    @FXML void btnAddRevestimento(ActionEvent event) { abrirRegistro("Revestimento"); }
    @FXML void btnAddCorreia(ActionEvent event) { abrirRegistro("Correia"); }
    @FXML void btnAddOleoHidr(ActionEvent event) { abrirRegistro("OleoHidraulico"); }
    @FXML void btnAddOleoLub(ActionEvent event) { abrirRegistro("OleoLubrificacao"); }

    @FXML void handleVoltar(ActionEvent event) {
        janelaService.abrirJanela("/fxml/ui/manutencao/TelaInicialManutencao.fxml", "Manutenção", c -> {});
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private void editarBritador() {
        if (fichaTecnicaAtual == null) return;
        abrirJanelaComController("/fxml/ui/manutencao/EditarBritador.fxml", "Editar", c -> {
            if (c instanceof EditarBritadorController con) con.setFichaTecnica(fichaTecnicaAtual);
        });
        carregarDados();
    }

    private void abrirRegistro(String tipo) {
        abrirJanelaComController("/fxml/ui/manutencao/RegistrarTroca.fxml", "Registrar", c -> {
            if (c instanceof RegistrarTrocaController con) {
                con.setTipo(tipo);
                con.setFichaTecnica(fichaTecnicaAtual);
            }
        });
        carregarDados();
    }

    private void abrirJanelaComController(String fxml, String titulo, java.util.function.Consumer<Object> config) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if (config != null) config.accept(loader.getController());
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostrarAlerta(String titulo, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}