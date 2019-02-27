let log = document.getElementById('log');

new Noty({
   type: 'success',
   layout: 'topRight',
   theme: 'nest',
   text: 'Hello, just testing! 🤖',
   timeout: '4000',
   progressBar: true,
   closeWith: ['click'],
   killer: true,
}).show();