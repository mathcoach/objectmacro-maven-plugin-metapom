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
    
}
