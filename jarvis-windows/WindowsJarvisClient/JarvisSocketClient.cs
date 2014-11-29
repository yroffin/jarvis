﻿using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using Newtonsoft.Json;

namespace WindowsJarvisClient
{
    // State object for receiving data from remote device.
    public class StateObject
    {
        // Client socket.
        public Socket workSocket = null;
        // Size of receive buffer.
        public const int BufferSize = 256;
        // Receive buffer.
        public byte[] buffer = new byte[BufferSize];
        // Received data string.
        public StringBuilder sb = new StringBuilder();
    }

    // Asynchronous Client
    public class AsynchronousClient
    {
        // The port number for the remote device.
        private const int port = 5000;

        // ManualResetEvent instances signal completion.
        private ManualResetEvent connectDone =
            new ManualResetEvent(false);
        private ManualResetEvent sendDone =
            new ManualResetEvent(false);
        private ManualResetEvent receiveDone =
            new ManualResetEvent(false);

        // Internal member
        private String name = "Default name";
        private Boolean isRenderer = false;
        private Boolean isSensor = false;
        private Boolean canAnswer = false;
        private Boolean identified = false;
        private JarvisDatagramSession session;

        // The response from the remote device.
        private String response = String.Empty;

        // Main form element
        private Socket client;
        private InterfaceConsole mainJarvisClientForm;

        public AsynchronousClient(InterfaceConsole mainJarvisClientForm)
        {
            this.mainJarvisClientForm = mainJarvisClientForm;
        }

        private void StartClientSocket()
        {
            // Connect to a remote device.
            try
            {
                // Establish the remote endpoint for the socket.
                // The name of the 
                // remote device is "host.contoso.com".
                IPHostEntry ipHostInfo = Dns.GetHostEntry("localhost");
                IPAddress ipAddress = ipHostInfo.AddressList[1];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

                // Create a TCP/IP socket.
                client = new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream, ProtocolType.Tcp);

                // Connect to the remote endpoint.
                client.BeginConnect(remoteEP,
                    new AsyncCallback(ConnectCallback), client);
                connectDone.WaitOne();

                // Send test data to the remote device.
                Send(client, "{}");
                sendDone.WaitOne();

                // Receive the response from the remote device.
                Receive(client);
                receiveDone.WaitOne();

                // Write the response to the console.
                writeLine("Response received : {0}", response);

                // Release the socket.
                client.Shutdown(SocketShutdown.Both);
                client.Close();

            }
            catch (Exception e)
            {
                writeLine(e.ToString());
            }
        }

        // Internal console method
        private void writeLine(string message)
        {
            writeLine("{0}", message);
        }

        // Internal console method
        private void writeLine(string format, string message)
        {
            console(String.Format(format + "\n", message));
        }

        // Internal console method
        private void console(byte[] p)
        {
            console(System.Text.Encoding.UTF8.GetString(p));
        }

        // Internal console method
        private void console(String text)
        {
            mainJarvisClientForm.appendText(text);
        }

        // Internal console method
        private bool isDebugEnabled()
        {
            return true;
        }

        // Connect callback
        private void ConnectCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.
                Socket client = (Socket)ar.AsyncState;

                // Complete the connection.
                client.EndConnect(ar);

                writeLine("Socket connected to {0}",
                    client.RemoteEndPoint.ToString());

                // Signal that the connection has been made.
                connectDone.Set();
            }
            catch (Exception e)
            {
                writeLine(e.ToString());
            }
        }

        // Receive
        private void Receive(Socket client)
        {
            try
            {
                // Create the state object.
                StateObject state = new StateObject();
                state.workSocket = client;

                // Begin receiving the data from the remote device.
                client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                    new AsyncCallback(ReceiveCallback), state);
            }
            catch (Exception e)
            {
                writeLine(e.ToString());
            }
        }

        // Receive callback
        private void ReceiveCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the state object and the client socket 
                // from the asynchronous state object.
                StateObject state = (StateObject)ar.AsyncState;
                Socket client = state.workSocket;

                // Read data from the remote device.
                int bytesRead = client.EndReceive(ar);

                if (bytesRead > 0)
                {
                    // There might be more data, so store the data received so far.
                    state.sb.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));

                    JarvisDatagram message = JsonConvert.DeserializeObject<JarvisDatagram>(System.Text.Encoding.UTF8.GetString(state.buffer));

                    // Console tracking
                    console(state.buffer);

                    if (identified == false)
                    {
                        if (message.code.StartsWith("welcome")
                                && message.session != null
                                && message.session.client != null
                                && message.session.client.id != null)
                        {
                            // fill element session session will be send with each
                            // message
                            session = new JarvisDatagramSession();
                            session.client = new JarvisDatagramClient();
                            session.client.id = message.session.client.id;

                            // fill the session with parameters
                            session.client.name = name;
                            session.client.isRenderer = isRenderer;
                            session.client.isSensor = isSensor;
                            session.client.canAnswer = canAnswer;
                        }
                        else
                        {
                            throw new Exception("First message must be a welcome message, session must be filled");
                        }
                        // resend this message (session element will be filled)
                        sendMessage(message);
                        identified = true;
                    }
                    else
                    {
                        // default behaviour
                        onNewMessage(message);
                    }

                    // Get the rest of the data.
                    client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                        new AsyncCallback(ReceiveCallback), state);
                }
                else
                {
                    // All the data has arrived; put it in response.
                    if (state.sb.Length > 1)
                    {
                        response = state.sb.ToString();
                    }
                    // Signal that all bytes have been received.
                    receiveDone.Set();
                }
            }
            catch (Exception e)
            {
                writeLine(e.ToString());
            }
        }

        private void onNewMessage(JarvisDatagram message)
        {
            if (isDebugEnabled())
            {
                writeLine("Message {}", JsonConvert.SerializeObject(message));
            }
            if (message.code.StartsWith("welcome"))
            {
                JarvisDatagram nextMessage = new JarvisDatagram();
                sendMessage(nextMessage);
            }
        }

        private void sendMessage(JarvisDatagram message)
        {
            Send(client, JsonConvert.SerializeObject(message));
            sendDone.WaitOne();
        }

        // Send
        private void Send(Socket client, String data)
        {
            // Convert the string data to byte data using ASCII encoding.
            byte[] byteData = Encoding.ASCII.GetBytes(data);

            // Begin sending the data to the remote device.
            client.BeginSend(byteData, 0, byteData.Length, 0,
                new AsyncCallback(SendCallback), client);
        }

        // Send callback
        private void SendCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.
                Socket client = (Socket)ar.AsyncState;

                // Complete sending the data to the remote device.
                int bytesSent = client.EndSend(ar);
                writeLine("Sent {0} bytes to server.", bytesSent + "");

                // Signal that all bytes have been sent.
                sendDone.Set();
            }
            catch (Exception e)
            {
                writeLine(e.ToString());
            }
        }

        // Start method (main entry point)
        public static void startClient(InterfaceConsole mainJarvisClientForm)
        {
            // The constructor for the Thread class requires a ThreadStart 
            // delegate that represents the method to be executed on the 
            // thread.  C# simplifies the creation of this delegate.
            Thread t = new Thread(new ThreadStart(new AsynchronousClient(mainJarvisClientForm).StartClientSocket));

            // Start ThreadProc.  Note that on a uniprocessor, the new 
            // thread does not get any processor time until the main thread 
            // is preempted or yields.  Uncomment the Thread.Sleep that 
            // follows t.Start() to see the difference.
            t.Start();
        }
    }
}