package de.htwsaar.pluginusertest;

import de.htwsaar.hpb.sql.MCreateTable;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author hbui
 */
public class CreateTableTest {
    
    @Test
    void createTable() {
        String name = "sqltablename";
        MCreateTable createTable = new MCreateTable(name);
        createTable.newCreateColumn("a", "varchar");
        createTable.newCreateColumn("b", "int");
        String sqlCmd = createTable.toString();
        String expected = "CREATE TABLE IF NOT EXISTS sqltablename (\n" +
"        a varchar  ,\n" +
"        b int  \n" +
")ENGINE=InnoDB DEFAULT CHARACTER SET=utf8  ;";
        assertThat(sqlCmd).isEqualToIgnoringWhitespace(expected);
    }
    
    @Test
    void createTableWithConstrains() {
        String tbName = "fruit";
        MCreateTable table = new MCreateTable(tbName);
        table.newCreateColumn("id", "BIG INT");
        table.newCreateColumn("origin", "VARCHAR").newNotNull();
        table.newCreateColumn("best_before", "datetime").newNotNull();
        table.newCreateColumn("charge", "BIG INT");
        table.newConstraints("someconstrain").newPrimaryKey("id");
        String result = table.toString();
        String expected = "CREATE TABLE IF NOT EXISTS fruit (\n" +
"        id BIG INT  ,\n" +
"        origin VARCHAR  NOT NULL  ,\n" +
"        best_before datetime  NOT NULL  ,\n" +
"        charge BIG INT  ,\n" +
"        CONSTRAINTS someconstrain  PRIMARY KEY(id) \n" +
")ENGINE=InnoDB DEFAULT CHARACTER SET=utf8  ;";
        assertThat(result).isEqualToIgnoringWhitespace(expected);
    }
}
