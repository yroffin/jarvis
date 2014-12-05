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
using System.Threading;
using System.Windows.Forms;

namespace WindowsJarvisClient
{
    public partial class MainJarvisClientForm : Form, InterfaceConsoleSphinx4 
    {
        private InterfaceRunnable socketClient = null;
        private InterfaceRunnable sphinxClient = null;

        public MainJarvisClientForm()
        {
            InitializeComponent();
        }

        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        public delegate void appendTextCallback(string text);

        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        public delegate void fixUtteranceCallback(string uttid, string text);

        // This method demonstrates a pattern for making thread-safe
        // calls on a Windows Forms control. 
        //
        // If the calling thread is different from the thread that
        // created the TextBox control, this method creates a
        // SetTextCallback and calls itself asynchronously using the
        // Invoke method.
        //
        // If the calling thread is the same as the thread that created
        // the RichText control, the Text property is set directly.

        public void appendText(string text)
        {
            // InvokeRequired required compares the thread ID of the
            // calling thread to the thread ID of the creating thread.
            // If these threads are different, it returns true.
            if (this.console.InvokeRequired)
            {
                appendTextCallback d = new appendTextCallback(appendText);
                this.Invoke(d, new object[] { text });
            }
            else
            {
                this.console.AppendText(text);
            }
        }

        public void fixUtterance(string uttid, string hypText)
        {
            // InvokeRequired required compares the thread ID of the
            // calling thread to the thread ID of the creating thread.
            // If these threads are different, it returns true.
            if (this.textUttid.InvokeRequired)
            {
                fixUtteranceCallback d = new fixUtteranceCallback(fixUtterance);
                this.Invoke(d, new object[] { uttid, hypText });
            }
            else
            {
                this.textUttid.Text = uttid;
                this.textHypthesis.Text = hypText;
                handleText(hypText);
            }
        }

        private void handleText(string hypText)
        {
            // Send only when not null
            if (hypText != "")
            {
                JarvisDatagram nextMessage = new JarvisDatagram();
                nextMessage.code = "evt";
                nextMessage.evt = new JarvisDatagramEvent();
                nextMessage.evt.data = this.textUttid.Text;
                nextMessage.evt.script = "{\"plugin\":\"aiml\", \"args\":{\"sentence\":\"" + hypText + "\"}}";
                socketClient.sendMessage(nextMessage);
            }
        }

        private void MainJarvisClientForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (socketClient != null)
            {
                socketClient.stop();
            }
            if (sphinxClient != null)
            {
                sphinxClient.stop();
            }
            e.Cancel = false;
        }

        private void MainJarvisClientForm_Load(object sender, EventArgs e)
        {
            if (socketClient == null)
            {
                socketClient = JarvisMicrophoneClient.startClient(this);
            }
            if (sphinxClient == null)
            {
                sphinxClient = JarvisSphinx4Client.startClient(this);
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            handleText(this.textHypthesis.Text);
        }
    }
}
