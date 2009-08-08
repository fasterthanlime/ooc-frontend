package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;

public class FunctionDecl extends Declaration implements Scope {

	public static enum FunctionDeclType {
		FUNC,
		IMPL,
		OVER,
	}
	
	private OocDocComment comment;
	
	private boolean isFinal;
	private boolean isStatic;
	private boolean isAbstract;
	private boolean isExtern;
	
	private TypeDeclaration typeDecl;

	private FunctionDeclType declType;
	private String suffix;
	private final NodeList<Line> body;
	private Type returnType;
	private final NodeList<Argument> arguments;
	
	public FunctionDecl(FunctionDeclType declType, String name, String suffix,
			boolean isFinal, boolean isStatic, boolean isAbstract, boolean isExtern) {
		
		super(name);
		this.declType = declType;
		this.suffix = suffix;
		this.isFinal = isFinal;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isExtern = isExtern;
		this.body = new NodeList<Line>();
		this.returnType = new Type("void");
		this.arguments = new NodeList<Argument>();
		
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public FunctionDeclType getDeclType() {
		return declType;
	}
	
	public void setDeclType(FunctionDeclType declType) {
		this.declType = declType;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
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
	
	public boolean isExtern() {
		return isExtern;
	}
	
	public void setExtern(boolean isExtern) {
		this.isExtern = isExtern;
	}
	
	public TypeDeclaration getTypeDecl() {
		return typeDecl;
	}
	
	public void setTypeDecl(TypeDeclaration typeDecl) {
		this.typeDecl = typeDecl;
	}
	
	/**
	 * @return true if it's a member function
	 */
	public boolean isMember() {
		return typeDecl != null;
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
		return new Type("Func");
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
		if(isMember()) iter.next();
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
			dst.append(typeDecl.getName()).append('_');
		}
		writeSuffixedName(dst);
		
	}

	public void writeSuffixedName(Appendable dst) throws IOException {
		
		dst.append(name);
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
	
}
