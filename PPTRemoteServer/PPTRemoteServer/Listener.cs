using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace PPTRemoteServer
{
    class Listener
    {
        NetworkStream NS;
        Controller controller;
        RemoteServer server;
        public Listener(NetworkStream networkStream,RemoteServer server)
        {
            NS = networkStream;
            this.server = server;
            controller = new Controller();
            new Thread(new ThreadStart(this.startListener)).Start();
        }

        private void startListener()
        {
            while(true)
            {
                byte cmd = readCommand();
                if (cmd == MSGVAL.ERROR)
                {
                    break;
                }
                else
                {
                    parsesCommand(cmd);
                }
            }
            server.onNetworkDown();
        }

        private void parsesCommand(byte cmd)
        {
            switch (cmd)
            {
                case MSGVAL.START:
                    controller.startPlay();
                    break;
                case MSGVAL.STOP:
                    controller.endPlay();
                    break;
                case MSGVAL.JUMP:
                    ushort index = readUshort();
                    if (index == 0)
                        return;
                    controller.jumpTo(index);
                    break;
                case MSGVAL.BLACK_SCREEN:
                    controller.blacksCreen();
                    break;
                case MSGVAL.WHITE_SCREEN:
                    controller.whiteCreen();
                    break;
                default:
                    break;
            }
        }

        private ushort readUshort()
        {
            byte[] data = new byte[2];
            try
            {
                NS.Read(data, 0, 2);
            }
            catch (Exception e)
            {
                return 0;
            }
            return BitConverter.ToUInt16(data, 0);
        }

       
        private byte readCommand()
        {
            byte[] data = new byte[1];
            int result;
            try
            {
                result = NS.Read(data, 0, 1);
            }
            catch (Exception)
            {
                result = 0;
            }
            if (result == 0)
            {
                return MSGVAL.ERROR;
            }
            return data[0];
        }

    }
}
