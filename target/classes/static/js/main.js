// dom elements
const searchForm = document.getElementById('search-form');
const flightResults = document.getElementById('flight-results');
const flightsList = document.getElementById('flights-list');
const seatSelection = document.getElementById('seat-selection');
const seatMap = document.getElementById('seat-map');
const priceRange = document.getElementById('price-range');
const priceDisplay = document.getElementById('price-display');

// api url testimiseks
const API_BASE_URL = 'http://localhost:8080';

// seat prefereneces
const windowSeatPref = document.getElementById('window-seat');
const extraLegroomPref = document.getElementById('extra-legroom');
const nearExitPref = document.getElementById('near-exit');

// updating price display
priceRange.addEventListener('input', (e) => {
    priceDisplay.textContent = `€${e.target.value}`;
});

// flight search handler
searchForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = new FormData(searchForm);
    const searchParams = {
        destination: formData.get('destination'),
        date: formData.get('date'),
        maxPrice: formData.get('price-range')
    };

    try {
        // show loading if wooden pc
        flightsList.innerHTML = '<p class="loading">Searching for flights...</p>';
        flightResults.classList.remove('hidden');

        console.log('Sending search request with params:', searchParams);

        const response = await fetch(`${API_BASE_URL}/api/flights/search`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(searchParams)
        });

        console.log('Response status:', response.status);
        
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Server not found. Please ensure the server is running.');
            }
            const data = await response.json();
            throw new Error(data.message || `Server returned ${response.status}: ${data.error || 'Unknown error'}`);
        }

        const data = await response.json();
        console.log('Response data:', data);
        displayFlights(data);
    } catch (error) {
        console.error('Search error details:', error);
        flightsList.innerHTML = `
            <div class="error">
                <p>Failed to search flights:</p>
                <p>${error.message}</p>
                <p>Please ensure the server is running at ${API_BASE_URL} and try again.</p>
                <p>Technical details: ${error.toString()}</p>
            </div>`;
    }
});

// show flight search resulkts
function displayFlights(flights) {
    flightsList.innerHTML = '';
    if (!Array.isArray(flights) || flights.length === 0) {
        flightsList.innerHTML = '<p>No flights found matching your criteria.</p>';
        return;
    }

    flights.forEach(flight => {
        const flightCard = document.createElement('div');
        flightCard.className = 'flight-card';
        flightCard.innerHTML = `
            <h3>${flight.destination}</h3>
            <p>Date: ${new Date(flight.date).toLocaleDateString()}</p>
            <p>Time: ${flight.departureTime}</p>
            <p>Price: €${flight.price}</p>
            <p>Available Seats: ${flight.availableSeats}/${flight.totalSeats}</p>
            <button onclick="selectFlight(${flight.id})">Select Flight</button>
        `;
        flightsList.appendChild(flightCard);
    });
}

// upon selecting flight show seat map
async function selectFlight(flightId) {
    try {
        // wooden pc loading screen
        seatMap.innerHTML = '<p class="loading">Loading seat map...</p>';
        seatSelection.classList.remove('hidden');
        seatSelection.scrollIntoView({ behavior: 'smooth' });

        const response = await fetch(`${API_BASE_URL}/api/flights/${flightId}/seats`);
        
        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.message || `Server returned ${response.status}: ${data.error || 'Unknown error'}`);
        }

        const data = await response.json();
        displaySeatMap(data);
    } catch (error) {
        console.error('Error:', error);
        seatMap.innerHTML = `
            <div class="error">
                <p>Failed to load seat map: ${error.message}</p>
                <p>Please ensure the server is running at ${API_BASE_URL} and try again.</p>
                <p>Technical details: ${error.toString()}</p>
            </div>`;
    }
}

// seat map showing
function displaySeatMap(seats) {
    seatMap.innerHTML = '';
    if (!Array.isArray(seats) || seats.length === 0) {
        seatMap.innerHTML = '<p>No seat information available.</p>';
        return;
    }

    // seat grid
    const seatGrid = document.createElement('div');
    seatGrid.className = 'seat-grid';

    // this makes seats by rows
    const rows = {};
    seats.forEach(seat => {
        const rowNum = seat.number.replace(/[A-Z]/g, '');
        if (!rows[rowNum]) {
            rows[rowNum] = [];
        }
        rows[rowNum].push(seat);
    });

    // this makes the rows
    Object.keys(rows).sort((a, b) => Number(a) - Number(b)).forEach(rowNum => {
        const rowSeats = rows[rowNum];
        const rowElement = document.createElement('div');
        rowElement.className = 'seat-row';
        
        // this adds them numbers
        const rowLabel = document.createElement('div');
        rowLabel.className = 'row-label';
        rowLabel.textContent = rowNum;
        rowElement.appendChild(rowLabel);

        // this adds the seats themselves
        rowSeats.sort((a, b) => a.number.localeCompare(b.number)).forEach(seat => {
            const seatElement = document.createElement('div');
            seatElement.className = `seat ${seat.status.toLowerCase()}`;
            seatElement.dataset.seat = seat.number;
            seatElement.dataset.isWindow = seat.window;
            seatElement.dataset.hasExtraLegroom = seat.hasExtraLegroom;
            seatElement.dataset.isNearExit = seat.nearExit;
            seatElement.textContent = seat.number.replace(/\d+/, '');
            
            if (seat.status.toLowerCase() === 'available') {
                seatElement.onclick = () => selectSeat(seat, seatElement);
            }
            
            rowElement.appendChild(seatElement);
        });

        seatGrid.appendChild(rowElement);
    });

    seatMap.appendChild(seatGrid);
    updateRecommendedSeats();
}

// this should handle seat selection
function selectSeat(seat, seatElement) {
    // this removes previously selected seat
    document.querySelectorAll('.seat.selected').forEach(el => {
        el.classList.remove('selected');
    });

    //  this selects the new seat
    seatElement.classList.add('selected');
    updateRecommendedSeats();
}

// this will be the recommende seat updater
function updateRecommendedSeats() {
    // removes previous recs
    document.querySelectorAll('.seat.recommended').forEach(el => {
        el.classList.remove('recommended');
    });

    const preferences = {
        windowSeat: windowSeatPref.checked,
        extraLegroom: extraLegroomPref.checked,
        nearExit: nearExitPref.checked
    };

    // this is the logic for recommended seats
    document.querySelectorAll('.seat.available').forEach(seatElement => {
        let isRecommended = false;
        if (preferences.windowSeat && seatElement.dataset.isWindow === 'true') {
            isRecommended = true;
        }
        if (preferences.extraLegroom && seatElement.dataset.hasExtraLegroom === 'true') {
            isRecommended = true;
        }
        if (preferences.nearExit && seatElement.dataset.isNearExit === 'true') {
            isRecommended = true;
        }
        if (isRecommended) {
            seatElement.classList.add('recommended');
        }
    });
}

// this is the listener for each rec
[windowSeatPref, extraLegroomPref, nearExitPref].forEach(pref => {
    pref.addEventListener('change', updateRecommendedSeats);
});

// this is so u can only search flights from today up to whenever in the future
const dateInput = document.getElementById('date');
const today = new Date().toISOString().split('T')[0];
dateInput.min = today;

// price display
priceDisplay.textContent = `€${priceRange.value}`; 