using System.Net;
using System.Net.Sockets;
using System.Net.WebSockets;
using System.Text;

// var port = 7073;
// string server = "localhost:7073";
string server = "djxmmx.net";
int port = 17;

byte[] data = Array.Empty<byte>();

IPHostEntry hostEntry = Dns.GetHostEntry(server);

var ipe = new IPEndPoint(hostEntry.AddressList[0], port);
using var socket = new Socket(
    AddressFamily.InterNetworkV6,
    SocketType.Dgram,
    ProtocolType.Udp);

socket.ReceiveTimeout = 5000;
socket.SendTimeout = 5000;

socket.SendTo(data, ipe);

byte[] data2 = new byte[1024];
EndPoint remote = (EndPoint)ipe;

var n = socket.ReceiveFrom(data2, ref remote);

Console.WriteLine($"Message size: {n}");
Console.WriteLine($"Messaged received from: {remote}");
Console.WriteLine(Encoding.ASCII.GetString(data2, 0, n));