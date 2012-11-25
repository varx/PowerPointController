using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace PPTRemoteServer
{
    class RemoteServer
    {
        NetworkStream NS;
        int port;

        TcpListener tcpListener;
        TcpClient cmdClient;
        TcpClient imgClient;

        Listener listenr;
        ThumbServer thumbServer;

        public void start(int port)
        {
            this.port = port;
            tcpListener = new TcpListener(port);
            waitConnect();
        }

        public void jumpTo(int index)
        {
            try
            {
                sendCommand(MSGVAL.JUMP);
                sendUshort((ushort)index);
            }
            catch (Exception)
            {
                onNetworkDown();
            }
        }

        public void endPlay()
        {
            try
            {
                sendCommand(MSGVAL.STOP);
            }
            catch (Exception)
            {

                onNetworkDown();
            }
        }

        public void closeFile()
        {
            try
            {
                sendCommand(MSGVAL.CLOSE);
            }
            catch (Exception)
            {
                onNetworkDown();
            }
        }


        private void waitConnect()
        {
            try
            {
                tcpListener.Start();
                cmdClient = tcpListener.AcceptTcpClient();

                NetworkStream networkStream = cmdClient.GetStream();
                networkStream.ReadByte();
                NS = networkStream;
                
                imgClient = tcpListener.AcceptTcpClient();
                networkStream = imgClient.GetStream();
                networkStream.ReadByte();
                thumbServer = new ThumbServer(networkStream);

                tcpListener.Stop();

                listenr = new Listener(NS, this);
                if (ThisAddIn.isFileOpen)
                {
                    sendFileInfo();
                }
                ThisAddIn.isClientOnline = true;
            }
            catch (Exception)
            {
                return;
            }
        }


        public void disConnect()
        {
            if ((!ThisAddIn.isClientOnline) && (tcpListener != null))
                tcpListener.Stop();
            if (NS != null)
            {
                NS.Close();
            }
            if (thumbServer != null)
            {
                thumbServer.stopServer();
            }
            if (cmdClient != null)
            {
                cmdClient.Close();
                cmdClient = null;
            }
            if (imgClient != null)
            {
                imgClient.Close();
                imgClient = null;
            }
        }


        public void sendFileInfo()
        {
            if(ThisAddIn.isFileOpen)
            {
                int totle = ThisAddIn.totle;
                List<string> titles = ThisAddIn.titles;
                List<string> notes=ThisAddIn.notes;
                try
                {
                    sendCommand(MSGVAL.FILENAME);
                    sendString(ThisAddIn.fileName);
                    sendCommand(MSGVAL.TOTLE);
                    sendUshort((ushort)totle);
                    for (int i = 0; i < totle; i++)
                    {
                        if (notes[i] != null)
                        {
                            sendCommand(MSGVAL.NOTE);
                            sendString(notes[i]);
                        }
                        sendCommand(MSGVAL.TITLE);
                        sendString(titles[i]);
                    }
                }
                catch (Exception)
                {
                    onNetworkDown();
                }
            }
        }

        public void onNetworkDown()
        {
            ThisAddIn.isClientOnline = false;
            disConnect();
            waitConnect();
        }

        private void sendCommand(byte cmd)
        {
            try
            {
                NS.WriteByte(cmd);
            }
            catch (Exception)
            {
                throw;
            }
        }


        private void sendString(string str)
        {
            try
            {
                byte[] data = System.Text.Encoding.UTF8.GetBytes(str);
                sendUshort((ushort)data.Length);
                NS.Write(data,0,data.Length);
            }
            catch (Exception)
            {
                throw;
            }
        }

        private void sendUshort(ushort val)
        {
            byte[] data = BitConverter.GetBytes(val);
            data = data.Reverse().ToArray();
            try
            {
                NS.Write(data, 0, 2);
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}
