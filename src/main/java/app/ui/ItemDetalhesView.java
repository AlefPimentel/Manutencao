package app.ui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import app.model.Item;
import app.service.ItemService; // Agora usando Singleton, mas aqui recebe no construtor

import java.time.format.DateTimeFormatter; // <--- O IMPORT CERTO

public class ItemDetalhesView {

    private final VBox paneDetalhes;
    private final Label lblMensagemVazia, lblNomeItem, lblEstoqueItem, lblLocalItem, lblLocalArmazenamento;
    private final Label lblEspecificacaoItem, lblDataItem, lblDescricaoItem;
    private final ImageView imgFotoItem;
    private final ItemService itemService;

    // Formatador moderno e estático (economiza memória)
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ItemDetalhesView(
            VBox paneDetalhes, Label lblMensagemVazia, Label lblNomeItem, Label lblEstoqueItem,
            Label lblLocalItem, Label lblLocalArmazenamento, Label lblEspecificacaoItem,
            Label lblDataItem, Label lblDescricaoItem, ImageView imgFotoItem, ItemService itemService
    ) {
        this.paneDetalhes = paneDetalhes;
        this.lblMensagemVazia = lblMensagemVazia;
        this.lblNomeItem = lblNomeItem;
        this.lblEstoqueItem = lblEstoqueItem;
        this.lblLocalItem = lblLocalItem;
        this.lblLocalArmazenamento = lblLocalArmazenamento;
        this.lblEspecificacaoItem = lblEspecificacaoItem;
        this.lblDataItem = lblDataItem;
        this.lblDescricaoItem = lblDescricaoItem;
        this.imgFotoItem = imgFotoItem;
        this.itemService = itemService;
    }

    public void mostrar(Item item, String localArmaz, String descricaoLimpa) {
        lblNomeItem.setText(item.getNome());
        lblEstoqueItem.setText(String.valueOf(item.getEstoque()));
        lblLocalItem.setText(item.getLocalAplicacao());
        lblEspecificacaoItem.setText(item.getEspecificacao());

        // --- CORREÇÃO DO CRASH ---
        if (item.getDataCadastro() != null) {
            // LocalDate sabe se formatar usando o DateTimeFormatter
            lblDataItem.setText(item.getDataCadastro().format(FORMATADOR_DATA));
        } else {
            lblDataItem.setText("-");
        }

        lblLocalArmazenamento.setText(localArmaz != null ? localArmaz : "-");
        lblDescricaoItem.setText(descricaoLimpa != null && !descricaoLimpa.isEmpty() ? descricaoLimpa : "-");

        if (item.getFoto() != null && !item.getFoto().isEmpty()) {
            try {
                // Singleton ou instância passada, o método é o mesmo
                String caminhoTratado = itemService.prepararCaminhoImagem(item.getFoto());
                imgFotoItem.setImage(new Image(caminhoTratado, true)); // 'true' carrega em background
            } catch (Exception e) {
                imgFotoItem.setImage(null);
            }
        } else {
            imgFotoItem.setImage(null);
        }

        paneDetalhes.setVisible(true);
        lblMensagemVazia.setVisible(false);
    }

    public void limpar() {
        paneDetalhes.setVisible(false);
        lblMensagemVazia.setVisible(true);
        lblNomeItem.setText("");
        lblEstoqueItem.setText("");
        lblLocalItem.setText("");
        lblEspecificacaoItem.setText("");
        lblDataItem.setText("");
        lblDescricaoItem.setText("");
        lblLocalArmazenamento.setText("-");
        imgFotoItem.setImage(null);
    }
}