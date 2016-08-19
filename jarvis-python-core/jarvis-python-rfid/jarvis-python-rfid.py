from flask import Flask
from flask import jsonify

import signal
import read

app = Flask(__name__)

@app.route("/api/rfid")
def rfid():
    (status,uid,tagType) = read.rfidRead(__MIFAREReader);
    return jsonify(status=status,uid=uid,tagType=tagType)

global __MIFAREReader
__MIFAREReader = read.rfidInit()

if __name__ == "__main__":
    app.run(host='0.0.0.0')
