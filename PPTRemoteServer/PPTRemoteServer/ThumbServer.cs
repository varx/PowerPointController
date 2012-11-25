using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using PowerPoint = Microsoft.Office.Interop.PowerPoint;
using System.IO;

namespace PPTRemoteServer
{
    class ThumbServer
    {
        NetworkStream NS;

        public ThumbServer(NetworkStream stream)
        {
            NS = stream;
            Thread thread = new Thread(new ThreadStart(this.ServerThread));
            thread.Start();
        }

        public void stopServer()
        {
            NS.Close();
        }

        private void ServerThread()
        {
            Directory.CreateDirectory("D:\\thumb\\");
            ushort slideId;
            while ((slideId = readUshort()) != 0)
            {
                sendImage(getThumb(slideId));
            }
            NS.Close();
        }

        private ushort readUshort()
        {
            byte[] data = new byte[2];
            try
            {
                if (NS.Read(data, 0, 2) == 0)
                    return 0;
            }
            catch (Exception e)
            {
                Console.WriteLine("network error");
                return 0;
            }
            return BitConverter.ToUInt16(data, 0);
        }

        private byte[] getThumb(int index)
        {
            if (index < 1 || index > ThisAddIn.totle)
                return new byte[1];
            string fileName = "D:\\thumb\\slide" + index;
            PowerPoint.Presentation pres = Globals.ThisAddIn.Application.ActivePresentation;
            if (!File.Exists(fileName))
                pres.Slides[index].Export(fileName, "PNG", 480, 320);
            FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read);
            sendInt((int)fs.Length);
            byte[] img = new byte[fs.Length];
            fs.Read(img, 0, (int)fs.Length);
            fs.Close();
            return img;
        }

        private void sendInt(int val)
        {
            byte[] data = BitConverter.GetBytes(val);
            NS.Write(data.Reverse().ToArray(), 0, 4);
        }

        private void sendImage(byte[] img)
        {
            try
            {
                NS.Write(img, 0, img.Length);
            }
            catch(Exception e)
            {

            }
        }

    }
}
