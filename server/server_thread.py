import socket
import threading
from connection_thread import ConnectionThread

class ServerThread(threading.Thread):
    def __init__(self, host, port):
        super().__init__()
        self.host = host
        self.port = port
        self.running = True
        self.server_socket = None
    
    def run(self):
        
        # define the socket
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))

        # put the socket into listening mode
        self.sock.listen(1)
        print('Listening at', self.sock.getsockname())
        
        while self.running:
            try:
                # establish connection with client
                sc, sockname = self.sock.accept()
                print('Accepted a new connection from {} to {}'.format(sc.getpeername(), sc.getsockname()))

                # start a new connection thread
                self.server_socket = ConnectionThread(sc, sockname, self)
                self.server_socket.start()
                print('Ready to receive messages from', sc.getpeername())
            
            except socket.timeout:
                pass