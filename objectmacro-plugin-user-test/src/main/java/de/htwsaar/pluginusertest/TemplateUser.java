package de.htwsaar.pluginusertest;
import de.htwsaar.sql.imp.template.MNotSoSimple;
/**
 *
 * @author phucluoi
 * @version Feb 3, 2013
 */
public class TemplateUser {
	public void useTemplate(String dummy){
		MNotSoSimple s = new MNotSoSimple(dummy);
		System.out.println(s.toString());
		
	}
}
