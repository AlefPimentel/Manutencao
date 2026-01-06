package app.controller.manutencao;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import app.dao.daomanutencao.LubrificarDAO;
import app.dao.daomanutencao.TcDAO;
import app.model.manutencao.Linha;
import app.model.manutencao.Lubrificar;
import app.model.manutencao.Tc;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaLinhaController {

    @FXML private Label lblTituloLinha;
    @FXML private FlowPane painelBotoesTc;

    @FXML private TableView<Lubrificar> tabelaLubrificacao;
    @FXML private TableColumn<Lubrificar, String> colData, colResponsavel, colGraxa, colEquipamento;
    @FXML private TableColumn<Lubrificar, Integer> colHorimetro;
    @FXML private Label lblNomeTc, lblMotor, lblRedutor, lblCorreia, lblTambor, lblRoleteCarga, lblRoleteRetorno, lblCavaleteCarga, lblCavaleteRetorno, lblDescricao;
    @FXML private Button btnEditar;

    private final TcDAO tcDAO = TcDAO.getInstance();
    private final LubrificarDAO lubrificarDAO = LubrificarDAO.getInstance();
    private Tc tcSelecionada;
    private Linha linhaAtual;

    public void setLinhaAtual(Linha linha) {
        this.linhaAtual = linha;
        if (linha != null) {
            lblTituloLinha.setText("MANUTENÇÃO - " + linha.getNome().toUpperCase());
            carregarListaTcs();
            carregarUltimasLubrificacoes();
        }
    }

    @FXML public void initialize() { carregarUltimasLubrificacoes(); }

    private void carregarListaTcs() {
        painelBotoesTc.getChildren().clear();
        if (linhaAtual == null) return;

        List<Tc> listaTcs = tcDAO.listarPorLinha(linhaAtual.getId());
        for (Tc tc : listaTcs) {
            Button btn = new Button(tc.getNome());
            btn.setStyle("-fx-min-width: 80; -fx-min-height: 80; -fx-background-color: white; -fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
            btn.setOnAction(e -> exibirDetalhes(tc));
            painelBotoesTc.getChildren().add(btn);
        }
    }

    private void exibirDetalhes(Tc tc) {
        this.tcSelecionada = tc;
        lblNomeTc.setText(tc.getNome());
        lblMotor.setText(validaTexto(tc.getMotor()));
        lblRedutor.setText(validaTexto(tc.getMancal()));
        lblCorreia.setText(validaTexto(tc.getCorreia()));
        lblTambor.setText(validaTexto(tc.getRolo()));
        lblRoleteCarga.setText(validaTexto(tc.getRoletec()));
        lblRoleteRetorno.setText(validaTexto(tc.getRoleter()));
        lblCavaleteCarga.setText(validaTexto(tc.getCavaletec()));
        lblCavaleteRetorno.setText(validaTexto(tc.getCavaleter()));
        lblDescricao.setText(validaTexto(tc.getDesc()));
        btnEditar.setDisable(false);
    }

    private String validaTexto(String texto) { return (texto == null || texto.trim().isEmpty()) ? "-" : texto; }

    private void carregarUltimasLubrificacoes() {
        if (linhaAtual == null) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getData().format(dtf)));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavel"));
        colHorimetro.setCellValueFactory(new PropertyValueFactory<>("horimetro"));
        colGraxa.setCellValueFactory(new PropertyValueFactory<>("graxa"));
        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("nomeEquipamento"));
        tabelaLubrificacao.setItems(FXCollections.observableArrayList(lubrificarDAO.listarPorLinha(linhaAtual.getId())));
    }

    @FXML
    void handleNovaTc(ActionEvent event) {
        if (linhaAtual == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/CadastrarTc.fxml"));
            Parent root = loader.load();
            CadastrarTcController controller = loader.getController();
            controller.setLinha(linhaAtual);
            Stage stage = new Stage();
            stage.setTitle("Nova TC");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarListaTcs();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void handleEngraxar(ActionEvent event) {
        if (linhaAtual == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/RegistrarLubrificacao.fxml"));
            Parent root = loader.load();
            RegistrarLubrificacaoController controller = loader.getController();
            controller.setLinha(linhaAtual);
            Stage stage = new Stage();
            stage.setTitle("Registrar Lubrificação");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarUltimasLubrificacoes();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- BOTÃO EDITAR AGORA FUNCIONAL ---
    @FXML
    void handleEditarTc(ActionEvent event) {
        if (tcSelecionada == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/TelaEditarTc.fxml"));
            Parent root = loader.load();

            // Passa a TC selecionada para o controller da tela de edição
            TelaEditarTcController controller = loader.getController();
            controller.setTc(tcSelecionada);

            Stage stage = new Stage();
            stage.setTitle("Editar " + tcSelecionada.getNome());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // Espera fechar

            // ATUALIZA A TELA APÓS EDIÇÃO
            carregarListaTcs(); // Recarrega botões (caso nome tenha mudado)

            // Tenta reselecionar e atualizar os detalhes
            // (Como a lista foi recriada, precisamos buscar o objeto atualizado no banco ou na lista)
            // Aqui simplifiquei: limpo a seleção para o usuário clicar de novo, ou você pode implementar uma busca pelo ID.
            lblNomeTc.setText("Selecione...");
            lblMotor.setText("-");
            // ... limpar outros campos se quiser ...
            btnEditar.setDisable(true);
            tcSelecionada = null;

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela de edição: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/TelaInicialManutencao.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            boolean estavaMaximizado = stage.isMaximized();
            stage.setScene(new Scene(root));
            if (estavaMaximizado) {
                stage.setMaximized(false);
                stage.setMaximized(true);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}