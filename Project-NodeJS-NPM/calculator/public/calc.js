function send() {
    let request = new XMLHttpRequest();
    request.onreadystatechange = () => {
        if (request.readyState === 4 && request.status === 200) {
            document.querySelector(
                "#result",
            ).innerText = JSON.parse(
                request.responseText,
            ).result || "NaN";
        }
    };
    request.open("POST", "http://localhost:3001/");
    request.setRequestHeader(
        "Content-Type",
        "application/json;charset=UTF-8",
    );

    let left = document.querySelector("#left").value.trim();
    if (parseFloat(left) == left) {
        left = parseFloat(left);
    }

    let right = document.querySelector("#right").value.trim();
    if (parseFloat(right) == right) {
        right = parseFloat(right);
    }

    request.send(JSON.stringify({
        "left": left,
        "op": document.querySelector("#op").value,
        "right": right,
    }));
}
