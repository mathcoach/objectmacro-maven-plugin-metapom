package de.htwsaar.pluginusertest;

import de.htwsaar.pluginusertest.TemplateUser;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hbui
 */
public class TemplateUserTest {
	
	public TemplateUserTest() {
	}

	@Test
	public void testUseTemplate() {
		try{
			TemplateUser u = new TemplateUser();
			u.useTemplate("hallo");
		}catch(Exception ex){
			fail("Not expected any exeption.");
		}
	}
}