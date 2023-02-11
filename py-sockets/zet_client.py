import socket

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
    host = "localhost"
    port = 8888 #8001

    sock.connect((host, port))
    sock.sendall(b'Hello from Python.')

    print(str(sock.recv(4096), 'utf-8'))

