package app.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import app.dao.ItemDAO;
import app.model.Item;
import app.model.Particao;
import app.service.ItemService;
import app.service.ParticaoService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class EditarGeralController {

    @FXML private Label lblTitulo;
    @FXML private VBox formItem;
    @FXML private VBox formParticao;

    @FXML private TextField txtNomeItem, txtEspecificacao, txtLocalAplicacao, txtLocalArmazenamento;
    @FXML private TextField txtNomeParticao;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Particao> cbParticaoItem;
    @FXML private Label lblEstoqueVisual, lblAvisoParticao;
    @FXML private ImageView imgPreview;
    @FXML private Button btnExcluir;

    private Object objetoEmEdicao;
    private String caminhoFoto = null;

    // Singleton Services
    private final ItemService itemService = ItemService.getInstance();
    private final ParticaoService particaoService = ParticaoService.getInstance();
    private final ItemDAO itemDAO = new ItemDAO(); // Pode manter assim ou criar Service para Update/Delete

    public void setItem(Item item) {
        this.objetoEmEdicao = item;
        ativarModoItem();
        preencherDadosItem(item);
    }

    public void setParticao(Particao particao) {
        this.objetoEmEdicao = particao;
        ativarModoParticao();
        preencherDadosParticao(particao);
    }

    private void ativarModoItem() {
        formItem.setVisible(true);
        formParticao.setVisible(false);
        lblTitulo.setText("EDITAR ITEM");
        cbParticaoItem.setItems(FXCollections.observableArrayList(particaoService.listarTodas()));
        cbParticaoItem.setConverter(new StringConverter<Particao>() {
            @Override public String toString(Particao p) { return p == null ? null : p.getNomeParticao(); }
            @Override public Particao fromString(String s) { return null; }
        });
    }

    private void ativarModoParticao() {
        formItem.setVisible(false);
        formParticao.setVisible(true);
        lblTitulo.setText("EDITAR PARTIÇÃO");
    }

    private void preencherDadosItem(Item item) {
        txtNomeItem.setText(item.getNome());
        txtEspecificacao.setText(item.getEspecificacao());
        txtLocalAplicacao.setText(item.getLocalAplicacao());
        lblEstoqueVisual.setText(String.valueOf(item.getEstoque()));
        caminhoFoto = item.getFoto();

        String desc = item.getDescricao();
        if (desc != null && desc.contains("[Local:")) {
            try {
                int inicio = desc.indexOf("[Local:");
                int fim = desc.indexOf("]", inicio);
                if (fim > inicio) {
                    txtLocalArmazenamento.setText(desc.substring(inicio + 7, fim).trim());
                    String tagCompleta = desc.substring(inicio, fim + 1);
                    txtDescricao.setText(desc.replace(tagCompleta, "").trim());
                } else {
                    txtDescricao.setText(desc);
                }
            } catch (Exception e) {
                txtDescricao.setText(desc);
            }
        } else {
            txtDescricao.setText(desc != null ? desc : "");
            txtLocalArmazenamento.clear();
        }

        cbParticaoItem.getItems().stream()
                .filter(p -> p.getIdParticao() == item.getIdParticao())
                .findFirst()
                .ifPresent(p -> cbParticaoItem.setValue(p));

        btnExcluir.setDisable(item.getEstoque() > 0);
        carregarImagemPreview(caminhoFoto);
    }

    private void preencherDadosParticao(Particao p) {
        txtNomeParticao.setText(p.getNomeParticao());
        boolean podeExcluir = particaoService.podeExcluir(p.getIdParticao());
        btnExcluir.setDisable(!podeExcluir);
        lblAvisoParticao.setVisible(!podeExcluir);
    }

    private void carregarImagemPreview(String caminho) {
        if (caminho != null && !caminho.isEmpty()) {
            try {
                imgPreview.setImage(new Image(itemService.prepararCaminhoImagem(caminho)));
            } catch (Exception e) { imgPreview.setImage(null); }
        } else { imgPreview.setImage(null); }
    }

    // --- AÇÃO SEGURA SEM REFLECTION ---
    @FXML
    void handleNovaEntrada(ActionEvent event) {
        if (!(objetoEmEdicao instanceof Item item)) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/TelaEntradaItem.fxml"));
            Parent root = loader.load();

            // MUDANÇA: Pega o controller com tipo correto direto
            EntradaItemController controller = loader.getController();
            controller.setItem(item);

            Stage stage = new Stage();
            stage.setTitle("Entrada de Estoque");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Item atualizado = itemService.buscarPorId(item.getIdItem());
            if (atualizado != null) {
                this.objetoEmEdicao = atualizado;
                lblEstoqueVisual.setText(String.valueOf(atualizado.getEstoque()));
                btnExcluir.setDisable(atualizado.getEstoque() > 0);
            }
        } catch (IOException e) {
            alertaErro("Erro ao abrir tela de entrada: " + e.getMessage());
        }
    }

    @FXML
    void handleSalvar(ActionEvent event) {
        if (objetoEmEdicao instanceof Item item) salvarItem(item);
        else if (objetoEmEdicao instanceof Particao p) salvarParticao(p);
    }

    private void salvarItem(Item item) {
        if (txtNomeItem.getText().isEmpty() || cbParticaoItem.getValue() == null) {
            alertaErro("Nome e Partição são obrigatórios.");
            return;
        }
        item.setNome(txtNomeItem.getText());
        item.setIdParticao(cbParticaoItem.getValue().getIdParticao());
        item.setEspecificacao(txtEspecificacao.getText());
        item.setLocalAplicacao(txtLocalAplicacao.getText());

        String desc = txtDescricao.getText().trim();
        String locArm = txtLocalArmazenamento.getText().trim();
        if (!locArm.isEmpty()) desc += "\n[Local: " + locArm + "]";

        item.setDescricao(desc);
        item.setFoto(caminhoFoto);

        if (itemDAO.atualizar(item)) fecharJanela();
        else alertaErro("Erro ao salvar item no banco.");
    }

    private void salvarParticao(Particao p) {
        if (txtNomeParticao.getText().trim().isEmpty()) {
            alertaErro("Nome inválido.");
            return;
        }
        p.setNomeParticao(txtNomeParticao.getText());
        if (particaoService.atualizar(p)) fecharJanela();
        else alertaErro("Erro ao salvar partição.");
    }

    @FXML
    void handleExcluir(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja excluir?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sucesso = false;
            if (objetoEmEdicao instanceof Item item) {
                sucesso = itemDAO.deletar(item.getIdItem());
            } else if (objetoEmEdicao instanceof Particao p) {
                sucesso = particaoService.deletar(p.getIdParticao());
            }

            if (sucesso) fecharJanela();
            else alertaErro("Falha ao excluir o registro.");
        }
    }

    @FXML
    void handleAlterarFoto(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.png", "*.jpeg"));
        File f = fc.showOpenDialog(lblTitulo.getScene().getWindow());
        if (f != null) {
            caminhoFoto = f.toURI().toString();
            carregarImagemPreview(caminhoFoto);
        }
    }

    @FXML void handleCancelar(ActionEvent event) { fecharJanela(); }

    private void fecharJanela() { ((Stage) lblTitulo.getScene().getWindow()).close(); }
    private void alertaErro(String msg) { new Alert(Alert.AlertType.ERROR, msg).show(); }
}