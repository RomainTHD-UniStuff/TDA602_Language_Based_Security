# THIS PROJECT IS INTENTIONALLY INSECURE AND HIDEOUS TO READ

Things to talk about:

- JS injection using eval or injection sink
- NPM chaining vulnerability
-- malicious packages
-- unmaintained / out of date code
-- account takeover
- single-threaded, so exposed to DoS (see ReDos ?)

Only things related to NodeJS !
Things not explored:
- SQL injection
- XSS
- HTTP / HTTPS misconfig
- cookie theft
- ORM access
- CSRF / SSRF

On average, 79 (!) third-party packages used from 39 maintainers

Some packages are used by hundred of thousand packages

More than 40% of all packages use packages with public vulnerabilities
Take Angular / React as example? (or lodash, etc)

Problems with npm:
- locked dependencies (package.json)
- heavy reuse
- micropackages
- no privilege management
- implicit trust
