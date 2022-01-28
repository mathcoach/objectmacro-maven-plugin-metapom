package de.htwsaar.pluginusertest;

import de.htwsaar.sql.imp.template.MNotSoSimple;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;


/**
 *
 * @author hbui
 */
public class NotSoSimpleTest {
		
    @DisplayName("use template ")
	@Test
	public void testUseTemplate() {
        MNotSoSimple template = new MNotSoSimple("test");
        String result = template.toString();
        assertThat(result).isEqualToIgnoringNewLines("hallo not so simple test !!!!!");
	}
}