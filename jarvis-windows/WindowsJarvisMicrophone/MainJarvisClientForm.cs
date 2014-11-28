using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

using WindowsJarvisClient;

namespace WindowsJarvisClient
{
    public partial class MainJarvisClientForm : Form, InterfaceConsole 
    {
        public MainJarvisClientForm()
        {
            InitializeComponent();
        }

        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        public delegate void appendTextCallback(string text);

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

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
        }

        private void button2_Click(object sender, EventArgs e)
        {
            AsynchronousClient.startClient(this);
        }
    }
}
