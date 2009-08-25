package org.ooc.frontend.compilers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.compiler.ProcessUtils;

public abstract class BaseCompiler implements Compiler {
	
	protected List<String> command = new ArrayList<String>();
	
	
	
	@Override
	public int launch() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		return process.waitFor();
	}

}
