package app.service;

import app.model.Item;

public class DescricaoService {

    private static DescricaoService instance;

    private DescricaoService() {}

    public static synchronized DescricaoService getInstance() {
        if (instance == null) {
            instance = new DescricaoService();
        }
        return instance;
    }

    public String extrairLocalArmazem(Item item) {
        if (item == null) return "-";
        String descricao = item.getDescricao();
        if (descricao == null) return "-";

        if (descricao.contains("[Local:")) {
            try {
                int inicio = descricao.indexOf("[Local:");
                int fim = descricao.indexOf("]", inicio);
                if (fim > inicio) {
                    return descricao.substring(inicio + 7, fim).trim();
                }
            } catch (Exception e) {
                // fallback
            }
        }
        return "-";
    }

    public String extrairDescricaoLimpa(Item item) {
        if (item == null) return "-";
        String descricao = item.getDescricao();
        if (descricao == null || descricao.trim().isEmpty()) return "-";

        if (descricao.contains("[Local:")) {
            try {
                int inicio = descricao.indexOf("[Local:");
                int fim = descricao.indexOf("]", inicio);
                if (fim > inicio) {
                    String limpa = descricao.replace(descricao.substring(inicio, fim + 1), "").trim();
                    return limpa.isEmpty() ? "-" : limpa;
                }
            } catch (Exception e) {
                // fallback
            }
        }
        return descricao.trim();
    }
}