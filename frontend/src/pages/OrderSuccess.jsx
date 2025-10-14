import { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const OrderSuccess = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8 text-center">
        <div className="mb-6">
          <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
            <svg
              className="w-8 h-8 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>
        </div>
        
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          Order Placed Successfully!
        </h1>
        <p className="text-gray-600 mb-6">
          Your order #{orderId} has been confirmed and will be delivered to you soon.
        </p>

        <div className="space-y-3">
          <button
            onClick={() => navigate(`/orders`)}
            className="w-full bg-primary text-white py-2 rounded-lg hover:bg-opacity-90"
          >
            View My Orders
          </button>
          <button
            onClick={() => navigate('/')}
            className="w-full bg-gray-200 text-gray-700 py-2 rounded-lg hover:bg-gray-300"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccess;
