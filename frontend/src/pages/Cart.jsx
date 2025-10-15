import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../utils/api';
import { useState, useEffect } from 'react';

const Cart = () => {
  const { cart, updateQuantity, removeFromCart, getTotal, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loyaltyPoints, setLoyaltyPoints] = useState(0);
  const [usePoints, setUsePoints] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadLoyaltyPoints();
  }, []);

  const loadLoyaltyPoints = async () => {
    try {
      const response = await authAPI.getLoyaltyPoints();
      setLoyaltyPoints(response.data.loyaltyPoints || 0);
    } catch (error) {
      console.error('Error loading loyalty points:', error);
      setLoyaltyPoints(0);
    } finally {
      setLoading(false);
    }
  };

  const PLATFORM_FEE = 2;
  const subtotal = getTotal();
  const pointsToEarn = (subtotal / 100.0) * 0.5;
  
  // Calculate discount from points
  let pointsDiscount = 0;
  if (usePoints && loyaltyPoints > 0) {
    const totalBeforeDiscount = subtotal + PLATFORM_FEE;
    pointsDiscount = Math.min(loyaltyPoints, totalBeforeDiscount);
  }
  
  const total = subtotal + PLATFORM_FEE - pointsDiscount;

  const handleCheckout = () => {
    if (subtotal < 100) {
      alert('Minimum order value is ₹100');
      return;
    }
    // Pass usePoints state to checkout
    navigate('/checkout', { state: { usePoints } });
  };

  if (cart.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Your cart is empty</h2>
          <button
            onClick={() => navigate('/')}
            className="bg-primary text-white px-6 py-2 rounded-lg hover:bg-opacity-90"
          >
            Browse Vendors
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-2xl font-bold text-gray-900">Your Cart</h1>
            <button
              onClick={clearCart}
              className="text-red-600 hover:text-red-700"
            >
              Clear Cart
            </button>
          </div>

          <div className="space-y-4 mb-6">
            {cart.map((item) => (
              <div key={item.id} className="flex items-center space-x-4 border-b pb-4">
                <img
                  src={item.imageUrl}
                  alt={item.name}
                  className="w-20 h-20 object-cover rounded"
                />
                <div className="flex-1">
                  <h3 className="text-lg font-semibold">{item.name}</h3>
                  <p className="text-gray-600">₹{item.price}</p>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300"
                  >
                    -
                  </button>
                  <span className="w-8 text-center">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300"
                  >
                    +
                  </button>
                </div>
                <div className="text-right">
                  <p className="font-semibold">₹{(item.price * item.quantity).toFixed(2)}</p>
                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="text-red-600 text-sm hover:text-red-700"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="border-t pt-4 space-y-2">
            {/* Loyalty Program Section */}
            <div className="bg-gradient-to-r from-orange-50 to-red-50 p-4 rounded-lg mb-4 border border-orange-200">
              <div className="flex items-center justify-between mb-3">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                    🏫 Campus Loyalty Program
                  </h3>
                  <p className="text-sm text-gray-600 mt-1">Earn 0.5 points for every ₹100 spent</p>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-3 mb-3">
                <div className="bg-white p-3 rounded-md shadow-sm">
                  <p className="text-xs text-gray-600">Available Points</p>
                  <p className="text-2xl font-bold text-primary">{loyaltyPoints.toFixed(1)}</p>
                  <p className="text-xs text-gray-500">= ₹{loyaltyPoints.toFixed(2)} off</p>
                </div>
                <div className="bg-white p-3 rounded-md shadow-sm">
                  <p className="text-xs text-gray-600">You'll Earn</p>
                  <p className="text-2xl font-bold text-green-600">+{pointsToEarn.toFixed(1)}</p>
                  <p className="text-xs text-gray-500">points on this order</p>
                </div>
              </div>
              
              {loyaltyPoints > 0 && (
                <div className="flex items-center justify-between bg-white p-3 rounded-md shadow-sm">
                  <div className="flex-1">
                    <p className="font-medium text-gray-900">Use points for discount</p>
                    <p className="text-xs text-gray-600">
                      Save ₹{Math.min(loyaltyPoints, subtotal + PLATFORM_FEE).toFixed(2)} on this order
                    </p>
                  </div>
                  <button
                    onClick={() => setUsePoints(!usePoints)}
                    className={`relative inline-flex h-8 w-14 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 ${
                      usePoints ? 'bg-primary' : 'bg-gray-300'
                    }`}
                  >
                    <span
                      className={`inline-block h-6 w-6 transform rounded-full bg-white transition-transform ${
                        usePoints ? 'translate-x-7' : 'translate-x-1'
                      }`}
                    />
                  </button>
                </div>
              )}
            </div>

            <div className="flex justify-between text-gray-700">
              <span>Subtotal</span>
              <span>₹{subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-gray-700">
              <span>Platform Fee</span>
              <span>₹{PLATFORM_FEE.toFixed(2)}</span>
            </div>
            {pointsDiscount > 0 && (
              <div className="flex justify-between text-green-600 font-medium">
                <span>Points Discount</span>
                <span>-₹{pointsDiscount.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-xl font-bold text-gray-900">
              <span>Total</span>
              <span>₹{total.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <p className="text-red-600 text-sm">
                Minimum order value is ₹100. Add ₹{(100 - subtotal).toFixed(2)} more.
              </p>
            )}
          </div>

          <button
            onClick={handleCheckout}
            disabled={subtotal < 100}
            className="w-full mt-6 bg-primary text-white py-3 rounded-lg hover:bg-opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart;
