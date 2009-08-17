package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.middle.hobgoblins.Checker;
import org.ooc.middle.hobgoblins.CaseEnforcer;
import org.ooc.middle.hobgoblins.CoverMerger;
import org.ooc.middle.hobgoblins.DefaultConstructorGiver;
import org.ooc.middle.hobgoblins.MemberHandler;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.hobgoblins.ReturnHandler;
import org.ooc.middle.hobgoblins.SuffixConflictAnnihilator;
import org.ooc.middle.hobgoblins.SuperResolver;
import org.ooc.middle.hobgoblins.TypeResolver;
import org.ooc.middle.hobgoblins.Unwrapper;

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
	public void process(Module module) throws IOException {

		new CaseEnforcer().process(module);
		
		new ReturnHandler().process(module);
		new SuperResolver().process(module);
		
		new DefaultConstructorGiver().process(module);
		new Unwrapper().process(module);
		new CoverMerger().process(module);
		new MemberHandler().process(module);
		
		new TypeResolver().process(module);
		new Resolver().process(module);
		
		new Checker().process(module);
		new SuffixConflictAnnihilator().process(module);
		
	}

}
