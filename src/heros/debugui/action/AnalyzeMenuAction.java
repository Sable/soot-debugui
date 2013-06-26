package heros.debugui.action;

import org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction;
import org.eclipse.jface.action.IAction;

public class AnalyzeMenuAction extends AbstractLaunchHistoryAction {

	public AnalyzeMenuAction() {
		super("heros.launchGroups.analyze");
	}
	
	@Override
	public void run(IAction action) {
		super.run(action);
		System.out.println("AnalyzeMenuAction");
	}
	

}
