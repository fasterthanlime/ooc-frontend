package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.hobgoblins.AccessChecker;
import org.ooc.middle.hobgoblins.CaseEnforcer;
import org.ooc.middle.hobgoblins.DefaultConstructorGiver;
import org.ooc.middle.hobgoblins.MemberHandler;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
import org.ooc.middle.hobgoblins.SuffixConflictAnnihilator;
import org.ooc.middle.hobgoblins.SuperResolver;
import org.ooc.middle.hobgoblins.TypeResolver;
import org.ooc.middle.hobgoblins.Unwrapper;
import org.ooc.middle.hobgoblins.VoidReturnCrusher;

/**
 * The Tinkerer(TM) handles all the work that there's to be done
 * between parsing and generating, e.g.
 *  - Resolving symbols (functions, variables)
 *  - Ensuring encapsulation isn't violated (e.g. unauthorized access
 *  to private members)
 *  - Huh.. other things, I guess, see all classes in this package 
 * 
 * @author Amos Wenger
 */
public class Tinkerer implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		new CaseEnforcer().process(unit);
		
		new VoidReturnCrusher().process(unit);
		new SuperResolver().process(unit);
		
		new DefaultConstructorGiver().process(unit);
		new Unwrapper().process(unit);
		new MemberHandler().process(unit);
		
		new TypeResolver().process(unit);
		new ModularAccessResolver().process(unit);
		
		new AccessChecker().process(unit);
		new SuffixConflictAnnihilator().process(unit);
		
	}

}
