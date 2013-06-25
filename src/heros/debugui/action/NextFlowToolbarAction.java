package heros.debugui.action;

import heros.debugui.drawing.EdgeDrawingManager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class NextFlowToolbarAction implements IWorkbenchWindowActionDelegate{
	
	EdgeDrawingManager manager;

	@Override
	public void run(IAction arg0) {
		manager.drawNextEntirePath();
		
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		manager = EdgeDrawingManager.getInstance();
	}
	


}
