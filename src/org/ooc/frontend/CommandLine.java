package org.ooc.frontend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.ooc.frontend.compilers.AbstractCompiler;
import org.ooc.frontend.compilers.Gcc;
import org.ooc.frontend.compilers.Icc;
import org.ooc.frontend.compilers.Tcc;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.Include.Mode;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.Parser;
import org.ooc.middle.OocCompilationError;
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
	
	protected BuildParams params = new BuildParams();
	protected boolean timing = false;
	List<String> additionals = new ArrayList<String>();
	List<String> compilerArgs = new ArrayList<String>();
	private AbstractCompiler compiler = null;
	
	public CommandLine(String[] args) throws InterruptedException, IOException {
		
		String modulePath = "";
		List<String> nasms = new ArrayList<String>();
		
		for(String arg: args) {
			if(arg.startsWith("-")) {
        		String option = arg.substring(1);
        		if(option.startsWith("locale")) {
        			
        			Locale.setDefault(new Locale(arg.substring(arg.indexOf('=') + 1)));
        		
        		} else if(option.startsWith("backend=")) {
        			
        			params.backend = BackendFactory.getBackend(option.substring("backend=".length()).trim());
        			
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
        			
        		} else if(option.startsWith("c")) {
        			
        			params.link = false;
        			
        		} else if(option.startsWith("L")) {
        			
        			params.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			params.dynamicLibs.add(arg.substring(2));
        			
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
        			
        			params.run = true;
        			
        		} else if(option.equals("V") || option.equals("-version") || option.equals("version")) {
        			
        			CompilerVersion.printVersion();
        			System.exit(0);
        			
        		} else if(option.equals("h") || option.equals("-help") || option.equals("help")) {
        			
        			Help.printHelp();
        			System.exit(0);
        			
        		} else if(option.equals("gcc")) {
        			
        			compiler = new Gcc();
        			
        		} else if(option.equals("icc")) {
        			
        			compiler = new Icc();
        			
        		} else if(option.equals("tcc")) {
        			
        			compiler = new Tcc();
        			
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
        	} else if(arg.startsWith("+")) {
        		compilerArgs.add(arg.substring(1));
        	} else {
        		if(!modulePath.isEmpty()) {
        			if(arg.toLowerCase().endsWith(".s")) {
        				nasms.add(arg);
        			} else if(!arg.toLowerCase().endsWith(".ooc")) {
            			additionals.add(arg);
            		} else {
            			System.err.println("You can't specify multiple .ooc files at command line."
        					+"\nDependencies are resolved automatically, so just specify your main file");
            			return;
            		}
        		} else {
        			modulePath = arg;
        			if(!arg.toLowerCase().endsWith(".ooc")) {
        				modulePath += ".ooc";
        			}
        		}
        	}
		}
		
		if(modulePath.isEmpty()) {
			System.err.println("ooc: no files.");
			return;
		}
		
		if(compiler == null) compiler = new Gcc();
		
		if(!nasms.isEmpty()) {
			compileNasms(nasms, additionals);
		}
		
		if(params.sourcePath.isEmpty()) params.sourcePath.add(".");
		params.sourcePath.add(params.distLocation + File.separator + "sdk");
		
		parse(modulePath);
		
		if(params.clean) {
			FileUtils.deleteRecursive(params.outPath);
		}
		
	}
	
	private void compileNasms(List<String> nasms, Collection<String> list) throws IOException, InterruptedException {
		
		boolean has = false;
		
		List<String> command = new ArrayList<String>();
		command.add(findExec("nasm").getPath());
		command.add("-f");
		command.add("elf");
		for(String nasm: nasms) {
			if(nasm.endsWith(".s")) {
				command.add(nasm);
				has = true;
			}
		}
		
		if(has) {
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			ProcessUtils.redirectIO(process);
			int code = process.waitFor();
			if(code != 0) {
				System.err.println("nasm failed, aborting compilation process");
				System.exit(code);
			}
			
			for(String nasm: nasms) {
				if(nasm.endsWith(".s")) {
					list.add(nasm.substring(0, nasm.length() - 1) + "o");
				} else {
					list.add(nasm);
				}
			}
		} else {
			list.addAll(nasms);
		}
		
	}

	protected void parse(String modulePath) throws InterruptedException, IOException {
		
		params.outPath.mkdirs();
		long tt1 = System.nanoTime();
		Module module = new Parser(params).parse(modulePath);
		module.setMain(true);
		translate(module, new HashSet<Module>());
		long tt2 = System.nanoTime();
		compile(module);
		long tt3 = System.nanoTime();

		if(timing)
			System.out.printf("Took %.2f ms for ooc, and %.2f for C compiler\n",
					Float.valueOf((tt2 - tt1) / 1000000.0f),
					Float.valueOf((tt3 - tt2) / 1000000.0f));
		
	}

	protected void translate(Module module, Set<Module> done) throws IOException {
		try {
			done.add(module);
			new Tinkerer().process(module, params);
			for(Import imp: module.getImports()) {
				if(!done.contains(imp.getModule())) {
					translate(imp.getModule(), done);
				}
			}
			new CGenerator(params.outPath, module).generate();
		} catch(OocCompilationError err) {
			System.err.println(err);
			System.exit(1);
		}
	}

	protected void compile(Module module) throws Error,
			IOException, InterruptedException {
		
		for(Include inc: module.getIncludes()) {
			if(inc.getMode() == Mode.LOCAL) {
				FileUtils.copy(new File(inc.getPath() + ".h"),
						new File(params.outPath, inc.getPath() + ".h"));
			}
		}
		
		if(params.debug) compiler.setDebugEnabled();
		compiler.addIncludePath(new File(params.distLocation, "libs/headers/").getPath());
		compiler.addIncludePath(params.outPath.getPath());
		addDeps(compiler, module, new HashSet<Module>());
		for(String dynamicLib: params.dynamicLibs) {
			compiler.addDynamicLibrary(dynamicLib);
		}
		for(String additional: additionals) {
			compiler.addObjectFile(additional);
		}
		for(String compilerArg: compilerArgs) {
			compiler.addObjectFile(compilerArg);
		}
		
		if(params.link) {
			compiler.setOutputPath(module.getSimpleName());
			Collection<String> libs = getAllLibsFromUses(module);
			for(String lib: libs) compiler.addObjectFile(lib);
			compiler.addObjectFile(new File(params.distLocation, "libs/"
					+ Target.guessHost().toString() + "/libgc.a").getPath());
		} else {
			compiler.setCompileOnly();
		}
		
		if(params.verbose) compiler.printCommandLine();
		
		int code = compiler.launch();
		if(code != 0) {
			System.err.println("C compiler failed, aborting compilation process");
			System.exit(code);
		}
		
		if(params.run) {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("./"+module.getSimpleName());
			Process process = builder.start();
			ProcessUtils.redirectIO(process);
			process.waitFor();
		}
		
	}

	protected Collection<String> getAllLibsFromUses(Module module) throws IOException, InterruptedException {

		Set<String> list = new HashSet<String>();
		Set<Module> done = new HashSet<Module>();
		getAllLibsFromUses(module, list, done);
		return list;
		
	}

	protected void getAllLibsFromUses(Module module, Set<String> list, Set<Module> done) throws IOException, InterruptedException {

		if(done.contains(module)) return;
		done.add(module);
		
		for(Use use: module.getUses()) {
			compileNasms(use.getUseDef().getLibs(), list);
		}
		
		for(Import imp: module.getImports()) {
			getAllLibsFromUses(imp.getModule(), list, done);
		}
		
	}

	protected File findExec(String name) throws Error {
		
		File execFile = ShellUtils.findExecutable(name);
		if(execFile == null) {
			execFile = ShellUtils.findExecutable(name+".exe");
		}
		if(execFile == null) {
			throw new Error(name+" not found :/");
		}
		return execFile;
		
	}

	protected void addDeps(AbstractCompiler compiler, Module module, Set<Module> done) {
		
		done.add(module);
		compiler.addObjectFile(new File(params.outPath, module.getPath(".c")).getPath());
		
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				addDeps(compiler, imp.getModule(), done);
			}
		}
		
	}

}
