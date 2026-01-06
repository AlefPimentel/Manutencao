package app.model;

public class Particao {

    private int idParticao;
    private String nomeParticao; // <-- MUDANÇA: Usar camelCase

    // --- Getters e Setters para idParticao ---
    public int getIdParticao() {
        return idParticao;
    }

    public void setIdParticao(int idParticao) {
        this.idParticao = idParticao;
    }

    // --- Getters e Setters para nomeParticao ---

    // O método DEVE se chamar getNomeParticao()
    public String getNomeParticao() {
        return nomeParticao;
    }

    // O método DEVE se chamar setNomeParticao()
    public void setNomeParticao(String nomeParticao) {
        this.nomeParticao = nomeParticao;
    }
}
