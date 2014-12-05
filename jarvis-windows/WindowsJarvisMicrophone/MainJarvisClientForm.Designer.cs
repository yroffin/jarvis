namespace WindowsJarvisClient
{
    partial class MainJarvisClientForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }



        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.console = new System.Windows.Forms.RichTextBox();
            this.textUttid = new System.Windows.Forms.TextBox();
            this.textHypthesis = new System.Windows.Forms.TextBox();
            this.send = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // console
            // 
            this.console.BackColor = System.Drawing.SystemColors.InfoText;
            this.console.Font = new System.Drawing.Font("Yu Gothic", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.console.ForeColor = System.Drawing.SystemColors.Info;
            this.console.Location = new System.Drawing.Point(13, 64);
            this.console.Name = "console";
            this.console.ReadOnly = true;
            this.console.Size = new System.Drawing.Size(871, 326);
            this.console.TabIndex = 1;
            this.console.Text = "";
            // 
            // textUttid
            // 
            this.textUttid.Font = new System.Drawing.Font("Mistral", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.textUttid.Location = new System.Drawing.Point(13, 12);
            this.textUttid.Name = "textUttid";
            this.textUttid.Size = new System.Drawing.Size(275, 46);
            this.textUttid.TabIndex = 3;
            // 
            // textHypthesis
            // 
            this.textHypthesis.Font = new System.Drawing.Font("Mistral", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.textHypthesis.Location = new System.Drawing.Point(294, 12);
            this.textHypthesis.Name = "textHypthesis";
            this.textHypthesis.Size = new System.Drawing.Size(505, 46);
            this.textHypthesis.TabIndex = 4;
            // 
            // send
            // 
            this.send.Location = new System.Drawing.Point(809, 12);
            this.send.Name = "send";
            this.send.Size = new System.Drawing.Size(75, 46);
            this.send.TabIndex = 5;
            this.send.Text = "Send";
            this.send.UseVisualStyleBackColor = true;
            this.send.Click += new System.EventHandler(this.button1_Click);
            // 
            // MainJarvisClientForm
            // 
            this.ClientSize = new System.Drawing.Size(897, 404);
            this.Controls.Add(this.send);
            this.Controls.Add(this.textHypthesis);
            this.Controls.Add(this.textUttid);
            this.Controls.Add(this.console);
            this.Name = "MainJarvisClientForm";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainJarvisClientForm_FormClosing);
            this.Load += new System.EventHandler(this.MainJarvisClientForm_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.RichTextBox console;
        private System.Windows.Forms.TextBox textUttid;
        private System.Windows.Forms.TextBox textHypthesis;
        private System.Windows.Forms.Button send;
    }
}

