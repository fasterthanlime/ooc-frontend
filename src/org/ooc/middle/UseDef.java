package org.ooc.middle;

import java.util.ArrayList;
import java.util.List;

public class UseDef {

	public static class Requirement {
		String name;
		List<Integer> version;
	}
	
	protected String identifier;
	protected String name = "";
	protected String description = "";
	protected List<Requirement> requirements;
	
	public UseDef(String identifier) {
		this.identifier = identifier;
		this.requirements = new ArrayList<Requirement>();
	}
	
}
