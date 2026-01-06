package app.model.manutencao;

public enum ModeloBritador {
    HP400("HP400"),
    HP4("HP4"),
    C110("C110");

    private final String nome;

    ModeloBritador(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    // Método auxiliar para buscar pelo nome (útil no banco)
    public static ModeloBritador fromString(String text) {
        for (ModeloBritador b : ModeloBritador.values()) {
            if (b.nome.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null; // Ou lançar exceção
    }
}