package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.SourceUnit;

/**
 * The Tinkerer(TM) handles all the work that there's to be done
 * between parsing and generating, e.g.
 *  - Resolving symbols (functions, variables)
 *  - Ensuring encapsulation isn't violated (e.g. unauthorized access
 *  to private members)
 *  - Huh.. other things, I guess. 
 * 
 * @author Amos Wenger
 */
public class Tinkerer implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		new CaseEnforcer().process(unit);
		new VoidReturnCrusher().process(unit);
		new DefaultConstructorGiver().process(unit);
		new ThisMemberAdder().process(unit);
		new Unwrapper().process(unit);
		
		new TypeResolver().process(unit);
		new VarAccessResolver().process(unit);
		new FuncCallResolver().process(unit);
		
	}

}
