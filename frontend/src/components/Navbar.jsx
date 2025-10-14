import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { getItemCount } = useCart();

  return (
    <nav className="bg-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="flex items-center">
            <span className="text-2xl font-bold text-primary">SRMiggy</span>
          </Link>

          <div className="flex items-center space-x-6">
            {isAuthenticated ? (
              <>
                <Link to="/" className="text-gray-700 hover:text-primary">
                  Home
                </Link>
                <Link to="/orders" className="text-gray-700 hover:text-primary">
                  My Orders
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link to="/admin" className="text-gray-700 hover:text-primary">
                    Admin
                  </Link>
                )}
                <Link to="/cart" className="relative text-gray-700 hover:text-primary">
                  Cart
                  {getItemCount() > 0 && (
                    <span className="absolute -top-2 -right-2 bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                      {getItemCount()}
                    </span>
                  )}
                </Link>
                <div className="flex items-center space-x-3">
                  <span className="text-sm text-gray-600">{user?.username}</span>
                  <button
                    onClick={logout}
                    className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-700 hover:text-primary">
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90"
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
