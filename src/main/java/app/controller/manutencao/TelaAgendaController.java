package app.controller.manutencao;

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
import javafx.stage.Stage;
import app.dao.daomanutencao.AgendaDAO;
import app.model.manutencao.Agenda;
import app.service.JanelaService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaAgendaController {

    @FXML private DatePicker dpData;
    @FXML private TextField txtDescricao;
    @FXML private TableView<Agenda> tabelaAgenda;
    @FXML private TableColumn<Agenda, String> colData;
    @FXML private TableColumn<Agenda, String> colDescricao;
    @FXML private TableColumn<Agenda, Void> colAcao;

    // MUDANÇA: Singleton
    private final AgendaDAO agendaDAO = AgendaDAO.getInstance();
    private final JanelaService janelaService = JanelaService.getInstance();

    @FXML
    public void initialize() {
        dpData.setValue(LocalDate.now().plusDays(7));
        configurarTabela();
        carregarDados();
    }

    private void configurarTabela() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colData.setCellValueFactory(cell -> {
            if (cell.getValue().getData() != null) return new SimpleStringProperty(cell.getValue().getData().format(dtf));
            return new SimpleStringProperty("-");
        });

        colDescricao.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colAcao.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Concluir/Excluir");
            {
                btn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Agenda a = getTableView().getItems().get(getIndex());
                    agendaDAO.deletar(a.getId());
                    carregarDados();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void carregarDados() {
        tabelaAgenda.setItems(FXCollections.observableArrayList(agendaDAO.listarTodos()));
    }

    @FXML
    void handleAdd(ActionEvent event) {
        if (dpData.getValue() == null || txtDescricao.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha a data e a descrição.").show();
            return;
        }

        Agenda nova = new Agenda();
        nova.setData(dpData.getValue());
        nova.setTipo(txtDescricao.getText());

        if (agendaDAO.inserir(nova)) {
            txtDescricao.clear();
            carregarDados();
        } else {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar.").show();
        }
    }

    @FXML
    void handleVoltar(ActionEvent event) {
        janelaService.abrirJanela("/fxml/ui/manutencao/TelaInicialManutencao.fxml", "Manutenção", c -> {});
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}