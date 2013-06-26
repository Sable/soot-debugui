package heros.debugui.action;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;
import org.eclipse.jface.action.IAction;

public class AnalyzeToolbarAction extends AbstractLaunchToolbarAction {

	public AnalyzeToolbarAction() {
		super("heros.launchGroups.analyze");
	}
	
	@Override
	public void run(IAction action) {
		super.run(action);
		System.out.println("AnalyzeToolbarAction");
	}
	
	
	

}
