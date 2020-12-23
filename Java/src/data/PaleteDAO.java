package data;

import business.Paletes.Palete;
import business.Paletes.QrCode;

import java.sql.*;
import java.util.*;

public class PaleteDAO implements Map<Integer,Palete> {
    private static PaleteDAO singleton = null;


    private PaleteDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Palete (" +
                    "Identificador INT NOT NULL AUTO_INCREMENT ," +
                    "Material varchar(45) NOT NULL ," +
                    "Peso DOUBLE NOT NULL ," +
                    "Preco DOUBLE NOT NULL ,"+
                    "Localizacao INT NOT NULL ," +
                    "Espera TINYINT(1) DEFAULT 1 ," +
                    "QrCode VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (Identificador));";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Implementação do padrão Singleton
     * @return devolve a instância única desta classe
     */
    public static PaleteDAO getInstance() {
        if (PaleteDAO.singleton == null) {
            PaleteDAO.singleton = new PaleteDAO();
        }
        return PaleteDAO.singleton;
    }

    /**
     * @return número de paletes na base de dados
     */
    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Palete")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    /**
     * Método que verifica se existem paletes
     * @return true se existirem 0 paletes
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Método que cerifica se um identificador de palete existe na base de dados
     * @param key id da Palete
     * @return true se a Palete existe
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs =
                     stm.executeQuery("SELECT Identificador FROM Palete WHERE Identificador="+key)) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se uma palete existe na base de dados
     * @param value Identificador da palete
     * @return true se palete existe
     */
    @Override
    public boolean containsValue(Object value) {
        Palete a = (Palete) value;
        return this.containsKey(a.getIdentificador());
    }

    /**
     * Obter uma palete, dado o seu identificador
     * @param key identificador da palete
     * @return palete caso exista (null noutro caso)
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public Palete get(Object key) {
        Palete a = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Palete WHERE Identificador="+key)) {
            if (rs.next()) {
                a = new Palete(rs.getInt("Localizacao"), rs.getString("Material"), rs.getDouble("Peso"),
                               rs.getDouble("Preco"),rs.getBoolean("Espera"),rs.getInt("Identificador"),new QrCode(rs.getString("QrCode")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    /**
     * Insere uma palete na base de dados
     * @param key Identificador da Palete
     * @param a A palete
     * @return Devolve o valor existente, caso exista um.
     * @throws NullPointerException Em caso de erro;
     */
    @Override
    public Palete put(Integer key, Palete a) {
        Palete res = null;
        int espera = 0;
        if(a.getEspera()) espera++;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            if(this.containsKey(a.getIdentificador())) res = this.get(key);
            stm.executeUpdate(
                    "INSERT INTO Palete VALUES ('"+key+"', '"+a.getMaterial()+"', '"+a.getPeso()+"', '"+a.getPreco()+"', '"+a.getLocalizacao()+"', '"
                                                    +espera+"', '"+a.getQrCode().getCode()+"')" +
                            "ON DUPLICATE KEY UPDATE Localizacao = " + a.getLocalizacao() +", Espera = " +espera);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    /**
     * Remover uma palete, dado o seu identificador
     * @param key Identificador da palete a remover
     * @return A palete removida
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public Palete remove(Object key) {
        Palete t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Palete WHERE Identificador='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    /**
     * Adicionar um conjunto de paletes à base de dados
     * @param paletes as alunos a adicionar
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public void putAll(Map<? extends Integer, ? extends Palete> paletes) {
        for(Palete a : paletes.values()) {
            this.put(a.getIdentificador(), a);
        }
    }

    /**
     * Apagar todas as paletes
     * @throws NullPointerException Em caso de erro.
     */
    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Palete");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Retorna o key set de todos os identificadores
     * @return Set com todos os identificadores
     */
    @Override
    public Set<Integer> keySet() {
        Set<Integer> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Palete ")) {
            while (rs.next()) {
                r.add(rs.getInt("Identificador"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * @return Todas as paletes da base de dados
     */
    @Override
    public Collection<Palete> values() {
        Collection<Palete> col = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Palete")) {
            while (rs.next()) {
                col.add(this.get(rs.getInt("Identificador")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return col;
    }

    /**
     * @return Entry Set de todas as paletes
     */
    @Override
    public Set<Entry<Integer, Palete>> entrySet() {
        Set<Entry<Integer,Palete>> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Palete")) {
            while (rs.next()) {
                r.add(new AbstractMap.SimpleEntry<>(rs.getInt("Identificador"),this.get(rs.getInt("Identificador"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }
}
