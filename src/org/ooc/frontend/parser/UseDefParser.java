package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ooc.middle.UseDef;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UseDefParser {

	private static Map<String, UseDef> cache = new HashMap<String, UseDef>();
	
	public static UseDef parse(String identifier, BuildParams params) throws IOException {
		
		UseDef cached = cache.get(identifier);
		if(cached != null) return cached;
		
		File file = params.sourcePath.getFile(identifier+".use");
		if(file == null) {
			throw new CompilationFailedError(null, "Use not found in the sourcepath: "+identifier);
		}
		System.out.println("Use "+identifier+" resolved alright.");
		
		UseDef def = new UseDef(identifier);
		cache.put(identifier, def);
		
		SourceReader reader = SourceReader.getReaderFromFile(file);
		reader.skipWhitespace();
		while(reader.hasNext()) {
			if(reader.matches("#", true)) {
				reader.skipLine();
				continue;
			}
			
			reader.skipWhitespace();
		}
		
		return def;
		
	}

	
	
}
