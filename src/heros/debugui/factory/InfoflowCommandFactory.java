package heros.debugui.factory;

import heros.debugui.Option;
import heros.debugui.IO.FolderSearcher;
import heros.debugui.exception.WrongAddressFormatException;
import heros.debugui.launching.HerosLaunchConstants;
import heros.debugui.launching.ServerSocketManager;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class InfoflowCommandFactory implements IFactory{

	@Override
	public String[] generateCommand(IJavaProject project , ILaunchConfiguration configuration) throws CoreException, WrongAddressFormatException, UnknownHostException, IOException{
		
		String analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		
		//check address
		String[] address = Option.getInstance().getAddress().split(":");
		if (address.length != 2)
			throw new WrongAddressFormatException();

		if (!address[1].equals("0") && !address[1].matches("[1-9][0-9]*"))
			throw new WrongAddressFormatException();

		String[] sourceFolders = getJavaProjectSourceDirectories(project);
		if(sourceFolders.length == 0)
			//TODO throw error
			System.out.println("Throw error");
		
		StringBuilder command = new StringBuilder();
		
		
		for(String sourceFolder : sourceFolders){
			List<String> sourceFiles = FolderSearcher.listFiles(sourceFolder);
			for(String sourceFile : sourceFiles){
				command.append("<file>");
				command.append(sourceFile);
				command.append("</file>");
			}
		}
		
		if(command.length() == 0)
			//TODO throw error
			System.out.println("Throw error");
		
		command.append("<command>");
		command.append("java -cp <library>soot-finoflow-android</library> ");
		command.append("soot.jimple.infoflow.android.TestApps.Main ");
		command.append(analysisMainClass);
		
		command.append(" ");
		for(String sourceFolder : sourceFolders){
			command.append(sourceFolder);
			command.append(File.pathSeparator);
		}
		command.setLength(command.lastIndexOf(File.pathSeparator));
		command.append(" ");
		
		String[] env = ServerSocketManager.openSocketAndGetIpAndPort(configuration).split(":");
		String callIP = env[0];
		String callPort = env[1];
		
		
		command.append(callIP);
		command.append(" ");
		command.append(callPort);
		command.append("</command>");

		return new String[] { address[0], address[1], command.toString() };
	}
	
	public String[] getJavaProjectSourceDirectories (IJavaProject project) throws CoreException, JavaModelException{
	    ArrayList<String> paths = new ArrayList<String>();

	    IClasspathEntry[] classpathEntries = project.getResolvedClasspath(true);
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    String userDir = System.getProperty("user.dir");
	    
	    // foreach entry in the classpath look if it is a source folder and
	    // add the relative path to the list;
	    for(IClasspathEntry entry : classpathEntries){
	    	IResource member = workspace.getRoot().findMember(entry.getPath());
	    	if(entry.getContentKind() == IPackageFragmentRoot.K_SOURCE && member != null){
	    		String file = member.getLocation().toOSString();
	    		
	    		if(userDir.length()+1 < file.length()){
	    			paths.add(file.substring(userDir.length()+1));
	    		}
	    		
	    		
	    	}
	    }
	    
	    return paths.toArray(new String[0]);


	} 
	
	

}
