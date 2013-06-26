package heros.debugui.launching;

import heros.debugsupport.SerializableEdgeData;
import heros.debugui.drawing.EdgeDrawing;
import heros.debugui.drawing.EdgeDrawingManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;


public class ServerSocketManager {
	
	public static final String ENV_VAR_NAME = "HEROS_DEBUG_PORT";
	
	public static String openSocketAndGetIpAndPort(ILaunchConfiguration configuration){
		String hostNameAndPort = ServerSocketManager.openSocket(configuration);
		return hostNameAndPort;
	}

	public static String[] openSocketAndUpdateEnvironment(
			ILaunchConfiguration configuration, String[] environment) throws CoreException {
		String hostNameAndPort = ServerSocketManager.openSocket(configuration);
		if(environment==null) environment = new String[0];
		String[] newEnv = new String[environment.length+1];
		System.arraycopy(environment, 0, newEnv, 1, environment.length);
		newEnv[0] = ENV_VAR_NAME + "="+hostNameAndPort;
		return newEnv;
	}

	private static String openSocket(final ILaunchConfiguration configuration) {
		
		
		
		final ServerSocket serverSocket;
		
		try {
			serverSocket = new ServerSocket(0);
			String host = serverSocket.getInetAddress().getHostAddress();
			int port = serverSocket.getLocalPort();
			
			new Thread("SocketListener") {

				@Override
				public void run() {
					try {
						System.out.println("Starting server");
						//wait at most 10 seconds for the socket to open
						//this allows the thread to terminate if no debugging is initiated by the analysis
						//serverSocket.setSoTimeout(10000);
						serverSocket.setSoTimeout(200000);
						Socket socket = serverSocket.accept();
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
						//EdgeDrawing edgeDrawing = new EdgeDrawing(projectName);
						//EdgeDrawingManager edgeManager = EdgeDrawingManager.getInstance();
						//edgeManager.setEdgeDrawing(edgeDrawing);
						
						List<List<SerializableEdgeData>> paths = (List<List<SerializableEdgeData>>) ois.readObject();
						ois.close();
						System.out.println(paths.size());
						for(List<SerializableEdgeData> path : paths){
							
							//edgeManager.addPath(path);
						}
						
						
						
						socket.close();
						serverSocket.close();
						
						System.out.println("Terminate Server");
						
					} catch(SocketTimeoutException e) {
						//ignore; just terminate thread
					} catch(EOFException e) {
						//ignore; just terminate thread
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}.start();
			//allow other thread to get to the point where we
			//accept connections on the server socket before we proceed here
			//Thread.yield();
			
			return host + ":" +port;
			
			
		} catch (IOException e) {
			throw new RuntimeException("Problem creating server socket",e);
		} 
			
		
		
		/*
		try {
			serverSocket = new ServerSocket(0);
			String host = serverSocket.getInetAddress().getHostAddress();
			int port = serverSocket.getLocalPort();

			new Thread("SocketListener") {

				@Override
				public void run() {
					try {
						//wait at most 10 seconds for the socket to open
						//this allows the thread to terminate if no debugging is initiated by the analysis
						serverSocket.setSoTimeout(10000);
						Socket socket = serverSocket.accept();
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
						EdgeDrawing edgeDrawing = new EdgeDrawing(projectName);
						while(true) {
							SerializableEdgeData obj = (SerializableEdgeData) ois.readObject();
							edgeDrawing.openEditorAndDrawEdge(obj);
						}
					} catch(SocketTimeoutException e) {
						//ignore; just terminate thread
					} catch(EOFException e) {
						//ignore; just terminate thread
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}.start();
			//allow other thread to get to the point where we
			//accept connections on the server socket before we proceed here
			Thread.yield();
			
			return host + ":" +port;
		} catch (IOException e) {
			throw new RuntimeException("Problem creating server socket",e);
		} 
		*/
	}
}
