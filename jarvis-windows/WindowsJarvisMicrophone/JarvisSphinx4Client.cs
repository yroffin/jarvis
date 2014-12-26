/** 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

using System;
using System.Net;
using System.Threading;
using System.Text;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace WindowsJarvisClient
{
    // Local interface
    public interface InterfaceConsoleSphinx4 : InterfaceConsole
    {
        void fixUtterance(string uttid, string text);
    }

    // Asynchronous Client
    public class AsynchronousSphinxClient : InterfaceRunnable
    {
        // Call back
        private delegate int CallbackUtterance(string uttid, string text);

        // constructor
        public AsynchronousSphinxClient(InterfaceConsoleSphinx4 mainJarvisClientForm)
        {
            this.mainJarvisClientForm = mainJarvisClientForm;
            /**
             * look in cmd line
             */
            String[] argv = Environment.GetCommandLineArgs();
            for (int argc = 0; argc < argv.Length; argc++)
            {
                String value = argv[argc];
                String[] stringSeparators = new String[] { "=" };
                if (value.Length > 1 && value.StartsWith("-D"))
                {
                    String[] result = value.Replace("-D", "").Split(stringSeparators, StringSplitOptions.None);
                    if (result.Length > 1 && result[0].StartsWith("JARVIS_LANGAGE_DIR"))
                    {
                        this.jarvisLangageDir = result[1];
                    }
                }
            }
            /**
             * then in env
             */
            if (this.jarvisLangageDir == null)
            {
                this.jarvisLangageDir = Environment.GetEnvironmentVariable("JARVIS_LANGAGE_DIR");
                if (jarvisLangageDir == null) throw new Exception("JARVIS_LANGAGE_DIR undefined");
            }

            mainJarvisClientForm.appendText("JARVIS_LANGAGE_DIR:" + jarvisLangageDir + "\n");
        }

        // Main form element and local private member
        private InterfaceConsoleSphinx4 mainJarvisClientForm;
        private Sphinx4Interface obj;
        private bool runit = true;
        private String jarvisLangageDir;

        public void run()
        {
            mainJarvisClientForm.appendText("Interface\n");
            
            obj = new Sphinx4Interface();

            mainJarvisClientForm.appendText("Init ...");
            obj.init(jarvisLangageDir + "/model/lm/fr/wsj/F2",
                jarvisLangageDir + "/model/lm/fr/language/french3g62K.lm.dmp",
                jarvisLangageDir + "/model/lm/fr/language/frenchWords62K.dic");
            mainJarvisClientForm.appendText(" ok\n");

            mainJarvisClientForm.appendText("Models:" + jarvisLangageDir + "/model/lm/fr/wsj/F2\n");
            mainJarvisClientForm.appendText("Dump:" + jarvisLangageDir + "/model/lm/fr/language/french3g62K.lm.dmp\n");
            mainJarvisClientForm.appendText("Dictionnary:" + jarvisLangageDir + "/model/lm/fr/language/frenchWords62K.dic\n");

            obj.openMicrophone();
            for (;runit; )
            {
                mainJarvisClientForm.appendText("Ready\n");
                Sphinx4InterfaceSentence sentence = new Sphinx4InterfaceSentence();
                obj.readFromMicrophone(sentence);
                HandlerUtterance(sentence.uttid, sentence.hyp);
            }
            obj.closeMicrophone();
            obj.liberation();
        }

        private Thread thread;

        public void setThread(Thread t)
        {
            thread = t;
        }

        public Thread getThread()
        {
            return thread;
        }

        private int HandlerUtterance(string uttid, string text)
        {
            mainJarvisClientForm.fixUtterance(uttid, text);
            return 0;
        }

        public void stop()
        {
            runit = false;
            thread.Join();
        }

        public void sendMessage(JarvisDatagram message)
        {
        }
    }

    class JarvisSphinx4Client
    {

        // Start method (main entry point)
        public static InterfaceRunnable startClient(InterfaceConsoleSphinx4 mainJarvisClientForm)
        {
            // The constructor for the Thread class requires a ThreadStart 
            // delegate that represents the method to be executed on the 
            // thread.  C# simplifies the creation of this delegate.
            InterfaceRunnable worker = new AsynchronousSphinxClient(mainJarvisClientForm);
            Thread t = new Thread(new ThreadStart(worker.run));
            worker.setThread(t);

            // Start ThreadProc.  Note that on a uniprocessor, the new 
            // thread does not get any processor time until the main thread 
            // is preempted or yields.  Uncomment the Thread.Sleep that 
            // follows t.Start() to see the difference.
            t.Start();
            return worker;
        }
    }
}
