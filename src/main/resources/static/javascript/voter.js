function createMemberObject() {
    const membershipId = document.getElementById("membershipId").value;
    const name = document.getElementById("name").value;
    const age = document.getElementById("age").value;
    const region = document.getElementById("region").value;

    return {
        membershipId,
        name,
        age,
        region
    };
}

function hideElement(elementName) {
    document.getElementById(elementName).style.display = "none";
}

function showElement(elementName) {
    document.getElementById(elementName).style.display = "block";
}

// Hide element initially.
hideElement("allCandidates");
hideElement("voterIdentity");
hideElement("vote");

function populateOptions(options) {
    const select = document.getElementById("candidates");
    // Reset options
    select.innerHTML = "";

    // Sort the candidates into alphabetical order.
    let alphabeticalBirds = options.sort((bird1, bird2) => {
        let bird1Name = bird1.commonName.toLowerCase();
        let bird2Name = bird2.commonName.toLowerCase();

        if (bird1Name < bird2Name) {
            return -1;
        } else if (bird2Name < bird1Name) {
            return 1;
        }
        return 0;
    });

    // Populate the select with each candidate.
    alphabeticalBirds.forEach(bird =>
        select.add(new Option(bird.commonName, bird.commonName))
    );
}

function listCandidates() {
    showElement("allCandidates");
    showElement("voterIdentity");
    showElement("vote");
    const output = document.getElementById("allCandidates");
    const url = "http://localhost:8080/birds";
    fetch(url)
        .then(async (response) => {
            let data = await response.json();
            output.innerText = JSON.stringify(data, null, 4);
            populateOptions(data);
        })
        .catch(error => output.innerHTML = error);
}

async function validateMember(member) {
    const url = "https://pmaier.eu.pythonanywhere.com/sawb/member/" + member.membershipId;

    try {
        const response = await fetch(url);
        const jsonObject = await response.json();
        return response.status === 200 && jsonObject.member.name.toLowerCase() === member.name.toLowerCase();
    } catch (error) {
        return false;
    }
}

async function voteForCandidate() {
    const {value: candidate} = document.getElementById("candidates");
    const output = document.getElementById("voteResponse");
    const url = "http://localhost:8080/birds/vote";
    const member = createMemberObject();
    const payload = {[candidate]: member};

    if (!await validateMember(member)) {
        output.innerText = "The member provided is not valid!";
        return;
    }

    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        })
        let message = await response.text();
        if (response.status !== 202) {
            message = (message && JSON.parse(message).message) || "This member has already voted for this candidate.";
        }
        output.innerText = `Status ${response.status}\n${message}`;
    } catch (error) {
        output.innerText = `Error: ${error}`;
    }
}

async function retractVote() {
    const output = document.getElementById("voteResponse");
    const url = "http://localhost:8080/birds/vote";
    const member = createMemberObject();
    const payload = member.membershipId;

    if (!await validateMember(member)) {
        output.innerText = "The member provided is not valid!";
        return;
    }

    try {
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {"Content-Type": "text/plain"},
            body: payload
        })
        let message = await response.text();
        if (response.status !== 204) {
            message = (message && JSON.parse(message).message) || "This member has no vote to retract.";
        }
        output.innerText = `Status ${response.status}\n${message}`;
    } catch (error) {
        output.innerText = `Error: ${error}`;
    }
}

async function showVote() {
    const output = document.getElementById("voteResponse");
    const member = createMemberObject();
    const payload = member.membershipId;
    const url = "http://localhost:8080/birds/vote/" + payload;


    if (!await validateMember(member)) {
        output.innerText = "The member provided is not valid!";
        return;
    }

    try {
        const response = await fetch(url);
        let message = "No response message...";
        if (response.status !== 200) {
            message = (message && JSON.parse(await response.text()).message);
        } else {
            message = JSON.stringify(await response.json(), null, 4);
        }
        output.innerText = `Status ${response.status}\n${message}`;
    } catch (error) {
        output.innerText = `Error: ${error}`;
    }
}
