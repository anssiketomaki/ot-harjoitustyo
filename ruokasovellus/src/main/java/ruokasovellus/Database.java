
package ruokasovellus;

import java.sql.*;
import java.sql.SQLException;
import java.sql.Connection;


/**
* Tietokantaa käyttävä luokka
* 
*/

public class Database {
   /**
    * Tietokannan yhteys. 
    */
    public Connection db;
    
    
    public Database() throws SQLException {
        
    }
    /**
    * Metodi luo tietokannan taulut ja on ajastettu toimimaan kun ikkuna aukeaa.
    * 
    * @return totuusarvo menivätkö tietokantatoiminnot läpi poikkeusta
    */
    public boolean createTables() {
        try {
            this.openConnection();
            Statement s = db.createStatement();
            s.execute("CREATE TABLE Incredients(id INTEGER PRIMARY KEY, name TEXT UNIQUE, kcal INTEGER, ch INTEGER, prot INTEGER, fat INTEGER)");
            s.execute("CREATE TABLE Portions(id INTEGER PRIMARY KEY, name TEXT UNIQUE)");
            s.execute("CREATE TABLE DishContents(portion_id INTEGER, incredient_id INTEGER, amount INTEGER)");
            s.execute("CREATE TABLE Diary(date TEXT UNIQUE, kcal INTEGER, ch INTEGER, prot INTEGER, fat INTEGER, water INTEGER)");

            s.execute("PRAGMA foreign_keys= ON");
            
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("VIRHE: Taulujen luominen ei onnistunut.");
            return false;
        }
    }
    /**
    * Metodi poistaa tietokannan taulut (tämä tarpeen testauksessa).
    * 
    * @return totuusarvo menivätkö tietokantatoiminnot läpi poikkeusta
    */
    public boolean dropTables() {
        try {
            this.openConnection();
            Statement s = db.createStatement();
            s.execute("PRAGMA foreign_keys=OFF");
            s.execute("DROP TABLE Diary");
            s.execute("DROP TABLE DishContents");
            s.execute("DROP TABLE Incredients");
            s.execute("DROP TABLE Portions");
            s.execute("PRAGMA foiregn_keys=ON");
            
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("VIRHE: Taulujen poistaminen ei onnistunut.");
            return false;
        }
    }
    /**
    * Metodi sulkee ohjelman yhteyden tietokantaan.
    */
    public void closeConnection() {
        try {
            db.close();
        } catch (SQLException e) {
            System.out.println("yhteyden sulku epäonnistui.");
        }
    }
    /**
    * Metodi avaa ohjelmalle yhteyden tietokantaan.
    */
    public void openConnection() {
        try {
            db = DriverManager.getConnection("jdbc:sqlite:ruokasovellus.db");
        } catch (SQLException e) {
            System.out.println("yhteyden avaus epäonnistui.");
        }
    }
    
}
