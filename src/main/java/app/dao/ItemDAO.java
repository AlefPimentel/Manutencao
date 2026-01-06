package app.dao;

import app.database.ConnectionFactory;
import app.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // ... (listarTodos e buscarPorId continuam IGUAIS, não mudei) ...
    public List<Item> listarTodos() {
        List<Item> lista = new ArrayList<>();
        String sql = "SELECT * FROM Item";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { lista.add(mapearItem(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Item buscarPorId(int idItem) {
        String sql = "SELECT * FROM Item WHERE idItem = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idItem);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapearItem(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =====================================================================
    // MUDANÇA IMPORTANTE AQUI: O método agora retorna boolean, mas
    // ATUALIZA o ID do objeto 'item' passado por referência.
    // =====================================================================
    public boolean inserir(Item item) {
        String sql = """
            INSERT INTO Item 
            (nome, foto, estoque, localAplicacao, especificacao, descricao, dataCadastro, idParticao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             // Adicionamos RETURN_GENERATED_KEYS para pegar o ID novo
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getFoto());
            stmt.setInt(3, item.getEstoque());
            stmt.setString(4, item.getLocalAplicacao());
            stmt.setString(5, item.getEspecificacao());
            stmt.setString(6, item.getDescricao());

            if (item.getDataCadastro() != null) {
                stmt.setDate(7, Date.valueOf(item.getDataCadastro()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.setInt(8, item.getIdParticao());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Recupera o ID gerado e coloca no objeto
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setIdItem(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir item: " + e.getMessage());
        }
        return false;
    }

    // ... (atualizar, deletar e mapearItem continuam IGUAIS) ...
    public boolean atualizar(Item item) {
        String sql = "UPDATE Item SET nome=?, foto=?, estoque=?, localAplicacao=?, especificacao=?, descricao=?, dataCadastro=?, idParticao=? WHERE idItem=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getFoto());
            stmt.setInt(3, item.getEstoque());
            stmt.setString(4, item.getLocalAplicacao());
            stmt.setString(5, item.getEspecificacao());
            stmt.setString(6, item.getDescricao());
            if (item.getDataCadastro() != null) stmt.setDate(7, Date.valueOf(item.getDataCadastro()));
            else stmt.setNull(7, Types.DATE);
            stmt.setInt(8, item.getIdParticao());
            stmt.setInt(9, item.getIdItem());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deletar(int idItem) {
        String sql = "DELETE FROM Item WHERE idItem = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idItem);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Item mapearItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setIdItem(rs.getInt("idItem"));
        item.setNome(rs.getString("nome"));
        item.setFoto(rs.getString("foto"));
        item.setEstoque(rs.getInt("estoque"));
        item.setLocalAplicacao(rs.getString("localAplicacao"));
        item.setEspecificacao(rs.getString("especificacao"));
        item.setDescricao(rs.getString("descricao"));
        Date dataSql = rs.getDate("dataCadastro");
        if (dataSql != null) item.setDataCadastro(dataSql.toLocalDate());
        item.setIdParticao(rs.getInt("idParticao"));
        return item;
    }
}