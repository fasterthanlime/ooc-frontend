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
	
		OocGenerator generator = new OocGenerator();
		WhitelessComparator comparator = new WhitelessComparator();
		
		for(String fileName: fileNames) {
		
			System.out.printf("%s...\t", fileName);
			File file = new File(fileName);
			
			try {
				long t1 = System.nanoTime();
				SourceUnit unit = new Parser().parse(file);
				long t2 = System.nanoTime();
				System.out.printf("Parsing...%.2f ms\t", Float.valueOf((t2 - t1) / 1000000.0f));
				
				t1 = System.nanoTime();
				generator.generate(new File("."), unit);
				t2 = System.nanoTime();
				System.out.printf("Generating...%.2f ms\t", Float.valueOf((t2 - t1) / 1000000.0f));
				
				t1 = System.nanoTime();
				boolean same = comparator.compare(file, new File(file.getPath() + ".gen"));
				t2 = System.nanoTime();
				if(same) {
					System.out.printf("Comparing... %.2f ms\tSame!\n", Float.valueOf((t2 - t1) / 1000000.0f));
				} else {
					System.out.printf("Comparing... %.2f ms\tSLUT ALERT!\n", Float.valueOf((t2 - t1) / 1000000.0f));
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
		System.out.println("Everything went fine =)");	
		
	}

}
