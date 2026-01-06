package app.dao;

import app.database.ConnectionFactory;
import app.model.Registro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    // --- LISTAR TUDO ---
    public List<Registro> listarGlobal() {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Registro ORDER BY data DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearRegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar registros: " + e.getMessage());
        }
        return lista;
    }

    // --- LISTAR POR ITEM ---
    public List<Registro> listarPorItem(int idItem) {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Registro WHERE idItem = ? ORDER BY data DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearRegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar por item: " + e.getMessage());
        }
        return lista;
    }

    // --- INSERIR NOVO REGISTRO ---
    public boolean inserir(Registro registro) {
        // ATENÇÃO: No banco a coluna ainda se chama 'nome' (provavelmente), mas no código é 'tipo'.
        // Mantive 'nome' no INSERT para não quebrar seu banco existente.
        String sql = """
            INSERT INTO Registro 
            (nome, quantidade, responsavel, data, idItem, nome_item_historico) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getTipo()); // Pega o TIPO (Entrada/Saída)
            stmt.setInt(2, registro.getQuantidade());
            stmt.setString(3, registro.getResponsavel());

            // CONVERSÃO DE DATA (LocalDate -> SQL Date)
            if (registro.getData() != null) {
                stmt.setDate(4, Date.valueOf(registro.getData()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, registro.getIdItem());
            stmt.setString(6, registro.getNomeItem()); // Snapshot do nome

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao inserir registro: " + e.getMessage());
            return false;
        }
    }

    private Registro mapearRegistro(ResultSet rs) throws SQLException {
        Registro r = new Registro();
        r.setIdRegistro(rs.getInt("idRegistro"));

        // No banco é 'nome', no objeto é 'tipo'
        r.setTipo(rs.getString("nome"));

        r.setQuantidade(rs.getInt("quantidade"));
        r.setResponsavel(rs.getString("responsavel"));

        // CONVERSÃO DE DATA (SQL Date -> LocalDate)
        Date dataSql = rs.getDate("data");
        if (dataSql != null) {
            r.setData(dataSql.toLocalDate());
        }

        r.setIdItem(rs.getInt("idItem"));
        r.setNomeItem(rs.getString("nome_item_historico"));

        return r;
    }
}