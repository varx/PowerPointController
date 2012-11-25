using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PPTRemoteServer
{
    class MSGVAL
    {
        public const byte START = 0x01;
        public const byte STOP = 0x02;
        public const byte NEXT = 0x03;
        public const byte PRE = 0x04;
        public const byte FILENAME = 0x05;
        public const byte TOTLE = 0x06;
        public const byte JUMP = 0x07;
        public const byte TITLE = 0x08;
        public const byte NOTE = 0x18;
        public const byte CLOSE = 0x09;

        public const byte BLACK_SCREEN=0x20;
        public const byte WHITE_SCREEN = 0x21;

        public const byte ERROR = 0xFE;
        public const byte BYE = 0xFF;

        public const byte SOCKET_IDCMD=0x71;
        public const byte SOCKET_IDIMG = 0x72;
    }
}
