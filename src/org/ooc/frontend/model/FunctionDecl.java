package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class FunctionDecl extends Declaration implements Scope {

	public static Type type = new Type("Func", Token.defaultToken);
	
	protected OocDocComment comment;
	
	protected boolean isFinal;
	protected boolean isStatic;
	protected boolean isAbstract;
	protected boolean isProto = false;
	protected boolean fromPointer = false;
	
	protected TypeDecl typeDecl;

	protected String suffix;
	protected final NodeList<Line> body;
	protected Type returnType;
	protected final NodeList<Argument> arguments;
	
	public FunctionDecl(String name, String suffix, boolean isFinal,
			boolean isStatic, boolean isAbstract, boolean isExtern, Token startToken) {
		this(name, suffix, isFinal, isStatic, isAbstract, isExtern ? "" : null, startToken);
	}
	
	public FunctionDecl(String name, String suffix, boolean isFinal,
			boolean isStatic, boolean isAbstract, String externName, Token startToken) {
		
		super(name, externName, startToken);
		this.suffix = suffix;
		this.isFinal = isFinal;
		// TODO check if that's alright
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.body = new NodeList<Line>(startToken);
		this.returnType = name.equals("main") ? IntLiteral.type : new Type("Void", Token.defaultToken);
		this.arguments = new NodeList<Argument>(startToken);
		
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public boolean isFromPointer() {
		return fromPointer;
	}
	
	public void setFromPointer(boolean fromPointer) {
		this.fromPointer = fromPointer;
	}
	
	public boolean isProto() {
		return isProto;
	}
	
	public void setProto(boolean isProto) {
		this.isProto = isProto;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return typeDecl;
	}
	
	public void setTypeDecl(TypeDecl typeDecl) {
		this.typeDecl = typeDecl;
	}
	
	/**
	 * @return true if it's a member function
	 */
	public boolean isMember() {
		return typeDecl != null && !isFromPointer();
	}
	
	public NodeList<Line> getBody() {
		return body;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}
	
	public NodeList<Argument> getArguments() {
		return arguments;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		arguments.accept(visitor);
		returnType.accept(visitor);
		body.accept(visitor);
	}

	public boolean isConstructor() {
		return name.equals("new");
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == returnType) {
			returnType = (Type) kiddo;
			return true;
		}
		
		return false;
		
	}

	public String getArgsRepr() {
		
		StringBuilder sB = new StringBuilder();
		sB.append('(');
		Iterator<Argument> iter = arguments.iterator();
		if(isMember() && !isStatic() && iter.hasNext()) iter.next();
		while(iter.hasNext()) {
			Argument arg = iter.next();
			if(arg instanceof VarArg) sB.append("...");
			else sB.append(arg.getType());
			
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
		
	}
	
	@Override
	public String toString() {
		
		return getClass().getSimpleName()+" : "+name+getArgsRepr();
		
	}

	public String getFullName() {
		
		StringBuilder sB = new StringBuilder();
		try {
			writeFullName(sB);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sB.toString();
		
	}

	public void writeFullName(Appendable dst) throws IOException {
		
		if(isMember()) {
			dst.append(typeDecl.getExternName()).append('_');
		}
		writeSuffixedName(dst);
		
	}

	public void writeSuffixedName(Appendable dst) throws IOException {
		
		dst.append(getExternName());
		if(!suffix.isEmpty()) {
			dst.append('_').append(suffix);
		}
		
	}

	public String getProtoRepr() {
		return name+getArgsRepr();
	}

	public boolean sameProto(FunctionDecl decl2) {
		return name.equals(decl2.getName()) && (suffix.equals(decl2.getSuffix()));
	}

	public boolean isEntryPoint() {
		return name.equals("main");
	}
	
}
