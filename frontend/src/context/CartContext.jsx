import { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [vendorId, setVendorId] = useState(null);

  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    const savedVendorId = localStorage.getItem('cartVendorId');
    if (savedCart) {
      setCart(JSON.parse(savedCart));
    }
    if (savedVendorId) {
      setVendorId(Number(savedVendorId));
    }
  }, []);

  const saveCart = (newCart, newVendorId) => {
    setCart(newCart);
    setVendorId(newVendorId);
    localStorage.setItem('cart', JSON.stringify(newCart));
    localStorage.setItem('cartVendorId', newVendorId.toString());
  };

  const addToCart = (menuItem, vendor) => {
    // Check if adding from different vendor
    if (vendorId && vendorId !== vendor.id) {
      if (!window.confirm('This will clear your current cart. Continue?')) {
        return false;
      }
      clearCart();
    }

    const existingItem = cart.find(item => item.id === menuItem.id);
    let newCart;
    
    if (existingItem) {
      newCart = cart.map(item =>
        item.id === menuItem.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      );
    } else {
      newCart = [...cart, { ...menuItem, quantity: 1 }];
    }

    saveCart(newCart, vendor.id);
    return true;
  };

  const removeFromCart = (menuItemId) => {
    const newCart = cart.filter(item => item.id !== menuItemId);
    saveCart(newCart, vendorId);
  };

  const updateQuantity = (menuItemId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(menuItemId);
      return;
    }

    const newCart = cart.map(item =>
      item.id === menuItemId ? { ...item, quantity } : item
    );
    saveCart(newCart, vendorId);
  };

  const clearCart = () => {
    setCart([]);
    setVendorId(null);
    localStorage.removeItem('cart');
    localStorage.removeItem('cartVendorId');
  };

  const getTotal = () => {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  };

  const getItemCount = () => {
    return cart.reduce((sum, item) => sum + item.quantity, 0);
  };

  const value = {
    cart,
    vendorId,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getTotal,
    getItemCount,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
