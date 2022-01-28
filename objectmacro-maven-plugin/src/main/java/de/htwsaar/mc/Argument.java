package de.htwsaar.mc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hong-Phuc Bui
 * @version Feb 3, 2013
 */
public final class Argument {
	private final List<String> argv;
	private String l;
	private String d;
	private String p;
	private String g;
	private String s;
	private String i;
	private String f;
	
	public Argument(){
		argv = new ArrayList<String>();
	}
	

	public void setLanguage(String language){
		l = language;
		argv.add("-t");
		argv.add(l);
	}

	public void setDirectory(String directory){
		File destinateDir = new File(directory);
		d = destinateDir.getAbsolutePath();
		argv.add("-d");
		argv.add(d);
	}
	/** 
	 * 
	 */
	public String getDirectory(){
		return d;
	}
	
	public void setPackagename(String packagename){
		p = packagename;
		argv.add("-p");
		argv.add(p);
	}
	
	/**
	 * 
	 */
	public String getPackagename(){
		return p;
	}
	
	public void setGenerateCode(String generateCode){
		g = generateCode;
		argv.add(g);
	}

	public void setStrict(String strict){
		s = strict;
		argv.add(s);
	}

	public void setInformative(String informative){
		i = informative;
		argv.add(i);
	}

	public void setFile(String file){
		f = file;
		argv.add(file);
	}
	
	public String getFile(){
		return f;
	}	
    
    /**
     * returns a copy of argv.
     * 
     * @return a copy of argv.
     */
	public String[] getStringArgv(){
		return argv.toArray(new String[0]);
	}

    /**
     * returns a copy of argv.
     * 
     * @return a copy of argv
     */
	public List<String> getArgv(){
		List<String> clone = new ArrayList<String>(argv.size());
		for(String c : argv){
			clone.add(c);
		}
		return clone;
	}
	
    /**
     * returns String which represents arguments ob objectmacro, as if it is called
     * from within a console. Use this method only for debug!
     * 
     * @return may  look like <code>-t java -d {/abslute/path/to/output/directory} -p {your.organisation.org} --generate-code --strict --informative {absolute/path/to/template.objectmacro} </code>
     */
    @Override
    public String toString() {
        return String.join(" ", argv);
    }
    
}
