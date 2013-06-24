package heros.debugui.launching;

import heros.debugui.Option;
import heros.debugui.exception.WrongAddressFormatException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import library.LibraryManager;
import heros.debugui.IO.FolderSearcher;
import io.FileIO;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import protocol.Protocol;
import protocol.phase.TransmitLibaryPhase;

import command.CommandFactory;
import command.GeneralCommand;

import connection.Connection;
import connection.ConnectionListener;

public class HerosDebugLaunchDelegate extends JavaLaunchDelegate {
	

	private IJavaProject analysisProject;
	private String analysisMainClass;
	//private Socket connection;
	

	public HerosDebugLaunchDelegate() {
	}
	
	private ConnectionListener initServer(ILaunchConfiguration configuration) throws IOException, CoreException{
		
		
		//TODO aus optionen herauslesen und adden
		LibraryManager.getInstance().addLibrary("soot-finoflow-android", "/home/aura/lib/soot-infoflow-android.jar");
		
		ConnectionListener listener = new ConnectionListener(Integer.valueOf(Option.LOCAL_ADDRESS.split(":")[1]));
		new Thread(listener).start();
		return listener;
	}
	


	
	
	/**
	 * Generates an analysis command to analyse the compiled project
	 * @param configuration
	 * @return
	 * @throws CoreException
	 * @throws WrongAddressFormatException
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private String[] generateCommand(ILaunchConfiguration configuration) throws CoreException, WrongAddressFormatException, UnknownHostException, IOException{
		
		String[] address = Option.getInstance().getAddress().split(":");
		
		if(address.length != 2) throw new WrongAddressFormatException();
		
		if(!address[1].equals("0") && !address[1].matches("[1-9][0-9]*")) throw new WrongAddressFormatException();
		
		String userDir = System.getProperty("user.dir");
		
		String path = getProgramArguments(configuration).split(":")[0];
		path = path.substring(4) + configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "").replace(".", "/");
		path = path.substring(userDir.length()+1);
		
		analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		String analysisProjectName = configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, "");
		String classPath = getProgramArguments(configuration).split(":")[0].substring(4).replace(userDir + File.separator, "");
		
		String destination = classPath.substring(0, classPath.lastIndexOf(analysisProjectName)+ analysisProjectName.length()) +File.separator +"bin";
		
		StringBuilder command = new StringBuilder();
		
		String arguments = getProgramArguments(configuration);
		arguments = arguments.substring(0, arguments.length()-1);
		
		
		
		String[] env = getEnvironment(configuration)[0].split(":");
		
		String callIP = env[0];
		String callPort = env[1];
		
		List<String> list = FolderSearcher.listFiles(classPath);
		
		for(String file : list){
			command.append("<file>");
			command.append(file);
			command.append("</file>");
		}
		
		command.append("<command>");
		command.append("java -cp <library>soot-finoflow-android</library> ");
		command.append("soot.jimple.infoflow.android.TestApps.Main ");
		command.append(analysisMainClass);
		command.append(" ");
		command.append(classPath);
		command.append(" ");
		command.append(callIP);
		command.append(" ");
		command.append(callPort);
		command.append("</command>");
		
		
		
		
		return new String[]{address[0], address[1], command.toString()};
	}
	
	private void runClient(String[] args) throws UnknownHostException, IOException{
		
//		if(args.length != 3) {
//			System.out.println("Wrong number of arguments");
//			System.exit(1);
//		}
//		
//		
//		Connection con = new Connection(connection);
//		
//		GeneralCommand c = new CommandFactory(/*correctCommand2*/ args[2]).generate();
//		
//		Protocol p = new Protocol(new TransmitLibaryPhase(c), con, new FileIO());
//		
//		while(!p.isFinished())
//			p.handle();
		
		client.Main.main(args);
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		//getEnvironment(configuration);
		
		String analysisProjectName = configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, "");
		analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(analysisProjectName);
		analysisProject = JavaCore.create(project);
		
		
		try{
			
			//check if server destination is local and start a local server if needed
			ConnectionListener listener = null;
			if(Option.getInstance().getAddress().equals(Option.LOCAL_ADDRESS)){
				listener = initServer(configuration);
				Thread.yield();
			}
			
			//analyse compiled project
			String [] command  = generateCommand(configuration);
			runClient(command);
			
			//if Server has been started close it
			if(listener != null){
				listener.stop();
				Thread.yield();
			}
			
		} catch (CoreException e){
			System.err.println("Couldn't assemble neccessary informations");
			return;
		} catch (UnknownHostException e){
			System.err.println("Couldn't connect to the server");
			return;
		} catch (IOException e){
			System.err.println("Couldn't start the server");
			return;
		} catch (WrongAddressFormatException e) {
			System.err.println("Wrong address format. ADDRESS:PORT");
		}
		
		
		
		
		
		
		/*
		String analysisProjectName = configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, "");
		analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(analysisProjectName);
		analysisProject = JavaCore.create(project);
		
		*/
		
		//super.launch(configuration, "run", launch, monitor);
	}
	
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		//String originalMainClass = super.getMainTypeName(configuration);
		String cp = classPathOfAnalyzedProjectAsString(configuration);
		return "-cp " + cp + " ";// + originalMainClass;
	}
	
	@Override
	public String getMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return analysisMainClass;
	}
	
	@Override
	public String[] getClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		return analysisProjectClassPathAsStringArray();
	}
	
	@Override
	public String[] getEnvironment(ILaunchConfiguration configuration) throws CoreException {
		return ServerSocketManager.openSocketAndUpdateEnvironment(configuration, super.getEnvironment(configuration));
	}
	
	protected URL[] classPathOfProject(IJavaProject project, boolean includeOutputFolder) {
		IClasspathEntry[] cp;
		try {
			cp = project.getResolvedClasspath(true);
			List<URL> urls = new ArrayList<URL>();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			if(includeOutputFolder) {
				String uriString = workspace.getRoot().getFile(
						project.getOutputLocation()).getLocationURI().toString()
						+ "/";
				urls.add(new URI(uriString).toURL());
			}
			for (IClasspathEntry entry : cp) {
				IResource member = workspace.getRoot().findMember(entry.getPath());
				String file;
				if(member!=null)
					file = member.getLocation().toOSString();
				else
					file = entry.getPath().toOSString();
				URL url = new File(file).toURI().toURL();
				urls.add(url);
			}
			URL[] array = new URL[urls.size()];
			urls.toArray(array);
			return array;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return new URL[0];
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new URL[0];
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new URL[0];
		}
	}
	
	protected String[] analysisProjectClassPathAsStringArray() {		
		URL[] cp = classPathOfProject(analysisProject,true);
		String[] res = new String[cp.length];
		for (int i = 0; i < res.length; i++) {
			URL entry = cp[i];
			res[i] = entry.getPath() + File.pathSeparator;
		}
		return res;
	}
	
	protected String classPathOfAnalyzedProjectAsString(ILaunchConfiguration config) {
		StringBuffer cp = new StringBuffer();
		try {
			for (URL url : classPathOfProject(getJavaProject(config),false)) {
				cp.append(url.getPath());
				cp.append(File.pathSeparator);
			}
			return cp.toString();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

}
