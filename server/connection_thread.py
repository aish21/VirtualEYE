import threading
import time
import cv2

import numpy as np
import matplotlib
matplotlib.use('Agg')

class ConnectionThread(threading.Thread):
    
    def __init__(self, sc, sockname, server):
        super().__init__()
        self.sc = sc
        self.sockname = sockname
        self.server = server
        self.running = True

    def run(self):
        prev_time = time.time();
        new_time = 0;
        while self.running:
            
            # receive number of digits in size of image byte_array.
            message = self.sc.recv(3)
            if(message):

                s = int(message.decode('utf-8')[2:])

                # receive size of byte_array
                message = self.sc.recv(s+2)
                size = int(message.decode('utf-8')[2:])

                # receive full image byte_array
                bytes_full_data = self.listen_for_image(size)

                # convert bytes_array to np array
                nparr = np.frombuffer(bytes_full_data, np.uint8)
                img_np = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
                print(img_np)

                    
                # calculating fps and putting it on image.
                new_time = time.time()
                fps = str(int(1/(new_time-prev_time+.0001)))
                prev_time = new_time
                cv2.putText(img_np, fps, (7, 70), cv2.FONT_HERSHEY_SIMPLEX, 3, (100, 255, 0), 3, cv2.LINE_AA)
                
                # create a opencv window to show image
                # cv2.imshow(str(self.sc.getpeername()), img_np)
                # if cv2.waitKey(1) & 0xFF == ord('q'):
                #     break
                # cap = cv2.VideoCapture('http://192.168.1.87:9090/')
                    
            else:
                print('{} has closed the connection'.format(self.sockname))
                self.sc.close()
                return
    
    def listen_for_image(self, size):
        bytes_data = b''
        while(size>0):
            part = self.sc.recv(min(size, 4096))
            bytes_data += part
            size -= len(part)
        return bytes_data
    
    def close(self):
        self.running = False