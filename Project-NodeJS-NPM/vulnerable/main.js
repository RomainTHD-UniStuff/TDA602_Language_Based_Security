const path = require("path");

require("dotenv").config({
    path: path.join(__dirname, ".env"),
});

function getFunction_1(operator) {
    try {
        return require(path.join(__dirname, "functions", process.env[operator]));
    } catch (e) {
        return (a, b) => NaN;
    }
}

function getFunction_2(operator) {
    try {
        return require(path.join(__dirname, "functions", operator + ".js"));
    } catch (e) {
        return (a, b) => NaN;
    }
}

module.exports = {
    getFunction: getFunction_2,
};
