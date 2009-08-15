package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ModuleParser {

	public static Module parse(String fullName, File file, SourceReader sReader,
			TokenReader reader, Parser parser) throws IOException {
		
		Module module = new Module(fullName);
		
		while(reader.hasNext()) {

			if(reader.peek().type == LINESEP) {
				reader.skip(); continue;
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
			// TODO store comments somewhere..
			if(CommentParser.parse(sReader, reader) != null) continue;
			
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start + reader.prev().length),
					"Expected declaration, include, or import in source unit, but got "+reader.prev().type);
			
		}
		
		parser.getCache().put(module.getFullName(), module);
		for(Import imp: module.getImports()) {
			Module cached = parser.getCache().get(imp.getPath());
			if(cached == null) {
				cached = parser.parse(imp.getPath());
				parser.getCache().put(imp.getPath(), cached);
			}
			imp.setModule(cached);
		}
		
		return module;
		
	}
	
}
