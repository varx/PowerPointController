using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;
using PowerPoint = Microsoft.Office.Interop.PowerPoint;
using Office = Microsoft.Office.Core;
using System.Threading;
using Microsoft.Office.Interop.PowerPoint;
using System.IO;

namespace PPTRemoteServer
{
    public partial class ThisAddIn
    {

        private const int portNum = 12306;
        private RemoteServer server;

        public static string fileName;
        public static int totle = 1;
        public static List<string> titles;
        public static List<string> notes;

        public static bool isFileOpen = false;
        public static bool isClientOnline = false;
        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            Application.PresentationClose += Application_PresentationClose;
            Application.AfterPresentationOpen += Application_AfterPresentationOpen;
            Application.SlideShowEnd += Application_SlideShowEnd;
            Application.SlideShowNextSlide += Application_SlideShowNextSlide;

            startServer();
        }

        //跳转,包括上一页下一页
        void Application_SlideShowNextSlide(SlideShowWindow Wn)
        {
            if (!Controller.isPlaying)
            {
                Controller.isPlaying = true;
                Controller.show = Wn;
            }
            if (isClientOnline)
                server.jumpTo(Wn.View.Slide.SlideIndex);
        }


        //结束时
        void Application_SlideShowEnd(Presentation Pres)
        {
            Controller.isPlaying = false;

            if (isClientOnline)
                server.endPlay();
            //System.Windows.Forms.MessageBox.Show("SlideShowEnd");
        }

        //打开文件
        void Application_AfterPresentationOpen(PowerPoint.Presentation Pres)
        {
            titles = new List<string>();
            notes = new List<string>();
            fileName = Pres.Name;
            totle = Pres.Slides.Count;
            foreach (PowerPoint.Slide slide in Pres.Slides)
            {
                titles.Add(getTitle(slide));
                notes.Add(getNote(slide));
            }
            isFileOpen = true;
            if (isClientOnline)
                server.sendFileInfo();
        }

        private string getNote(Slide slide)
        {
            try
            {
                for (int i = slide.NotesPage.Shapes.Count; i > 0; i--)
                {
                    Shape shape = slide.NotesPage.Shapes[i];
                    if ((shape.Type == Office.MsoShapeType.msoPlaceholder) && (shape.PlaceholderFormat.Type == PpPlaceholderType.ppPlaceholderBody))
                    {
                        return shape.TextFrame.TextRange.Text;
                    }
                }
            }
            catch (Exception)
            {
            }
            return null;
        }


        private string getTitle(Slide slide)
        {
            try
            {
                for (int i = 0; i < slide.Shapes.Count; i++)
                {
                    Shape shape = slide.Shapes[i + 1];
                    try
                    {
                        if (shape.TextFrame.TextRange.Text.Length > 0)
                        {
                            return shape.TextFrame.TextRange.Text;
                        }
                    }
                    catch (Exception)
                    {
                    }
                }
                return "";
            }
            catch (Exception)
            {
                return "";
            }
        }


        //关闭文件
        void Application_PresentationClose(PowerPoint.Presentation Pres)
        {
            //System.Windows.Forms.MessageBox.Show("PresentationClose");

            isFileOpen = false;
            titles = null;
            totle = 0;
            if (isClientOnline)
                server.closeFile();

        }



        public void startServer()
        {
            server = new RemoteServer();
            Thread serverThread = new Thread(new ThreadStart(this.serverThread));
            serverThread.Name = "serverThread";
            serverThread.Start();
        }

        /**
         * 
         * * * * * 入口* * * * * * 
         * 
         * */
        private void serverThread()
        {
            server.start(portNum);
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {
            if (server != null)
                server.disConnect(); 
        }

        #region VSTO generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }
        
        #endregion
    }
}
