document.addEventListener("DOMContentLoaded", async function () {
    const mapElement = document.getElementById('map');
    if (!mapElement) return;

    const defaultCity = "Paris";
    const orderIdElem = document.getElementById("order-id");
    const userAddress = orderIdElem.dataset.address || defaultCity;

   let userCoords = [48.8566, 2.3522];


    try {
        const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(userAddress)}`);
        const data = await response.json();
        if (data && data.length > 0) {
            userCoords = [parseFloat(data[0].lat), parseFloat(data[0].lon)];
        }
    } catch (e) {
        console.warn("Not possible to geolocate your coordinates", e);
    }

    const restaurantCoords = [userCoords[0] - 0.008, userCoords[1] - 0.008];

    const map = L.map('map').setView(userCoords, 14);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
    }).addTo(map);

    setTimeout(() => { map.invalidateSize(); }, 200);

    // Маркеры
    L.marker(restaurantCoords).addTo(map).bindPopup("<b>TacoCloud Kitchen</b> 🌮");
    L.marker(userCoords).addTo(map).bindPopup(`<b>Your address:</b><br>${userAddress} 🏠`);

    // Иконка Курьера
    const courierIcon = L.divIcon({
        html: '<div style="font-size: 32px; line-height: 1; filter: drop-shadow(0 4px 6px rgba(0,0,0,0.3));">🛵</div>',
        className: 'courier-emoji-marker',
        iconSize: [35, 35],
        iconAnchor: [17, 17]
    });

    const courierMarker = L.marker(restaurantCoords, { icon: courierIcon }).addTo(map);

    const orderId = orderIdElem.dataset.id;
    const eventSource = new EventSource(`/api/orders/${orderId}/track`);

    eventSource.onmessage = function (event) {
        try {
            const data = JSON.parse(event.data);

            const statusElem = document.getElementById("status-text");
            if (statusElem) statusElem.textContent = data.status;

            if (data.progress !== undefined) {
                const progress = data.progress;
                const curLat = restaurantCoords[0] + (userCoords[0] - restaurantCoords[0]) * progress;
                const curLng = restaurantCoords[1] + (userCoords[1] - restaurantCoords[1]) * progress;

                courierMarker.setLatLng([curLat, curLng]);
            }
        } catch (e) {
            console.error(e);
        }
    };

    eventSource.onerror = function () {
        eventSource.close();
    };
});