package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ABSTRACT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.ARROW;
import static org.ooc.frontend.model.tokens.Token.TokenType.ASSIGN;
import static org.ooc.frontend.model.tokens.Token.TokenType.AS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.BIN_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.CHAR_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLASS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.COLON;
import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.CONST_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.COVER_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.DEC_FLOAT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DEC_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOUBLE_DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.EQUALS;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXCL;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXTERN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FALSE;
import static org.ooc.frontend.model.tokens.Token.TokenType.FINAL_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FOR_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FROM_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FUNC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.GT;
import static org.ooc.frontend.model.tokens.Token.TokenType.GTE;
import static org.ooc.frontend.model.tokens.Token.TokenType.HEX_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.IF_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.IMPORT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.INCLUDE_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;
import static org.ooc.frontend.model.tokens.Token.TokenType.LT;
import static org.ooc.frontend.model.tokens.Token.TokenType.LTE;
import static org.ooc.frontend.model.tokens.Token.TokenType.MINUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.ML_COMMENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.NEW_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.NOT_EQ;
import static org.ooc.frontend.model.tokens.Token.TokenType.NULL;
import static org.ooc.frontend.model.tokens.Token.TokenType.OCT_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.OOCDOC;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.PERCENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.PLUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.RETURN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.SLASH;
import static org.ooc.frontend.model.tokens.Token.TokenType.SL_COMMENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.STAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.STATIC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.STRING_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.SUPER_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.THIS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.TILDE;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRIPLE_DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRUE;
import static org.ooc.frontend.model.tokens.Token.TokenType.WHILE_KW;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Tokenizer;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class Parser {

	// unit.fullName -> unit
	private final Map<String, Module> cache = new HashMap<String, Module>();
	private BuildParams params;
	
	public Parser(BuildParams params) {
		this.params = params;
	}

	public Module parse(String path) throws IOException {

		if(params.debug)
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
		Module module = module(fullName, file, sReader, new TokenReader(tokens));
		//new XStream().toXML(unit, new FileWriter("tree.xml"));
		return module;
		
	}
	
	private Module module(String fullName, File file, SourceReader sReader, TokenReader reader) throws IOException {
		
		Module module = new Module(fullName);
		
		while(reader.hasNext()) {

			if(reader.peek().type == LINESEP) {
				reader.skip(); continue;
			}
			
			Declaration declaration = declaration(sReader, reader);
			if(declaration != null) {
				module.getBody().add(declaration);
				continue;
			}
			
			if(include(sReader, reader, module.getIncludes())) {
				continue;
			}
			
			if(importStatement(sReader, reader, module.getImports())) {
				continue;
			}
			
			if(comment(sReader, reader) != null) {
				// TODO store comments somewhere..
				continue;
			}
			
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start + reader.prev().length),
					"Expected declaration, include, or import in source unit, but got "+reader.prev().type);
			
		}
		
		cache.put(module.getFullName(), module);
		for(Import imp: module.getImports()) {
			Module cached = cache.get(imp.getPath());
			if(cached == null) {
				cached = parse(imp.getPath());
				cache.put(imp.getPath(), cached);
			}
			imp.setModule(cached);
		}
		
		return module;
		
	}
	
	private Visitable comment(SourceReader sReader, TokenReader reader) {
		
		Token t = reader.peek();
		
		if(t.type == SL_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader));
		}

		if(t.type == ML_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader)); // FIXME lazy 
		}
		
		return null;
		
	}

	private boolean include(SourceReader sReader, TokenReader reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == LINESEP) {
				includes.add(new Include(sb.toString()));
				break;
			}
			if(t.type == COMMA) {
				includes.add(new Include(sb.toString()));
				sb.setLength(0);
			} else if(t.type == NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == SLASH) {
				sb.append('/');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}
	
	private boolean importStatement(SourceReader sReader, TokenReader reader, NodeList<Import> imports) throws EOFException, CompilationFailedError {

		if(reader.peek().type != IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == LINESEP) {
				imports.add(new Import(sb.toString()));
				break;
			}
			if(t.type == COMMA) {
				imports.add(new Import(sb.toString()));
				sb.setLength(0);
			} else if(t.type == NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == DOT) {
				if(t.type == STAR) {
					System.out.println("Encountered import blah.*");
				}
				sb.append('.');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}

	private Line line(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.peek().type == SL_COMMENT) {
			Token t = reader.read();
			return new SingleLineComment(t.get(sReader));
		}
		
		while(reader.peek().type == LINESEP) reader.skip();
		
		if(!reader.hasNext()) {
			reader.reset(mark);
			return null;
		}
		
		Statement statement = statement(sReader, reader);
		if(statement == null) {
			reader.reset(mark);
			return null;	
		}
		
		if(!(statement instanceof ControlStatement)) {
			Token next = reader.read();
			if(next.type != LINESEP && next.type != SL_COMMENT) {
				throw new CompilationFailedError(sReader.getLocation(next.start),
						"Missing semi-colon at the end of a line (got a "+next.type+" instead)");
			}
		}
		return new Line(statement);
		
	}

	private Statement statement(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Foreach foreach = foreach(sReader, reader);
		if(foreach != null) return foreach;
		
		Conditional conditional = conditional(sReader, reader);
		if(conditional != null) return conditional;
		
		Return ret = returnStatement(sReader, reader);
		if(ret != null) return ret;
		
		Expression expression = expression(sReader, reader);
		if(expression != null) return expression;
		
		reader.reset(mark);
		return null;
		
	}
	
	private Conditional conditional(
			SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token token = reader.read();
		if(token.type != WHILE_KW && token.type != IF_KW) {
			reader.reset(mark);
			return null;
		}
		
		if(reader.read().type != OPEN_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected opening parenthesis after "+token.get(sReader));
		}
		
		Expression condition = expression(sReader, reader);
		if(condition == null) {
			throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression as while condition");
		}
		
		if(reader.read().type != CLOS_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected closing parenthesis after expression of an "+token.get(sReader));
		}
		
		Conditional statement;
		if(token.type == WHILE_KW) {
			statement = new While(condition);
		} else if(token.type == IF_KW) {
			statement = new If(condition);
		} else {
			reader.reset(mark);
			return null;
		}
		fillControlStatement(sReader, reader, statement);
		
		return statement;
		
	}
	
	private Foreach foreach(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == FOR_KW) {
			boolean hasParen = false;
			if(reader.peek().type == OPEN_PAREN) {
				reader.skip();
				hasParen = true;
			}
			
			VariableDecl variable = variableDecl(sReader, reader);
			if(variable == null) {
				return null;
			}
			
			if(reader.read().type != COLON) {
				return null;
			}
			
			Expression collection = expression(sReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression after colon in a foreach");
			}
			
			if(hasParen && reader.read().type != CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected closing parenthesis at the end of a parenthesized foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection);
			fillControlStatement(sReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}

	private void fillControlStatement(SourceReader sReader,
			TokenReader reader, ControlStatement controlStatement)
			throws EOFException, IOException, CompilationFailedError {
		
		boolean hasBrack = false;
		if(reader.peek().type == OPEN_BRACK) {
			reader.skip();
			hasBrack = true;
		}
		
		if(hasBrack) {
			while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
				Line line = line(sReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected line inside of "+controlStatement.getClass().getSimpleName());
				}
				controlStatement.getBody().add(line);
			}
			reader.skip(); // the closing bracket
		} else {
			Line only = line(sReader, reader);
			if(only == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
				"Expected line inside of bracket-less "+controlStatement.getClass().getSimpleName());
			}
			controlStatement.getBody().add(only);
		}
	}

	private Instantiation instantiation(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == NEW_KW) {
			Token tName = reader.peek();
			if(tName.type == NAME) {
				reader.skip();
				String name = tName.get(sReader);
				Instantiation inst = new Instantiation(name, ""); // TODO add '#' parsing.
				exprList(sReader, reader, inst.getArguments()); // we don't care whether we have args or not
				return inst;
			}
			Instantiation inst = new Instantiation();
			exprList(sReader, reader, inst.getArguments()); // we don't care whether we have args or not
			return inst;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Return returnStatement(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == RETURN_KW) {
			Expression expr = expression(sReader, reader);
			if(expr == null) return new Return();
			return new ValuedReturn(expr);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private FunctionCall functionCall(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token tName = reader.read();
		if(tName.type != NAME && tName.type != THIS_KW && tName.type != SUPER_KW) {
			reader.reset(mark);
			return null;
		}
		String name = tName.get(sReader);
		
		String suffix = "";
		if(reader.peek().type == TILDE) {
			reader.skip();
			Token tSuff = reader.read();
			if(tSuff.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(tSuff.start),
				"Expecting suffix after 'functionname~' !");
			}
			suffix = tSuff.get(sReader);
		}

		FunctionCall call = new FunctionCall(name, suffix);
		
		if(!exprList(sReader, reader, call.getArguments())) {
			reader.reset(mark);
			return null; // not a function call
		}
		
		return call;
		
	}

	private boolean exprList(SourceReader sReader, TokenReader reader, NodeList<Expression> list) throws IOException {

		int mark = reader.mark();
		
		if(!reader.hasNext()) return false;
		
		if(reader.read().type != OPEN_PAREN) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peek().type == CLOS_PAREN) {
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.read().type != COMMA) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected comma between arguments of a function call");
				}
			} else {
				reader.skipNonWhitespace();
				Expression expr = expression(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected expression as argument of function call");
				}
				list.add(expr);
			}
			comma = !comma;
			
		}
		
		return true;
		
	}
	
	private Declaration declaration(SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();
		
		VariableDecl varDecl = variableDecl(sReader, reader);
		if(varDecl != null) return varDecl;
		
		FunctionDecl funcDecl = functionDecl(sReader, reader);
		if(funcDecl != null) return funcDecl;
		
		ClassDecl classDecl = classDecl(sReader, reader);
		if(classDecl != null) return classDecl;
		
		CoverDecl coverDecl = coverDecl(sReader, reader);
		if(coverDecl != null) return coverDecl;
		
		reader.reset(mark);
		return null;
	}
	
	private VariableDecl variableDecl(SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();

		List<VariableDeclAtom> atoms = new ArrayList<VariableDeclAtom>();
		
		if(reader.peek().type != NAME) {
			reader.reset(mark);
			return null;
		}
		while(reader.peek().type == NAME) {
			String name = reader.read().get(sReader);
			Expression expr = null;
			if(reader.peek().type == ASSIGN) {
				reader.skip();
				expr = expression(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected expression as an initializer to a variable declaration.");
				}
			}
			atoms.add(new VariableDeclAtom(name, expr));
			if(reader.peek().type != COMMA) break;
			reader.skip();
		}
		
		if(reader.read().type != COLON) {
			reader.reset();
			return null;
		}
		
		boolean isConst = false;
		boolean isStatic = false;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == CONST_KW) {
				isConst = true;
				reader.skip();
			} else if(t.type == STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else {
				break;
			}
		}
		
		Type type = type(sReader, reader);
		if(type == null) {
			reader.reset(mark);
			return null;
		}
		
		VariableDecl decl = new VariableDecl(type, isConst, isStatic);
		decl.getAtoms().addAll(atoms);
		return decl;
	}
	
	private CoverDecl coverDecl(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == NAME || tName.type == NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		if(reader.read().type == COVER_KW) {
			
			Type fromType = null;
			if(reader.peek().type == FROM_KW) {
				reader.skip();
				fromType = type(sReader, reader);
				if(fromType == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected cover's base type name after the from keyword.");
				}
			}
			
			CoverDecl coverDecl = new CoverDecl(name, fromType);
			if(comment != null) coverDecl.setComment(comment);
			
			Token t2 = reader.read();
			if(t2.type != OPEN_BRACK) {
				if(t2.type == LINESEP) {
					return coverDecl; // empty cover, acts like a typedef
				}
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin cover declaration, got "+t2.type);
			}
			
			while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
			
				VariableDecl varDecl = variableDecl(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					if(fromType != null) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"You can't add member variables to a Cover which already has a base type (in this case, "
								+fromType.getName()+")");
					}
					coverDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = functionDecl(sReader,reader);
				if(funcDecl != null) {
					coverDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration");
			
			}
			reader.skip();
			
			return coverDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private ClassDecl classDecl(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == NAME || tName.type == NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isAbstract = reader.peek().type == ABSTRACT_KW;
		if(isAbstract) {
			reader.skip();
		}
		
		if(reader.read().type == CLASS_KW) {
			
			String superName = "";
			if(reader.peek().type == FROM_KW) {
				reader.skip();
				Token tSuper = reader.read();
				if(tSuper.type != NAME) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected super-class name after the from keyword.");
				}
				superName = tSuper.get(sReader);
				
			}
			
			Token t2 = reader.read();
			if(t2.type != OPEN_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin class declaration.");
			}
			
			ClassDecl classDecl = new ClassDecl(name, isAbstract);
			classDecl.setSuperName(superName);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
			
				if(reader.peek().type == LINESEP) {
					reader.skip(); continue;
				}
				
				VariableDecl varDecl = variableDecl(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					classDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = functionDecl(sReader,reader);
				if(funcDecl != null) {
					classDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration, got "+reader.peek().type);
			
			}
			reader.skip();
			
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private FunctionDecl functionDecl(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == NAME || tName.type == NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isExtern = false;
		
		Token kw = reader.peek();
		while(kw.type == ABSTRACT_KW
		   || kw.type == STATIC_KW
		   || kw.type == FINAL_KW
		   || kw.type == EXTERN_KW
		   ) {
			
			reader.skip();
			
			switch(kw.type) {
			case ABSTRACT_KW: isAbstract = true; break;
			case STATIC_KW: isStatic = true; break;
			case FINAL_KW: isFinal = true; break;
			case EXTERN_KW: isExtern = true; break;
			default:
			}
			
			kw = reader.peek();
		}
		
		if(reader.read().type != FUNC_KW) {
			reader.reset(mark);
			return null;
		}
		
		String suffix = "";
		if(reader.peek().type == TILDE) {
			reader.skip();
			Token tSuffix = reader.peek();
			if(tSuffix.type == NAME) {
				reader.skip();
				suffix = tSuffix.get(sReader);
			}
		}
		
		FunctionDecl functionDecl = new FunctionDecl(
				name, suffix, isFinal, isStatic, isAbstract, isExtern);
		if(comment != null) functionDecl.setComment(comment);
		
		if(reader.peek().type == OPEN_PAREN) {
			reader.skip();
			boolean comma = false;
			while(true) {
				
				if(reader.peek().type == CLOS_PAREN) {
					reader.skip(); // skip the ')'
					break;
				}
				if(comma) {
					if(reader.read().type != COMMA) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
								"Expected comma between arguments of a function definition");
					}
				} else {
					Argument arg = argument(sReader, reader, isExtern);
					if(arg == null) {
						throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
								"Expected variable declaration as an argument of a function definition");
					}
					functionDecl.getArguments().add(arg);
				}
				comma = !comma;
				
			}
		}
		
		if(reader.peek().type == LINESEP) return functionDecl;
		
		Token t = reader.read();
		if(t.type == ARROW) {
			Type returnType = type(sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			t = reader.read();
		}
		
		if(t.type == LINESEP) return functionDecl;

		if(t.type != OPEN_BRACK) {
			reader.rewind();
			Line line = line(sReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
			}
			functionDecl.getBody().add(line);
			return functionDecl;
		}
	
		while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
			if(reader.peek().type == LINESEP) {
				reader.skip(); continue;
			}
		
			Line line = line(sReader, reader);
			if(line == null && reader.hasNext() && reader.peek().type != CLOS_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected statement in function body. Found "+reader.peek().type+" instead.");
			}
			functionDecl.getBody().add(line);
		}
		reader.skip();
		
		return functionDecl;
	}
	
	private Argument argument(SourceReader sReader, TokenReader reader, boolean isExtern) throws IOException {

		if(reader.peek().type == TRIPLE_DOT) {
			reader.skip();
			return new VarArg();
		}
		
		
		Token t = reader.read();
		if(t.type == NAME) {
			if(reader.peek().type == COLON) {
				reader.skip();
				Type type = type(sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected argument type after its name and ':'");
				}
				return new RegularArgument(type, t.get(sReader));
			}
			reader.rewind();
		}
		
		if(t.type == ASSIGN) {
			Token t2 = reader.read();
			if(t2.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberAssignArgument(t2.get(sReader));
		}
		
		if(t.type == DOT) {
			Token t2 = reader.read();
			if(t2.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberArgument(t2.get(sReader));
		}
		
		if(isExtern) {
			Type type = type(sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected argument type in extern func definition ':'");
			}
			return new TypeArgument(type);
		}
		
		reader.reset();
		return null;
		
	}
	
	private Access access(SourceReader sReader, TokenReader reader) {
		
		int mark = reader.mark();
		
		VariableAccess varAcc = variableAccess(sReader, reader);
		if(varAcc != null) {
			return varAcc;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private VariableAccess variableAccess(SourceReader sReader, TokenReader reader) {
		
		int mark = reader.mark();
		
		Token t = reader.peek();
		if(t.type == NAME || t.type == THIS_KW) {
			reader.skip();
			return new VariableAccess(t.get(sReader));
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private Type type(SourceReader sReader, TokenReader reader) {
		
		String name = "";
		int pointerLevel = 0;
		
		while(reader.hasNext()) {
			Token t = reader.peek();
			if(t.type == TokenType.UNSIGNED) {
				reader.skip();
				name += "unsigned ";
			} else if(t.type == TokenType.SIGNED) {
				reader.skip();
				name += "signed ";
			} else if(t.type == TokenType.LONG) {
				reader.skip();
				name += "long ";
			} else break;
		}
			
		if(reader.peek().type == NAME) {
			name += reader.read().get(sReader);
		}
		
		while(reader.peek().type == STAR) {
			pointerLevel++;
			reader.skip();
		}
		
		if(!name.isEmpty()) {
			return new Type(name.trim(), pointerLevel);
		}
		return null;
		
	}
	
	private Expression expression(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		if(reader.peek().type == EXCL) {
			reader.skip();
			Expression inner = expression(sReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Not(inner);
		}
		
		Expression expr = flatExpression(sReader, reader);
		if(expr == null) {
			return null;
		}
		
		while(reader.hasNext()) {
			
			Token t = reader.peek();
			if(t.type == NAME) {
				
				FunctionCall call = functionCall(sReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call);
					continue;
				}
				
				VariableAccess varAccess = variableAccess(sReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess);
					continue;
				}
				
			}
			
			if(t.type == DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = expression(sReader, reader);
				if(upper == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper);
				
			}
			
			if(t.type == OPEN_SQUAR) {

				reader.skip();
				Expression index = expression(sReader, reader);
				if(index == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression for the index of an array access");
				}
				if(reader.read().type != CLOS_SQUAR) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				expr = new ArrayAccess(expr, index);
				continue;
				
			}
			
			if(t.type == ASSIGN) {
				
				reader.skip();
				Expression rvalue = expression(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression after '='.");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Attempting to assign to a constant, e.g. "+expr);
				}
				expr = new Assignment((Access) expr, rvalue);
				continue;
				
			}
			
			if(t.type == PLUS || t.type == STAR
					|| t.type == MINUS || t.type == SLASH || t.type == PERCENT
					|| t.type == GT || t.type == LT || t.type == GTE || t.type == LTE
					|| t.type == EQUALS || t.type == NOT_EQ) {
				
				reader.skip();
				Expression rvalue = expression(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected rvalue after binary operator");
				}
				switch(t.type) {
					case PLUS:  expr = new Add(expr, rvalue); break;
					case STAR:  expr = new Mul(expr, rvalue); break;
					case MINUS: expr = new Sub(expr, rvalue); break;
					case SLASH: expr = new Div(expr, rvalue); break;
					case PERCENT: expr = new Mod(expr, rvalue); break;
					case GT: expr = new Compare(expr, rvalue, CompareType.GREATER); break;
					case GTE: expr = new Compare(expr, rvalue, CompareType.GREATER_OR_EQUAL); break;
					case LT: expr = new Compare(expr, rvalue, CompareType.LESSER); break;
					case LTE: expr = new Compare(expr, rvalue, CompareType.LESSER_OR_EQUAL); break;
					case EQUALS: expr = new Compare(expr, rvalue, CompareType.EQUAL); break;
					case NOT_EQ: expr = new Compare(expr, rvalue, CompareType.NOT_EQUAL); break;
					default: throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Unknown binary operation yet");
				}
				continue;
				
				
			}
			
			if(t.type == AS_KW) {
				
				reader.skip();
				Type type = type(sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected destination type after 'as' keyword (e.g. for casting)");
				}
				expr = new Cast(expr, type);
				continue;
				
			}
			
			return expr;
			
		}
		
		return null;
		
	}
	
	private Expression flatExpression(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		int parenNumber = 0;
		Token t = reader.peek();
		while(t.type == OPEN_PAREN) {
			reader.skip();
			parenNumber++;
			t = reader.peek();
		}
		
		Expression expression;
		
		if(parenNumber > 0) {
			expression = expression(sReader, reader);
		} else {
			expression = flatUnparenExpression(sReader, reader);
		}
		
		if(expression == null) {
			if(parenNumber > 0) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression in parenthesis");
			}
			reader.reset(mark);
			return null;
		}
		
		while(parenNumber > 0) {
			if(reader.read().type != CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Missing closing parenthesis.");
			}
			expression = new Parenthesis(expression);
			parenNumber--;
		}
		
		return expression;
		
	}
	
	private Expression flatUnparenExpression(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Literal literal = literal(sReader, reader);
		if(literal != null) {
			return literal;
		}

		Instantiation instantiation = instantiation(sReader, reader);
		if(instantiation != null) return instantiation;
		
		FunctionCall funcCall = functionCall(sReader, reader);
		if(funcCall != null) return funcCall;
		
		Declaration declaration = declaration(sReader, reader);
		if(declaration != null) return declaration;
				
		Access access = access(sReader, reader);
		if(access != null) return access;
		
		reader.reset(mark);
		return null;
		
	}

	private Literal literal(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == STRING_LIT) {
			return new StringLiteral(t.get(sReader));
		}
		if(t.type == CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(t.get(sReader)));			} catch (SyntaxError e) {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Malformed char literal");
			}
		}
		if(t.type == DEC_INT) 
			return new IntLiteral(Long.parseLong(t.get(sReader).replace("_", "")), Format.DEC);
		if(t.type == HEX_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader).replace("_", "").toUpperCase(), 16), Format.HEX);
		if(t.type == OCT_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader).replace("_", "").toUpperCase(), 8), Format.OCT);
		if(t.type == BIN_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader).replace("_", "").toUpperCase(), 2), Format.BIN);
		if(t.type == DEC_FLOAT)
			return new FloatLiteral(Double.parseDouble(t.get(sReader).replace("_", "")));
		if(t.type == TRUE)
			return new BoolLiteral(true);
		if(t.type == FALSE)
			return new BoolLiteral(false);
		if(t.type == NULL)
			return new NullLiteral();
		
		reader.reset(mark);
		return null;
		
	}
		
}
