package org.ooc.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.ShellUtils;
import org.ooc.backend.cdirty.CGenerator;
import org.ooc.compiler.ProcessUtils;
import org.ooc.compiler.libraries.Target;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.parser.Parser;
import org.ooc.libs.DistribLocator;
import org.ooc.middle.Tinkerer;

public class CommandLine {

	public static void main(String[] argv) throws InterruptedException {
		
		if(argv.length == 0) {
			System.out.println("Usage: oof [OPTIONS] file.ooc\nOptions:\n\t-c: generates C instead of regenerating ooc");
			System.exit(0);
		}
		
		new CommandLine(argv);
	
	}
	
	public CommandLine(String[] fileNames) throws InterruptedException {
	
		File distLoc = DistribLocator.locate();
		
		long tt1 = System.nanoTime();
		
		int count = 0;
		File outPath = new File("./ooc_tmp");
		outPath.mkdirs();
		
		for(String fileName: fileNames) {
			process(outPath, fileName, distLoc);
			count++;
		}
		
		long tt2 = System.nanoTime();

		System.out.printf("Everything went fine =) Total time: %.2f ms for %d "+
				(count > 1 ? "files" : "file")+"\n",
			Float.valueOf((tt2 - tt1) / 1000000.0f), Integer.valueOf(count));
		
	}

	private void process(File outPath, String fileName, File distLoc) throws InterruptedException {
		
		System.out.printf(fileName);
		File file = new File(fileName);
		
		try {
			long t1 = System.nanoTime();
			SourceUnit unit = new Parser().parse(file);
			long t2 = System.nanoTime();
			System.out.printf("Parsing...%.2f ms\n", Float.valueOf((t2 - t1) / 1000000.0f));
			
			t1 = System.nanoTime();
			new Tinkerer().process(unit);
			t2 = System.nanoTime();
			System.out.printf("Resolving...%.2f ms\n", Float.valueOf((t2 - t1) / 1000000.0f));
			
			t1 = System.nanoTime();
			new CGenerator(outPath, unit).generate();
			t2 = System.nanoTime();
			System.out.printf("Generating...%.2f ms\n", Float.valueOf((t2 - t1) / 1000000.0f));
			
			t1 = System.nanoTime();
			compile(outPath, unit, distLoc);
			t2 = System.nanoTime();
			System.out.printf("Compiling...%.2f ms\n", Float.valueOf((t2 - t1) / 1000000.0f));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println();
		
	}

	private void compile(File outPath, SourceUnit unit, File distLoc) throws Error,
			IOException, InterruptedException {
		
		List<String> command = new ArrayList<String>();
		
		File gccFile = ShellUtils.findExecutable("gcc");
		if(gccFile == null) {
			gccFile = ShellUtils.findExecutable("gcc.exe");
		}
		if(gccFile == null) {
			throw new Error("GCC not found :/");
		}
		command.add(gccFile.getPath());
		
		command.add("-std=c99");
		command.add("-I");
		command.add(new File(distLoc, "libs/headers/").getPath());
		// FIXME ooh hardcoded, that is bad.
		command.add(new File(distLoc, "libs/universal/mango/mangoobject.c").getPath());
		command.add(new File(outPath, unit.getName() + ".c").getPath());
		command.add("-o");
		command.add(unit.getSimpleName());
		command.add(new File(distLoc, "libs/" + Target.guessHost().toString() + "/libgc.a").getPath());
		
		StringBuilder commandLine = new StringBuilder();
		for(String arg: command) {
			commandLine.append(arg);
			commandLine.append(' ');
		}
		System.out.println(commandLine.toString());
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File("."));
		builder.command(command);
		
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		process.waitFor();
		
	}

}
