import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useState, useEffect } from 'react';
import { walletAPI } from '../utils/api';

const Cart = () => {
  const { cart, updateQuantity, removeFromCart, getTotal, clearCart } = useCart();
  const navigate = useNavigate();
  const [loyaltyPoints, setLoyaltyPoints] = useState(0);
  const [pointsToEarn, setPointsToEarn] = useState(0);
  const [useLoyaltyPoints, setUseLoyaltyPoints] = useState(false);

  const PLATFORM_FEE = 2;
  const DELIVERY_FEE = 10;
  const subtotal = getTotal();
  const deliveryFee = subtotal < 100 ? DELIVERY_FEE : 0;
  let total = subtotal + deliveryFee + PLATFORM_FEE;

  // Calculate loyalty discount
  const loyaltyDiscount = useLoyaltyPoints ? Math.min(loyaltyPoints, total) : 0;
  total = total - loyaltyDiscount;

  useEffect(() => {
    loadLoyaltyInfo();
  }, [subtotal]);

  const loadLoyaltyInfo = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await walletAPI.calculateLoyaltyPoints(subtotal);
      setLoyaltyPoints(response.data.currentPoints);
      setPointsToEarn(response.data.pointsEarnedFromOrder);
    } catch (error) {
      console.error('Error loading loyalty info:', error);
    }
  };

  const handleCheckout = () => {
    navigate('/checkout', { state: { useLoyaltyPoints } });
  };

  if (cart.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center transition-colors duration-300">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Your cart is empty</h2>
          <button
            onClick={() => navigate('/')}
            className="bg-primary dark:bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-opacity-90 transition-all"
          >
            Browse Vendors
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 py-8 transition-colors duration-300">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Your Cart</h1>
            <button
              onClick={clearCart}
              className="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 transition-colors"
            >
              Clear Cart
            </button>
          </div>

          <div className="space-y-4 mb-6">
            {cart.map((item) => (
              <div key={item.id} className="flex items-center space-x-4 border-b dark:border-gray-700 pb-4">
                <img
                  src={item.imageUrl}
                  alt={item.name}
                  className="w-20 h-20 object-cover rounded"
                />
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white">{item.name}</h3>
                  <p className="text-gray-600 dark:text-gray-400">₹{item.price}</p>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="w-8 h-8 rounded-full bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 text-gray-900 dark:text-white transition-colors"
                  >
                    -
                  </button>
                  <span className="w-8 text-center text-gray-900 dark:text-white">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="w-8 h-8 rounded-full bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 text-gray-900 dark:text-white transition-colors"
                  >
                    +
                  </button>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900 dark:text-white">₹{(item.price * item.quantity).toFixed(2)}</p>
                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="text-red-600 dark:text-red-400 text-sm hover:text-red-700 dark:hover:text-red-300 transition-colors"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="border-t dark:border-gray-700 pt-4 space-y-2">
            <div className="flex justify-between text-gray-700 dark:text-gray-300">
              <span>Subtotal</span>
              <span>₹{subtotal.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <div className="flex justify-between text-gray-700 dark:text-gray-300">
                <span>Delivery Fee (orders below ₹100)</span>
                <span>₹{deliveryFee.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-700 dark:text-gray-300">
              <span>Platform Fee</span>
              <span>₹{PLATFORM_FEE.toFixed(2)}</span>
            </div>
            {loyaltyDiscount > 0 && (
              <div className="flex justify-between text-green-600 dark:text-green-400">
                <span>Loyalty Discount</span>
                <span>-₹{loyaltyDiscount.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-xl font-bold text-gray-900 dark:text-white">
              <span>Total</span>
              <span>₹{total.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <p className="text-red-600 dark:text-red-400 text-sm">
                Minimum order value is ₹100. Add ₹{(100 - subtotal).toFixed(2)} more to avoid delivery fee.
              </p>
            )}
          </div>

          {/* Campus Loyalty Program Section */}
          <div className="border-t dark:border-gray-700 mt-4 pt-4">
            <div className="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-lg p-4 mb-4">
              <div className="flex items-center mb-2">
                <span className="text-2xl mr-2">🏫</span>
                <h3 className="font-semibold text-gray-900 dark:text-white">Campus Loyalty Program</h3>
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between items-center">
                  <span className="text-gray-700 dark:text-gray-300">Available Points:</span>
                  <span className="font-semibold text-blue-600 dark:text-blue-400">{loyaltyPoints.toFixed(1)} pts (₹{loyaltyPoints.toFixed(2)})</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-700 dark:text-gray-300">You'll Earn:</span>
                  <span className="font-semibold text-green-600 dark:text-green-400">+{pointsToEarn.toFixed(1)} pts</span>
                </div>
                <p className="text-xs text-gray-600 dark:text-gray-400 mt-2">
                  Earn 0.5 points for every ₹100 spent • 1 point = ₹1 discount
                </p>
              </div>
            </div>

            {loyaltyPoints > 0 && (
              <div className="flex items-center justify-between p-3 bg-white dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 rounded-lg hover:border-blue-300 dark:hover:border-blue-500 transition-colors">
                <div className="flex items-center space-x-3">
                  <div className={`w-12 h-6 rounded-full transition-colors duration-300 ${
                    useLoyaltyPoints ? 'bg-blue-600' : 'bg-gray-300 dark:bg-gray-600'
                  } relative cursor-pointer`}
                  onClick={() => setUseLoyaltyPoints(!useLoyaltyPoints)}
                  >
                    <div className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full transition-transform duration-300 ${
                      useLoyaltyPoints ? 'transform translate-x-6' : ''
                    }`}></div>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">Use Loyalty Points</p>
                    <p className="text-xs text-gray-600 dark:text-gray-400">
                      {useLoyaltyPoints ? `Applying ${Math.min(loyaltyPoints, subtotal + deliveryFee + PLATFORM_FEE).toFixed(1)} pts` : 'Apply points to get discount'}
                    </p>
                  </div>
                </div>
                {useLoyaltyPoints && (
                  <span className="text-green-600 dark:text-green-400 font-semibold">
                    Save ₹{loyaltyDiscount.toFixed(2)}
                  </span>
                )}
              </div>
            )}
          </div>

          <button
            onClick={handleCheckout}
            className="w-full mt-6 bg-primary dark:bg-orange-500 text-white py-3 rounded-lg hover:bg-opacity-90 transition-all"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart;
