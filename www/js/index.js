var captureCfg = null;
var PCM = [];
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', onDeviceReady, false);
    },
};

app.initialize();

function onDeviceReady() {
console.log("Device Ready");

// Listen to audioinput events. 
window.addEventListener("audioinput", onAudioInput, false);

captureCfg = {
    sampleRate: 44100,
    bufferSize: 8192, 
    channels: 1,
    format: 'PCM_16BIT'
	};	
}

function record(){
console.log("record clicked");
	audioinput.start(captureCfg);
console.log("recording");
//CRASHES AFTER HERE
}

function stopRecord(){
	audioinput.stop();
}

function onAudioInput(evt) {
    
    // 'evt.data' is an integer array containing normalized audio data. 
	
	//CRASHES BEFORE HERE
    console.log("onAudioInput!");
    PCM = evt.data;
    // ... do something with the evt.data array ... 
}
 



