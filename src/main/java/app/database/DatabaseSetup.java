package app.database;

import app.model.manutencao.ModeloBritador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void criarTabelas() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            // Ativa chaves estrangeiras no SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            // --- ESTOQUE ---
            String sqlParticao = "CREATE TABLE IF NOT EXISTS Particao (idParticao INTEGER PRIMARY KEY AUTOINCREMENT, nome_particao VARCHAR(50) NOT NULL);";

            String sqlItem = """
                CREATE TABLE IF NOT EXISTS Item (
                    idItem INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(50) NOT NULL,
                    foto VARCHAR(50),
                    estoque INTEGER,
                    localAplicacao VARCHAR(50),
                    especificacao VARCHAR(50),
                    descricao VARCHAR(250),
                    dataCadastro DATE,
                    idParticao INTEGER,
                    FOREIGN KEY (idParticao) REFERENCES Particao(idParticao) ON DELETE SET NULL
                );
            """;

            String sqlRegistro = """
                CREATE TABLE IF NOT EXISTS Registro (
                    idRegistro INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(50) NOT NULL,
                    quantidade INTEGER,
                    responsavel VARCHAR(50),
                    data DATE,
                    idItem INTEGER,
                    nome_item_historico VARCHAR(100),
                    FOREIGN KEY (idItem) REFERENCES Item(idItem) ON DELETE SET NULL
                );
            """;

            // --- MANUTENÇÃO ---

            String sqlConfigMaquinas = """
               CREATE TABLE IF NOT EXISTS ConfiguracaoMaquinas (
                   modelo VARCHAR(50) PRIMARY KEY, 
                   correia VARCHAR(100),
                   revestimento VARCHAR(100),
                   oleoLub VARCHAR(100),
                   oleoHidr VARCHAR(100),
                   horimetro_atual INTEGER DEFAULT 0,
                   data_atualizacao_horimetro DATE
               );
            """;

            String sqlConfigAvisos = """
                CREATE TABLE IF NOT EXISTS ConfiguracaoAvisos (
                    modelo_ou_linha VARCHAR(50),
                    componente VARCHAR(50),
                    vida_util_horas INTEGER,
                    PRIMARY KEY (modelo_ou_linha, componente)
                );
            """;

            String sqlTroca = """
                CREATE TABLE IF NOT EXISTS Troca (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data DATE,
                    responsavel VARCHAR(100),
                    horimetro INTEGER,
                    tipo VARCHAR(50),
                    especificacao VARCHAR(100),
                    cfsf BOOLEAN,
                    nome_equipamento VARCHAR(50),
                    id_equipamento INTEGER,
                    FOREIGN KEY (id_equipamento) REFERENCES Tc(id) ON DELETE SET NULL
                );
            """;

            String sqlLubrificar = """
                CREATE TABLE IF NOT EXISTS Lubrificar (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data DATE,
                    responsavel VARCHAR(50),
                    horimetro INTEGER,
                    graxa VARCHAR(50),
                    id_linha INTEGER,
                    FOREIGN KEY (id_linha) REFERENCES Linha(id) ON DELETE CASCADE
                );
            """;

            String sqlAgenda = """
                CREATE TABLE IF NOT EXISTS Agenda (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data DATE,
                    tipo VARCHAR(50)
                );
            """;

            String sqlLinha = """
                CREATE TABLE IF NOT EXISTS Linha (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(50) NOT NULL
                );
            """;

            String sqlTc = """
                CREATE TABLE IF NOT EXISTS Tc (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(100),
                    roletec VARCHAR(100),
                    roleter VARCHAR(100),
                    correia VARCHAR(100),
                    cavaletec VARCHAR(100),
                    cavaleter VARCHAR(100),
                    descricao VARCHAR(250),
                    rolo VARCHAR(100),
                    mancal VARCHAR(100),
                    motor VARCHAR(100),
                    id_linha INTEGER,
                    FOREIGN KEY (id_linha) REFERENCES Linha(id) ON DELETE SET NULL
                );
            """;

            // Executa criação das tabelas (Estoque e Manutenção apenas)
            stmt.execute(sqlParticao);
            stmt.execute(sqlItem);
            stmt.execute(sqlRegistro);

            stmt.execute(sqlConfigMaquinas);
            stmt.execute(sqlConfigAvisos);
            stmt.execute(sqlLinha);
            stmt.execute(sqlTc);
            stmt.execute(sqlTroca);
            stmt.execute(sqlLubrificar);
            stmt.execute(sqlAgenda);

            System.out.println("Tabelas verificadas/criadas.");

            verificarAtualizacoesSchema(conn);
            inicializarFichasTecnicas(conn);
            inicializarAvisosPadrao(conn);

            // Removido: inicializarMotivosParada(conn);

        } catch (SQLException e) {
            System.err.println("Erro no setup: " + e.getMessage());
        }
    }

    private static void verificarAtualizacoesSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.executeQuery("SELECT id_equipamento FROM Troca LIMIT 1"); }
            catch (SQLException e) { stmt.execute("ALTER TABLE Troca ADD COLUMN id_equipamento INTEGER REFERENCES Tc(id) ON DELETE SET NULL"); }

            try { stmt.executeQuery("SELECT horimetro_atual FROM ConfiguracaoMaquinas LIMIT 1"); }
            catch (SQLException e) { stmt.execute("ALTER TABLE ConfiguracaoMaquinas ADD COLUMN horimetro_atual INTEGER DEFAULT 0"); }

            try { stmt.executeQuery("SELECT data_atualizacao_horimetro FROM ConfiguracaoMaquinas LIMIT 1"); }
            catch (SQLException e) { stmt.execute("ALTER TABLE ConfiguracaoMaquinas ADD COLUMN data_atualizacao_horimetro DATE"); }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar atualizações de schema: " + e.getMessage());
        }
    }

    private static void inicializarFichasTecnicas(Connection conn) {
        String checkSql = "SELECT count(*) FROM ConfiguracaoMaquinas WHERE modelo = ?";
        String insertSql = "INSERT INTO ConfiguracaoMaquinas (modelo, correia, revestimento, oleoLub, oleoHidr, horimetro_atual) VALUES (?, ?, ?, ?, ?, 0)";
        try {
            for (ModeloBritador modelo : ModeloBritador.values()) {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, modelo.getNome());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, modelo.getNome());
                            insertStmt.setString(2, "Padrão");
                            insertStmt.setString(3, "Padrão");
                            insertStmt.setString(4, "Padrão");
                            insertStmt.setString(5, "Padrão");
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) { System.err.println("Erro ao inicializar fichas: " + e.getMessage()); }
    }

    private static void inicializarAvisosPadrao(Connection conn) {
        String sql = "INSERT OR IGNORE INTO ConfiguracaoAvisos (modelo_ou_linha, componente, vida_util_horas) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "HP400"); stmt.setString(2, "Revestimento"); stmt.setInt(3, 2000); stmt.addBatch();
            stmt.setString(1, "HP400"); stmt.setString(2, "Correia"); stmt.setInt(3, 5000); stmt.addBatch();
            stmt.setString(1, "HP400"); stmt.setString(2, "OleoHidraulico"); stmt.setInt(3, 1000); stmt.addBatch();
            stmt.setString(1, "HP400"); stmt.setString(2, "OleoLubrificacao"); stmt.setInt(3, 500); stmt.addBatch();
            stmt.setString(1, "HP4"); stmt.setString(2, "Revestimento"); stmt.setInt(3, 2000); stmt.addBatch();
            stmt.setString(1, "HP4"); stmt.setString(2, "Correia"); stmt.setInt(3, 5000); stmt.addBatch();
            stmt.setString(1, "HP4"); stmt.setString(2, "OleoHidraulico"); stmt.setInt(3, 1000); stmt.addBatch();
            stmt.setString(1, "HP4"); stmt.setString(2, "OleoLubrificacao"); stmt.setInt(3, 500); stmt.addBatch();
            stmt.setString(1, "C110"); stmt.setString(2, "Revestimento"); stmt.setInt(3, 1500); stmt.addBatch();
            stmt.setString(1, "C110"); stmt.setString(2, "Correia"); stmt.setInt(3, 5000); stmt.addBatch();
            stmt.setString(1, "C110"); stmt.setString(2, "OleoHidraulico"); stmt.setInt(3, 1000); stmt.addBatch();
            stmt.setString(1, "C110"); stmt.setString(2, "OleoLubrificacao"); stmt.setInt(3, 500); stmt.addBatch();
            stmt.setString(1, "LINHA_GERAL"); stmt.setString(2, "Lubrificacao"); stmt.setInt(3, 40); stmt.addBatch();
            stmt.executeBatch();
        } catch (SQLException e) { System.err.println("Erro ao inicializar avisos: " + e.getMessage()); }
    }
}