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
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center transition-all duration-500">
        <div className="text-center animate-fade-in">
          <div className="mb-6 text-6xl animate-bounce-soft">🛒</div>
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Your cart is empty</h2>
          <p className="text-gray-600 dark:text-gray-400 mb-6">Start adding delicious items!</p>
          <button
            onClick={() => navigate('/')}
            className="bg-gradient-to-r from-primary-500 to-primary-600 dark:from-orange-500 dark:to-orange-600 text-white px-8 py-3 rounded-xl hover:shadow-glow hover:scale-105 transition-all duration-300 font-semibold"
          >
            Browse Vendors
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-orange-50 to-gray-100 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 py-8 transition-all duration-500">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="backdrop-blur-lg bg-white/80 dark:bg-gray-800/80 rounded-2xl shadow-glass dark:shadow-dark-glass p-6 border border-gray-200/50 dark:border-gray-700/50 animate-slide-up">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold bg-gradient-to-r from-primary-600 to-orange-600 dark:from-orange-400 dark:to-orange-500 bg-clip-text text-transparent">
              Your Cart
            </h1>
            <button
              onClick={clearCart}
              className="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 transition-all duration-300 hover:scale-110 font-medium px-4 py-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20"
            >
              Clear Cart
            </button>
          </div>

          <div className="space-y-4 mb-6">
            {cart.map((item, index) => (
              <div 
                key={item.id} 
                className="flex items-center space-x-4 border-b dark:border-gray-700 pb-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 p-3 rounded-lg transition-all duration-300 group animate-fade-in"
                style={{ animationDelay: `${index * 50}ms` }}
              >
                <img
                  src={item.imageUrl}
                  alt={item.name}
                  className="w-24 h-24 object-cover rounded-xl shadow-md group-hover:scale-105 transition-transform duration-300"
                />
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white">{item.name}</h3>
                  <p className="text-primary-600 dark:text-orange-400 font-semibold">₹{item.price}</p>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="w-10 h-10 rounded-full bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-600 hover:from-gray-200 hover:to-gray-300 dark:hover:from-gray-600 dark:hover:to-gray-500 text-gray-900 dark:text-white transition-all duration-300 font-bold hover:scale-110 shadow-md"
                  >
                    -
                  </button>
                  <span className="w-10 text-center text-gray-900 dark:text-white font-semibold text-lg">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-orange-600 dark:from-orange-500 dark:to-orange-600 hover:from-primary-600 hover:to-orange-700 dark:hover:from-orange-600 dark:hover:to-orange-700 text-white transition-all duration-300 font-bold hover:scale-110 shadow-md hover:shadow-glow"
                  >
                    +
                  </button>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-900 dark:text-white text-lg">₹{(item.price * item.quantity).toFixed(2)}</p>
                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="text-red-600 dark:text-red-400 text-sm hover:text-red-700 dark:hover:text-red-300 transition-all duration-300 hover:scale-110 font-medium"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="border-t dark:border-gray-700 pt-4 space-y-3 bg-gradient-to-br from-gray-50 to-white dark:from-gray-700/50 dark:to-gray-800/50 p-4 rounded-xl">
            <div className="flex justify-between text-gray-700 dark:text-gray-300 text-lg">
              <span>Subtotal</span>
              <span className="font-semibold">₹{subtotal.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <div className="flex justify-between text-gray-700 dark:text-gray-300">
                <span className="text-sm">Delivery Fee (orders below ₹100)</span>
                <span className="font-semibold text-amber-600 dark:text-amber-400">₹{deliveryFee.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-700 dark:text-gray-300">
              <span className="text-sm">Platform Fee</span>
              <span className="font-semibold">₹{PLATFORM_FEE.toFixed(2)}</span>
            </div>
            {loyaltyDiscount > 0 && (
              <div className="flex justify-between text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-900/20 p-2 rounded-lg animate-slide-down">
                <span className="font-medium">🎉 Loyalty Discount</span>
                <span className="font-bold">-₹{loyaltyDiscount.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-2xl font-bold text-gray-900 dark:text-white pt-3 border-t dark:border-gray-600">
              <span>Total</span>
              <span className="bg-gradient-to-r from-primary-600 to-orange-600 dark:from-orange-400 dark:to-orange-500 bg-clip-text text-transparent">
                ₹{total.toFixed(2)}
              </span>
            </div>
            {subtotal < 100 && (
              <div className="text-red-600 dark:text-red-400 text-sm bg-red-50 dark:bg-red-900/20 p-3 rounded-lg animate-pulse-soft">
                💡 Add ₹{(100 - subtotal).toFixed(2)} more to avoid delivery fee!
              </div>
            )}
          </div>

          {/* Campus Loyalty Program Section */}
          <div className="border-t dark:border-gray-700 mt-6 pt-6">
            <div className="bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-blue-900/30 dark:via-indigo-900/30 dark:to-purple-900/30 rounded-2xl p-5 mb-4 shadow-lg backdrop-blur-sm border border-blue-200/50 dark:border-blue-700/50 animate-slide-up">
              <div className="flex items-center mb-3">
                <span className="text-3xl mr-3 animate-bounce-soft">🏫</span>
                <h3 className="font-bold text-xl bg-gradient-to-r from-blue-600 to-indigo-600 dark:from-blue-400 dark:to-indigo-400 bg-clip-text text-transparent">
                  Campus Loyalty Program
                </h3>
              </div>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between items-center bg-white/50 dark:bg-gray-800/50 p-3 rounded-lg">
                  <span className="text-gray-700 dark:text-gray-300 font-medium">Available Points:</span>
                  <span className="font-bold text-lg bg-gradient-to-r from-blue-600 to-purple-600 dark:from-blue-400 dark:to-purple-400 bg-clip-text text-transparent">
                    {loyaltyPoints.toFixed(1)} pts (₹{loyaltyPoints.toFixed(2)})
                  </span>
                </div>
                <div className="flex justify-between items-center bg-white/50 dark:bg-gray-800/50 p-3 rounded-lg">
                  <span className="text-gray-700 dark:text-gray-300 font-medium">You'll Earn:</span>
                  <span className="font-bold text-lg text-green-600 dark:text-green-400">+{pointsToEarn.toFixed(1)} pts</span>
                </div>
                <p className="text-xs text-gray-600 dark:text-gray-400 mt-3 bg-white/30 dark:bg-gray-800/30 p-2 rounded-lg">
                  ✨ Earn 0.5 points for every ₹100 spent • 1 point = ₹1 discount
                </p>
              </div>
            </div>

            {loyaltyPoints > 0 && (
              <div className="flex items-center justify-between p-4 bg-gradient-to-r from-white to-gray-50 dark:from-gray-700 dark:to-gray-800 border-2 border-blue-200 dark:border-blue-600 rounded-xl hover:border-blue-400 dark:hover:border-blue-400 transition-all duration-300 shadow-md hover:shadow-lg group">
                <div className="flex items-center space-x-4">
                  <div 
                    className={`w-14 h-7 rounded-full transition-all duration-300 cursor-pointer ${
                      useLoyaltyPoints 
                        ? 'bg-gradient-to-r from-blue-500 to-indigo-600 shadow-glow' 
                        : 'bg-gray-300 dark:bg-gray-600'
                    } relative`}
                    onClick={() => setUseLoyaltyPoints(!useLoyaltyPoints)}
                  >
                    <div className={`absolute top-0.5 left-0.5 w-6 h-6 bg-white rounded-full transition-all duration-300 shadow-md ${
                      useLoyaltyPoints ? 'transform translate-x-7' : ''
                    }`}>
                      <span className="absolute inset-0 flex items-center justify-center text-xs">
                        {useLoyaltyPoints ? '✓' : '○'}
                      </span>
                    </div>
                  </div>
                  <div>
                    <p className="font-bold text-gray-900 dark:text-white">Use Loyalty Points</p>
                    <p className="text-xs text-gray-600 dark:text-gray-400">
                      {useLoyaltyPoints ? `Applying ${Math.min(loyaltyPoints, subtotal + deliveryFee + PLATFORM_FEE).toFixed(1)} pts` : 'Apply points to get instant discount'}
                    </p>
                  </div>
                </div>
                {useLoyaltyPoints && (
                  <span className="text-green-600 dark:text-green-400 font-bold text-lg animate-scale-up">
                    Save ₹{loyaltyDiscount.toFixed(2)}
                  </span>
                )}
              </div>
            )}
          </div>

          <button
            onClick={handleCheckout}
            className="w-full mt-6 bg-gradient-to-r from-primary-500 to-orange-600 dark:from-orange-500 dark:to-orange-700 text-white py-4 rounded-xl hover:shadow-glow-lg hover:scale-[1.02] transition-all duration-300 font-bold text-lg"
          >
            Proceed to Checkout →
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart;
