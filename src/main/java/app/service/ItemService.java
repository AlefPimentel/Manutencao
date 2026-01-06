package app.service;

import app.dao.ItemDAO;
import app.model.Item;
import java.io.File;
import java.util.List;

public class ItemService {

    // 1. Instância estática única
    private static ItemService instance;

    private final ItemDAO dao;

    // 2. Construtor privado para impedir 'new ItemService()' fora daqui
    private ItemService() {
        this.dao = new ItemDAO();
    }

    // 3. Método público para pegar a instância
    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    public List<Item> listarTodos() {
        return dao.listarTodos();
    }

    public Item buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public String prepararCaminhoImagem(String caminhoAtual) {
        if (caminhoAtual == null || caminhoAtual.isEmpty()) {
            return null;
        }
        if (!caminhoAtual.startsWith("file:")) {
            File file = new File(caminhoAtual);
            return file.toURI().toString();
        }
        return caminhoAtual;
    }
}