package org.ooc.frontend.test;

import java.io.File;
import java.io.IOException;

import org.ooc.backend.ooc.OocGenerator;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.parser.Parser;

public class CommandLine {

	public static void main(String[] argv) {
	
		if(argv.length < 1) {
			System.out.println("Usage: oof file.ooc");
			System.exit(0);
		}
		
		new CommandLine(argv);
	
	}
	
	public CommandLine(String[] fileNames) {
	
		for(String fileName: fileNames) {
		
			System.out.println("Processing "+fileName);
			
			try {
				long t1 = System.currentTimeMillis();
				SourceUnit unit = new Parser().parse(new File(fileName));
				long t2 = System.currentTimeMillis();
				System.out.printf("Parsing took %d ms\n", Long.valueOf(t2 - t1));
				
				t1 = System.currentTimeMillis();
				new OocGenerator().generate(new File("."), unit);
				t2 = System.currentTimeMillis();
				System.out.printf("Generating ooc took %d ms\n", Long.valueOf(t2 - t1));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
		System.out.println("Everything went fine =)");	
		
	}

}
