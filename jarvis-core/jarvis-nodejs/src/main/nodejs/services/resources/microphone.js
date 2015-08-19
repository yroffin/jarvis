var isMacOrWin = require('os').type() == 'Darwin' || require('os').type().indexOf('Windows') > -1;
var spawn = require('child_process').spawn
var PassThrough = require('stream').PassThrough;

var ps = null;

var audio = new PassThrough;
var info = new PassThrough;

var start = function(options) {
    options = options || {};

    if(ps == null) {
        ps = isMacOrWin
            ? spawn('"C:/Program Files (x86)/sox-14-4-2/sox.exe', ['-d', '-t', 'dat', '-p'])
            : spawn('arecord', ['-D', 'plughw:1,0', '-f', 'dat']);

        ps.stdout.pipe(audio);
        ps.stderr.pipe(info);
    }
};

// "C:\Program Files (x86)\sox-14-4-2\sox.exe" -t waveaudio 0 -p > c:/temp/toto.flac

var stop = function() {
    if(ps) {
        ps.kill();
        ps = null;
    }
};

exports.audioStream = audio;
exports.infoStream = info;
exports.startCapture = start;
exports.stopCapture = stop;