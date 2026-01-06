package app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import app.dao.RegistroDAO;
import app.model.Item;
import app.model.Registro;

import java.time.format.DateTimeFormatter; // <--- O IMPORT CERTO
import java.util.List;

public class MovimentacaoController {

    @FXML private Label lblTituloItem;
    @FXML private TableView<Registro> tabelaRegistros;
    @FXML private TableColumn<Registro, String> colData;
    @FXML private TableColumn<Registro, String> colItem;
    @FXML private TableColumn<Registro, String> colTipo;
    @FXML private TableColumn<Registro, Integer> colQtd;
    @FXML private TableColumn<Registro, String> colResponsavel;

    private Item itemAtual;
    private final RegistroDAO registroDAO = new RegistroDAO(); // DAO não tem singleton ainda, pode manter assim

    // Formatador para LocalDate
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarTabela();
        carregarDados();
    }

    public void setItem(Item item) {
        this.itemAtual = item;
        if (item != null) {
            lblTituloItem.setText("HISTÓRICO: " + item.getNome().toUpperCase());
        } else {
            lblTituloItem.setText("REGISTRO GERAL DE MOVIMENTAÇÕES");
        }
        carregarDados();
    }

    private void configurarTabela() {
        // --- CORREÇÃO DO CRASH NA TABELA ---
        colData.setCellValueFactory(cellData -> {
            if (cellData.getValue().getData() != null) {
                // Formata o LocalDate corretamente
                return new SimpleStringProperty(cellData.getValue().getData().format(dtf));
            }
            return new SimpleStringProperty("-");
        });

        if (colItem != null) {
            colItem.setCellValueFactory(new PropertyValueFactory<>("nomeItem"));
            colItem.setStyle("-fx-font-weight: bold;");
        }

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavel"));

        // Cores (Verde/Vermelho)
        colTipo.setCellFactory(column -> new TableCell<Registro, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("Entrada")) {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void carregarDados() {
        List<Registro> lista;
        if (itemAtual != null) {
            lista = registroDAO.listarPorItem(itemAtual.getIdItem());
        } else {
            lista = registroDAO.listarGlobal();
        }
        tabelaRegistros.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    void handleVoltar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}