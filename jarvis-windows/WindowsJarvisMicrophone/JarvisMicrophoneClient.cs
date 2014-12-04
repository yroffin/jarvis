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

using System.Threading;

namespace WindowsJarvisClient
{
    class AsynchronousMicrophoneClient : AsynchronousClient
    {
        public AsynchronousMicrophoneClient(InterfaceConsole mainJarvisClientForm) : base(mainJarvisClientForm)
        {
        }
    }

    class JarvisMicrophoneClient
    {
        // Start method (main entry point)
        public static InterfaceRunnable startClient(InterfaceConsole mainJarvisClientForm)
        {
            // The constructor for the Thread class requires a ThreadStart 
            // delegate that represents the method to be executed on the 
            // thread.  C# simplifies the creation of this delegate.
            InterfaceRunnable worker = new AsynchronousMicrophoneClient(mainJarvisClientForm);
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
