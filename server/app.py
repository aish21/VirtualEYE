from flask import Flask, render_template
from flask_socketio import SocketIO, emit, send

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*")

@app.route('/')
def index():
    return render_template('index.html')

@socketio.on('connect')
def test_connect():
    print('connection established')
    send("hi")

if __name__ == '__main__':
    socketio.run(app, ssl_context = "adhoc")