package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.hobgoblins.CaseEnforcer;
import org.ooc.middle.hobgoblins.Checker;
import org.ooc.middle.hobgoblins.CoverMerger;
import org.ooc.middle.hobgoblins.DefaultConstructorGiver;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.hobgoblins.ReturnHandler;
import org.ooc.middle.hobgoblins.SuffixConflictAnnihilator;
import org.ooc.middle.hobgoblins.Unwrapper;

/**
 * The Tinkerer(TM) handles all the work that there's to be done
 * between parsing and generating, e.g.
 *  - Resolving symbols (functions, variables)
 *  - Ensuring encapsulation isn't violated (e.g. unauthorized access
 *  to protected members)
 *  - Huh.. other things, I guess, see all classes in this package 
 * 
 * @author Amos Wenger
 */
public class Tinkerer implements Hobgoblin {

	@Override
	public void process(Module module, BuildParams params) throws IOException {

		new CaseEnforcer().process(module, params);
		
		new DefaultConstructorGiver().process(module, params);
		new Unwrapper().process(module, params);
		new CoverMerger().process(module, params);
		
		new Resolver().process(module, params);
		
		new Checker().process(module, params);
		new SuffixConflictAnnihilator().process(module, params);
		new ReturnHandler().process(module, params);
		
	}

}
