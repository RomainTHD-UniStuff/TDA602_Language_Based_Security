# THIS PROJECT IS INTENTIONALLY INSECURE AND HIDEOUS TO READ

# What this project is and isn't

With this project, we'll see how the NodeJS / NPM ecosystem is crippled
with vulnerabilities and single point of failure.

However, since our main focus is on NodeJS specifically, we will not see
common web vulnerabilities that can happen to any plateform or language,
like SQL / NoSQL injection, XSS, cookie theft, CSRF, SSRF, etc. NodeJS
is not really better nor worse than PHP or Django here.

# Problems with NodeJS

First, on a language perspective, JavaScript is unsafe by design and
allows things like string evaluation, injection sink, object mutability,
etc.

```js
let array = [1, 2, 3, 4, 5]
// Injection sink, here if `key` is `"length"` this might break some
//  things later on
array[key] = 0;

array.__proto__.toString = () => "<script>alert(1)</script>"
// Everything is mutable by default, so it is even possible to change
//  the class `Array` to change all future arrays objects
["a", "b", "c"].toString() // This will print the script tag
```

Moreover, NodeJS is single-threaded, which means that any DoS attack can
completely take down a server. A common threat is ReDoS, where a complex
regex is provided and evaluated, which will crash the system. Another
attack vector could be huge database queries which will slow the whole
system, as opposed to languages like Java or Python for example where
"true" parallelism is achievable.

Additionnally, NodeJS and JavaScript don't really have any permission
system, which means that any script has the same access as the main
script.

Finally, NodeJS relies on a global mutable state modifiable from every
script, with the use of global variables like `process`, `window`,
`global`, etc.

# Problems with NPM

The way NPM works is that every dependency it needs is downloaded per
project, and each sub-dependency is downloaded per parent dependency,
which means that a same package can be downloaded several times, with
the same version or different ones.

```
package
    ├─ dependency A
    │   ├─ sub-dependency 1
    │   └─ sub-dependency 2
    │
    └─ dependency B
        ├─ sub-dependency 2
        └─ sub-dependency 3
```

Dependencies version are also frozen and stored in a separate file

```json
{
    "node_modules/accepts": {
        "version": "1.3.8"
    }, "node_modules/array-flatten": {
        "version": "1.1.1"
    }, "node_modules/body-parser": {
        "version": "1.20.0"
    }
}
```

Because of this, if one dependency is out-of-date, unmaintained or
vulnerable, this huge chain of implicit trust gets broken, and since the
versions are frozen these packages cannot be easily updated.

Worse, on average, a project needs 79 third-party packages from 39
different maintainers, which create huge opportunities for attackers.
NPM works using micropackages, packages that are really small and do one
single thing, but these package gets downloaded millions of time each
week by hundred of thousands packages, so in a way it creates hundred
single points of failure.

The sad thing is that everyone is aware of this, but simply accept it.
More than 40% of all packages use packages with public vulnerabilities,
a gigantic proportion.

And because of the way NodeJS works, with locked dependencies that needs
to manually be kept updated and no privilege management whatsoever,
another single point of failure can be NPM account takeover or namespace
collision, with potentially catastrophic results.

Some attacks have already been made using the techniques above. For
example, the main developer of `node-ipc` implemented in one of its
update a chunk of code that would replace all files of any Russian or
Belarussian IP-based device with heart emojis. This package currently
still have hundred of thousands downloads each weeks with hundred of
packages depending on it, so we can easily imagine the damages it has
done. Another example of protestware, `fakerjs`, with millions of
downloads and thousand of dependant packages, where the main developer
simply removed all source files, breaking a significant part of its
dependant packages.
