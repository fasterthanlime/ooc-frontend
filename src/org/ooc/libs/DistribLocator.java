package org.ooc.libs;

import java.io.File;
import java.util.StringTokenizer;

import org.ooc.outputting.FileUtils;

public class DistribLocator {

	public static File locate() {
		
		String classPath = System.getProperty("java.class.path");
		StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
		while(st.hasMoreTokens()) {
			File distribLocation = new File(st.nextToken(), "../");
			File idFile = new File(distribLocation, "sdk/ooc_sdk_id");
			if(idFile.exists()) {
				return FileUtils.resolveRedundancies(distribLocation);
			}
		}
		
		return null;
		
	}

	
	
}
