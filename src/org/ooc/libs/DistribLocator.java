package org.ooc.libs;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.ooc.outputting.FileUtils;

public class DistribLocator {

	public static File locate() {
		
		String classPath = System.getProperty("java.class.path");
		System.out.println("Classpath = "+classPath);
		StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
		while(st.hasMoreTokens()) {
			File distribLocation = new File(st.nextToken(), "../");
			File idFile = new File(distribLocation, "sdk/ooc_sdk_id");
			if(idFile.exists()) {
				try {
					System.out.println("We found it =) It's in "+distribLocation.getCanonicalPath());
					return FileUtils.resolveRedundancies(distribLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
		
	}

	
	
}
