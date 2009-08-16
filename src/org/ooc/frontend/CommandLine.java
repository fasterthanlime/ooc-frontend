package org.ooc.frontend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.ooc.ShellUtils;
import org.ooc.backend.cdirty.CGenerator;
import org.ooc.backends.BackendFactory;
import org.ooc.compiler.CompilerVersion;
import org.ooc.compiler.Help;
import org.ooc.compiler.ProcessUtils;
import org.ooc.compiler.libraries.Target;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.Parser;
import org.ooc.middle.Tinkerer;
import org.ooc.outputting.FileUtils;

public class CommandLine {
	
	public static void main(String[] argv) throws InterruptedException, IOException {
		
		if(argv.length == 0) {
			System.out.println("Usage: ooc [OPTIONS] file.ooc");
			System.exit(0);
		}
		
		new CommandLine(argv);
	
	}
	
	private BuildParams params = new BuildParams();
	private boolean timing = false;
	
	public CommandLine(String[] args) throws InterruptedException, IOException {
		
		String module = "";
		
		for(String arg: args) {
			if(arg.startsWith("-")) {
        		String option = arg.substring(1);
        		if(option.startsWith("locale")) {
        			
        			Locale.setDefault(new Locale(arg.substring(arg.indexOf('=') + 1)));
        		
        		} else if(option.startsWith("gui")) {
        			
        			//gui = true;
        			
        		} else if(option.startsWith("backend=")) {
        			
        			params.backend = BackendFactory.getBackend(option.substring("backend=".length()).trim());
        			
        		} else if(option.startsWith("daemon")) {
        			
        			/*
        			daemon = true;
        			int index = arg.indexOf(':');
        			if(index != -1) {
        				daemonPort = Integer.parseInt(arg.substring(index + 1));
        			}
        			*/
        			
        		} else if(option.startsWith("sourcepath")) {
        			
        			String sourcePathOption = arg.substring(arg.indexOf('=') + 1);
        			StringTokenizer tokenizer = new StringTokenizer(sourcePathOption, File.pathSeparator);
        			while(tokenizer.hasMoreTokens()) {
        				params.sourcePath.add(tokenizer.nextToken());
        			}
        			
        		} else if(option.startsWith("outpath")) {
        			
        			params.outPath = new File(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("incpath")) {
        			
        			params.incPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("I")) {
        			
        			params.incPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("libpath")) {
        			
        			params.libPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("L")) {
        			
        			params.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			params.dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.startsWith("freeargs")) {
        			
        			//ignoreUnknownArgs = true;
        			
        		} else if(option.equals("noclean")) {
        			
        			params.clean = false;
        			
        		} else if(option.equals("shout")) {
        			
        			params.shout = true;
        			
        		} else if(option.equals("timing") || option.equals("t")) {
        			
        			timing = true;
        			
        		} else if(option.equals("debug") || option.equals("g")) {
        			
        			params.debug = true;
        			
        		} else if(option.equals("verbose") || option.equals("v")) {
        			
        			params.verbose = true;
        			
        		} else if(option.equals("run") || option.equals("r")) {
        			
        			//run = true;
        			
        		} else if(option.equals("V") || option.equals("-version") || option.equals("version")) {
        			
        			CompilerVersion.printVersion();
        			System.exit(0);
        			
        		} else if(option.equals("h") || option.equals("-help") || option.equals("help")) {
        			
        			Help.printHelp();
        			System.exit(0);
        			
        		} else if(option.equals("help-backends") || option.equals("-help-backends")) {
        			
        			Help.printHelpBackends();
        			System.exit(0);
        			
        		} else if(option.equals("help-gcc") || option.equals("-help-gcc")) {
        			
        			Help.printHelpGcc();
        			System.exit(0);
        			
        		} else if(option.equals("help-make") || option.equals("-help-make")) {
        			
        			Help.printHelpMake();
        			System.exit(0);
        			
        		} else if(option.equals("help-none") || option.equals("-help-none")) {
        			
        			Help.printHelpNone();
        			System.exit(0);
        			
        		} else {
        			
        			System.err.println("Unrecognized option: '"+arg+"'");
        			
        		}
        	} else {
        		if(!module.isEmpty()) {
        			System.err.println("You can't specify multiple .ooc files at command line."
        					+"\nDependencies are resolved automatically, so just specify your main file");
        			return;
        		}
        		module = arg;
        	}
		}
		
		if(module.isEmpty()) {
			System.err.println("ooc: no files.");
			return;
		}
		
		if(params.sourcePath.isEmpty()) params.sourcePath.add(".");
		params.sourcePath.add(params.distLocation + File.separator + "sdk");
		
		parse(module);
		
		if(params.clean) {
			FileUtils.deleteRecursive(params.outPath);
		}
		
	}
	
	private void parse(String fileName) throws InterruptedException, IOException {
		long tt1 = System.nanoTime();
		params.outPath.mkdirs();
		Module module = new Parser(params).parse(fileName);
		translate(module, new HashSet<Module>());
		compile(module);
		long tt2 = System.nanoTime();

		if(timing)
			System.out.printf("Took %.2f ms.\n", Float.valueOf((tt2 - tt1) / 1000000.0f));
	}

	private void translate(Module module, Set<Module> done) throws IOException {
		done.add(module);
		new Tinkerer().process(module);
		new CGenerator(params.outPath, module).generate();
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				translate(imp.getModule(), done);
			}
		}
	}

	private void compile(Module module) throws Error,
			IOException, InterruptedException {
		
		List<String> command = new ArrayList<String>();
		
		command.add(findGCC().getPath());
		
		command.add("-std=c99");
		command.add("-I");
		command.add(new File(params.distLocation, "libs/headers/").getPath());
		// FIXME ooh hardcoded, that is bad.
		command.add(new File(params.distLocation, "libs/universal/mango/mangoobject.c").getPath());
		addDeps(command, module, new HashSet<Module>());
		command.add("-o");
		command.add(module.getSimpleName());
		command.add(new File(params.distLocation, "libs/" + Target.guessHost().toString() + "/libgc.a").getPath());
		for(String dynamicLib: params.dynamicLibs) {
			command.add("-l");
			command.add(dynamicLib);
		}
		
		StringBuilder commandLine = new StringBuilder();
		for(String arg: command) {
			commandLine.append(arg);
			commandLine.append(' ');
		}
		System.out.println(commandLine.toString());
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		process.waitFor();
		
	}

	private File findGCC() throws Error {
		
		File gccFile = ShellUtils.findExecutable("gcc");
		if(gccFile == null) {
			gccFile = ShellUtils.findExecutable("gcc.exe");
		}
		if(gccFile == null) {
			throw new Error("GCC not found :/");
		}
		return gccFile;
		
	}

	private void addDeps(List<String> command, Module module, Set<Module> done) {
		
		done.add(module);
		command.add(new File(params.outPath, module.getPath(".c")).getPath());
		
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				addDeps(command, imp.getModule(), done);
			}
		}
		
	}

}
