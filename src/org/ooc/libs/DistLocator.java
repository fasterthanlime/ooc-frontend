package org.ooc.libs;

import java.io.File;
import java.util.StringTokenizer;

import org.ooc.outputting.FileUtils;

public class DistLocator {

	public static File locate() {
		
		String classPath = System.getProperty("java.class.path");
		
		StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			String base = "";
			for(int i = 0; i < 8; i++) {
				base += "../";
				File distribLocation = new File(token, base);
				File idFile = FileUtils.resolveRedundancies(new File(distribLocation, "sdk/ooc_sdk_id"));
				if(idFile.exists()) {
					return FileUtils.resolveRedundancies(distribLocation);
				}
			}
		}
		
		return null;
		
	}
	
}
