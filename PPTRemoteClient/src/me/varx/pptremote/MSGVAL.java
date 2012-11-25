package me.varx.pptremote;

public class MSGVAL {
	public static final byte START	=	0x01;
	public static final byte STOP	=	0x02;
	public static final byte NEXT	=	0x03; 
	public static final byte PRE	=	0x04;
	public static final byte FILENAME=	0x05;
	public static final byte TOTLE = 0x06;
    public static final byte JUMP = 0x07;
    public static final byte TITLE = 0x08;
    public static final byte NOTE = 0x18;
    
    public static final byte CLOSE=0x09;
    public static final byte BYE  =(byte) 0xFF;
    
    public static final byte BLACK_SCREEN=0x20;
    public static final byte WHITE_SCREEN=0x21;
    
    public static final byte SOCKET_IDCMD=0x71;
    public static final byte SOCKET_IDIMG=0x72;
    
    public static final byte CONNECT  = 0x11;
    public static final byte DISCONNECT=0x12;
    
    /**
     * Handleœ˚œ¢
     */
    public static final int CONNECT_OK=100;
    public static final int CONNECTF_FAIL=101;
    public static final int NEWFILE=102;
    public static final int CLOSEFILE=103;
    
    
    public static final int GOT_THUMB=777;
    
    public static final int ERROR_NET_UNKONW_HOST=100;
    public static final int ERROR_NET_IO=101;
}
