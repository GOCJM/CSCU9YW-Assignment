async function tallyVotes() {
    const output = document.getElementById("talliedVotes");
    const url = "http://localhost:8080/birds-enriched";

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization' : true
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