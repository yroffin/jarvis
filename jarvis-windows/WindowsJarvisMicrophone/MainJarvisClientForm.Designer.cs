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
            this.button2 = new System.Windows.Forms.Button();
            this.console = new System.Windows.Forms.RichTextBox();
            this.SuspendLayout();
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(12, 12);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 0;
            this.button2.Text = "button2";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // console
            // 
            this.console.BackColor = System.Drawing.SystemColors.InfoText;
            this.console.Font = new System.Drawing.Font("Courier New", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.console.ForeColor = System.Drawing.SystemColors.Info;
            this.console.Location = new System.Drawing.Point(119, 12);
            this.console.Name = "console";
            this.console.ReadOnly = true;
            this.console.Size = new System.Drawing.Size(1024, 200);
            this.console.TabIndex = 1;
            this.console.Text = "";
            // 
            // MainJarvisClientForm
            // 
            this.ClientSize = new System.Drawing.Size(1148, 215);
            this.Controls.Add(this.console);
            this.Controls.Add(this.button2);
            this.Name = "MainJarvisClientForm";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.RichTextBox console;
    }
}

