# trueid

1. The google extension is found in the Google Extension folder. ContentScript.js is crossbrowser-compatible.
2. index.html in the Index folder is the dummy page for our demo, at http://trueid.surge.sh/
3. The mid folder is where the middle layer (the proxy server for the application) is located.

## Problem Statement

It is tedious for the user to keep a synced authentication method between multiple computers, especially for shared computers.

## So what does this do to solve that?

We created a browser extension/script, which will and can be preinstalled in shared computers, an app which stores all your authentication methods, and SSL certified proxy server to communicate between the two. 
The extension injects a login button to the login form of the webpage. Clicking on it prompts the user to key in his phone number. His phone will receive a notification, asking for his approval to log in. The user presses OK, and he's logged in. 

The whole process probably takes around 10 seconds and the user only needs to remember his phone number.

##That's cool! How does it does that?

Well, the script itself is written in web technologies, nothing fancy about it. It prompts the server to send a push notification to the phone. The server itself is written in Node.js, nothing too fancy about that, just JSON endpoints here and there, secured by a SSL certificate so it comes with a HTTPS connection. The phone application is written in Java, for Android. It stores the password, encrypted of course, and sends it back through to the server, and back to the web extension. 

There's nothing fancy about the application technology itself. It's just made by four guys who got tired of typing passwords every damn time on computer we don't use everyda (hint hint LWN library computers)

I do totally recommend [surge](http://surge.sh/) for static hosting though. Braindead simple stuff, if you ask me.

##What's next for TrueID?

We'll probably be using it ourselves, and write a port of the native app to ioS. Maybe we could expand the business model to provide ads and support through premium account holders, but that's all pretty far from now.

##What about the hackathon? Any thoughts while you guys were coding and stuff?

Just shooting a thought off my head. Google's documentation is pretty horrible, but the stackoverflow community made it bearable, at least. Always apply the DRY rule, and use the wisdom of the ancients. Important stuff like that.

##The folders are organised pretty weird. Who did that?

I did. Good luck and have fun!