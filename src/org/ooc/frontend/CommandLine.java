package org.ooc.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.ooc.ShellUtils;
import org.ooc.backend.cdirty.CGenerator;
import org.ooc.compiler.CompilerVersion;
import org.ooc.compiler.Help;
import org.ooc.compiler.ProcessUtils;
import org.ooc.compiler.libraries.Target;
import org.ooc.compiler.pkgconfig.PkgConfigFrontend;
import org.ooc.compiler.pkgconfig.PkgInfo;
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
import org.ooc.frontend.parser.ModuleParser;
import org.ooc.frontend.parser.Parser;
import org.ooc.middle.Tinkerer;
import org.ooc.middle.UseDef;
import org.ooc.outputting.FileUtils;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;

public class CommandLine {
	
	public static void main(String[] argv) throws InterruptedException, IOException {
		new CommandLine(argv);
	}
	
	protected BuildParams params = new BuildParams();
	List<String> additionals = new ArrayList<String>();
	List<String> compilerArgs = new ArrayList<String>();
	private AbstractCompiler compiler = null;
	
	public CommandLine(String[] args) throws InterruptedException, IOException {
		
		List<String> modulePaths = new ArrayList<String>();
		List<String> nasms = new ArrayList<String>();
		
		for(String arg: args) {
			if(arg.startsWith("-")) {
        		String option = arg.substring(1);
        		if(option.startsWith("locale")) {
        			
        			Locale.setDefault(new Locale(arg.substring(arg.indexOf('=') + 1)));
        		
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
        			
        		} else if(option.startsWith("editor")) {
        			
        			params.editor = arg.substring(arg.indexOf('=') + 1);
        			
        		} else if(option.startsWith("c")) {
        			
        			params.link = false;
        			
        		} else if(option.startsWith("L")) {
        			
        			params.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			params.dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.equals("dyngc")) {
        			
        			params.dynGC = true;
        			
        		} else if(option.equals("noclean")) {
        			
        			params.clean = false;
        			
        		} else if(option.equals("shout")) {
        			
        			params.shout = true;
        			
        		} else if(option.equals("timing") || option.equals("t")) {
        			
        			params.timing = true;
        			
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
        			
        		} else if(option.equals("slave")) {
        			
        			params.slave = true;
        			
        		} else {
        			
        			System.err.println("Unrecognized option: '"+arg+"'");
        			
        		}
        	} else if(arg.startsWith("+")) {
        		compilerArgs.add(arg.substring(1));
        	} else {
        			String lowerArg = arg.toLowerCase();
					if(lowerArg.endsWith(".s")) {
        				nasms.add(arg);
        			} else if(lowerArg.endsWith(".o") || lowerArg.endsWith(".c") || lowerArg.endsWith(".cpp")) {
            			additionals.add(arg);
            		} else {
        				modulePaths.add(arg);
            		}
        	}
		}
		
		if(modulePaths.isEmpty()) {
			System.err.println("ooc: no files.");
			return;
		}
		
		if(compiler == null) compiler = new Gcc();
		
		if(!nasms.isEmpty()) {
			compileNasms(nasms, additionals);
		}
		
		if(params.sourcePath.isEmpty()) params.sourcePath.add(".");
		params.sourcePath.add(params.distLocation + File.separator + "sdk");
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		do {
			ModuleParser.clearCache();
			int successCount = 0;
			for(String modulePath: modulePaths) {
				try {
					int code = parse(modulePath);
					if(code == 0) successCount++;
				} catch(CompilationFailedError err) {
					System.err.println(err);
					if(!params.editor.isEmpty()) {
						launchEditor(params.editor, err);
					}
				}
				if(params.clean) FileUtils.deleteRecursive(params.outPath);
			}
			
			if(modulePaths.size() > 1) {
				System.out.println(modulePaths.size()+" compiled ("+successCount
						+" success, "+(modulePaths.size() - successCount)+" failed)");
			}
			if(params.slave) {
				System.out.println(".-------------( ready )-------------.\n");
				reader.readLine();
			}
		} while(params.slave);
		
	}
	
	private void launchEditor(String editor, CompilationFailedError err) throws IOException, InterruptedException {
		
		ProcessBuilder builder = new ProcessBuilder();
		FileLocation location = err.getLocation();
		String absolutePath = new File(location.getFileName()).getAbsolutePath();
		builder.command(editor, absolutePath+":"+location.getLineNumber()+":"+(location.getLinePos() - 1));
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		process.waitFor();
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

	protected int parse(String modulePath) throws InterruptedException, IOException {
		
		params.outPath.mkdirs();
		long tt1 = System.nanoTime();
		Module module = new Parser(params).parse(modulePath);
		module.setMain(true);
		long tt2 = System.nanoTime();
		tinker(module, new HashSet<Module>());
		long tt3 = System.nanoTime();
		output(module, new HashSet<Module>());
		long tt4 = System.nanoTime();
		int code = compile(module);
		long tt5 = System.nanoTime();

		if(params.timing) {
			System.out.printf("parse: %.2f ms\ttink: %.2f ms\tout: %.2f\tcc: %.2f ms\tTOTAL %.2f ms\n",
					Float.valueOf((tt2 - tt1) / 1000000.0f),
					Float.valueOf((tt3 - tt2) / 1000000.0f),
					Float.valueOf((tt4 - tt3) / 1000000.0f),
					Float.valueOf((tt5 - tt4) / 1000000.0f),
					Float.valueOf((tt5 - tt1) / 1000000.0f));
		}
		
		if(code == 0 && params.run) {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("./"+module.getSimpleName());
			Process process = builder.start();
			ProcessUtils.redirectIO(process);
			process.waitFor();
		}
		return code;
		
	}
	
	protected void output(Module module, Set<Module> done) throws IOException {
		done.add(module);
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				output(imp.getModule(), done);
			}
		}
		new CGenerator(params.outPath, module).generate();
	}

	protected void tinker(Module module, Set<Module> done) throws IOException {
		done.add(module);
		new Tinkerer().process(module, params);
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				tinker(imp.getModule(), done);
			}
		}
	}

	protected int compile(Module module) throws Error,
			IOException, InterruptedException {
		
		compiler.reset();
		
		for(Include inc: module.getIncludes()) {
			if(inc.getMode() == Mode.LOCAL) {
				try {
					FileUtils.copy(new File(inc.getPath() + ".h"),
						new File(params.outPath, inc.getPath() + ".h"));
				} catch(Exception e) { e.printStackTrace(); }
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
			Collection<String> libs = getFlagsFromUse(module);
			for(String lib: libs) compiler.addObjectFile(lib);
			
			if(params.dynGC) {
				compiler.addDynamicLibrary("gc");
			} else {
				compiler.addObjectFile(new File(params.distLocation, "libs/"
						+ Target.guessHost().toString() + "/libgc.a").getPath());
			}
		} else {
			compiler.setCompileOnly();
		}
		
		if(params.verbose) compiler.printCommandLine();
		
		int code = compiler.launch();
		if(code != 0) {
			System.err.println("C compiler failed, aborting compilation process");
		}
		return code;
		
	}

	protected Collection<String> getFlagsFromUse(Module module) throws IOException, InterruptedException {

		Set<String> list = new HashSet<String>();
		Set<Module> done = new HashSet<Module>();
		getFlagsFromUse(module, list, done);
		return list;
		
	}

	protected void getFlagsFromUse(Module module, Set<String> list, Set<Module> done) throws IOException, InterruptedException {

		if(done.contains(module)) return;
		done.add(module);
		
		for(Use use: module.getUses()) {
			UseDef useDef = use.getUseDef();
			compileNasms(useDef.getLibs(), list);
			for(String pkg: useDef.getPkgs()) {
				PkgInfo info = PkgConfigFrontend.getInfo(pkg);
				for(String cflag: info.cflags) {
					if(!list.contains(cflag)) {
						list.add(cflag);
					}
				}
				for(String library: info.libraries) {
					if(!list.contains(library)) {
						list.add("-l"+library); // FIXME lazy
					}
				}
			}
		}
		
		for(Import imp: module.getImports()) {
			getFlagsFromUse(imp.getModule(), list, done);
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
