package heros.debugui.factory;

import heros.debugui.exception.WrongAddressFormatException;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;

public interface IFactory {
	
	public String[] generateCommand(IJavaProject project, ILaunchConfiguration configuration) throws CoreException, WrongAddressFormatException, UnknownHostException, IOException;

}
