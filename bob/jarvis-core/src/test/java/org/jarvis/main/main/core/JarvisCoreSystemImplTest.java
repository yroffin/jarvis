package org.jarvis.main.main.core;

/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import java.io.IOException;

import junit.framework.TestCase;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;

/**
 * Simple program to demonstrate the use of the FreeTTS speech synthesizer. This
 * simple program shows how to use FreeTTS without requiring the Java Speech API
 * (JSAPI).
 */
public class JarvisCoreSystemImplTest extends TestCase {

	public void testSimple() throws AimlParsingError, IOException {
		IJarvisCoreSystem jarvis = new JarvisCoreSystemImpl();
	}
}
