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
            this.textHyp = new System.Windows.Forms.TextBox();
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
            // textHyp
            // 
            this.textHyp.Font = new System.Drawing.Font("Mistral", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.textHyp.Location = new System.Drawing.Point(13, 12);
            this.textHyp.Name = "textHyp";
            this.textHyp.Size = new System.Drawing.Size(870, 46);
            this.textHyp.TabIndex = 3;
            // 
            // MainJarvisClientForm
            // 
            this.ClientSize = new System.Drawing.Size(897, 404);
            this.Controls.Add(this.textHyp);
            this.Controls.Add(this.console);
            this.Name = "MainJarvisClientForm";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainJarvisClientForm_FormClosing);
            this.Load += new System.EventHandler(this.MainJarvisClientForm_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.RichTextBox console;
        private System.Windows.Forms.TextBox textHyp;
    }
}

