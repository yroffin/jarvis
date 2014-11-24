package org.jarvis.main.core.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class JarvisConsole {

	/**
	 * @param args
	 * @throws IOException
	 * @throws AimlParsingError
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws AimlParsingError,
			IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class
				.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		System.out.println("Default encoding: "
				+ Charset.defaultCharset().displayName());
		IJarvisCoreSystem jarvis = new JarvisCoreSystemImpl();
		System.out.println("Initializing ...");
		jarvis.initialize(args[0]);
		System.out.println("Ready ...");
		String line = null;
		System.out.print("You: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			line = br.readLine();
			for (; line != null;) {
				if (line.length() > 0) {
					List<IAimlHistory> result = jarvis.ask(line);
					for (IAimlHistory value : result) {
						System.out.println(value.getAnswer());
					}
				}
				line = br.readLine();
				System.out.print("Me: ");
			}
		} catch (IOException e) {
		}
		br.close();
	}

	public static void direct(String[] args, String lines[])
			throws AimlParsingError, IOException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class
				.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		System.out.println("Default encoding: "
				+ Charset.defaultCharset().displayName());
		IJarvisCoreSystem jarvis = new JarvisCoreSystemImpl();
		System.out.println("Initializing ...");
		jarvis.initialize(args[0]);
		System.out.println("Ready ...");
		System.out.print("You: ");

		for(String line : lines) {
			if (line.length() > 0) {
				List<IAimlHistory> result = jarvis.ask(line);
				for (IAimlHistory value : result) {
					System.out.println(value.getAnswer());
				}
			}
		}
	}
}
