package data;

import business.Paletes.Palete;
import business.Robot.Instrucao;
import business.Robot.Robot;

import java.sql.*;
import java.util.*;

public class RobotDAO implements Map<Integer, Robot> {
    private static RobotDAO singleton = null;


    private RobotDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Robot (" +
                    "Identificador INT NOT NULL AUTO_INCREMENT ," +
                    "Localizacao INT NOT NULL DEFAULT 0," +
                    "Recolhida TINYINT(1) NOT NULL ," +
                    "Instrucao VARCHAR(100) DEFAULT NULL ," +
                    "idPalete INT DEFAULT NULL ," +
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
    public static RobotDAO getInstance() {
        if (RobotDAO.singleton == null) {
            RobotDAO.singleton = new RobotDAO();
        }
        return RobotDAO.singleton;
    }

    /**
     * @return número de robots na base de dados
     */
    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Robot")) {
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
     * Método que verifica se existem robots
     * @return true se existirem 0 robots
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Método que cerifica se um identificador de robot existe na base de dados
     * @param key id da Prateleira
     * @return true se a Prateleira existe
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs =
                     stm.executeQuery("SELECT Identificador FROM Robot WHERE Identificador="+key)) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se um robot existe na base de dados
     * @param value Identificador da prateleira
     * @return true se prateleira existe
     */
    @Override
    public boolean containsValue(Object value) {
        Palete a = (Palete) value;
        return this.containsKey(a.getIdentificador());
    }


    /**
     * Obter um robot, dado o seu identificador
     * @param key identificador do robot
     * @return robot caso exista (null noutro caso)
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public Robot get(Object key) {
        Robot a = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Robot WHERE Identificador="+key)) {
            if (rs.next()) {
                if (rs.getString("Instrucao") == null) {
                    a = new Robot(rs.getInt("Localizacao"),rs.getInt("Identificador"),false,null);
                } else {
                    a = new Robot(rs.getInt("Localizacao"), rs.getInt("Identificador"),rs.getInt("Recolhida") == 1 ,new Instrucao(rs.getString("Instrucao"), rs.getInt("idPalete")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    /**
     * Insere um robot na base de dados
     * @param key Identificador do robot
     * @param a O robot
     * @return Devolve o valor existente, caso exista um
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public Robot put(Integer key, Robot a) {
        Robot res = null;
        Instrucao i = a.getInstrucao();
        int recolhida = 0;
        if (a.getRecolhida()) recolhida++;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            if(this.containsKey(a.getIdentificador())) res = this.get(key);
            if(i == null)
                stm.executeUpdate(
                        "INSERT INTO Robot VALUES ('"+key+"', '"+a.getLocalizacao()+"', 0 ,NULL ,NULL)" +
                                "ON DUPLICATE KEY UPDATE Localizacao = "+a.getLocalizacao()+",Instrucao = NULL , Recolhida = 0 ,idPalete = NULL ");
            else
                stm.executeUpdate("INSERT INTO Robot VALUES ('"+key+"', '"+a.getLocalizacao()+"', '"+recolhida+"', '"+i.getCaminho()+"', "+i.getiDpalete()+")" +
                        "ON DUPLICATE KEY UPDATE Localizacao = "+a.getLocalizacao()+", Instrucao = '"+i.getCaminho()+"', idPalete = "+i.getiDpalete()+", Recolhida = "+recolhida );
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    /**
     * Remover um robot, dado o seu identificador
     * @param key Identificador do robot a remover
     * @return O robot removido
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public Robot remove(Object key) {
        Robot t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Robot WHERE Identificador='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    /**
     * Adicionar um conjunto de robots à base de dados
     * @param robot as alunos a adicionar
     * @throws NullPointerException Em caso de erro
     */
    @Override
    public void putAll(Map<? extends Integer, ? extends Robot> robot) {
        for(Robot a : robot.values()) {
            this.put(a.getIdentificador(), a);
        }
    }

    /**
     * Apagar todos os robots
     * @throws NullPointerException Em caso de erro.
     */
    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Robot");
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
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Robot ")) {
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
     * @return Todos os robots da base de dados
     */
    @Override
    public Collection<Robot> values() {
        Collection<Robot> col = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Robot")) {
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
     * @return Entry Set de todos os robots
     */
    @Override
    public Set<Entry<Integer, Robot>> entrySet() {
        Set<Entry<Integer,Robot>> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Robot")) {
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
