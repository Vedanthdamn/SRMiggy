import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { slotAPI, orderAPI, paymentAPI, walletAPI } from '../utils/api';

const Checkout = () => {
  const { cart, vendorId, getTotal, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState(user?.address || '');
  const [customerPhone, setCustomerPhone] = useState(user?.phone || '');
  const [loading, setLoading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState('mock');
  const [walletBalance, setWalletBalance] = useState(0);

  useEffect(() => {
    loadSlots();
    loadWalletBalance();
  }, []);

  const loadSlots = async () => {
    try {
      const response = await slotAPI.getActive();
      setSlots(response.data);
    } catch (error) {
      console.error('Error loading slots:', error);
    }
  };

  const loadWalletBalance = async () => {
    try {
      const response = await walletAPI.getBalance();
      setWalletBalance(response.data);
    } catch (error) {
      console.error('Error loading wallet balance:', error);
    }
  };

  const handlePlaceOrder = async () => {
    if (!selectedSlot) {
      alert('Please select a delivery slot');
      return;
    }

    const PLATFORM_FEE = 2;
    const subtotal = getTotal();
    const total = subtotal + PLATFORM_FEE;

    if (paymentMethod === 'wallet' && walletBalance < total) {
      alert('Insufficient wallet balance. Please add money to your wallet or use another payment method.');
      return;
    }

    setLoading(true);

    try {
      // Create order
      const orderData = {
        vendorId,
        slotId: parseInt(selectedSlot),
        deliveryAddress,
        customerPhone,
        items: cart.map(item => ({
          menuItemId: item.id,
          quantity: item.quantity,
        })),
      };

      const orderResponse = await orderAPI.create(orderData);
      const orderId = orderResponse.data.id;

      if (paymentMethod === 'wallet') {
        // Pay with wallet
        await paymentAPI.payWithWallet(orderId);
      } else {
        // Create payment order
        const paymentResponse = await paymentAPI.createOrder(orderId);
        const { providerOrderId } = paymentResponse.data;

        // Simulate payment (in real scenario, this would redirect to payment gateway)
        const mockPaymentId = 'MOCK_PAY_' + Math.random().toString(36).substr(2, 9);
        const mockSignature = 'MOCK_SIG_' + Math.random().toString(36).substr(2, 9);

        // Verify payment
        await paymentAPI.verify({
          providerOrderId,
          providerPaymentId: mockPaymentId,
          providerSignature: mockSignature,
        });
      }

      // Clear cart and navigate to success page
      clearCart();
      navigate(`/order-success/${orderId}`);
    } catch (error) {
      console.error('Error placing order:', error);
      alert(error.response?.data?.message || 'Failed to place order');
    } finally {
      setLoading(false);
    }
  };

  const PLATFORM_FEE = 2;
  const DELIVERY_FEE = 10;
  const subtotal = getTotal();
  const deliveryFee = subtotal < 100 ? DELIVERY_FEE : 0;
  const total = subtotal + deliveryFee + PLATFORM_FEE;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-6">Checkout</h1>

          <div className="space-y-6">
            {/* Delivery Details */}
            <div>
              <h2 className="text-lg font-semibold mb-4">Delivery Details</h2>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Delivery Address
                  </label>
                  <input
                    type="text"
                    value={deliveryAddress}
                    onChange={(e) => setDeliveryAddress(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Phone Number
                  </label>
                  <input
                    type="tel"
                    value={customerPhone}
                    onChange={(e) => setCustomerPhone(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Delivery Slot Selection */}
            <div>
              <h2 className="text-lg font-semibold mb-4">Select Delivery Slot</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {slots.map((slot) => (
                  <button
                    key={slot.id}
                    onClick={() => setSelectedSlot(slot.id.toString())}
                    className={`p-4 border-2 rounded-lg text-center ${
                      selectedSlot === slot.id.toString()
                        ? 'border-primary bg-primary bg-opacity-10'
                        : 'border-gray-300 hover:border-primary'
                    }`}
                  >
                    <div className="font-semibold">{slot.displayName}</div>
                  </button>
                ))}
              </div>
            </div>

            {/* Payment Method Selection */}
            <div>
              <h2 className="text-lg font-semibold mb-4">Payment Method</h2>
              <div className="space-y-3">
                <div
                  onClick={() => setPaymentMethod('wallet')}
                  className={`p-4 border-2 rounded-lg cursor-pointer ${
                    paymentMethod === 'wallet'
                      ? 'border-primary bg-primary bg-opacity-10'
                      : 'border-gray-300 hover:border-primary'
                  }`}
                >
                  <div className="flex justify-between items-center">
                    <div className="flex items-center">
                      <span className="text-2xl mr-3">💰</span>
                      <div>
                        <div className="font-semibold">Wallet</div>
                        <div className="text-sm text-gray-600">
                          Available Balance: ₹{walletBalance.toFixed(2)}
                        </div>
                      </div>
                    </div>
                    {paymentMethod === 'wallet' && (
                      <span className="text-primary">✓</span>
                    )}
                  </div>
                  {paymentMethod === 'wallet' && walletBalance < total && (
                    <div className="mt-2 text-sm text-red-600">
                      Insufficient balance. Please add ₹{(total - walletBalance).toFixed(2)} more.
                    </div>
                  )}
                </div>
                <div
                  onClick={() => setPaymentMethod('mock')}
                  className={`p-4 border-2 rounded-lg cursor-pointer ${
                    paymentMethod === 'mock'
                      ? 'border-primary bg-primary bg-opacity-10'
                      : 'border-gray-300 hover:border-primary'
                  }`}
                >
                  <div className="flex justify-between items-center">
                    <div className="flex items-center">
                      <span className="text-2xl mr-3">💳</span>
                      <div>
                        <div className="font-semibold">Other Payment Methods</div>
                        <div className="text-sm text-gray-600">
                          Credit/Debit Card, UPI, Net Banking
                        </div>
                      </div>
                    </div>
                    {paymentMethod === 'mock' && (
                      <span className="text-primary">✓</span>
                    )}
                  </div>
                </div>
              </div>
            </div>

            {/* Order Summary */}
            <div>
              <h2 className="text-lg font-semibold mb-4">Order Summary</h2>
              <div className="space-y-2">
                {cart.map((item) => (
                  <div key={item.id} className="flex justify-between text-gray-700">
                    <span>
                      {item.name} x {item.quantity}
                    </span>
                    <span>₹{(item.price * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
                <div className="border-t pt-2 mt-2">
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
                  <div className="flex justify-between text-xl font-bold text-gray-900 mt-2">
                    <span>Total</span>
                    <span>₹{total.toFixed(2)}</span>
                  </div>
                </div>
              </div>
            </div>

            <button
              onClick={handlePlaceOrder}
              disabled={loading || !selectedSlot || (paymentMethod === 'wallet' && walletBalance < total)}
              className="w-full bg-primary text-white py-3 rounded-lg hover:bg-opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Processing...' : `Place Order & Pay ₹${total.toFixed(2)}`}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
