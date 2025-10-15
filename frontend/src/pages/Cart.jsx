import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';

const Cart = () => {
  const { cart, updateQuantity, removeFromCart, getTotal, clearCart } = useCart();
  const navigate = useNavigate();

  const PLATFORM_FEE = 2;
  const DELIVERY_FEE = 10;
  const subtotal = getTotal();
  const deliveryFee = subtotal < 100 ? DELIVERY_FEE : 0;
  const total = subtotal + deliveryFee + PLATFORM_FEE;

  const handleCheckout = () => {
    navigate('/checkout');
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
            <div className="flex justify-between text-gray-700">
              <span>Subtotal</span>
              <span>₹{subtotal.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <div className="flex justify-between text-gray-700">
                <span>Delivery Fee (orders below ₹100)</span>
                <span>₹{deliveryFee.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-700">
              <span>Platform Fee</span>
              <span>₹{PLATFORM_FEE.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-xl font-bold text-gray-900">
              <span>Total</span>
              <span>₹{total.toFixed(2)}</span>
            </div>
            {subtotal < 100 && (
              <p className="text-red-600 text-sm">
                Minimum order value is ₹100. Add ₹{(100 - subtotal).toFixed(2)} more to avoid delivery fee.
              </p>
            )}
          </div>

          <button
            onClick={handleCheckout}
            className="w-full mt-6 bg-primary text-white py-3 rounded-lg hover:bg-opacity-90"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart;
