package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Tokenizer;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class Parser {

	// unit.fullName -> unit
	protected final Map<String, Module> cache = new HashMap<String, Module>();
	protected BuildParams params;
	
	public Parser(BuildParams params) {
		this.params = params;
	}

	public Module parse(String path) throws IOException {

		if(params.verbose)
			System.out.println("Parsing "+path);
		
		File file = params.sourcePath.getFile(path);
		if(file == null) {
			throw new CompilationFailedError(null, "File "+path+" not found in sourcePath."
				+" sourcePath = "+params.sourcePath);
		}
		
		SourceReader sReader = SourceReader.getReaderFromFile(file);
		List<Token> tokens = new Tokenizer().parse(sReader);
		String fullName = path.substring(0, path.lastIndexOf('.'))
			.replace(File.separatorChar, '.').replace('/', '.');
		Module module = ModuleParser.parse(fullName, file, sReader, new TokenReader(tokens), this);
		return module;
		
	}
	
	public Map<String, Module> getCache() {
		return cache;
	}
		
}
