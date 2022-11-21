async function changePollStatus(status) {
    let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    let header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const output = document.getElementById("pollStatus")
    const url = "http://localhost:8080/poll";

    try {
        const response = await fetch(url,{
            method:"POST",
            headers: {
                [header]: token,
                "charset": "UTF-8",
                "Content-Type": "application/json"
            },
            body: status
        });
        if (response.ok) {
            output.innerText = `Poll Status: ${status ? "OPEN" : "CLOSED"}`;
        } else {
            let data = await response.text()
            output.innerText = `Poll Status: CLOSED\n${data}`;
        }
    } catch (error) {
        output.innerText = error.message;
    }

}

async function openPoll() {
    await changePollStatus(true);
}

async function closePoll() {
    await changePollStatus(false);
}

async function tallyVotes() {
    let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    let header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const output = document.getElementById("talliedVotes");
    const url = "http://localhost:8080/birds-enriched";

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                [header]: token,
                "charset": "UTF-8"
            }
        });
        let data;
        if (response.ok) {
            data = await response.json();
            const message = JSON.stringify(data, null, 4);
            output.innerText = `Status ${response.status}\n${message}`;
        } else {
            data = JSON.parse(await response.text()).message;
            output.innerText = `Status ${response.status}\n${data}`;
        }
    } catch (error) {
        output.innerText = error.message;
    }
}