import io
import logging
import socketserver
from datetime import datetime
from http import server
from threading import Condition
from picamera2 import Picamera2
from picamera2.outputs import FileOutput
from picamera2.encoders import H264Encoder
from picamera2.encoders import MJPEGEncoder

class StreamingOutput(io.BufferedIOBase):
    def __init__(self):
        self.frame = None
        self.condition = Condition()

    def write(self, buf):
        with self.condition:
            self.frame = buf
            self.condition.notify_all()


class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True


class StreamingHandler(server.BaseHTTPRequestHandler):
    #url=ip:port
    def do_GET(self):
        #url/
        #Κάνει redirect στο endpoint url/stream.mjpg
        if self.path == '/':
            self.send_response(301)
            self.send_header('Location', '/stream.mjpg')
            self.end_headers()    
        #url/stream.mjpg
        elif self.path == '/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, pri-vate')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            #Στέλνουμε ένα frame, κάθε φορά που είναι διαθέ-σιμο 
            try:
                while True:
                    with mjpeg_output.condition:
                        mjpeg_output.condition.wait()
                        frame = mjpeg_output.frame
                    self.wfile.write(b'--FRAME\r\n')
                    self.send_header('Content-Type', 'im-age/jpeg')
                    self.send_header('Content-Length', len(frame))
                    self.end_headers()
                    self.wfile.write(frame)
                    self.wfile.write(b'\r\n')
            except Exception as e:
                logging.warning('Removed streaming client %s: %s',
                    self.client_address, str(e))
        #url/capture_image
        elif self.path == '/capture_image':
            #καταγράφουμε την ημερομηνία για να δώσουμε αργό-τερα ένα όνομα στο αρχείο μας
            current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            picam2.capture_file(current_time+'.jpeg')
            self.send_response(200)
            self.end_headers()
        #url/start_encoder
        elif self.path == '/start_encoder':
            current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            output = current_time+'.h264'
            #αρχίζει η καταγραφή του βίντεο με h264_encoder
            #όνομα αρχείου η ημερομηνία καταγραφής του βίντεο
            #και με τη μικρότερη ανάλυση που δηλώσαμε
            picam2.start_encoder(h264_encoder, output, name = "lores")
            self.send_response(200)
            self.end_headers()
        #url/stop_encoder
        elif self.path == '/stop_encoder':
            #σταματάει η καταγραφή του βίντεο
            picam2.stop_encoder(h264_encoder)
            self.send_response(200)
            self.end_headers()
        else:
            self.send_error(404)
            self.end_headers()



picam2 = Picamera2()
#προσθέτουμε κάποιες ρυθμίσεις για τη κάμερα, όπως τη κύρια ανάλυση που θα καταγράφει και μία μικρότερη
config = picam2.create_video_configuration(main={"size": (1280, 720)}, lores={"size": (640, 360)})
picam2.configure(config)
h264_encoder = H264Encoder()
mjpeg_encoder = MJPEGEncoder()
mjpeg_output = StreamingOutput()
#ξεκινάει η καταγραφή του βίντεο στη μνήμη της συσκευής
picam2.start_recording(mjpeg_encoder, FileOutput(mjpeg_output))

#ξεκινάμε το server χρησιμοποιώντας την ip του raspberry
try:
    address = ('ip', 8000)
    server = StreamingServer(address, StreamingHandler)
    server.serve_forever()
finally:
    picam2.stop_recording()
