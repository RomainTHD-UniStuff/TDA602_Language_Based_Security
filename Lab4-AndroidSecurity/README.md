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

In the MACLocation app, the `DatabaseActivity.onCreate` method will, as
its name suggests, create the app. It will look at the caller intent and
act differently depending on its action. Here, if the field
`"ITEM_ACTION"` is set to `"GET_ITEMS_ACTION"`, it will retrieve all
database content and send it back to the caller.

The problem is that if a third-party app opens this activity with the
right action, the activity won't check which app the intent is coming
from and will exfiltrate all its database to the external app through
the result intent.

The intent code is the following one:

```java
// Activity intent
Intent i = new Intent(Intent.ACTION_MAIN);
// Explicitely set the component that will receive the intent
i.setComponent(new ComponentName(
    "lbs.lab.maclocation", // Target package
    "lbs.lab.maclocation.DatabaseActivity" // Target activity
));
// MIME data type, equal to `DatabaseActivity.class.getCanonicalName()`
//  Used to trick the DatabaseActivity condition
i.setType("lbs.lab.maclocation.DatabaseActivity");
// Action, here fetch all data, but could also be SET_ITEMS_ACTION to
//  override the database content
i.putExtra(ITEM_ACTION, GET_ITEMS_ACTION);
```

There are currently a few checks in `DatabaseActivity`:
- The MIME data type should be set to
`"lbs.lab.maclocation.DatabaseActivity"`, which is easy to set
- The `"ITEM_ACTION"` field should be set to `"GET_ITEMS_ACTION"`, again
we could guess it using the source code or a decompiler
- The caller activity should be named `MainActivity`, easy to spoof

This last check is the one which should be changed. Instead of checking
the value of `getShortClassName()` to compare the activity names, it
should use `getClassName()`, that will differentiate
`lbs.lab.maclocation.MainActivity` and `lbs.lab.macintent.MainActivity`.

Finally, once the data retrieved, we need to deserialize it by creating
our own implementation of `lbs.lab.maclocation.Item`, and then
exfiltrate it like we did previously in the first part.

# 3 - Protections and mitigations

There could be several ways to protect against these threats, on several
levels.

## 3.1 - Against permission escape: part 1

First, the permissions could be implemented per intent, not per app. If
an app A calls an app B, and B has the internet permission but not A, A
is still able to access internet through B. However, a defense would be
for the intent from A to B to require some kind of permission, which
would force A to declare the internet permission. This would still be
pretty challenging though, as we need to be sure which permissions are
used, otherwise if B suddently adds the camera permission it would break
A even though it doesn't use it.

## 3.2 - Against data leak: part 2

A way to avoid data leak would be to somehow track the data from B to A.
If the data of B is leaked back to A without a kind of approval, then
the data would be made unusable by the system. This is still a rough
idea though, as this would probably lead to many challenge to face.

Maybe we could also rank the data importance, like the information flow
security lectures we had. A high (important) app would only be able to
share its data to other high apps, and low apps would not be able to
fetch this high data. Or, to continue with data tracking, low apps could
be allowed as long as there isn't an information flow from high data to
low data, ie the data use would be severely restricted.

Intents should also use a kind of signature, to avoid spoofing its
origin. This is already something that can - and should - be done, but
the Android kernel should go even further. What we saw earlier is that
to deserialize the data fetched we needed to create our own
implementation of the data object. This shouldn't be allowed, and a
serialized object should only be deserializable by the implementation
in its own app, not by any third-party app, even if the implementation
is the same.
