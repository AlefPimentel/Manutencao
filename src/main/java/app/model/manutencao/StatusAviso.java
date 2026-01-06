package app.model.manutencao;

public class StatusAviso {
    private String componente;
    private int horasRestantes;
    private String status; // "VERDE", "AMARELO", "VERMELHO"
    private String mensagem;

    public StatusAviso(String componente, int horasRestantes, String status, String mensagem) {
        this.componente = componente;
        this.horasRestantes = horasRestantes;
        this.status = status;
        this.mensagem = mensagem;
    }

    public String getComponente() { return componente; }
    public int getHorasRestantes() { return horasRestantes; }
    public String getStatus() { return status; }
    public String getMensagem() { return mensagem; }
}