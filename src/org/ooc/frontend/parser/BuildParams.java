package org.ooc.frontend.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.PathList;
import org.ooc.libs.DistLocator;

public class BuildParams {

	public File distLocation = DistLocator.locate();
	
	public final PathList sourcePath = new PathList();
	public final PathList libPath = new PathList();
	public final PathList incPath = new PathList();
	
	public File outPath = new File("ooc_tmp");
	
	public boolean clean = true;
	public boolean debug = false;
	public boolean verbose = false;
	public boolean shout = false;
	public boolean link = true;
	public boolean run = false;
	public boolean timing = false;
	public boolean dynGC = false; // Should link dynamically with libgc (Boehm)
	
	public List<String> dynamicLibs = new ArrayList<String>();
	
}
