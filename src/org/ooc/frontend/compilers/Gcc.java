package org.ooc.frontend.compilers;

/**
 * Gnu Compilers Collection 
 * 
 * @author Amos Wenger
 */
public class Gcc extends BaseCompiler {

	public Gcc() {
		super("gcc");
	}
	
	protected Gcc(String executableName) {
		super(executableName);
	}

	@Override
	public void addDynamicLibrary(String library) {
		command.add("-l");
		command.add(library);
	}

	@Override
	public void addIncludePath(String path) {
		command.add("-I");
		command.add(path);
	}

	@Override
	public void addLibraryPath(String path) {
		command.add("-L");
		command.add(path);
	}

	@Override
	public void addObjectFile(String file) {
		command.add(file);
	}

	@Override
	public void addOption(String option) {
		command.add(option);
	}

	@Override
	public void setOutputPath(String path) {
		command.add("-o");
		command.add(path);
	}

	@Override
	public void setCompileOnly() {
		command.add("-c");
	}

	@Override
	public void setDebugEnabled() {
		command.add("-g");
	}

}
