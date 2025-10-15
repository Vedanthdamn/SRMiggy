import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { useTheme } from '../context/ThemeContext';
import { useState, useEffect } from 'react';
import { walletAPI } from '../utils/api';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { getItemCount } = useCart();
  const { isDark, toggleTheme } = useTheme();
  const [walletBalance, setWalletBalance] = useState(0);

  useEffect(() => {
    if (isAuthenticated) {
      loadWalletBalance();
    }
  }, [isAuthenticated]);

  const loadWalletBalance = async () => {
    try {
      const response = await walletAPI.getBalance();
      setWalletBalance(response.data);
    } catch (error) {
      console.error('Error loading wallet balance:', error);
    }
  };

  return (
    <nav className="bg-white dark:bg-gray-800 shadow-lg transition-colors duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="flex items-center">
            <span className="text-2xl font-bold text-primary dark:text-orange-400">SRMiggy</span>
          </Link>

          <div className="flex items-center space-x-6">
            {/* Dark Mode Toggle */}
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors duration-200"
              aria-label="Toggle dark mode"
            >
              {isDark ? (
                <span className="text-xl">☀️</span>
              ) : (
                <span className="text-xl">🌙</span>
              )}
            </button>

            {isAuthenticated ? (
              <>
                <Link to="/" className="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 transition-colors">
                  Home
                </Link>
                <Link to="/wallet" className="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 flex items-center transition-colors">
                  <span className="mr-1">💰</span>
                  <span>₹{walletBalance.toFixed(2)}</span>
                </Link>
                <Link to="/orders" className="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 transition-colors">
                  My Orders
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link to="/admin" className="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 transition-colors">
                    Admin
                  </Link>
                )}
                <Link to="/cart" className="relative text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 transition-colors">
                  Cart
                  {getItemCount() > 0 && (
                    <span className="absolute -top-2 -right-2 bg-primary dark:bg-orange-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                      {getItemCount()}
                    </span>
                  )}
                </Link>
                <div className="flex items-center space-x-3">
                  <span className="text-sm text-gray-600 dark:text-gray-400">{user?.username}</span>
                  <button
                    onClick={logout}
                    className="bg-primary dark:bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition-all"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-orange-400 transition-colors">
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-primary dark:bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition-all"
                >
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
