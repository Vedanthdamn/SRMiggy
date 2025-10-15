import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { vendorAPI, menuAPI } from '../utils/api';
import { useCart } from '../context/CartContext';

const VendorMenu = () => {
  const { id } = useParams();
  const [vendor, setVendor] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
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

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-xl text-gray-600">Loading menu...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-start space-x-6">
            <img
              src={vendor.imageUrl}
              alt={vendor.name}
              className="w-32 h-32 object-cover rounded-lg"
            />
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900">{vendor.name}</h1>
              <p className="mt-2 text-gray-600">{vendor.description}</p>
              <div className="mt-2 flex items-center">
                <span className="text-yellow-500">★</span>
                <span className="ml-1 text-gray-700">{vendor.rating}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Menu</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {menuItems.map((item) => (
            <div
              key={item.id}
              className="bg-white rounded-lg shadow-md overflow-hidden"
            >
              <img
                src={item.imageUrl}
                alt={item.name}
                className="w-full h-40 object-cover"
              />
              <div className="p-4">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-semibold text-gray-900">
                    {item.name}
                  </h3>
                  {item.isVeg ? (
                    <span className="text-green-600 text-xs border border-green-600 px-1">
                      VEG
                    </span>
                  ) : (
                    <span className="text-red-600 text-xs border border-red-600 px-1">
                      NON-VEG
                    </span>
                  )}
                </div>
                <p className="text-gray-600 text-sm mb-3">{item.description}</p>
                <div className="flex items-center justify-between">
                  <span className="text-xl font-bold text-gray-900">
                    ₹{item.price}
                  </span>
                  <button
                    onClick={() => handleAddToCart(item)}
                    className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90"
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
