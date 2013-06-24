package heros.debugui.drawing;

import heros.debugsupport.SerializableEdgeData;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;

public class EdgeDrawingManager extends EdgeDrawing{
	

	
	public EdgeDrawingManager(String projectName) {
		super(projectName);
		paths = new ArrayList<List<SerializableEdgeData>>();
	}

	private final List<List<SerializableEdgeData>> paths;
	
	private int currentFlow = 0;
	
	private int currentArrow = 0;
	

	
	public void addPath(List<SerializableEdgeData> path){
		synchronized (paths) {
			paths.add(path);
		}
	}
	
	
	private void removePath(int index){
		if(index >= 0 && index < paths.size()){
			for(SerializableEdgeData edge : paths.get(index)){
				IPath path;
				try {
					path = javaProject.findType(/*edge.className*/ "test.Hello").getPath();
					IFile file = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					String osString = file.getLocation().toOSString();
					
					SPArrow arrow = new SPArrow(osString, edge.startLine, edge.startColumn, edge.endLine, edge.endColumn);
					SourcePainterRegistry.removeArrowOfFile(arrow, osString);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void drawPath(int index){
		if(index >= 0 && index < paths.size()){
			List<SerializableEdgeData> path = paths.get(index);
			for(SerializableEdgeData edge : path){
				openEditorAndDrawEdge(edge);
			}
		}
	}
	
	
	
	
	
	
	
	public void removeLastPath(){
		
		if(currentFlow > 0){
			currentFlow--;
			removePath(currentFlow);
			
			if(currentFlow > 0){
				currentFlow--;
				drawPath(currentFlow);
				currentFlow++;
			}
		}
	}
	
	public void drawNextEntirePath(){
		if(currentFlow > 0)
			removePath(currentFlow-1);
		
		if(currentFlow < paths.size()+1){
			drawPath(currentFlow);
			currentFlow++;
			currentArrow = 0;
		}
	}
	
	
	
	

}
