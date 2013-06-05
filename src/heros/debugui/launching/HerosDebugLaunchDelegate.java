package heros.debugui.launching;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import library.LibraryManager;

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

import connection.ConnectionListener;

public class HerosDebugLaunchDelegate extends JavaLaunchDelegate {

	private IJavaProject analysisProject;
	private String analysisMainClass;
	
	//TODO nur zum test
	private final String sinks  = "--sink soot.jimple.infoflow.test.android.ConnectionManager: void publish(java.lang.String)%soot.jimple.infoflow.test.android.ConnectionManag: void publish(int)";
	private final String source = "--source soot.jimple.infoflow.test.android.TelephonyManager: java.lang.String getDeviceId()%soot.jimple.infoflow.test.android.AccountManager: java.lang.String getPassword()%soot.jimple.infoflow.test.android.AccountManager: java.lang.String[] getUserData(java.lang.String)"; 
	private String classPath = "--cp /home/aura/git/soot-infoflow/bin";
	private String ep = "--eps soot.jimple.infoflow.test.ArrayTestCode: void concreteWriteReadSamePosTest()";

	public HerosDebugLaunchDelegate() {
	}
	
	private void initServer() throws IOException{
		
		//TODO zum test
		LibraryManager.getInstance().addLibrary("soot", "/home/aura/workspace/ServerApplikation/lib/soot-2.5.0.jar");
		LibraryManager.getInstance().addLibrary("java", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar");
		LibraryManager.getInstance().addLibrary("soot-finoflow", "/home/aura/lib/soot-infoflow.jar");
		
		
		
		new Thread(new ConnectionListener(1337)).start();
		
	}
	
	private String[] generateCommand(ILaunchConfiguration configuration) throws CoreException{
		
		String userDir = System.getProperty("user.dir");
		
		String path = getProgramArguments(configuration).split(":")[0];
		path = path.substring(4) + configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "").replace(".", "/");
		path = path.substring(userDir.length()+1);
		
		String analysisProjectName = configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, "");
		analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		
		//String file = analysisProjectName + File.separator + analysisMainClass;
		
		StringBuilder command = new StringBuilder();
		
		String arguments = getProgramArguments(configuration);
		arguments = arguments.substring(0, arguments.length()-1);
		
		/*
		command.append("<command>");
		command.append("javac ");
		command.append(getProgramArguments(configuration));
		command.append("<file>");
		command.append(path +".java");
		command.append("</file>");
		command.append("</command>");
		command.append("<resultFile>");
		command.append(path +".class");
		command.append("</resultFiles>");
		*/
		
		command.append("<command>");
		command.append("java -cp <library>soot-finoflow</library> ");
		command.append("soot.jimple.infoflow.test.junit.Main ");
		command.append(sinks +" ");
		command.append(source +" ");
		command.append(ep +" ");
		command.append(classPath);
		
		command.append("</command>");
		
		
		
		/*
		command.append("<command>java -cp <library>soot</library>  soot.Main -cp .:<library>java</library> ");
		command.append(file);
		command.append("</command><file>");
		command.append(file + ".java");
		command.append("</file><resultFile>sootOutput/");
		command.append(file+ ".class");
		command.append("</resultFile>");
		*/
		
		//command.append(configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, ""));
		
		return new String[]{"localHost", "1337", command.toString()};
	}
	
	private void runClient(String[] command) throws UnknownHostException, IOException{
		client.Main.main(command);
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		String analysisProjectName = configuration.getAttribute(HerosLaunchConstants.PROJ_NAME_ID, "");
		analysisMainClass = configuration.getAttribute(HerosLaunchConstants.MAIN_CLASS_ID, "");
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(analysisProjectName);
		analysisProject = JavaCore.create(project);
		
		
		try{
			initServer();
			String[] command = generateCommand(configuration);
			Thread.yield();
			runClient(command);
		} catch (CoreException e){
			System.err.println("Couldn't assemble neccessary informations");
			return;
		} catch (UnknownHostException e){
			System.err.println("Couldn't connect to the server");
			return;
		} catch (IOException e){
			System.err.println("Couldn't start the server");
			return;
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
		String originalMainClass = super.getMainTypeName(configuration);
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
