package app.controller.manutencao;

import javafx.animation.TranslateTransition;
import javafx.application.Platform; // IMPORTANTE
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane; // IMPORTANTE
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import app.dao.daomanutencao.LinhaDAO;
import app.model.manutencao.Linha;
import app.model.manutencao.ModeloBritador;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TelaInicialManutencaoController {

    // ADICIONEI O STACKPANE AQUI
    @FXML private StackPane rootPane;
    @FXML private VBox sideMenu;
    @FXML private FlowPane painelLinhas;
    @FXML private Button btnModoExclusao;

    private boolean menuAberto = false;
    private boolean modoExclusao = false;

    private final LinhaDAO linhaDAO = LinhaDAO.getInstance();

    @FXML
    public void initialize() {
        carregarLinhas();

        // --- MÁGICA DA TELA CHEIA ---
        // Usamos runLater porque o Stage (Janela) ainda não existe no exato momento que o initialize roda
        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setMaximized(true); // Maximiza (ocupa tudo mas mantém barra do Windows)
                // stage.setFullScreen(true); // Se quiser tela cheia TOTAL (sem barra do Windows, tipo jogo)
            }
        });
    }

    // ... (RESTO DO CÓDIGO CONTINUA IGUAL, SÓ COPIAR O QUE JÁ TINHA) ...

    private void carregarLinhas() {
        painelLinhas.getChildren().clear();
        List<Linha> linhas = linhaDAO.listarTodas();

        for (Linha linha : linhas) {
            Button btn = new Button();

            if (modoExclusao) {
                btn.setText("❌ " + linha.getNome());
                btn.setStyle("""
                    -fx-background-color: #FEE2E2;
                    -fx-text-fill: #991B1B;
                    -fx-border-color: #EF4444;
                    -fx-border-width: 2;
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 12;
                    -fx-border-radius: 12;
                    -fx-min-width: 260;
                    -fx-min-height: 70;
                    -fx-cursor: hand;
                """);
            } else {
                btn.setText(linha.getNome());
                btn.setStyle("""
                    -fx-background-color: #2563EB;
                    -fx-text-fill: white;
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 12;
                    -fx-min-width: 260;
                    -fx-min-height: 70;
                    -fx-cursor: hand;
                    -fx-effect: dropshadow(three-pass-box, rgba(37,99,235,0.4), 15, 0, 0, 5);
                """);
            }

            btn.setOnAction(e -> {
                if (modoExclusao) {
                    confirmarExclusao(linha);
                } else {
                    abrirTelaLinha(linha);
                }
            });

            painelLinhas.getChildren().add(btn);
        }
    }

    @FXML
    void handleAlternarModoExclusao(ActionEvent event) {
        modoExclusao = !modoExclusao;
        if (modoExclusao) {
            btnModoExclusao.setText("Cancelar Exclusão");
            btnModoExclusao.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        } else {
            btnModoExclusao.setText("🗑 Excluir Linha");
            btnModoExclusao.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        }
        carregarLinhas();
    }

    private void confirmarExclusao(Linha linha) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir " + linha.getNome() + "?");
        alert.setContentText("ATENÇÃO: Isso apagará todas as TCs e históricos vinculados a esta linha. Essa ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (linhaDAO.deletar(linha.getId())) {
                carregarLinhas();
            } else {
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir a linha do banco de dados.").show();
            }
        }
    }

    private void abrirTelaLinha(Linha linha) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/TelaLinha.fxml"));
            Parent root = loader.load();
            TelaLinhaController controller = loader.getController();
            controller.setLinhaAtual(linha);
            Stage stage = new Stage();
            stage.setTitle("Manutenção - " + linha.getNome());
            stage.setScene(new Scene(root));
            stage.setMaximized(true); // Garante que a próxima tela também abra maximizada
            stage.show();
            ((Stage) painelLinhas.getScene().getWindow()).close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void handleNovaLinha(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/CadastrarLinha.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nova Linha de Produção");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarLinhas();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void toggleMenu(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideMenu);
        if (menuAberto) { transition.setToX(300); menuAberto = false; }
        else { transition.setToX(0); menuAberto = true; }
        transition.play();
    }

    @FXML void handleHp400(ActionEvent event) { abrirTelaBritadorGenerica(ModeloBritador.HP400, event); }
    @FXML void handleHp4(ActionEvent event) { abrirTelaBritadorGenerica(ModeloBritador.HP4, event); }
    @FXML void handleC110(ActionEvent event) { abrirTelaBritadorGenerica(ModeloBritador.C110, event); }

    private void abrirTelaBritadorGenerica(ModeloBritador modelo, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/TelaGenericaBritador.fxml"));
            Parent root = loader.load();
            TelaGenericaBritadorController controller = loader.getController();
            controller.setModelo(modelo);
            Stage stage = new Stage();
            stage.setTitle("Manutenção - " + modelo.getNome());
            stage.setScene(new Scene(root));
            stage.setMaximized(true); // Maximiza a próxima tela
            stage.show();
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela genérica: " + e.getMessage()).show();
        }
    }

    @FXML void handleHistoricoGeral(ActionEvent event) { abrirJanela("/fxml/ui/manutencao/TelaHistoricoManutencao.fxml", "Histórico", event); }
    @FXML void handleAgenda(ActionEvent event) { abrirJanela("/fxml/ui/manutencao/TelaAgenda.fxml", "Agenda", event); }

    @FXML
    void handleVoltar(ActionEvent event) {
        abrirJanela("/fxml/ui/TelaInicial.fxml", "Menu Principal", event);
    }

    private void abrirJanela(String fxmlPath, String titulo, ActionEvent eventToClose) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setMaximized(true); // Maximiza a próxima tela
            stage.show();
            ((Stage)((Node)eventToClose.getSource()).getScene().getWindow()).close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}