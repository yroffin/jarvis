using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace WindowsJarvisClient
{
    public interface InterfaceConsole
    {
        void appendText(string text);
    }
    public interface InterfaceRunnable
    {
        void sendMessage(JarvisDatagram message);
        void run();
        void stop();
        Thread getThread();
        void setThread(Thread t);
    }
}
