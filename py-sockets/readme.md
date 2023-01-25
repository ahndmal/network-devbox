The echo server sends the client message back to the client.

host = 'localhost'
port = 8001
The server runs on localhost on port 8001.

s.bind((host, port))
The bind method establishes the communication endpoint. It binds the socket to the specified address. The socket must not already be bound. (The format of address depends on the address family.)

s.listen()
The listen method enables a server to accept connections. The server can now listen for connections on a socket. The listen has a backlog parameter. It specifies the number of unaccepted connections that the system will allow before refusing new connections. The parameter is optional since Python 3.5. If not specified, a default backlog value is chosen.

con, addr = s.accept()
With accept, the server accepts a connection. It blocks and waits for an incoming connection. The socket must be bound to an address and listening for connections. The return value is a pair (con, addr) where con is a new socket object usable to send and receive data on the connection, and addr is the address bound to the socket on the other end of the connection.

Note that the accept creates a new socket for communication with a client, which is a different socket from the listening socket.
