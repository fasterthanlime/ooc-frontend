package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ModuleParser {

	// path -> module
	protected final static Map<String, Module> cache = new HashMap<String, Module>();
	
	public static Module parse(String fullName, File file, SourceReader sReader,
			TokenReader reader, Parser parser) throws IOException {
		
		Module module = new Module(fullName, sReader);
		cache.put(module.getFullName(), module);
		
		while(reader.hasNext()) {

			if(reader.peek().type == TokenType.LINESEP) {
				reader.skip();
				continue;
			}
			
			Declaration declaration = DeclarationParser.parse(sReader, reader);
			if(declaration != null) {
				if(declaration instanceof VariableDecl) {
					module.getBody().add(new Line(declaration));
				} else {
					module.getBody().add(declaration);
				}
				continue;
			}
			
			if(IncludeParser.parse(sReader, reader, module.getIncludes())) continue;
			if(ImportParser.parse(sReader, reader, module.getImports())) continue;
			if(UseParser.parse(sReader, reader, module.getUses())) continue;
			if(CommentParser.parse(sReader, reader) != null) continue;
			Line line = LineParser.parse(sReader, reader);
			if(line != null) {
				module.getLoadFunc().getBody().add(line);
				continue;
			}
			
			Token errToken = reader.peek();
			throw new CompilationFailedError(sReader.getLocation(errToken),
					"Expected declaration, include, or import in source unit, but got "+errToken.type);
			
		}

		for(Import imp: module.getImports()) {
			Module cached = cache.get(imp.getPath());
			if(cached == null) {
				cached = parser.parse(imp.getPath());
				cache.put(imp.getPath(), cached);
			}
			imp.setModule(cached);
		}
		
		return module;
		
	}
	
	public static void clearCache() {
		cache.clear();
	}
	
}
