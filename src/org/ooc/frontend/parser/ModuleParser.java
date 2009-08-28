package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ModuleParser {

	// path -> module
	protected final static Map<String, Module> cache = new HashMap<String, Module>();
	
	public static void parse(final Module module, final String fullName, final File file, final SourceReader sReader,
			final TokenReader reader, final Parser parser) {
		
		cache.put(module.getFullName(), module);
		
		try {
			addLangImports(module, parser);
			
			while(reader.hasNext()) {
	
				if(reader.peek().type == TokenType.LINESEP) {
					reader.skip();
					continue;
				}
				
				{
					ClassDecl classDecl = ClassDeclParser.parse(sReader, reader);
					if(classDecl != null) {
						module.getTypes().add(classDecl.getName(), classDecl);
						continue;
					}
				}
				
				{
					CoverDecl coverDecl = CoverDeclParser.parse(sReader, reader);
					if(coverDecl != null) {
						module.getTypes().add(coverDecl.getName(), coverDecl);
						continue;
					}
				}
				
				{
					OpDecl opDecl = OpDeclParser.parse(sReader, reader);
					if(opDecl != null) {
						module.getOps().add(opDecl);
						continue;
					}
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
				synchronized (cache) {
					Module cached = cache.get(imp.getName());
					if(cached == null) {
						cached = parser.parse(imp.getPath(), false);
						cache.put(imp.getName(), cached);
					}
					imp.setModule(cached);
				}
			}
		
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static void addLangImports(Module module, Parser parser) {
		
		if(module.getFullName().startsWith("lang.")) {
			if(!module.getFullName().equals("lang.Object")) {
				module.getImports().add(new Import("lang.Object", Token.defaultToken));
			}
			if(!module.getFullName().equals("lang.ooclib")) {
				module.getImports().add(new Import("lang.ooclib", Token.defaultToken));
			}
			return;
		}
		
		Collection<String> paths = parser.params.sourcePath.getRelativePaths("lang");
		for(String path: paths) {
			String impName = path.replace('/', '.');
			impName = impName.substring(0, impName.length() - 4); // ditch the '.ooc'
			if(!impName.equals(module.getFullName())) {
				module.getImports().add(new Import(impName, Token.defaultToken));
			}
		}
		
	}

	public static void clearCache() {
		cache.clear();
	}
	
}
