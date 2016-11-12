/**
 * 
 */
package org.jarvis.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * simple class to generate ui.txt
 */
public class MignifyTask {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Map<String,File> resources = new TreeMap<String,File>();
		String PATH = args[0].split(" ")[0].replace("\\","/");
		
		Files.walkFileTree(FileSystems.getDefault().getPath(PATH), new FileVisitor<Path>() {
			// Called after a directory visit is complete.
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			// called before a directory visit.
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			// This method is called for each file visited. The basic attributes
			// of the files are also available.
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String filename = file.toRealPath(LinkOption.NOFOLLOW_LINKS).toString().replace("\\","/").replace(PATH, "");
				String f = file.toRealPath(LinkOption.NOFOLLOW_LINKS).toString().replace("\\","/");
				if(filename.contains("/libs/")) return FileVisitResult.CONTINUE;
				if(filename.endsWith(".html") || filename.endsWith(".css") || filename.endsWith(".markdown") || filename.contains(".min.js") || filename.contains("config.js")) {
					resources.put(filename, new File(f));
				}
				return FileVisitResult.CONTINUE;
			}

			// if the file visit fails for any reason, the visitFileFailed
			// method is called.
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});

		FileWriter fw = new FileWriter(args[0].split(" ")[2]);
		for(Entry<String, File> res : resources.entrySet()) {
			fw.write(args[0].split(" ")[1].replace("\\","/") + res.getValue().getParentFile().getPath().replace("\\","/").replace(PATH, ""));
			fw.write(";");
			fw.write(res.getValue().getName().replace("\\","/"));
			fw.write("\n");
		}
		fw.close();
	}

}
