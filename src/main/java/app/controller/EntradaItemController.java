package app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import app.dao.ItemDAO;
import app.dao.RegistroDAO;
import app.model.Item;
import app.model.Registro;

import java.time.LocalDate; // Import Correto

public class EntradaItemController {

    @FXML private Label lblNomeItem;
    @FXML private Label lblEstoqueAtual;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtResponsavel;

    private Item itemAlvo;
    private final ItemDAO itemDAO = new ItemDAO();
    private final RegistroDAO registroDAO = new RegistroDAO();

    @FXML
    public void initialize() {
        txtQuantidade.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtQuantidade.setText(newV.replaceAll("[^\\d]", ""));
        });
    }

    public void setItem(Item item) {
        this.itemAlvo = item;
        lblNomeItem.setText(item.getNome());
        lblEstoqueAtual.setText("Estoque Atual: " + item.getEstoque());
    }

    @FXML
    void handleConfirmar(ActionEvent event) {
        if (txtQuantidade.getText().isEmpty() || txtResponsavel.getText().isEmpty()) {
            alerta("Preencha todos os campos.");
            return;
        }

        int qtdEntrada = Integer.parseInt(txtQuantidade.getText());
        if (qtdEntrada <= 0) {
            alerta("Quantidade inválida.");
            return;
        }

        // 1. Atualiza Item
        itemAlvo.setEstoque(itemAlvo.getEstoque() + qtdEntrada);

        // 2. Cria Registro
        Registro reg = new Registro();
        reg.setTipo("Entrada"); // CORRIGIDO: setTipo
        reg.setQuantidade(qtdEntrada);
        reg.setResponsavel(txtResponsavel.getText());
        reg.setData(LocalDate.now()); // Data Correta
        reg.setIdItem(itemAlvo.getIdItem());
        reg.setNomeItem(itemAlvo.getNome());

        // 3. Salva
        if (itemDAO.atualizar(itemAlvo) && registroDAO.inserir(reg)) {
            fecharJanela(event);
        } else {
            alerta("Erro ao salvar no banco.");
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private void fecharJanela(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.show();
    }
}