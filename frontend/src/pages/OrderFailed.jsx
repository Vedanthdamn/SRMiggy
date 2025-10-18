import { useNavigate } from 'react-router-dom';

const OrderFailed = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center transition-all duration-500 py-8 px-4">
      <div className="max-w-md w-full backdrop-blur-lg bg-white/90 dark:bg-gray-800/90 rounded-2xl shadow-lg dark:shadow-dark-glass p-8 text-center border border-gray-200/50 dark:border-gray-700/50 animate-slide-up">
        <div className="mb-6">
          <div className="mx-auto w-20 h-20 bg-gradient-to-br from-red-100 to-red-200 dark:from-red-900/30 dark:to-red-800/30 rounded-full flex items-center justify-center shadow-lg animate-bounce-soft">
            <svg
              className="w-12 h-12 text-red-600 dark:text-red-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={3}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </div>
        </div>
        
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          ❌ Failed to Place Order
        </h1>
        <p className="text-gray-600 dark:text-gray-400 mb-6 text-lg">
          We're sorry, but your order could not be processed. This could be due to payment issues or system errors.
        </p>

        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-700 rounded-xl p-4 mb-6">
          <p className="text-sm text-red-800 dark:text-red-300">
            💡 <strong>What you can do:</strong>
          </p>
          <ul className="text-sm text-red-700 dark:text-red-400 mt-2 text-left list-disc list-inside">
            <li>Check your wallet balance or payment method</li>
            <li>Verify your delivery details are correct</li>
            <li>Try placing the order again</li>
          </ul>
        </div>

        <div className="space-y-3">
          <button
            onClick={() => navigate('/checkout')}
            className="w-full bg-gradient-to-r from-primary-500 to-orange-600 dark:from-orange-500 dark:to-orange-700 text-white py-3 rounded-xl hover:shadow-glow hover:scale-105 transition-all duration-300 font-semibold"
          >
            Try Again
          </button>
          <button
            onClick={() => navigate('/cart')}
            className="w-full bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 py-3 rounded-xl hover:bg-gray-300 dark:hover:bg-gray-600 hover:scale-105 transition-all duration-300 font-semibold"
          >
            Back to Cart
          </button>
          <button
            onClick={() => navigate('/')}
            className="w-full bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 py-3 rounded-xl hover:bg-gray-200 dark:hover:bg-gray-700 hover:scale-105 transition-all duration-300 font-semibold"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderFailed;
