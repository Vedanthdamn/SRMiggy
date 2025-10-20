import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useEffect, useRef, useState } from 'react';

const OrderSuccess = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const paymentMethod = location.state?.paymentMethod || 'online';
  const orderData = location.state?.order;
  const message = location.state?.message || 'Order placed successfully!';
  const mapRef = useRef(null);
  const hostelPositionRef = useRef({ lat: 12.9786, lng: 77.6000 });
  const [riderPosition, setRiderPosition] = useState({ lat: 12.9716, lng: 77.5946 });
  const [mapLoaded, setMapLoaded] = useState(false);

  useEffect(() => {
    // Load Leaflet CSS and JS
    if (!document.getElementById('leaflet-css')) {
      const link = document.createElement('link');
      link.id = 'leaflet-css';
      link.rel = 'stylesheet';
      link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
      document.head.appendChild(link);
    }

    if (!window.L) {
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => {
        setMapLoaded(true);
      };
      document.body.appendChild(script);
    } else {
      setMapLoaded(true);
    }
  }, []);

  useEffect(() => {
    if (!mapLoaded || !window.L || !mapRef.current) return;

    const hostelPosition = hostelPositionRef.current;
    
    // Initialize map
    const map = window.L.map(mapRef.current).setView([riderPosition.lat, riderPosition.lng], 13);

    // Add OpenStreetMap tiles
    window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // Add delivery rider marker
    const riderIcon = window.L.divIcon({
      className: 'custom-div-icon',
      html: "<div style='background-color:#ff6b35;width:30px;height:30px;border-radius:50%;display:flex;align-items:center;justify-content:center;border:3px solid white;box-shadow:0 2px 8px rgba(0,0,0,0.3);'>🛵</div>",
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });
    
    const riderMarker = window.L.marker([riderPosition.lat, riderPosition.lng], { icon: riderIcon }).addTo(map);
    riderMarker.bindPopup('<b>Your Delivery Rider</b><br>On the way!').openPopup();

    // Add hostel destination marker
    const hostelIcon = window.L.divIcon({
      className: 'custom-div-icon',
      html: "<div style='background-color:#22c55e;width:30px;height:30px;border-radius:50%;display:flex;align-items:center;justify-content:center;border:3px solid white;box-shadow:0 2px 8px rgba(0,0,0,0.3);'>🏠</div>",
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });
    
    window.L.marker([hostelPosition.lat, hostelPosition.lng], { icon: hostelIcon })
      .addTo(map)
      .bindPopup('<b>Your Hostel</b><br>Destination');

    // Draw route line
    const routeLine = window.L.polyline([
      [riderPosition.lat, riderPosition.lng],
      [hostelPosition.lat, hostelPosition.lng]
    ], {
      color: '#ff6b35',
      weight: 4,
      opacity: 0.7,
      dashArray: '10, 10'
    }).addTo(map);

    // Fit map to show both markers
    map.fitBounds(routeLine.getBounds(), { padding: [50, 50] });

    // Simulate rider movement towards hostel
    const moveInterval = setInterval(() => {
      setRiderPosition(prev => {
        const newLat = prev.lat + (hostelPosition.lat - prev.lat) * 0.05;
        const newLng = prev.lng + (hostelPosition.lng - prev.lng) * 0.05;
        
        // Update marker position
        riderMarker.setLatLng([newLat, newLng]);
        
        // Update route line
        routeLine.setLatLngs([
          [newLat, newLng],
          [hostelPosition.lat, hostelPosition.lng]
        ]);
        
        // Check if rider reached destination
        const distance = Math.sqrt(
          Math.pow(hostelPosition.lat - newLat, 2) + 
          Math.pow(hostelPosition.lng - newLng, 2)
        );
        
        if (distance < 0.001) {
          clearInterval(moveInterval);
          riderMarker.bindPopup('<b>Delivery Complete!</b><br>Rider has arrived.').openPopup();
        }
        
        return { lat: newLat, lng: newLng };
      });
    }, 2000);

    // Cleanup
    return () => {
      clearInterval(moveInterval);
      map.remove();
    };
  }, [mapLoaded, riderPosition.lat, riderPosition.lng]);

  // Get success message based on payment method
  const getSuccessMessage = () => {
    return 'Order placed successfully!';
  };

  const getPaymentMessage = () => {
    if (paymentMethod === 'wallet') {
      return 'Payment has been deducted from your wallet. Check your wallet balance for updated amount.';
    } else if (paymentMethod === 'cod') {
      return 'Payment will be collected on delivery.';
    }
    return 'Payment has been processed successfully.';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 transition-all duration-500 py-8 px-4">
      <div className="max-w-4xl mx-auto space-y-6">
        {/* Success Message Card */}
        <div className="backdrop-blur-lg bg-white/90 dark:bg-gray-800/90 rounded-2xl shadow-lg dark:shadow-dark-glass p-8 text-center border border-gray-200/50 dark:border-gray-700/50 animate-slide-up">
          <div className="mb-6">
            <div className="mx-auto w-20 h-20 bg-gradient-to-br from-green-100 to-green-200 dark:from-green-900/30 dark:to-green-800/30 rounded-full flex items-center justify-center shadow-lg animate-bounce-soft">
              <svg
                className="w-12 h-12 text-green-600 dark:text-green-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={3}
                  d="M5 13l4 4L19 7"
                />
              </svg>
            </div>
          </div>
          
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
            {getSuccessMessage()}
          </h1>
          {message && (
            <div className="mb-3 text-green-600 dark:text-green-400 font-semibold text-base">
              ✅ {message}
            </div>
          )}
          <p className="text-gray-600 dark:text-gray-400 mb-4 text-lg">
            Your order <span className="font-bold text-primary-600 dark:text-orange-400">#{orderId}</span> has been confirmed!
          </p>
          <p className="text-gray-500 dark:text-gray-400 text-sm">
            {getPaymentMessage()}
          </p>
        </div>

        {/* Delivery Tracking Card */}
        <div className="backdrop-blur-lg bg-white/90 dark:bg-gray-800/90 rounded-2xl shadow-lg dark:shadow-dark-glass p-6 border border-gray-200/50 dark:border-gray-700/50 animate-fade-in">
          <div className="mb-4">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white flex items-center justify-center">
              <span className="mr-2">🚚</span> Live Delivery Tracking
            </h2>
            <p className="text-center text-green-600 dark:text-green-400 font-semibold mt-2 text-lg animate-pulse">
              🛵 Your order is on the way — arriving in 20 minutes
            </p>
          </div>
          
          {/* Map Container */}
          <div 
            ref={mapRef} 
            className="w-full h-96 rounded-xl overflow-hidden border-2 border-gray-200 dark:border-gray-700 shadow-inner"
            style={{ background: '#f3f4f6' }}
          >
            {!mapLoaded && (
              <div className="w-full h-full flex items-center justify-center">
                <div className="text-center">
                  <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-primary-500 dark:border-orange-500 mx-auto mb-4"></div>
                  <p className="text-gray-600 dark:text-gray-400">Loading map...</p>
                </div>
              </div>
            )}
          </div>

          {/* Delivery Info */}
          <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-gradient-to-br from-blue-50 to-blue-100 dark:from-blue-900/30 dark:to-blue-800/30 p-4 rounded-xl text-center border border-blue-200 dark:border-blue-700">
              <div className="text-3xl mb-2">⏱️</div>
              <p className="text-sm text-gray-600 dark:text-gray-400">ETA</p>
              <p className="text-xl font-bold text-blue-600 dark:text-blue-400">20 mins</p>
            </div>
            <div className="bg-gradient-to-br from-green-50 to-green-100 dark:from-green-900/30 dark:to-green-800/30 p-4 rounded-xl text-center border border-green-200 dark:border-green-700">
              <div className="text-3xl mb-2">📍</div>
              <p className="text-sm text-gray-600 dark:text-gray-400">Status</p>
              <p className="text-xl font-bold text-green-600 dark:text-green-400">In Transit</p>
            </div>
            <div className="bg-gradient-to-br from-orange-50 to-orange-100 dark:from-orange-900/30 dark:to-orange-800/30 p-4 rounded-xl text-center border border-orange-200 dark:border-orange-700">
              <div className="text-3xl mb-2">🛵</div>
              <p className="text-sm text-gray-600 dark:text-gray-400">Rider</p>
              <p className="text-xl font-bold text-orange-600 dark:text-orange-400">On the way</p>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="backdrop-blur-lg bg-white/90 dark:bg-gray-800/90 rounded-2xl shadow-lg dark:shadow-dark-glass p-6 border border-gray-200/50 dark:border-gray-700/50">
          <div className="space-y-3">
            <button
              onClick={() => navigate(`/orders`)}
              className="w-full bg-gradient-to-r from-primary-500 to-orange-600 dark:from-orange-500 dark:to-orange-700 text-white py-3 rounded-xl hover:shadow-glow hover:scale-105 transition-all duration-300 font-semibold"
            >
              📦 View My Orders
            </button>
            <button
              onClick={() => navigate('/')}
              className="w-full bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 py-3 rounded-xl hover:bg-gray-300 dark:hover:bg-gray-600 hover:scale-105 transition-all duration-300 font-semibold"
            >
              🏠 Back to Home
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccess;
