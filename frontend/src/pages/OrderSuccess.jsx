import { useParams, useNavigate } from 'react-router-dom';

const OrderSuccess = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center transition-all duration-500 py-8 px-4">
      <div className="max-w-md w-full backdrop-blur-lg bg-white/90 dark:bg-gray-800/90 rounded-2xl shadow-lg dark:shadow-dark-glass p-8 text-center border border-gray-200/50 dark:border-gray-700/50 animate-slide-up">
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
          Order Placed Successfully!
        </h1>
        <p className="text-gray-600 dark:text-gray-400 mb-6 text-lg">
          Your order <span className="font-bold text-primary-600 dark:text-orange-400">#{orderId}</span> has been confirmed and will be delivered to you soon.
        </p>

        <div className="space-y-3">
          <button
            onClick={() => navigate(`/orders`)}
            className="w-full bg-gradient-to-r from-primary-500 to-orange-600 dark:from-orange-500 dark:to-orange-700 text-white py-3 rounded-xl hover:shadow-glow hover:scale-105 transition-all duration-300 font-semibold"
          >
            View My Orders
          </button>
          <button
            onClick={() => navigate('/')}
            className="w-full bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 py-3 rounded-xl hover:bg-gray-300 dark:hover:bg-gray-600 hover:scale-105 transition-all duration-300 font-semibold"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccess;
