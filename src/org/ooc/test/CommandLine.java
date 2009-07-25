package org.ooc.test;

import java.io.File;
import java.io.IOException;

import org.ooc.backend.cdirty.CGenerator;
import org.ooc.backend.ooc.OocGenerator;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.parser.Parser;
import org.ooc.middle.Tinkerer;

public class CommandLine {

	public static void main(String[] argv) {
		
		new CommandLine(argv);
	
	}
	
	public CommandLine(String[] fileNames) {
	
		WhitelessComparator comparator = new WhitelessComparator();
		
		long tt1 = System.nanoTime();
		
		boolean outputC = false;
		int count = 0;
		File outPath = new File("./ooc_tmp");
		outPath.mkdirs();
		
		for(String fileName: fileNames) {
			
			if(fileName.equals("-c")) {
				outputC = true;
				continue;
			}
		
			String tabbedTitle = (fileName
					+ "                                                                                                       ")
					.substring(0, 45);			
			System.out.printf(tabbedTitle);
			File file = new File(fileName);
			
			try {
				long t1 = System.nanoTime();
				SourceUnit unit = new Parser().parse(file);
				long t2 = System.nanoTime();
				System.out.printf("Parsing...%.2f ms\t", Float.valueOf((t2 - t1) / 1000000.0f));
				
				t1 = System.nanoTime();
				new Tinkerer().process(unit);
				t2 = System.nanoTime();
				System.out.printf("Resolving...%.2f ms\t", Float.valueOf((t2 - t1) / 1000000.0f));
				
				t1 = System.nanoTime();
				if(outputC) {
					new CGenerator(outPath, unit).generate();
				} else {
					new OocGenerator(outPath, unit).generate();
				}
				t2 = System.nanoTime();
				System.out.printf("Generating...%.2f ms\t", Float.valueOf((t2 - t1) / 1000000.0f));
				
				if(!outputC) {
					t1 = System.nanoTime();
					boolean same = comparator.compare(file, new File(file.getPath() + ".gen"));
					t2 = System.nanoTime();
					if(same) {
						System.out.printf("Comparing... %.2f ms\tSame!", Float.valueOf((t2 - t1) / 1000000.0f));
					} else {
						System.out.printf("Comparing... %.2f ms\tSLUT ALERT!", Float.valueOf((t2 - t1) / 1000000.0f));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println();
			count++;
			
		}
		
		long tt2 = System.nanoTime();

		if(count == 0) {
			System.out.println("Usage: oof [OPTIONS] file.ooc\nOptions:\n\t-c: generates C instead of regenerating ooc");
		} else {		
			System.out.printf("Everything went fine =) Total time: %.2f ms for %d files\n",
				Float.valueOf((tt2 - tt1) / 1000000.0f), Integer.valueOf(count));
		}
		
	}

}
