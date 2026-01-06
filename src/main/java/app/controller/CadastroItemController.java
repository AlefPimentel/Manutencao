package app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import app.dao.ItemDAO;
import app.dao.ParticaoDAO;
import app.model.Item;
import app.model.Particao;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class CadastroItemController {

    @FXML private ComboBox<Particao> cbParticao;
    @FXML private TextField txtNome;
    @FXML private TextField txtEstoque;
    @FXML private TextField txtEspecificacao;
    @FXML private TextField txtLocalArmaz;
    @FXML private TextField txtLocalAplic;
    @FXML private TextArea txtDescricao;
    @FXML private ImageView imgPreview;

    private String caminhoFotoSelecionada = null;
    private final ParticaoDAO particaoDAO = new ParticaoDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    @FXML
    public void initialize() {
        carregarParticoes();
        configurarMascaraNumerica();
    }

    private void carregarParticoes() {
        List<Particao> lista = particaoDAO.listar();
        ObservableList<Particao> obsLista = FXCollections.observableArrayList(lista);
        cbParticao.setItems(obsLista);

        cbParticao.setConverter(new StringConverter<Particao>() {
            @Override
            public String toString(Particao p) { return p == null ? null : p.getNomeParticao(); }
            @Override
            public Particao fromString(String string) { return null; } // Não usado
        });
    }

    private void configurarMascaraNumerica() {
        txtEstoque.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtEstoque.setText(newV.replaceAll("[^\\d]", ""));
        });
    }

    @FXML
    void handleSelecionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Foto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            caminhoFotoSelecionada = file.toURI().toString();
            imgPreview.setImage(new Image(caminhoFotoSelecionada));
        }
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (cbParticao.getValue() == null || txtNome.getText().isEmpty()) {
            mostrarAlerta("Atenção", "Preencha a partição e o nome.");
            return;
        }

        try {
            Item novoItem = new Item();
            novoItem.setIdParticao(cbParticao.getValue().getIdParticao());
            novoItem.setNome(txtNome.getText());

            String qtd = txtEstoque.getText();
            novoItem.setEstoque(qtd.isEmpty() ? 0 : Integer.parseInt(qtd));

            novoItem.setEspecificacao(txtEspecificacao.getText());
            novoItem.setLocalAplicacao(txtLocalAplic.getText());

            // Concatena Local Armazenamento na descrição
            String desc = txtDescricao.getText();
            if (!txtLocalArmaz.getText().isEmpty()) {
                desc += "\n[Local: " + txtLocalArmaz.getText() + "]";
            }
            novoItem.setDescricao(desc);

            novoItem.setFoto(caminhoFotoSelecionada);
            novoItem.setDataCadastro(LocalDate.now()); // Data Correta (LocalDate)

            if (itemDAO.inserir(novoItem)) {
                handleCancelar(event);
            } else {
                mostrarAlerta("Erro", "Falha ao salvar no banco.");
            }

        } catch (Exception e) {
            mostrarAlerta("Erro Crítico", e.getMessage());
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}