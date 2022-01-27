package de.htwsaar.mc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.project.MavenProjectHelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.sablecc.objectmacro.launcher.ObjectMacro;

/**
 * Call Objectmacro to generate Java from ObjectMacro template.
 * This Plugin uses the Objectmacro-v1, which is distributed in 
 * <a href="http://downloads.sourceforge.net/sablecc/sablecc-4-beta.4.zip">sablecc-4.beta-4.zip</a>.
 *
 * @author Hong Phuc Bui
 *
 * @phase generate-resources
 */
@Mojo(name = "objectmacro", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class ObjectMacroCaller extends AbstractMojo {

	/**
	 * Set the output language, like option <code>-t ${language}</code> of Objectmacro. 
     * 
	 */
	@Parameter(defaultValue = "java")
	private String language;
    
    /**
     * In most case one does not have to use this option. 
     * This option is like <code>-d ${directory}</code>, it
     * sets the output directory, where generated files are written in. It is set
     * automatically to <code>${project.basedir}/target/generated-sources/objectmacro/</code>
     * or <code>${project.basedir}/target/generated-test-sources/objectmacro/</code>.
     * 
     */
	@Parameter
	private String directory;
    
    /**
     * defines the package name of generated class, like option <code>-p ${packagename}</code>. 
     * This value is, for example java, used in <code>package ${packagename};</code>
     * 
     */
	@Parameter(defaultValue = "template")
	private String packagename;
    
	/**
     * sets if Objectmacro generates code or not, like <code>--generate-code</code>
     * or <code>--no-code</code>.
     * <ul>
	 *      <li><code>true</code> => <code>--generate-code</code></li>
     *      <li><code>false</code> => <code>--no-code</code></li>
     * </ul>
	 */
	@Parameter(defaultValue = "true")
	private boolean generateCode;
	/**
	 * true => strict false => lenient.
	 */
    
    /**
     * is like option <code>--strict</code> or <code>--lenient"</code>.
     * 
     * <ul>
     *  <li><code>true</code> = <code>strict</code> </li>
     *  <li><code>false</code> = <code>lenient</code></li>
     * </ul>
     */
	@Parameter(defaultValue = "true")
	private boolean strict;    
    
    
	/**
     * set console log level of Objectmacro. It takes one of these values:
     * 
     * <ul>
	 *      <li><code>informative</code> = <code>--informative</code></li>
     *      <li><code>quite</code> = <code>--quiet</code> </li>
     *      <li><code>verbose</code> = <code>--verbose</code></li>
     * </ul>
	 */
	@Parameter(defaultValue = "informative")
	private String informative;

    /**
     * file name of the template. Per default the template file should be placed
     * in <code>${project.basedir}/src/{main|test}/objectmacro</code>, and this
     * parameter is set only to the plain file name. This plugin will search
     * the file in the default directory for objectmacro.
     * If the filename contains a 
     * file separator character, for example in <code>/</code> in linux, this 
     * plugin will try to resolve the file name to a file. This may be interesting
     * if the template file does not belong to the project.
     * See also 
     * <a href="#objectmacroDirPath">objectmacroDirPath</a> and
     * <a href="#objectmacroTestDirPath">objectmacroTestDirPath</a>
     * 
     */
	@Parameter(required = true)
	private String template;

	@Parameter(defaultValue = "${component.org.apache.maven.project.MavenProjectHelper}")
	private MavenProjectHelper projectHelper;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

    /**
     * default directory to search template files in main code.
     */
	@Parameter(defaultValue = "${basedir}/src/main/objectmacro")
	private String objectmacroDirPath;
	
    /**
     * default directory to search template files in test code.
     */
	@Parameter(defaultValue = "${basedir}/src/test/objectmacro")
	private String objectmacroTestDirPath;
	
	
    @Parameter( defaultValue = "${mojoExecution}", readonly = true )
	private MojoExecution execution;
	
	private final String fileSep = System.getProperty("file.separator");
	//private String baseDir = System.getProperty("project.basedir");
	//private String baseDir ;
	private static final String GEN_CODE = "--generate-code";
	private static final String NO_CODE = "--no-code";
	private static final String STRICT = "--strict";
	private static final String LENIENT = "--lenient";
	private static final String INFORMATIVE = "--informative";
	private static final String VERBOSE = "--verbose";
	private static final String QUIET = "--quiet";
	private static final Set<String> packageNameNoGo = new HashSet<String>();

	static {// TODO add more 
		packageNameNoGo.add("..");
		packageNameNoGo.add("#");
		packageNameNoGo.add("@");
		packageNameNoGo.add("?");
		packageNameNoGo.add("/");
		packageNameNoGo.add("%");
		packageNameNoGo.add("$");
		packageNameNoGo.add(" ");
        packageNameNoGo.add("-");
	}

    @Override
	public void execute() throws MojoFailureException {
		try {
			if (projectHelper == null) {
				//getLog().warn("projectHelper is null");
				projectHelper = new DefaultMavenProjectHelper();
			}
			if (project == null) {
				getLog().warn("project is null");
			}

			directory = findOutputDirPath();
			Argument argv = parseArgument();
			if (argv != null) {
				if (needCompile(argv.getFile(), argv.getDirectory(), argv.getPackagename())) {
					getLog().info("Call ObjectMacro with argv:");
					getLog().info(argv.toString());
					ObjectMacro.compile(argv.getStringArgv());
				} else {
					getLog().info("No need to compile template " + argv.getFile());
					getLog().info(" clean output directory to force re-compile template");
				}
				getLog().info("Add " + argv.getDirectory() + " to source and test");
				project.addCompileSourceRoot(argv.getDirectory());
				project.addTestCompileSourceRoot(argv.getDirectory());
			} else {
				//TODO: What is the convenient behavior if there are not 
				// templated files? I just put an warning out on screen.
				getLog().warn("no tag <template> found");
			}
		} catch (RuntimeException ex) {
			throw new MojoFailureException("Compile template file error: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new MojoFailureException("Compile template file error: " + ex.getMessage(), ex);
		}

	}

	private Argument parseArgument() {
		Argument a = new Argument();
		// TODO: optimize here, check the tag <file> first.
		String fileName = template;
		if (fileName == null) {
			getLog().warn("Configuration fail, cannot find the tag <template>");
			return null;
		} else {
			if (isFileNameValid(fileName)) {
				// option "-t language"
				if (isOptionValid(language)) {
					String localLanguage = language.trim();
					a.setLanguage(localLanguage);
				} else {
					throw new RuntimeException(language + " is not a valid option for output language");
				}

				// option "-d directory" // TODO: check validation directory
				if (isOptionValid(directory)) {
					String localDirectory = directory.trim();
					File outputDir = new File(localDirectory);
					if (!outputDir.isAbsolute()) {
						outputDir = new File(project.getBasedir(), localDirectory);
					}
					a.setDirectory(outputDir.getAbsolutePath());
				} else {
					throw new RuntimeException(directory + " is not a valid option for destinated directory");
				}

				// option "-p packagesname"
				if (isPackageNameValid(packagename)) {
					String localPackagename = packagename.trim();
					a.setPackagename(localPackagename);
				} else {
					throw new RuntimeException("package name not valid:" + packagename.trim());
				}

				// option "--generate-code" or "--no-code"
				String localGenerateCode = generateCode ? GEN_CODE : NO_CODE;
				a.setGenerateCode(localGenerateCode);

				// option "--strict" or "--lenient"
				String localStrict = strict ? STRICT : LENIENT;
				a.setStrict(localStrict);

				// option "--quiet" or "--informative" or "--verbose"
				String localInformative = INFORMATIVE;
				//String i = informative;
				informative = informative.trim().toLowerCase();
				if (informative.equals("quiet")) {
					localInformative = QUIET;
				} else if (informative.equals("informative")) {
					localInformative = INFORMATIVE;
				} else if (informative.equals("verbose")) {
					localInformative = VERBOSE;
				}
				a.setInformative(localInformative);
				File templateFile = guessTemplaceFile(template);
				
				a.setFile(templateFile.getAbsolutePath());
			} else {
				getLog().warn("Configuration fail, file name: " + fileName + " invalid");
				return null;
			}
		}
		return a;
	}

	private File guessTemplaceFile(String templaceParam) {
		if (templaceParam.contains(File.separator)) {
				File templateFile = new File(templaceParam);
				if (!templateFile.isAbsolute()) {
					templateFile = new File(project.getBasedir(), templaceParam);
				}
				return templateFile;
		}else{
			if (execution.getLifecyclePhase().equals(LifecyclePhase.GENERATE_TEST_SOURCES.id()) ){
				File objectMacroDir = new File(objectmacroTestDirPath);
				File templateFile = new File(objectMacroDir, templaceParam);
				return templateFile;
			}else{
				File objectMacroDir = new File(objectmacroDirPath);
				File templateFile = new File(objectMacroDir, templaceParam);
				return templateFile;
			}
		}
	}

	private String findOutputDirPath() {
		if (execution.getLifecyclePhase().equals(LifecyclePhase.GENERATE_TEST_SOURCES.id())){
			if (directory == null) {
				directory = new File(project.getBasedir(), "target/generated-test-sources/objectmacro/").getAbsolutePath();
			} else {
				File outputDir = new File(directory);
				if (!outputDir.isAbsolute()) {
					outputDir = new File(project.getBasedir(), "target/generated-test-sources/" + directory);
					directory = outputDir.getAbsolutePath();
				}
			}
		}else{
			if (directory == null) {
				directory = new File(project.getBasedir(), "target/generated-sources/objectmacro/").getAbsolutePath();
			} else {
				File outputDir = new File(directory);
				if (!outputDir.isAbsolute()) {
					outputDir = new File(project.getBasedir(), "target/generated-sources/" + directory);
					directory = outputDir.getAbsolutePath();
				}
			}
		}
		return directory;
	}

	boolean isOptionValid(String option) {
		if (option == null) {
			return false;
		}
		if (option.trim().length() == 0) {
			return false;
		}
		return true;
	}

	boolean isPackageNameValid(String pn) {
		if (!isOptionValid(pn)) {
			return false;
		}
		if (pn.trim().startsWith(".")) {
			return false;
		}
		for (String n : packageNameNoGo) {
			if (pn.contains(n)) {
				return false;
			}
		}
		return true;
	}

	// TODO: make more restrict here
	private boolean isFileNameValid(String file) {
		assert file != null;
		return true;
	}

	private boolean needCompile(String template, String directory, String packagename) {
		File templageFile = new File(template);
		if (templageFile.isFile()) {
			String destinatePath
					= directory
					+ packagename.replace(".", "/");
			getLog().info("Check timestamp for the directory: " + destinatePath);
			File destinateDir = new File(destinatePath);
			if (destinateDir.isDirectory()) { // if the last part of package is already a director
				long lastModiTemplate = templageFile.lastModified();
				long lastModiOutputPackage = destinateDir.lastModified();
                return lastModiTemplate > lastModiOutputPackage;
			} else {// if the last part of the package is not a directory/not exist
				return true;
			}
		} else {
			return true;
		}
	}
}
