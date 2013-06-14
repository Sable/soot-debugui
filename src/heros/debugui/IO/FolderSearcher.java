package heros.debugui.IO;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FolderSearcher {
	
	
	private List<String> fileNames;
	
	private FolderSearcher(){
		fileNames = new LinkedList<String>();
	}
	
	
	
	public static List<String> listFiles(String pathname){
		FolderSearcher temp = new FolderSearcher();
		temp.listFile(pathname);
		return temp.fileNames;
	}
	
	private void listFile(String pathname){
		File f = new File(pathname);
	    File[] listfiles = f.listFiles();
	    for (int i = 0; i < listfiles.length; i++) {
	        if (listfiles[i].isDirectory()) {
	            File[] internalFile = listfiles[i].listFiles();
	            for (int j = 0; j < internalFile.length; j++) {
	            	if(!internalFile[j].isDirectory())
	            		fileNames.add(internalFile[j].toString());
	                if (internalFile[j].isDirectory()) {
	                    String name = internalFile[j].getAbsolutePath();
	                    listFile(name);
	                }

	            }
	        } else {
	        	fileNames.add(listfiles[i].toString());
	        }

	    }
	}

}
