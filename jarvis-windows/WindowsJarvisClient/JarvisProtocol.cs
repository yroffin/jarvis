using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace WindowsJarvisClient
{
    public class JarvisProtocol
    {
    }

    public class JarvisDatagram
    {
	    public String code;

	    public JarvisDatagramEvent welcome;

	    public JarvisDatagramEvent bye;

	    public JarvisDatagramEvent ack;

	    public JarvisDatagramEvent request;

	    public JarvisDatagramEvent evt;

        // Internal use
       public JarvisDatagramSession session;
    }
}
