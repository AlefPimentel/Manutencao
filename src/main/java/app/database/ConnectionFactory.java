package app.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static Connection getConnection() {
        try {
            // Caminho da pasta do usuário (Funciona em Dev e Produção/.exe)
            String userHome = System.getProperty("user.home");
            String pathDir = userHome + File.separator + "SistemaPedreira";

            File diretorio = new File(pathDir);
            if (!diretorio.exists()) {
                diretorio.mkdirs(); // Cria a pasta se não existir
            }

            String url = "jdbc:sqlite:" + pathDir + File.separator + "appestoque.db";

            return DriverManager.getConnection(url);

        } catch (SQLException e) {
            throw new RuntimeException("ERRO CRÍTICO: Não foi possível conectar ao banco em: " + e.getMessage());
        }
    }
}