package app.service;

import app.dao.ParticaoDAO;
import app.model.Particao;
import java.util.List;

public class ParticaoService {

    private static ParticaoService instance;
    private final ParticaoDAO dao;

    private ParticaoService() {
        this.dao = new ParticaoDAO();
    }

    public static synchronized ParticaoService getInstance() {
        if (instance == null) {
            instance = new ParticaoService();
        }
        return instance;
    }

    public List<Particao> listarTodas() {
        return dao.listar();
    }

    public boolean atualizar(Particao particao) {
        if (particao.getNomeParticao() == null || particao.getNomeParticao().trim().isEmpty()) {
            return false;
        }
        return dao.atualizar(particao);
    }

    public boolean podeExcluir(int idParticao) {
        return dao.contarItens(idParticao) == 0;
    }

    public boolean deletar(int idParticao) {
        if (podeExcluir(idParticao)) {
            return dao.deletar(idParticao);
        }
        return false;
    }
}