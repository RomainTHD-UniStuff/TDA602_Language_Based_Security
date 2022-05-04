const express = require("express");
const app = express();
const port = 3001;

app.use((req, res, next) => {
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,PATCH,DELETE");
    next();
});

app.get("/", (req, res) => {
    console.log(req.query);
    res.send(req.query);
});

app.post("/", (req, res) => {
    console.log(req.body);
    res.send(req.body);
});

app.listen(port, () => {
    console.info(`Server listening on port ${port}`);
});
