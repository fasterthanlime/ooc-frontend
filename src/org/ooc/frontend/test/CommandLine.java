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
				long t1 = System.currentTimeMillis();
				SourceUnit unit = new Parser().parse(file);
				long t2 = System.currentTimeMillis();
				System.out.printf("Parsing...%d ms\t", Long.valueOf(t2 - t1));
				
				t1 = System.currentTimeMillis();
				generator.generate(new File("."), unit);
				t2 = System.currentTimeMillis();
				System.out.printf("Generating...%d ms\t", Long.valueOf(t2 - t1));
				
				t1 = System.currentTimeMillis();
				boolean same = comparator.compare(file, new File(file.getPath() + ".gen"));
				t2 = System.currentTimeMillis();
				if(same) {
					System.out.printf("Comparing... %d ms\tSame!\n", Long.valueOf(t2 - t1));
				} else {
					System.out.printf("Comparing... %d ms\tSLUT ALERT!\n", Long.valueOf(t2 - t1));
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
		System.out.println("Everything went fine =)");	
		
	}

}
