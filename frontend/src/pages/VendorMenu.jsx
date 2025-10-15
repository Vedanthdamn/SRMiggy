import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { vendorAPI, menuAPI } from '../utils/api';
import { useCart } from '../context/CartContext';

const VendorMenu = () => {
  const { id } = useParams();
  const [vendor, setVendor] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState('all'); // 'all', 'veg', 'non-veg'
  const { addToCart } = useCart();

  useEffect(() => {
    const loadVendorAndMenu = async () => {
      try {
        const [vendorRes, menuRes] = await Promise.all([
          vendorAPI.getById(id),
          menuAPI.getByVendor(id),
        ]);
        setVendor(vendorRes.data);
        setMenuItems(menuRes.data);
      } catch (error) {
        console.error('Error loading vendor menu:', error);
      } finally {
        setLoading(false);
      }
    };
    
    loadVendorAndMenu();
  }, [id]);

  const handleAddToCart = (menuItem) => {
    const success = addToCart(menuItem, vendor);
    if (success) {
      alert('Added to cart!');
    }
  };

  // Filter menu items based on selected filter type
  const filteredMenuItems = menuItems.filter((item) => {
    if (filterType === 'veg') return item.isVeg === true;
    if (filterType === 'non-veg') return item.isVeg === false;
    return true; // 'all' - show everything
  });

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
        <div className="text-xl text-gray-600 dark:text-gray-400">Loading menu...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
      <div className="bg-white dark:bg-gray-800 shadow transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-start space-x-6">
            <img
              src={vendor.imageUrl}
              alt={vendor.name}
              className="w-32 h-32 object-cover rounded-lg"
            />
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{vendor.name}</h1>
              <p className="mt-2 text-gray-600 dark:text-gray-400">{vendor.description}</p>
              <div className="mt-2 flex items-center">
                <span className="text-yellow-500">★</span>
                <span className="ml-1 text-gray-700 dark:text-gray-300">{vendor.rating}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Menu</h2>
          
          {/* Toggle Filter Buttons */}
          <div className="flex items-center space-x-2 bg-gray-100 dark:bg-gray-700 rounded-lg p-1 transition-colors">
            <button
              onClick={() => setFilterType('all')}
              className={`px-4 py-2 rounded-md font-medium transition-all duration-300 ${
                filterType === 'all'
                  ? 'bg-white dark:bg-gray-600 text-gray-900 dark:text-white shadow-md'
                  : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
              }`}
            >
              All
            </button>
            <button
              onClick={() => setFilterType('veg')}
              className={`px-4 py-2 rounded-md font-medium transition-all duration-300 flex items-center space-x-1 ${
                filterType === 'veg'
                  ? 'bg-green-100 dark:bg-green-900/50 text-green-700 dark:text-green-400 shadow-md'
                  : 'text-gray-600 dark:text-gray-300 hover:text-green-700 dark:hover:text-green-400'
              }`}
            >
              <span className="text-lg">🟢</span>
              <span>Veg</span>
            </button>
            <button
              onClick={() => setFilterType('non-veg')}
              className={`px-4 py-2 rounded-md font-medium transition-all duration-300 flex items-center space-x-1 ${
                filterType === 'non-veg'
                  ? 'bg-red-100 dark:bg-red-900/50 text-red-700 dark:text-red-400 shadow-md'
                  : 'text-gray-600 dark:text-gray-300 hover:text-red-700 dark:hover:text-red-400'
              }`}
            >
              <span className="text-lg">🔴</span>
              <span>Non-Veg</span>
            </button>
          </div>
        </div>
        
        {/* Items count indicator */}
        {filterType !== 'all' && (
          <div className="mb-4 text-sm text-gray-600 dark:text-gray-400">
            Showing {filteredMenuItems.length} {filterType === 'veg' ? 'vegetarian' : 'non-vegetarian'} {filteredMenuItems.length === 1 ? 'item' : 'items'}
          </div>
        )}
        
        {/* Menu Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredMenuItems.map((item) => (
            <div
              key={item.id}
              className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden transition-colors"
            >
              <img
                src={item.imageUrl}
                alt={item.name}
                className="w-full h-40 object-cover"
              />
              <div className="p-4">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                    {item.name}
                  </h3>
                  {item.isVeg ? (
                    <span className="text-green-600 dark:text-green-400 text-xs border border-green-600 dark:border-green-400 px-1">
                      VEG
                    </span>
                  ) : (
                    <span className="text-red-600 dark:text-red-400 text-xs border border-red-600 dark:border-red-400 px-1">
                      NON-VEG
                    </span>
                  )}
                </div>
                <p className="text-gray-600 dark:text-gray-400 text-sm mb-3">{item.description}</p>
                <div className="flex items-center justify-between">
                  <span className="text-xl font-bold text-gray-900 dark:text-white">
                    ₹{item.price}
                  </span>
                  <button
                    onClick={() => handleAddToCart(item)}
                    className="bg-primary dark:bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition-all"
                  >
                    Add to Cart
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default VendorMenu;
