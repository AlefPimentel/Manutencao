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

public class RetiradaItemController {

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
        lblEstoqueAtual.setText(String.valueOf(item.getEstoque()));
    }

    @FXML
    void handleConfirmar(ActionEvent event) {
        if (txtQuantidade.getText().isEmpty() || txtResponsavel.getText().isEmpty()) {
            alerta("Erro", "Preencha todos os campos.");
            return;
        }

        int qtdRetirada = Integer.parseInt(txtQuantidade.getText());
        if (qtdRetirada <= 0) {
            alerta("Erro", "Quantidade inválida.");
            return;
        }
        if (qtdRetirada > itemAlvo.getEstoque()) {
            alerta("Erro", "Estoque insuficiente. Disponível: " + itemAlvo.getEstoque());
            return;
        }

        // 1. Atualiza Item
        itemAlvo.setEstoque(itemAlvo.getEstoque() - qtdRetirada);

        // 2. Cria Registro
        Registro reg = new Registro();
        reg.setTipo("Saída"); // CORRIGIDO: setTipo em vez de setNome
        reg.setQuantidade(qtdRetirada);
        reg.setResponsavel(txtResponsavel.getText());
        reg.setData(LocalDate.now()); // Data Correta
        reg.setIdItem(itemAlvo.getIdItem());
        reg.setNomeItem(itemAlvo.getNome());

        // 3. Salva
        if (itemDAO.atualizar(itemAlvo) && registroDAO.inserir(reg)) {
            fecharJanela(event);
        } else {
            alerta("Erro", "Falha ao salvar no banco.");
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(event); }

    private void fecharJanela(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void alerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}